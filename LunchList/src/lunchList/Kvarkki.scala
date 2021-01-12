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

/**A `Restaurant` that is named Kvarkki*/
object Kvarkki extends Restaurant {
  var kvarkki_url = ""
  def name = "Kvarkki"
  def getMenu(day: String) : Map[String, Map[String, Map[String, Any]]] = {
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
    
    kvarkki_url = "https://www.sodexo.fi/en/ruokalistat/output/daily_json/93/" + y + "-" + (if (m < 10) '0') + m + "-" + d
    //var kvarkki_url = "https://www.sodexo.fi/en/ruokalistat/output/daily_json/130/2020-03-17"
    val urlConnection = new URL(kvarkki_url).openConnection()
    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
    urlConnection.connect()
    val JSONdata = scala.io.Source.fromInputStream(urlConnection.getInputStream).mkString
    val data: Map[String, Map[String, Map[String, Any]]] = JSON.parseFull(JSONdata) match {   
      case Some(d : Map[String, Map[String, Map[String, Any]]])=> d
      case _ => Map[String, Map[String, Map[String, Any]]]()
    }
    return data
  }
  
  def URL = kvarkki_url
  
  def textMenu(day: String, favouriteType: ArrayBuffer[String], allergens: Array[String], predicates: Array[String]): String = {
    var text = ""
    var hasFood = false
    val sep = Array('-', ' ', ',', '/', ':')
    try {
      val menu2 = Kvarkki.getMenu(day)("courses") 
      //println(URL)
      
      for((_, dish) <- menu2) {
        val category = dish.getOrElse("category", "")
        val title_en = dish.getOrElse("title_en", "")
        val title_fi = dish.getOrElse("title_fi", "")
        val propertiesText = dish.getOrElse("properties", "")
        val properties = propertiesText.toString().split(',').map(_.trim())
        val component = category + ": " + title_en + " ~ " + title_fi + " (" + propertiesText + ")"
        if ((favouriteType.isEmpty || favouriteType.forall(properties.contains(_))) &&
              allergens.forall(al => !component.toLowerCase().split(sep).filter(_.nonEmpty).contains(al.toLowerCase())) &&
              (predicates.isEmpty || predicates.exists(al => component.toLowerCase().split(sep).filter(_.nonEmpty).contains(al.toLowerCase())))) {    
          text += (if (title_en.toString().isEmpty()) title_fi else title_en) + (if (propertiesText != "") (" (" + propertiesText + ")") else "") + '\n'
          hasFood = true
        }    
      }
      if (!hasFood)
        text += "No match course found.\nYou can see more by adjusting favourite type and allergens." + '\n'
      return text
    } catch {
      case _ @ (_: NullPointerException) =>
        text += "No menu available."
        return text
      case _: java.net.UnknownHostException =>
        text += "No internet connection."
        return text
    }
  }
  
}