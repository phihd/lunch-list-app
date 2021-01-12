package lunchList

import scala.io.StdIn.{readLine,readInt,readBoolean}
import scala.swing._
import scala.util.parsing.json._
import scala.io.Source  
import java.io.FileInputStream
import java.util.{Calendar, Date}
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.sql.Timestamp
import scala.collection.mutable.ArrayBuffer

/**A `Fazer` object represents a restaurant owned by Fazer company*/
abstract class Fazer extends Restaurant {
  val ABloc_url = "https://www.fazerfoodco.fi/modules/json/json/Index?costNumber=3087&language=en"
  val Dipoli_url = "https://www.fazerfoodco.fi/modules/json/json/Index?costNumber=3101&language=en"
  
  
  
  def name: String
  
  def URL: String
  
  def getMenu(day: String): Map[String, List[Map[String, List[String]]]] = {
    val JSONdata = scala.io.Source.fromURL(URL).mkString
    val data: Map[String, List[Map[String, List[Map[String, List[String]]]]]] = JSON.parseFull(JSONdata) match {      //PARSING DATA
      case Some(d : Map[String, List[Map[String, List[Map[String, List[String]]]]]])=> d
      case _ => Map[String, List[Map[String, List[Map[String, List[String]]]]]]()
    }
    val now = LocalDate.now
    val today = now.getDayOfWeek.toString()
    var dayDist = Tools.dayToNum(day) - Tools.dayToNum(today)  
    if (dayDist < 0)
      dayDist += 7
    val processingDate = Tools.addDays(dayDist)
    var processingDateString = Tools.dateToString(processingDate, 1)
    //processingDateString = processingDateString.substring(0, 21) + '1' + processingDateString.substring(22)
    var processingMenu: Map[String, List[Map[String, List[String]]]]= Map()
    for(menu <- data("MenusForDays")) {
      if (menu("Date") == processingDateString) {
        processingMenu = menu 
      } 
    }
    return processingMenu
  }
  
  def textMenu(day: String, favouriteType: ArrayBuffer[String], allergens: Array[String], predicates: Array[String]): String = {
    var text = ""
    var hasFood = false
    val sep = Array('-', ' ', ',', '/', ':')
    try {
      val menu = ABloc.getMenu(day)  
      val setMenus: List[Map[String, List[String]]] = menu("SetMenus") 
      for(setMenu <- setMenus) {   
        val components: List[String] = setMenu("Components") 
        if (components.isEmpty)
          throw new NullPointerException  
        for(component <- components) {
          val properties = component.split(Array('(', ')')).drop(1).map(_.trim())
          if ((favouriteType.isEmpty || favouriteType.forall(properties.contains(_))) &&
              allergens.forall(al => !component.toLowerCase().split(sep).filter(_.nonEmpty).contains(al.toLowerCase())) &&
              (predicates.isEmpty || predicates.exists(al => component.toLowerCase().split(sep).filter(_.nonEmpty).contains(al.toLowerCase())))) {
            text += component + '\n'  
            hasFood = true
          }
        }
      }
      if (!hasFood)
        text += "No match course found.\nYou can see more by adjusting favourite type and allergens." + '\n'
      return text
    } catch {
      case _ @ (_: NullPointerException | _: NoSuchElementException) =>
        text += "No menu available."
        return text
      case _: java.net.UnknownHostException =>
        text += "No internet connection."
        return text
    }
  }  
}