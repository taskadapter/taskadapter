package com.taskadapter.model

import java.util

import com.taskadapter.connector.definition.TaskId

/**
  * Generic "Task" object. Internal representation for all "tasks" loaded from various external systems.
  */
final class GTask() {
  final private val fields = new util.HashMap[Field[_], Any]

  setChildren(new util.ArrayList[GTask]())
  setRelations(new util.ArrayList[GRelation]())

  /**
    * Copy-constructor creating a shallow clone.
    */
  def this(taskToClone: GTask) {
    this()
    fields.putAll(taskToClone.fields)
  }

  def getValue[T](field: Field[T]): T = fields.get(field).asInstanceOf[T]

  def setValue[T](field: Field[T], value: T): GTask = {
    fields.put(field, value)
    this
  }

  def getIdentity: TaskId = {
    var id = getId
    if (id == null) id = 0L
    TaskId(id, getKey)
  }

  /**
    * Like database ID for Redmine and Jira and Unique ID (row number) for MSP.
    */
  def getId: java.lang.Long = getValue(Id)

  /**
    * This is database ID for Redmine and Jira and Unique ID (row number) for MSP.
    */
  def setId(id: java.lang.Long): GTask = setValue(Id, id)

  def getParentIdentity: TaskId = getValue(ParentKey)

  def getSourceSystemId: TaskId = getValue(SourceSystemId)

  def setSourceSystemId(sourceSystemId: TaskId): Unit = setValue(SourceSystemId, sourceSystemId)

  /**
    *
    * @return the list of children of an empty list when no children. never NULL.
    */
  def getChildren: util.List[GTask] = getValue(Children)

  def addChildTask(child: GTask): Unit = getChildren.add(child)

  def setChildren(children: util.List[GTask]): Unit = setValue(Children, children)

  def hasChildren: Boolean = getChildren != null && !getChildren.isEmpty

  /**
    * Some systems like Jira can have string-based "key" like "TEST-1"
    * to identify issues. This is NOT a database identifier.
    */
  def getKey: String = getValue(Key)

  def setKey(key: String): GTask = setValue(Key, key)

  def getRelations: util.List[GRelation] = getValue(Relations)

  def setRelations(relations: util.List[GRelation]): Unit = setValue(Relations, relations)

  override def toString: String = fields.toString

  def setParentIdentity(parentIssueKey: TaskId): GTask = setValue(ParentKey, parentIssueKey)

  def getFields: util.Map[Field[_], Any] = fields

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