package models

import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class EventRecurring(id: Long,
                          recurringUnit: String,
                          recurringInterval: Int,
                          recurringStartDate: Long,
                          recurringEndDate: Long,
                          recurringEventId: Long)

object EventRecurring {

  val EVENT_RECURRINGS_TABLE_NAME = "event_recurrings"

  val ID = "event_recurring_id"
  val UNIT = "recurring_unit"
  val INTERVAL = "recurring_interval"
  val START_DATE = "recurring_start_date"
  val END_DATE = "recurring_end_date"
  val EVENT_ID = "recurring_event_id"

  def getEventRecurring(eventId: Long): EventRecurring = {
    DB.withConnection { implicit connection =>
      SQL("select * from " + EVENT_RECURRINGS_TABLE_NAME + " where " + EVENT_ID + " = {eventId};")
      .on('eventId -> eventId)
      .as(deserialise single)
    }
  }
  
  implicit val recurringFormat = Json.format[EventRecurring]

  def deserialise = {
    get[Long](ID) ~
      get[String](UNIT) ~
      get[Int](INTERVAL) ~
      get[Long](START_DATE) ~
      get[Option[Long]](END_DATE) ~
      get[Long](EVENT_ID) map {
        case id ~ unit ~ interval ~ startDate ~ endDate ~ eventId =>
          EventRecurring(id, unit, interval, startDate, endDate.getOrElse(-1L), eventId)
      }
  }

}