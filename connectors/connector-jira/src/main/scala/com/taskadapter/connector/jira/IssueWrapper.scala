package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.domain.input.IssueInput

case class IssueWrapper(key: String, issueInput: IssueInput, status: String)
