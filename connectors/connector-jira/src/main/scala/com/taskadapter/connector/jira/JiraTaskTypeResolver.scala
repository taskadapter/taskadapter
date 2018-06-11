package com.taskadapter.connector.jira

object JiraTaskTypeResolver {

  def resolveIssueTypeNameForCreate(wrapper: IssueWrapper, defaultTaskTypeName: String, defaultIssueTypeForSubtasks: String): String = {
    if (wrapper.taskType.isDefined && !wrapper.taskType.get.isEmpty) {
      wrapper.taskType.get
    } else {
      if (wrapper.issueInput.getField("parent") == null) defaultTaskTypeName
      else defaultIssueTypeForSubtasks
    }
  }
}
