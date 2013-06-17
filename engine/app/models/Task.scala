package models

import play.api.libs.json._

case class Task(event_name: String, longtitude: String, latitude: String)

object Task {
  
  def all(): List[Task] = populate

  def create(label: String) {}

  def delete(id: Long) {}
  
  implicit val taskFormat = Json.format[Task]

  def populate(): List[Task] = {
    Task("Salsa - M&M", "151.209493", "-33.859228") :: 
    Task("Salsa @ Bar 100", "151.209388", "-33.858218") :: 
    Task("Salsa @ Ivy", "151.207264", "-33.86663") :: 
    Nil
  }

}
