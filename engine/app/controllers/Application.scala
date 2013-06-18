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

  def newTask = TODO

  def deleteTask(id: Long) = TODO
  
}
