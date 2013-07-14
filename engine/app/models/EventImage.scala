package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import scala.math.Ordering

case class EventImage(id: Long,
                      imageUrl: String,
                      urlKind: String,
                      eventId: Long,
                      isMainImage: Boolean = false)

object EventImage {

  val EVENT_IMAGES_TABLE_NAME = "event_images"

  val ID = "event_image_id"
  val IMAGE_URL = "image_url"
  val URL_KIND = "url_kind"
  val EVENT_ID = "event_id"
  val IS_MAIN_IMAGE = "is_main_image"

  def getEventImages(eventId: Long): List[EventImage] = {
    val images = DB.withConnection { implicit connection =>
      SQL("select * from " +
        EVENT_IMAGES_TABLE_NAME +
        " where " + EVENT_ID + " = {eventId} " +
        " limit 20;").on('eventId -> eventId).as(deserialise *)
    }
    images.sortBy(_.isMainImage)(Ordering[Boolean].reverse)
  }

  def deserialise = {
    get[Long](ID) ~
      get[String](IMAGE_URL) ~
      get[String](URL_KIND) ~
      get[Long](EVENT_ID) ~
      get[Int](IS_MAIN_IMAGE) map {
        case id ~ imageUrl ~ urlKind ~ eventId ~ isMainImageCode =>
          EventImage(id, imageUrl, urlKind, eventId, isMainImage(isMainImageCode))
      }
  }
  
  def isMainImage(code: Int): Boolean = {
    if (code == 0)
      false
    else
      true
  }

}