package com.taskadapter.web.event

import rx.lang.scala.subjects.{PublishSubject, SerializedSubject}
import rx.lang.scala.{Observable, Subscription}

import scala.reflect.ClassTag

object EventBusImpl extends EventBus {
  private val subject = PublishSubject[Event]()
  private val bus = SerializedSubject[Event](subject)

  def post(event: Event): Unit = {
    if (bus.hasObservers) {
      bus.onNext(event)
    }
  }

  def observable[T](eventClass: Class[T]): Observable[T] = {
    bus.filter(o => o != null) // Filter out null objects, better safe than sorry
      .filter(o => eventClass.isInstance(o)) // We're only interested in a specific event class
      .asInstanceOf[Observable[T]] // Cast it for easier usage
  }

  /**
    * Subscribe to the event, run the provided action when the event is received.
    *
    * @param f          the function to apply when an event of [T] class arrived
    * @tparam T type of the event to listen for
    * @return the subscription
    */
  def subscribe[T: ClassTag](f: T => Unit): Subscription = {
    val myClass = myClassOf[T]
    val subscription = observable(myClass).subscribe(e => f.apply(e))
    subscription
  }

  def myClassOf[T: ClassTag]: Class[T] = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
}
