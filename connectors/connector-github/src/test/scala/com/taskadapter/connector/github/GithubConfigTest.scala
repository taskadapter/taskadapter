package com.taskadapter.connector.github

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class GithubConfigTest extends FunSpec with Matchers {

  it("defaultLabelIsSet") {
    new GithubConfig().getLabel shouldBe GithubConfig.DEFAULT_LABEL
  }
}