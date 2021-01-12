package lunchList

import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

/** An `User` object represents a user character controlled by the real-life user of the program.
 *  An `User` can adjust the filter condition to see the preferred menu items.
 *  
 *  @param name the user name
 */
class User(val name: String) {
  var allergens = Array[String]()
  var predicates = Array[String]()
  var favouriteType = ArrayBuffer[String]()
  var favouriteCourse = FileProcessing.openFileFavourite(this)
}

