package test.models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models.Event
import test.WhatNowTestSpec

class EventSpec extends WhatNowTestSpec {
  
  "Event" should {

    "all" in {
      running(fake) {
        val events = Event.all()
        events must not have size(0)
      }
    }
    
    "getEvent" in {
      running(fake) {
        val event = Event.getEvent(1L)
        event.id must equalTo(1L)
        event.name must equalTo("Salsa")
        event.address must equalTo("M&M")
        event.latitude must equalTo(-33.859228)
        event.longitude must equalTo(151.209493)
      }
    }

  }
}