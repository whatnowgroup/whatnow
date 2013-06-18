package models

import play.api.libs.json._

case class Task(id: Long, eventName: String, longtitude: String, latitude: String)

object Task {
  
  def all(): List[Task] = populate

  def create(eventName: String, longtitude: String, latitude: String) {}

  def delete(id: Long) {}
  
  implicit val taskFormat = Json.format[Task]

  def populate(): List[Task] = {
    Task(1L, "Salsa - M&M", "151.209493", "-33.859228") :: 
    Task(2L, "Salsa @ Bar 100", "151.209388", "-33.858218") :: 
    Task(3L, "Salsa @ Ivy", "151.207264", "-33.86663") :: 
    Nil
  }

}
