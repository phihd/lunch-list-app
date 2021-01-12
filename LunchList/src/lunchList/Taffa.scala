package lunchList

import scala.io.StdIn.{readLine,readInt,readBoolean}
import scala.swing._
import scala.util.parsing.json._
import scala.io.Source  
import java.io.FileInputStream
import java.util.{Calendar, Date}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.sql.Timestamp
import java.net.URL
import java.net.URLConnection
import scala.collection.mutable.ArrayBuffer

/**A `Restaurant` that is named Täffä*/
object Taffa extends Restaurant {
  val tf_name = "Täffä"
  var url = ""
  
  def name = tf_name
  
  def URL = url
  
  def getMenu(day: String) : Map[String, List[Map[String, List[Map[String, List[String]]]]]] = {
    val now = LocalDate.now
    val today = now.getDayOfWeek.toString()
    var dayDist = Tools.dayToNum(day) - Tools.dayToNum(today) 
    if (dayDist < 0)
      dayDist += 7
    val processingDate = Tools.addDays(dayDist)
    val processingDateString = Tools.dateToString(processingDate, 2)
    val date = LocalDate.parse(processingDateString, DateTimeFormatter.ofPattern(("yyyy-MM-dd'T00:00:00+02:00'")))
    val y = date.getYear
    val m = date.getMonthValue
    val d = date.getDayOfMonth
    
    url = "https://kitchen.kanttiinit.fi/restaurants/3/menu?day=" + y + "-" + (if (m < 10) '0') + m + "-" + d + "&lang=en"
    //println(url)
    val urlConnection = new URL(url).openConnection()
    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
    urlConnection.connect()
    val JSONdata = scala.io.Source.fromInputStream(urlConnection.getInputStream).mkString
    //println(JSONdata)
    val data: Map[String, List[Map[String, List[Map[String, List[String]]]]]] = JSON.parseFull(JSONdata) match {   
    case Some(d : Map[String, List[Map[String, List[Map[String, List[String]]]]]])=> d
    case _ => Map[String, List[Map[String, List[Map[String, List[String]]]]]]()
    }
    //for (menu <- data("courses"))
      //println(menu)
    return data
  }
  
  def textMenu(day: String, favouriteType: ArrayBuffer[String], allergens: Array[String], predicates: Array[String]): String = {
    Restaurant.addNotification("Restaurant closed")
    var text = ""
    var hasFood = false
    val sep = Array('-', ' ', ',', '/', ':')
    try {
      val menus = getMenu(day)("menus").head("courses") 
      for (menu <- menus) {
        val title = menu.getOrElse("title", "").toString()
        val properties = menu.getOrElse("properties", List[String]())
        var propertiesText = ""
        properties.foreach(propertiesText += _ + ',' + ' ')
        properties.map(_.trim())
        if ((favouriteType.isEmpty || favouriteType.forall(properties.contains(_))) &&
              allergens.forall(al => !title.toLowerCase().split(sep).filter(_.nonEmpty).contains(al.toLowerCase())) &&
              (predicates.isEmpty || predicates.exists(al => title.toLowerCase().split(sep).filter(_.nonEmpty).contains(al.toLowerCase())))) {
          text += title  + (if (!propertiesText.isEmpty) (" (" + propertiesText.dropRight(2) + ")") else "") + '\n'
          hasFood = true
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