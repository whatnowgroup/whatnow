package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

// event id will be just long generated by postgres
// user id will be generated by PLATFORMs in the form of UUID
case class Attendee(eventId : Long, userId : String)

object Attendee {
  
  val TABLE_NAME = "attendees"
  
  val EVENT_ID = "event_id"
  val USER_ID = "user_id"
  
  def attending(userId : String, event : Long) = {
    DB.withConnection { implicit connection =>
      SQL(
        "insert into " + TABLE_NAME + " (" + EVENT_ID + ", " + USER_ID + ") values ({eventid}, {userid});")
        .on('eventid -> event, 'userid -> userId).executeUpdate
    }
  }

  def unattending(userId : String, event : Int) = {
    DB.withConnection { implicit connection =>
      SQL(
        "delete from " + TABLE_NAME + " where " + EVENT_ID + " ={eventid} and " + USER_ID + " = {userid});")
        .on('eventid -> event, 'userid -> userId).executeUpdate
    }
  }
  
  def eventAttendees(eventId: Long): List[String] = {
    DB.withConnection { implicit connection =>
      SQL("select " + USER_ID + " u from " + TABLE_NAME + " where " + EVENT_ID + " = {eventId}")
      .on('eventId -> eventId)
      .as(get[String]("u") *)
    }
  }
  
  def friendsAttending(eventId: Long, friends: List[String]): List[String] = {
    eventAttendees(eventId).filter(a => friends.contains(a))
  }
  
  val deserialise = {
    get[Long](EVENT_ID) ~
      get[String](USER_ID) map {
        case eventId ~ userId =>
          Attendee(eventId, userId)
      }
  }
  
}
