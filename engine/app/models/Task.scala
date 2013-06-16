package models

import play.api.libs.json._

case class Task(id: Long, label: String)

object Task {
  
  def all(): List[Task] = populate

  def create(label: String) {}

  def delete(id: Long) {}
  
  implicit val taskFormat = Json.format[Task]

  def populate(): List[Task] = {
    Task(1L, "Test") :: 
    Task(2L, "Hello, World") :: 
    Task(3L, "Hell yeah") :: 
    Nil
  }

}
