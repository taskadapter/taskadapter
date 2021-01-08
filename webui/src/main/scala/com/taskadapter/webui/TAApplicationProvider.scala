package com.taskadapter.webui

import java.io.File

import com.taskadapter.webui.config.ApplicationSettings
import com.taskadapter.webui.service.{EditorManager, Preservices}
import org.slf4j.LoggerFactory
//import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker

/**
  * Provider of the Task Adapter application.
  */
object TAApplicationProvider {
  def withGoogleAnalytics: TAApplicationProvider = {
    new TAApplicationProvider(true)
  }

  def skipGoogleAnalytics: TAApplicationProvider = {
    new TAApplicationProvider(false)
  }
}

// TODO 14 not called anymore

/**
  * Creates a new task application provider. Initializes all core services
  * (like credentials manager, etc...).
  */
class TAApplicationProvider(reportGoogleAnalytics: Boolean) {
  val GOOGLE_ANALYTICS_ID = "UA-3768502-12"
  val log = LoggerFactory.getLogger(classOf[TAApplicationProvider])

  // Application config root folder.
  val rootFolder: File = ApplicationSettings.getDefaultRootFolder()

  /**
    * Global services.
    */
  private val services = new Preservices(rootFolder, EditorManager.fromResource("editors.txt"))

  log.info("Started TaskAdapter " + services.currentTaskAdapterVersion)

  if (services.licenseManager.isSomeValidLicenseInstalled) {
    val license = services.licenseManager.getLicense
    log.info("License info: valid until " + license.getExpiresOn + ". Registered to " + license.getEmail)
  } else {
    log.info("License NOT installed or is NOT valid. Trial mode.")
  }

  /**
    * Vaadin calls this when app is loaded in browser
    */
  def createInstance(/*event: UICreateEvent*/)  = {
    val tracker = if (reportGoogleAnalytics) {
//      val gaTracker = new GoogleAnalyticsTracker(GOOGLE_ANALYTICS_ID, "none")
//      gaTracker.extend(ui)
//      new GATrackerImpl(gaTracker)
      null
    } else {
      log.info("Skipping Google Analytics: started in dev mode")
      new NoOpGATracker()
    }
//    SessionController.initSession(new WebUserSession(ui), tracker)
    val action = if (services.licenseManager.isSomeValidLicenseInstalled) {
      "web_app_opened_licensed"
    } else {
      "web_app_opened_trial"
    }
    tracker.trackEvent(WebAppCategory, action, services.currentTaskAdapterVersion)
  }
}
