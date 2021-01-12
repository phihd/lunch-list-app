package lunchList

import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer

/** A `Restaurant` trait represents a pre-defined restaurant, which supplies menu items based on certain options.*/
trait Restaurant {
  
  def name: String
  
  def URL: String
  
  /**Return menu items in String based on certain options.
   * Return necessary notification if no items exist.*/ 
  def textMenu(day: String, favouriteType : ArrayBuffer[String], allergens: Array[String], predicates: Array[String]): String
  
  /**Shows menus by printing them on the console, which mostly used for text-based UI*/
  def displayMenu(day: String, favouriteType : ArrayBuffer[String], allergens: Array[String], predicates: Array[String]) = {
    val ABlocLine = "              ======== ABloc ========"
    val dipoliLine = "              ======== Dipoli ========"
    val kvarkkiLine = "              ======== Kvarkki ========"
    val line = " -------------------------------------- "
    this match {
      case ABloc => println(ABlocLine)
      case Dipoli => println(dipoliLine)
      case Kvarkki => println(kvarkkiLine)
    }
    println(line)
    println(textMenu(day, favouriteType, allergens, predicates))
  }
  
  /**Return dishes that a user has added to favourites.
   * If there no items in the favourite list or sold in selected day, inform the user about that. 
   */
  def textMenuFilteredByFavouriteCourses(day: String, user: User): String = {
    val menu = textMenu(day, user.favouriteType, user.allergens, user.predicates).split('\n').filter(_.nonEmpty)
    if (!user.favouriteCourse.contains(this) || menu.isEmpty)
      return "Not found any courses."
    val filteredMenu = menu.intersect(user.favouriteCourse(this)).filter(_.nonEmpty)
    if (filteredMenu.isEmpty)
      return "Not found any courses."
    var text = ""
    filteredMenu.foreach(text += _ + '\n')
    return text.dropRight(1)
  }
  
}

/**Companion object `Restaurant` helps store information of all restaurants*/
object Restaurant {
  val notiBuffer = Set[String](
      "No menu available.",
      "Not found any courses.",
      "No match course found.\nYou can see more by adjusting favourite type and allergens.\n",
      "No internet connection.")
      
  val restaurantList = Array(ABloc, Dipoli, Kvarkki, Maukas, Taffa)
  
  val stringToRestaurant = {
    restaurantList.map(res => (res.name, res)).toMap
  }
  
  
  
  def notification: Set[String] = {
    return notiBuffer
  }
  
  def addNotification(noti: String) = {
    notiBuffer += noti
  }
  
}