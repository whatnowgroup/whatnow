package controllers

import play.api._
import play.api.libs.json._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import models._

object Application extends Controller {

  def index = Action {
    Redirect(routes.Application.events)
  }

  def events = Action {
    Ok(Json.toJson(Event.all().map {
      t => Json.toJson(t)
    })).as("application/json")
  }
  
  def eventsAround(userId: String, longitudeLeft: String, longitudeRight: String, latitudeLeft: String, latitudeRight: String) = Action {
    Ok(Json.toJson(Event.eventsAround(userId, longitudeLeft, longitudeRight, latitudeLeft, latitudeRight).map {
      t => Json.toJson(t)
    })).as("application/json")
  }

  def newEvent = Action { implicit request =>
    request.body.asJson.map { json =>
      Event.create(
        (json \ "name").as[String],
        (json \ "address").as[String],
        (json \ "latitude").as[String],
        (json \ "longitude").as[String])
      Ok("done")
    }.getOrElse {
      Logger.debug(request.body.toString())
      BadRequest(request.body.toString());
    }
  }

  def deleteEvent(id : Long) = Action {
    Event.delete(id)
    Ok("deleted")
  }

  def locations(friendsList : String) = Action {
    val events = Event.all()
    if (!friendsList.isEmpty()) {
      val friendsAttend = Event.list((friendsList.split(",")).toList)
      println(friendsAttend)
    }
    Ok(Json.parse("[" +
      "{\"id\" : 1, \"name\" : \"Salsa\", \"address\" : \"M&M\", \"longitude\" : 151.209493, \"latitude\" : -33.859228, \"attending\" : 1}," +
      "{\"id\" : 2, \"name\" : \"Salsa\", \"address\" : \"Bar 100\", \"longitude\" : 151.209388, \"latitude\" : -33.858218}, " +
      "{\"id\" : 3, \"name\" : \"Salsa\", \"address\" : \"Ivy\", \"longitude\" : 151.207264, \"latitude\" : -33.86663}]"))
  }

  def attending(eventId : Int, userId : String) = Action {
    Attendee.attending(userId, eventId);
    Ok(Json.parse("{\"result\" : \"ok\"}"));
  }

  def unattending(eventId : Int, userId : String) = Action {
    Attendee.unattending(userId, eventId);
    Ok(Json.parse("{\"result\" : \"ok\"}"));
  }

}
