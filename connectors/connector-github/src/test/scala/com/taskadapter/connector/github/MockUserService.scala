package com.taskadapter.connector.github

import org.eclipse.egit.github.core.User
import org.eclipse.egit.github.core.service.UserService

class MockUserService extends UserService {
  override def getUser(login: String): User = {
    new User()
      .setLogin(login)
      .setName("name")
  }
}
