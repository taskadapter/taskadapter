package com.taskadapter.web.uiapi

import scala.beans.BeanProperty

case class Schedule(id: String,
                    configId: ConfigId,
                    @BeanProperty var intervalInMinutes: Int,
                    @BeanProperty var directionLeft: Boolean,
                    @BeanProperty var directionRight: Boolean)
