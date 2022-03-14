import play.api.libs.json.{JsError, JsPath, JsResult, JsSuccess, JsValue, Json, Reads, __}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.minLength

import javax.management.Query.{and, value}
import scala.Console.println
import scala.io.Source

object Xml extends App {
//  val path="./xmltojson/json.json"
//  val source: String = Source.fromFile(path).getLines().mkString
 // val json: JsValue = Json.parse(source)

  //val file=XML.load(path)

  case class Address( street: String)

  case class Person(fname: String, lname: String, address: String, phonenumber: Long)

 val json = Json.parse("""{"fname": "Preethi","lname": "Anbhu" ,"address":"hi","phonenumber":9941545658}""")

  val nameReads: Reads[String] = (JsPath \\ "fname").read[String]
  val nameResult: JsResult[String] = json.validate(nameReads)
  println(nameResult)
  implicit val residentReads: Reads[Person] = (
    (JsPath \ "fname").read[String](minLength[String](2)) and
      (JsPath \ "lname").read[String] and
      (JsPath \ "address").read[String] and
      (JsPath \ "phonenumber").read[Long]
    ) (Person.apply _)
  println("read residentreads")
//  implicit val resi:Reads[Address]  = (
//    (JsPath \ "doorno").read[Int] and
//      (JsPath \ "street").read[String]
//    ) (Address.apply _)
//  println("read resi")

      val residentFromJson = {
        Json.fromJson[Person](json)
      }
  Predef.println("res"+residentFromJson)
//  val xml = xstream.toXML(residentFromJson)

}