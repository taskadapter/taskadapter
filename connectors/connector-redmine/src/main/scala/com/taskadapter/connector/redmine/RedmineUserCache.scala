package com.taskadapter.connector.redmine

import com.taskadapter.redmineapi.bean.User
import org.slf4j.LoggerFactory

/**
  * Resolves users by either login name or full name. limitation of Redmine REST API...
  */
class RedmineUserCache(users: Seq[User]) {
  val logger = LoggerFactory.getLogger(classOf[RedmineUserCache])

  def findRedmineUserInCache(id: Int): Option[User] = {
    users.find(_.getId == id)
  }

  def findRedmineUserByLogin(loginName: String): Option[User] = {
    users.find(_.getLogin == loginName)
  }

  def findRedmineUserByFullName(fullName: String): Option[User] = {
    users.find(_.getFullName == fullName)
  }
}
