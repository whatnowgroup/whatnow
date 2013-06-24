package models

import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.Logger
import models._

case class Event(id : Long, eventName : String, address : String, longitude : String, latitude : String, attending: Boolean = false)

object Event {

  def all() : List[Event] = populate

  def create(eventName : String, address : String, longitude : String, latitude : String) : Unit = {
    DB.withConnection { implicit connection =>
      SQL(
        """insert into events 
          (event_name, event_address, event_latitude, event_longitude)
          values
          ({name}, {address}, {latitude}, {longitude})
      """).on(
          'name -> eventName,
          'address -> address,
          'latitude -> latitude,
          'longitude -> longitude).executeUpdate()
    }
  }

  def eventsAround(userId : String, longitudeLeft : String, longitudeRight : String, latitudeLeft : String, latitudeRight : String) : List[Event] = {
    DB.withConnection { implicit connection =>
      SQL("""
          select * from events e 
          left join attendees a on e.id=a.event_id  
          where a.user_id={userId} 
          and e.event_longitude between {longitudeLeft} and {longitudeRight}  
          and e.event_latitude between {latitudeLeft} and {latitudeRight}; 
          """)
        .on('userId -> userId,
          'longitudeLeft -> longitudeLeft,
          'longitudeRight -> longitudeRight,
          'latitudeLeft -> latitudeLeft,
          'latitudeRight -> latitudeRight)
        .as(Event.deserialise *)
    }
  }

  def delete(id : Long) {}

  lazy val withAttendees = Event.deserialise ~ (Attendee.deserialise ?) map {
    case events ~ attendees => (events, attendees)
  }

  def genInQuery(column : String, join : String, friendsList : List[String]) : String =
    friendsList match {
      case Nil => ""
      case x :: xs => join + " " + column + " = '" + x + "'" + genInQuery(column, join, xs)
    }

  def list(friendsList : List[String]) : List[(Event, Option[Attendee])] = {
    val query =
      "select * from events e, attendees a where e.id = a.event_id and (1 = 0 " +
        genInQuery("a.user_id", "or", friendsList) + ")"
    println(query)

    DB.withConnection { implicit connection =>
      SQL(query).as(Event.withAttendees *)
    }
  }

  implicit val eventFormat = Json.format[Event]

  val deserialise = {
    get[Long]("id") ~
      get[String]("event_name") ~
      get[String]("event_address") ~
      get[String]("event_latitude") ~
      get[String]("event_longitude") map {
        case id ~ event_name ~ event_address ~ event_latitude ~ event_longitude =>
          Event(id, event_name, event_address, event_latitude, event_longitude)
      }
  }

  def populate() : List[Event] = {
    DB.withConnection { implicit connection =>
      SQL("select * from events;").as(Event.deserialise *)
    }
  }
}
