package controllers

import play.api._
import play.api.libs.json._
import play.api.mvc._


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
        (json \ Constants.NAME).as[String],
        (json \ Constants.ADDRESS).as[String],
        (json \ Constants.LATITUDE).as[String],
        (json \ Constants.LONGITUDE).as[String])
      Ok("done")
    }.getOrElse {
      Logger.debug(request.body.toString())
      BadRequest(request.body.toString())
    }
  }

  def deleteEvent(id : Long) = Action {
    Event.delete(id)
    Ok("deleted")
  }

  def locations(friendsList : String) = Action {
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

  def friendsAttending = Action { implicit request =>
    request.body.asJson.map { json =>
      val friends = (json \ Constants.FRIENDS).as[List[String]]
      val longitudeLeft = (json \ Constants.LONGITUDE_LEFT).as[Double]
      val longitudeRight = (json \ Constants.LONGITUDE_RIGHT).as[Double]
      val latitudeLeft = (json \ Constants.LATITUDE_LEFT).as[Double]
      val latitudeRight = (json \ Constants.LATITUDE_RIGHT).as[Double]
      val result = Event.getMe(friends, longitudeLeft, longitudeRight, latitudeLeft, latitudeRight)
      val r = result.map (x => {
        val e = x._1
        Event(e.id, e.name, e.address, e.latitude, e.longitude, e.attending, e.description, e.shortDescription, e.rating, x._2)
      }).toList
      Ok(Json.toJson(r))
    }.getOrElse {
      Logger.debug(request.body.toString)
      BadRequest(request.body.toString())
    }
  }

  def eventsWithAttendingStatus(userId: String) = Action { implicit request =>

    Ok(Json.toJson(Event.populateWithAttendingStatus(userId).map {
      t => Json.toJson(t)
    })).as("application/json")

  }

}
