package com.taskadapter.connector.jira

import java.util

import com.atlassian.jira.rest.client.api.domain.{BasicComponent, Issue, IssueType, Priority, Version}
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser
import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.google.gson.reflect.TypeToken
import com.taskadapter.connector.jira.mock.{BasicComponentMock, IssueTypeMock, PriorityMock, VersionMock}
import com.taskadapter.connector.testlib.TestDataLoader
import org.codehaus.jettison.json.JSONObject
import scala.collection.JavaConverters._

object MockData {
  def loadIssue(fileName: String): Issue = {
    val issueParser = new IssueJsonParser
    val fileContents = Resources.toString(Resources.getResource(fileName), Charsets.UTF_8)
    val obj = new JSONObject(fileContents)
    issueParser.parse(obj)
  }

  def loadPriorities: Iterable[Priority] = TestDataLoader.load("priorities_6.4.11.json",
    new TypeToken[util.ArrayList[PriorityMock]]() {}.getType).asInstanceOf[java.lang.Iterable[Priority]]
    .asScala

  def loadIssueTypes: Iterable[IssueType] = TestDataLoader.load("issuetypes_jira5.0.6.json",
    new TypeToken[util.ArrayList[IssueTypeMock]]() {}.getType).asInstanceOf[java.lang.Iterable[IssueType]]
    .asScala

  def loadVersions: Iterable[Version] = {
    /* I deleted "release date" attribute from the versions file to fix
            * "Unable to invoke no-args constructor for class org.joda.time.Chronology" problem.
            * See the original file "versions_jira5.0.6.json"
            */ TestDataLoader.load("versions_without_release_date_jira5.0.6.json",
      new TypeToken[util.ArrayList[VersionMock]]() {}.getType).asInstanceOf[java.lang.Iterable[Version]]
      .asScala
  }

  def loadComponents: Iterable[BasicComponent] = TestDataLoader.load("components_jira5.0.6.json",
    new TypeToken[util.ArrayList[BasicComponentMock]]() {}.getType).asInstanceOf[java.lang.Iterable[BasicComponent]]
    .asScala
}
