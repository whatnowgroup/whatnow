package models

import java.util.Date
import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.Logger
import models._

case class EventDetail(eventId: Long, 
					   eventName: String,
					   eventAddress: String,
					   eventDetailsShort: String,
					   eventDetailsFull: String,
					   eventRating: Double,
					   mainImage: String,
					   imageList: List[String],
					   friendsAttending: List[String],
					   totalAttending: Int,
					   attending: Boolean,
					   eventDate: Long,
					   eventRecurring: EventRecurring)
					   
object EventDetail {
  
  def retriveDetails(eventId: Long): EventDetail = {
    val event = Event.getEvent(eventId)
    val imageList = EventImage.getEventImages(eventId)
    val attendees = Attendee.eventAttendees(eventId)
    val friendsAttending = Attendee.friendsAttending(eventId, Nil)
    val eventRecurring = EventRecurring.getEventRecurring(eventId)
    
    val mainImageUrl = imageList match {
      case List() => "";
      case x :: xs => x.imageUrl;
    }
    
    EventDetail(
        event.id, event.name, event.address, 
        event.shortDescription, event.description, event.rating,
        mainImageUrl, imageList.map(x => x.imageUrl), friendsAttending, 
        attendees.size,
        event.attending, eventRecurring.recurringStartDate, eventRecurring
        )
    
  }
  
  implicit val detailFormat = Json.format[EventDetail]
  
}