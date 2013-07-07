package models

import play.api.libs.json._

object RecurringUnit extends Enumeration {
  
  type RecurringUnit = Value
  
  val DAILY, WEEKLY, FORTNIGHTLY, MONTHLY, YEARLY = Value
  
  def recurringUnits: List[RecurringUnit] = {
    DAILY :: WEEKLY :: FORTNIGHTLY :: MONTHLY :: YEARLY :: Nil
  }
  
}