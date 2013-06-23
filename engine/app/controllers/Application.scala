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

  def newEvent = Action { implicit request =>
    request.body.asJson.map { json =>
      Event.create(
          (json \ "eventName").as[String], 
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

}
