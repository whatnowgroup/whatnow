package models

object UrlKind extends Enumeration {
  type UrlKind = Value
  val RELATIVE, ABSOLUTE, BOTH = Value
}