package com.jsontoxml
import play.api.libs.json.{JsPath, JsValue, Json, Reads}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{list, minLength}

import scala.Console.println
import scala.io.Source
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.CollectionHasAsScala


object Xml extends App {
  val path = "/home/preethia/xmltojson/json"
  val source: String = Source.fromFile(path).getLines().mkString
  val jsonList: List[JsValue] = Json.parse(source).as[List[JsValue]]
  Source.fromFile(path).close()
  implicit val PersonReads: Reads[Person] = (
    (JsPath \ "fname").read[String](minLength[String](2)) and
      (JsPath \ "lname").read[String] and
      (JsPath \ "address").read[String] and
      (JsPath \ "phonenumber").read[String]) (Person)



  val applicationConf: Config = ConfigFactory.load("json.conf")
  val listConf =applicationConf.getStringList("keyValue")
  val listConfig= listConf.asScala.toList
  val rootConf =applicationConf.getString("rootValue")
  case class Person(var firstName: String, var lastName: String, var address: String, var phoneNumber: String)



def getValues(person:Person):ListBuffer[String]={
  val classValue = ListBuffer[String]()
  classValue += person.firstName
  classValue += person.lastName
  classValue += person.address
  classValue+= person.phoneNumber
  classValue
}


  /** *
   *
   * @param jsonList List of json
   * @return a string in xml format
   */
  def jsonToXml(jsonList: List[JsValue]): String = {
    var stringEmpty = ""
     val caseClass = jsonList.map(x => Json.fromJson[Person](x).get)
      val zipped = caseClass.map(x => getValues(x).zip(listConfig))
      val stringXml = zipped.map(x => x.map(y => s"<${y._2}>${y._1}</${y._2}>").mkString)
      stringEmpty += stringXml.mkString
     stringEmpty
  }
  val finalString = jsonToXml(jsonList)
  println(s"<${rootConf}>${finalString}</${rootConf}>")
}