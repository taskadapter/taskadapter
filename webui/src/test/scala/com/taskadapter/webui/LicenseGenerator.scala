package com.taskadapter.webui

import com.taskadapter.license.License

object LicenseGenerator {
  def someLicense() : License = {
    new License()
  }
}
