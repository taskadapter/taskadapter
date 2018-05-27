package com.taskadapter.connector.basecamp

import com.taskadapter.model.GUser

trait UserResolver {
  /**
    * Find user by login.
    *
    * @return resolved user or <code>null</code> if no user found. May receive null as input.
    */
    def findUserByLogin(loginName: String): GUser

    def findUserByDisplayName(displayName: String): GUser
}