package com.taskadapter.connector.redmine

import java.util.Collections

import com.taskadapter.model.{EstimatedTime, GTask, SpentTime}
import com.taskadapter.redmineapi.bean.{Issue, ProjectFactory}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class GTaskToRedmineTest extends FunSpec with Matchers {

  private val project = ProjectFactory.create()

  it("sets TimeSpent") {
    val task = new GTask().setValue(SpentTime, 2.5f)
    convert(task).getSpentHours shouldBe 2.5f
  }

  it("sets EstimatedTime") {
    val task = new GTask().setValue(EstimatedTime, 2.5f)
    convert(task).getEstimatedHours shouldBe 2.5f
  }

  private def convert(gTask: GTask): Issue = getConverter().convert(gTask)

  private def getConverter(): GTaskToRedmine = {
    val config = new RedmineConfig()
    new GTaskToRedmine(config,
      null, project, new RedmineUserCache(Seq()), Collections.emptyList(),
      Collections.emptyList(),
      Collections.emptyList(), Collections.emptyList())
  }

}