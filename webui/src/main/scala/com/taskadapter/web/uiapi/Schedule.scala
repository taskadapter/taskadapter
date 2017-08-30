package com.taskadapter.web.uiapi

import scala.beans.BeanProperty

case class Schedule(configId: ConfigId,
                    @BeanProperty var configLabel: String,
                    @BeanProperty var intervalInMinutes: Int,
                    @BeanProperty var directionLeft: Boolean,
                    @BeanProperty var directionRight: Boolean)
