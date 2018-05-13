package com.taskadapter.model

import java.util

import com.taskadapter.connector.Field
import com.taskadapter.connector.definition.TaskId

/**
  * Generic "Task" object. Internal representation for all "tasks" loaded from various external systems.
  */
final class GTask() {
  // TODO REVIEW Why not EnumMap?
  final private val fields = new util.HashMap[String, AnyRef]

  setValue(GTaskDescriptor.FIELD.CHILDREN, new util.ArrayList[GTask])
  setValue(GTaskDescriptor.FIELD.RELATIONS, new util.ArrayList[GRelation])

  /**
    * Copy-constructor creating a shallow clone.
    */
  def this(taskToClone: GTask) {
    this()
    fields.putAll(taskToClone.fields)
  }

  def getValue(field: GTaskDescriptor.FIELD): Any = getValue(field.name)

  def getValue(field: Field): AnyRef = getValue(field.name)

  def getValue(field: String): AnyRef = fields.get(field)

  // TODO REVIEW This method could break getters. task.setValue(FIELD.ID, "'xj").
  //    Have you considered more type-safe field keys? Then this method would be
  //    public <T> void setValue(Field<T> field, T value)
  //    I definitely have shown you this technique (attributes in lpg).
  def setValue(field: GTaskDescriptor.FIELD, value: AnyRef): Unit = fields.put(field.name, value)

  def setValue(field: Field, value: AnyRef): Unit = fields.put(field.name, value)

  def setValue(field: String, value: AnyRef): Unit = fields.put(field, value)

  def getIdentity: TaskId = {
    var id = getId
    if (id == null) id = 0L
    new TaskId(id, getKey)
  }

  /**
    * Like database ID for Redmine and Jira and Unique ID (row number) for MSP.
    */
  def getId: java.lang.Long = getValue(GTaskDescriptor.FIELD.ID).asInstanceOf[java.lang.Long]

  /**
    * This is database ID for Redmine and Jira and Unique ID (row number) for MSP.
    */
  def setId(id: java.lang.Long): Unit = setValue(GTaskDescriptor.FIELD.ID, id)

  def getParentIdentity: TaskId = getValue(GTaskDescriptor.FIELD.PARENT_KEY).asInstanceOf[TaskId]

  def getSourceSystemId: TaskId = getValue(GTaskDescriptor.FIELD.SOURCE_SYSTEM_ID).asInstanceOf[TaskId]

  def setSourceSystemId(sourceSystemId: TaskId): Unit = setValue(GTaskDescriptor.FIELD.SOURCE_SYSTEM_ID, sourceSystemId)

  /**
    *
    * @return the list of children of an empty list when no children. never NULL.
    */
  def getChildren: util.List[GTask] = getValue(GTaskDescriptor.FIELD.CHILDREN).asInstanceOf[util.List[GTask]]

  def addChildTask(child: GTask): Unit = getChildren.add(child)

  def setChildren(children: util.List[GTask]): Unit = setValue(GTaskDescriptor.FIELD.CHILDREN, children)

  def hasChildren: Boolean = getChildren != null && !getChildren.isEmpty

  /**
    * Some systems like Jira can have string-based "key" like "TEST-1"
    * to identify issues. This is NOT a database identifier.
    */
  def getKey: String = getValue(GTaskDescriptor.FIELD.KEY).asInstanceOf[String]

  def setKey(key: String): Unit = setValue(GTaskDescriptor.FIELD.KEY, key)

  def getRelations: util.List[GRelation] = getValue(GTaskDescriptor.FIELD.RELATIONS).asInstanceOf[util.List[GRelation]]

  def setRelations(relations: util.List[GRelation]): Unit = setValue(GTaskDescriptor.FIELD.RELATIONS, relations)

  override def toString: String = fields.toString

  def setParentIdentity(parentIssueKey: TaskId): Unit = setValue(GTaskDescriptor.FIELD.PARENT_KEY, parentIssueKey)

  def getFields: util.Map[String, AnyRef] = fields

  override def equals(other: Any): Boolean = other match {
    case that: GTask =>
      fields == that.fields
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(fields)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}