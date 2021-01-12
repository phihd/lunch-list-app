package lunchList

import java.util.{Calendar, Date}
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.mutable.ArrayBuffer

/**A helper object `Tools` contains implementation small helper function to get menu data
 * and turn them into right format.
 */
object Tools {
  def dayToNum(day: String): Int = {
    day.toUpperCase() match {
      case "MONDAY" => 1
      case "TUESDAY" => 2
      case "WEDNESDAY" => 3
      case "THURSDAY" => 4
      case "FRIDAY" => 5
      case "SATURDAY" => 6
      case "SUNDAY" => 7
      case _ => -1
    }  
  }
  
  def addDays(days: Int): Date = {
    var date: Date = new Date();
    var calendar: Calendar = Calendar.getInstance(); 
    calendar.setTime(date); 
    calendar.add(Calendar.DATE, days);
    date = calendar.getTime();
    return date
  }  
  
  def dateToString(d: Date, timezone: Int): String = {
    val format = "yyyy-MM-dd'T00:00:00" + (if (timezone > 0) "+" else "-") + (if (timezone < 10) "0" else "") + timezone.toString() + ":00'"
    val dateFormat = new SimpleDateFormat(format)    
    dateFormat.format(d)
    }
  
  def stdForm(day: String): String = {
    val ch = day.head
    return ch.toUpper + day.toLowerCase().drop(1)
  }
  
  def Today: String = {
    val now = LocalDate.now()
    val today = now.getDayOfWeek.toString()
    return today
  }
  
  def daysFromNow(n: Int): ArrayBuffer[LocalDate] = {
    val dates = ArrayBuffer.fill(n)(LocalDate.now)
    for (iDay <- 0 until n) {
      val newDate = LocalDate.now().plusDays(iDay)
      dates(iDay) = newDate
    }
    return dates
  }
  
  def daysFromNow_getDayOfWeek(n: Int): ArrayBuffer[String] = {
    return daysFromNow(n).map(_.getDayOfWeek.toString())
  }
  
  def daysFromNowToString(n: Int): ArrayBuffer[String] = {
    return daysFromNow(n).map { newDate =>
      val shortDayOfWeek = newDate.getDayOfWeek.toString().take(3)
      val dayOfMonth = newDate.getDayOfMonth.toString()
      val month = newDate.getMonthValue.toString()
      shortDayOfWeek + " " + dayOfMonth + "." + month
    }
  }
  
}