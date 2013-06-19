package controllers

import play.api._
import play.api.libs.json._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import models._

object Application extends Controller {

  val taskForm = Form("label" -> nonEmptyText)

  def index = Action {
    Redirect(routes.Application.tasks)
  }

  def tasks = Action {
    Ok(Json.toJson(Task.all().map {
      t => Json.toJson(t)
    })).as("application/json")
  }

  def newTask = Action { 
    implicit request =>
    Logger.debug(request.body.toString())
    request.body.asJson.map { json =>
      Task.create(
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

  def deleteTask(id : Long) = TODO

}
