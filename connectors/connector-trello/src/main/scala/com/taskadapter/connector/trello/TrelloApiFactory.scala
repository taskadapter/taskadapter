package com.taskadapter.connector.trello

import com.julienvey.trello.Trello
import com.julienvey.trello.impl.TrelloImpl
import com.julienvey.trello.impl.http.ApacheHttpClient

object TrelloApiFactory {
  def createApi(appKey: String, token: String): Trello = {
    val a = new ApacheHttpClient
    new TrelloImpl(appKey, token, a)
  }
}
