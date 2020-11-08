package com.taskadapter.web.event

import rx.lang.scala.Observable

trait EventBus {
  def post(event: Event): Unit

  def observable[T](eventClass: Class[T]): Observable[T]
}
