import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.{Json, Reads}
object XmltoJson extends App {
  val res = Json.parse("""{ "name": "preeti","lname":"A I" }""")
 print((res))
  object JsonExampleV2 {
    implicit val r: Reads[Person] = (
      (__ \ "id").read[Long].map(MyIdentifier) and
        (__ \ "data").read[String]
      )(JsonExampleV2.apply _)
  }

  case class Address(street: String, city: String)
  case class Person(name: String, address: Address)

  // create the formats and provide them implicitly
  implicit val addressFormat = Json.format[Address]
  implicit val personFormat = Json.format[Person]

  // serialize a Person
  val fred = Person("Fred", Address("Awesome Street 9", "SuperCity"))
  val fredJsonString = Json.stringify(Json.toJson(Json.toJson(fred)))

  val personRead = Json.parse(fredJsonString).as[Person] //Person(Fred,Address(Awesome Street 9,SuperCity))
  print(personRead)
}
