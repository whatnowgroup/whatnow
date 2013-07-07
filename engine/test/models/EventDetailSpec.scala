package test.models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models.Event
import models.EventDetail
import test.WhatNowTestSpec

class EventDetailSpec extends WhatNowTestSpec {
  "Event Detail" should {
    
    "retrieveDetails" in {
      running(fake) {
        
        val event = Event.getEvent(1L)
        val eventDetail = EventDetail.retriveDetails(event.id)
        
        eventDetail.eventId should equalTo(1L)
        
      }
    }
    
  }
}