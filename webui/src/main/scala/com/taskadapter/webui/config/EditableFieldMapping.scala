package com.taskadapter.webui.config

import scala.beans.BeanProperty

/**
  * Wrapper for Vaadin which wants getters and setters on model classes.
  */
class EditableFieldMapping(@BeanProperty var fieldInConnector1: String,
                           val fieldTypeInConnector1: String,
                           @BeanProperty var fieldInConnector2: String,
                           val fieldTypeInConnector2: String,
                           @BeanProperty var selected: Boolean,
                           @BeanProperty var defaultValue: String)

