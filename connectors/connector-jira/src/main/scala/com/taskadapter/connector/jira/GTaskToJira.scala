package com.taskadapter.connector.jira

import java.util

import com.atlassian.jira.rest.client.api.domain.input.{ComplexIssueInputFieldValue, FieldInput, IssueInputBuilder}
import com.atlassian.jira.rest.client.api.domain.{BasicComponent, IssueFieldId, IssueType, Priority, TimeTracking, Version}
import com.google.common.collect.ImmutableList
import com.taskadapter.connector.common.ValueTypeResolver
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.exception.FieldConversionException
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.{Summary, _}
import org.joda.time.DateTime

import scala.collection.JavaConverters._

object GTaskToJira {
  def getVersion(versions: Iterable[Version], versionName: String): Version = {
    versions.find(_.getName == versionName).orNull
  }

  def getComponent(objects: Iterable[BasicComponent], name: String): Option[BasicComponent] = {
    objects.find(_.getName == name)
  }
}

class GTaskToJira(config: JiraConfig,
                  customFieldResolver: CustomFieldResolver,
                  versions: Iterable[Version],
                  components: Iterable[BasicComponent],
                  jiraPriorities: Iterable[Priority])
  extends ConnectorConverter[GTask, IssueWrapper] {

  val priorities = jiraPriorities.map(p => p.getName -> p).toMap

  def convertToJiraIssue(task: GTask) : IssueWrapper = {
    val issueInputBuilder = new IssueInputBuilder()
    issueInputBuilder.setProjectKey(config.getProjectKey)

    // issue type has to be set later in JiraTaskSaver because it depends on create/update

    if (task.getParentIdentity != null) {
      // See http://stackoverflow.com/questions/14699893/how-to-create-subtasks-using-jira-rest-java-client
      val parent = new util.HashMap[String, AnyRef]
      parent.put("key", task.getParentIdentity.key)
      val parentField = new FieldInput("parent", new ComplexIssueInputFieldValue(parent))
      issueInputBuilder.setFieldInput(parentField)
    }
    for (row <- task.getFields.entrySet.asScala) {
      try {
      processField(issueInputBuilder, row.getKey, row.getValue)
      } catch {
        case _: Exception => throw FieldConversionException(JiraConnector.ID, row.getKey, row.getValue)
      }
    }
    val affectedVersion = GTaskToJira.getVersion(versions, config.getAffectedVersion)
    val fixForVersion = GTaskToJira.getVersion(versions, config.getFixForVersion)
    if (affectedVersion != null) issueInputBuilder.setAffectedVersions(ImmutableList.of(affectedVersion))
    if (fixForVersion != null) issueInputBuilder.setFixVersions(ImmutableList.of(fixForVersion))
    val issueInput = issueInputBuilder.build

    val status = task.getValue(TaskStatus)
    IssueWrapper(task.getKey, issueInput, status, Option(task.getValue(TaskType)))
  }

  private def processField(issueInputBuilder: IssueInputBuilder, field: Field[_], value: Any) : Unit = {
    field match {
      case Children => // processed in another place
      case Id => // ignore ID field because it does not need to be provided when saving
      case Key => // processed in [[DefaultValueSetter]]
      case SourceSystemId => // processed in [[DefaultValueSetter]]
      case ParentKey => // processed above
      case Relations => // processed in another place

      case Summary => issueInputBuilder.setSummary(value.asInstanceOf[String])
      case Components =>
        // only first value from the list is used
        if (value != null) {
          val strings = value.asInstanceOf[Seq[String]]
          if (strings.nonEmpty) {
            val firstComponentName = strings.head
            val component = GTaskToJira.getComponent(components, firstComponentName)
            component.map(issueInputBuilder.setComponents(_))
          }
        } else {
            // this will erase any existing components in this task
            issueInputBuilder.setComponents()
        }
      case TaskStatus => // Task Status is processed separately, cannot be set to Issue due to JIRA API design.
      case Description => issueInputBuilder.setDescription(value.asInstanceOf[String])
      case DueDate => if (value != null) {
        val dueDateTime = new DateTime(value)
        issueInputBuilder.setDueDate(dueDateTime)
      }
      case AssigneeLoginName => if (value != null) {
        issueInputBuilder.setAssigneeName(value.asInstanceOf[String])
      }
      case ReporterLoginName => if (value != null) {
        issueInputBuilder.setReporterName(value.asInstanceOf[String])
      }
      case com.taskadapter.model.Priority =>
        val priorityNumber = value.asInstanceOf[Integer]
        val jiraPriorityName = config.getPriorities.getPriorityByMSP(priorityNumber)
        if (!jiraPriorityName.isEmpty) {
          val priority = priorities(jiraPriorityName)
          if (priority != null) issueInputBuilder.setPriority(priority)
        }
      case EstimatedTime => if (value != null) {
        val estimatedHours = value.asInstanceOf[Float]
        val timeTracking = new TimeTracking(Math.round(estimatedHours * 60), null, null)
        issueInputBuilder.setFieldValue(IssueFieldId.TIMETRACKING_FIELD.id, timeTracking)
      }
      case _ =>
        val fieldSchema = customFieldResolver.getId(field.name)
        if (fieldSchema.isDefined) {
          val fullIdForSave = fieldSchema.get.fullIdForSave
          val valueWithProperJiraType = getConvertedValue(fieldSchema.get, value)
          issueInputBuilder.setFieldValue(fullIdForSave, valueWithProperJiraType)
        }
    }
  }

  /**
    * If the value is not of type String, this will throw exception. This is to fail fast rather than to attempt
    * to recover from incorrect types in the passed data.
    */
  def isNonEmptyString(value: Any) : Boolean = {
    value !=null && value.asInstanceOf[String].trim != ""
  }

  def getConvertedValue(fieldSchema: JiraFieldDefinition, value: Any) : Any = {
    fieldSchema.typeName match {
      case "array" if fieldSchema.itemsTypeIfArray.get == "string" => List(value).asJava
      case "array" if fieldSchema.itemsTypeIfArray.get == "option" => getComplexValueList(value)
      case "number" => ValueTypeResolver.getValueAsFloat(value)
      case _ => value
    }
  }

  def getComplexValueList(value: Any) : util.List[ComplexIssueInputFieldValue] = {
    // TODO this check does not quite work due to type erasure
    if (value.isInstanceOf[Seq[String]]) {
      val seq = value.asInstanceOf[Seq[String]]
      seq.map(ComplexIssueInputFieldValue.`with`("value", _)
      )
      .toList.asJava
    } else {
      throw new RuntimeException(s"unknown value type: $value")
    }
  }

  @throws[ConnectorException]
  override def convert(source: GTask): IssueWrapper = convertToJiraIssue(source)
}
