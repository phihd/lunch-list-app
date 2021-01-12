package lunchList

import java.io.File
import java.nio.file.{Paths, Files}
import scala.io.Source
import java.io.FileOutputStream
import java.io.PrintWriter
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

/** The helper object `FileProcessing` used to save and load user's data.*/
object FileProcessing {
  def openFileFavourite(user: User): Map[Restaurant, ArrayBuffer[String]] = {
    val file = "src/" + user.name + "_favourite.txt"
    val favMap = Map[Restaurant, ArrayBuffer[String]]()
    if (Files.exists(Paths.get(file))) {
      var favLines = Source.fromFile(file).getLines()
      for (line <- favLines) {
        val restaurant = line.trim.takeWhile(_ != '>').dropRight(2).trim
        val dishes = {
          val text = line.drop(restaurant.length + 4).split('#').map(_.trim)
          ArrayBuffer(text: _*)
        }
        favMap += ((Restaurant.stringToRestaurant(restaurant), dishes))
      }
      return favMap
    }
    return Map[Restaurant, ArrayBuffer[String]]()
  }
  
  def saveFileFavourite(user: User) {
    val file = new FileOutputStream("src/" + user.name + "_favourite.txt", false)
    val writer = new PrintWriter(file)
    val data = user.favouriteCourse.map(x => (x._1.name, x._2.mkString("#")))
    writer.write(data.mkString("\n"))
    writer.close()
    file.close()
  }
  
  def clearFileFavourite(user: User) {
    user.favouriteCourse.clear()
    saveFileFavourite(user)
  }
}