package lunchList.ui

import lunchList._

import scala.io.StdIn.{readLine}
import scala.util.parsing.json._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ArrayBuffer

/** The object `UI` is an text-based UI display menu by printing items on the console.
 *  Mostly used to debug.
 */
object UI extends App {
    println("Lunch List Application")
    val name = readLine("What is your name?\n")
    val user = new User(name)
    println("Nice to meet you, " + user.name + "!")
    println()
    
   
    println("Select food types you want to see from the list, seperated by commas.")
    println("G - Gluten free, L - Lactose free, VL - Low lactose, M - Milk-free, * - Wellbeing, VS - Contains fresh garlic, A - Contains user.allergens")
    var reader = readLine("If you want to see everything, just click enter:\n")
    if (reader  == "") {                  
      println("You will see everything. Nice!")
    } else {
      user.favouriteType = ArrayBuffer(reader.split(","): _*)
      var printer = ""
      user.favouriteType.foreach(printer += _ + ",")
      println("Alright, you will see food of " + printer.dropRight(1)  + " type!")
    }
    
    
    reader = readLine("Select ingredients you prefer, seperated by commas. \nIf you want to see everything, just click enter:\n")
    if (reader == "") {
      println("Nothing. Nice!")
    } else {
      user.predicates = reader.split(",").toArray
      var printer = ""
      user.predicates.foreach(printer += _ + ",")
      println("You prefer " + printer.dropRight(1) + ". Application will take it into account")
    }
    
    reader = readLine("Select ingredients you are allergic to, seperated by commas. \nIf you want to see everything, just click enter:\n")
    if (reader == "") {
      println("You are allergic to nothing. Nice!")
    } else {
      user.allergens = reader.split(",").toArray
      var printer = ""
      user.allergens.foreach(printer += _ + ",")
      println("You are allergic to " + printer.dropRight(1) + ". Application will take it into account")
    }
    
    var day = ""
    private var isOkayDay = false
    while (!isOkayDay) {
      println("What day is today?\n")
      day = readLine()     
      if (Tools.dayToNum(day) != -1)
        isOkayDay = true
    }
   

    val menuLine = ("     ============ Menu On "+ Tools.stdForm(day) +" ============")     // lines to make menu look nicer    
    println(menuLine)   
    
    ABloc.displayMenu(day, user.favouriteType , user.allergens, user.predicates)
    println()
    println()
    
    Kvarkki.displayMenu(day, user.favouriteType , user.allergens, user.predicates)
    println()
    println()
    
    Dipoli.displayMenu(day, user.favouriteType , user.allergens, user.predicates)
  
}