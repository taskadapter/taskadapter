package com.taskadapter.connector.redmine

import com.taskadapter.model.GUser
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

  /**
    * @return None if the user is not found or if "users" weren't previously set via setUsers() method
    */
  def findRedmineUserInCache(loginName: String, displayName: String): Option[User] = {
    val valueToSearchFor = if (loginName == null) {
      displayName
    } else {
      loginName
    }
    if (valueToSearchFor == null) {
      logger.warn(s"Cannot resolve ($loginName $displayName) in cache - neither login name non display name are present")
      return None
    }
    users
      .find(u => valueToSearchFor == u.getLogin || valueToSearchFor == u.getFullName)
  }

  def findGUserInCache(loginName: String, displayName: String): Option[GUser] = {
    findRedmineUserInCache(loginName, displayName).map(RedmineToGUser.convertToGUser)
  }
}
