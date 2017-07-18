package com.taskadapter.config

case class ConnectorSetup(label: String,
                          host: String,
                          userName: String,
                          password: String,
                          useApiKey: Boolean,
                          apiKey: String)
