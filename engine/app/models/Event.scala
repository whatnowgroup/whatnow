package models

import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class Event(id: Long,
                 name: String,
                 address: String,
                 latitude: Double,
                 longitude: Double,
                 attending: Boolean = false,
                 description: String = "",
                 shortDescription: String = "",
                 rating: Double = 0D,
                 friends: List[String] = Nil)

object Event {

  val EVENTS_TABLE_NAME = "events"

  val ID = "event_id"
  val NAME = "event_name"
  val ADDRESS = "event_address"
  val LATITUDE = "event_latitude"
  val LONGITUDE = "event_longitude"
  val DESC = "event_description"
  val SHORT_DESC = "event_short_description"
  val RATING = "event_rating"

  val FRIENDS = "event_friends"

  def all(): List[Event] = populate()

  def create(eventName: String, address: String, longitude: String, latitude: String): Unit = {
    DB.withConnection { implicit connection =>
      SQL(
        "insert into " + EVENTS_TABLE_NAME +
          "(" + NAME + ", " + ADDRESS + ", " + LATITUDE + ", " + LONGITUDE + ")" +
          " values " +
          "({name}, {address}, {latitude}, {longitude})")
        .on(
          'name -> eventName,
          'address -> address,
          'latitude -> latitude,
          'longitude -> longitude).executeUpdate()
    }
  }

  def eventsAround(userId: String, longitudeLeft: String, longitudeRight: String, latitudeLeft: String, latitudeRight: String): List[Event] = {
    DB.withConnection { implicit connection =>
      SQL("select * from " + EVENTS_TABLE_NAME + " e " +
        "left join " + Attendee.TABLE_NAME + " a on e." + ID + "=a." + Attendee.EVENT_ID + " " +
        "where a." + Attendee.USER_ID + "={userId}" +
        "and e." + LONGITUDE + " between {longitudeLeft} and {longitudeRight}" +
        "and e." + LATITUDE + " between {latitudeLeft} and {latitudeRight};")
        .on('userId -> userId,
          'longitudeLeft -> longitudeLeft,
          'longitudeRight -> longitudeRight,
          'latitudeLeft -> latitudeLeft,
          'latitudeRight -> latitudeRight)
        .as(Event.deserialise *)
    }
  }

  def delete(id: Long) {}

  lazy val withAttendees = Event.deserialise ~ (Attendee.deserialise ?) map {
    case events ~ attendees => (events, attendees)
  }

  def genInQuery(column: String, join: String, friendsList: List[String]): String =
    friendsList match {
      case Nil     => ""
      case x :: xs => join + " " + column + " = '" + x + "'" + genInQuery(column, join, xs)
    }

  def list(friendsList: List[String]): List[(Event, Option[Attendee])] = {
    val query =
      "select * from " + EVENTS_TABLE_NAME + " e, " + Attendee.TABLE_NAME + " a where e." + ID + " = a." + Attendee.EVENT_ID + " and (1 = 0 " +
        genInQuery("a." + Attendee.USER_ID, "or", friendsList) + ")"
    println(query)

    DB.withConnection { implicit connection =>
      SQL(query).as(Event.withAttendees *)
    }
  }

  def getMe(friends: List[String],
            longitudeLeft: Double, longitudeRight: Double,
            latitudeLeft: Double, latitudeRight: Double): Map[Event, List[String]] = {
    val query =
      "select * from " + EVENTS_TABLE_NAME + " e, " +
        Attendee.TABLE_NAME +
        " a where e." + ID + " = a." + Attendee.EVENT_ID +
        " and e." + LONGITUDE + ">=" + longitudeLeft + " and e." + LONGITUDE + "<=" + longitudeRight +
        " and e." + LATITUDE + ">=" + latitudeLeft + " and e." + LATITUDE + "<=" + latitudeRight +
        " and (1 = 0 " + genInQuery("a." + Attendee.USER_ID, " or ", friends) + ")"

    DB.withConnection { implicit connection =>
      SQL(query).as(Event.withAttendees *)
    } groupBy(x => x._1) mapValues (v => v.map(_._2.get.userId))

  }

  implicit val eventFormat = Json.format[Event]

  val deserialise = {
    get[Long](ID) ~
      get[String](NAME) ~
      get[String](ADDRESS) ~
      get[Double](LATITUDE) ~
      get[Double](LONGITUDE) map {
        case id ~ name ~ address ~ latitude ~ longitude =>
          Event(id, name, address, latitude, longitude)
      }
  }

  val deserialiseWithFriends = {
    get[Long](ID) ~
      get[String](NAME) ~
      get[String](ADDRESS) ~
      get[Double](LATITUDE) ~
      get[Double](LONGITUDE) ~
      get[String](DESC) ~
      get[String](SHORT_DESC) ~
      get[Double](RATING) ~
      get[String](FRIENDS)  map {
      case id ~
        name ~ address ~
        latitude ~ longitude ~
        description ~ shortDescription ~ rating ~
        friends =>
      Event(id, name, address, latitude, longitude, true, description, shortDescription, rating, friends.split(",").toList)
      }
  }

  val detailDeserialise = {
    get[Long](ID) ~
      get[String](NAME) ~
      get[String](ADDRESS) ~
      get[Double](LATITUDE) ~
      get[Double](LONGITUDE) ~
      get[String](DESC) ~
      get[String](SHORT_DESC) ~
      get[Double](RATING) map {
        case id ~
          name ~ address ~
          latitude ~ longitude ~
          description ~ shortDescription ~ rating =>
          Event(id, name, address, latitude, longitude, false, description, shortDescription, rating)
      }
  }

  def getEvent(eventId: Long): Event = {
    DB.withConnection { implicit connection =>
      SQL("select * from " + EVENTS_TABLE_NAME + " where " + ID + " = {id};").on('id -> eventId).as(Event.deserialise.single)
    }
  }

  def populate(): List[Event] = {
    DB.withConnection { implicit connection =>
      SQL("select * from " + EVENTS_TABLE_NAME).as(Event.deserialise *)
    }
  }
}
