package models

import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.Logger

case class Event(id : Long, eventName : String, address : String, longtitude : String, latitude : String)

object Event {

  def all() : List[Event] = populate

  def create(eventName : String, address : String, longitude : String, latitude : String): Unit = {
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
          'longitude -> longitude
          ).executeUpdate()    
    }
  }

  def delete(id : Long) {}

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
