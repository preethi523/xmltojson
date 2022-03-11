import play.api.libs.json.{JsArray, JsBoolean, JsNull, JsNumber, JsObject, JsString, JsValue, Json}

import scala.util.{Failure, Success, Try}
import scala.xml._

object Xml extends App{
  val fruitXml: NodeSeq =
    <person>
      <name>
        <fname>Aubergine</fname>
        <lname>Eggplant</lname>
      </name>
      <address>
        <city> chennai</city>
        <district> Thiruvalur </district>
      </address>
    </person>

  println(toJson(fruitXml))
  def toJson(xml: NodeSeq): JsValue = {
    def isEmpty(node: Node): Boolean = node.child.isEmpty
    def isLeaf(node: Node) = {
      def descendant(n: Node): List[Node] = n match {
        case g: Group => g.nodes.toList.flatMap(x => x :: descendant(x))
        case _ => n.child.toList.flatMap { x => x :: descendant(x) }
      }
      !descendant(node).exists(_.isInstanceOf[Elem])
    }

    def isArray(nodeNames: Seq[String]) = nodeNames.size != 1 && nodeNames.toList.distinct.size == 1
    def directChildren(n: Node): NodeSeq = n.child.filter(c => c.isInstanceOf[Elem])
    def nameOf(n: Node) = (if (n.prefix ne null) n.prefix + ":" else "") + n.label
    def buildAttrs(n: Node) = n.attributes.map((a: MetaData) => (a.key, XValue(a.value.text))).toList

    sealed abstract class XElem extends Product with Serializable
    case class XValue(value: String) extends XElem
    case class XLeaf(value: (String, XElem), attrs: List[(String, XValue)]) extends XElem
    case class XNode(fields: List[(String, XElem)]) extends XElem
    case class XArray(elems: List[XElem]) extends XElem

    def toJsValue(x: XElem, flatten: Boolean = false): JsValue = x match {
      case x@XValue(_) => xValueToJsValue(x)
      case XLeaf((name, value), attrs) => (value, attrs) match {
        case (_, Nil) => toJsValue(value)
        case (XValue(""), xs) => JsObject(mkFields(xs))
        case (XValue(_), _ :: _) =>
          val values = JsObject(mkFields(("value" → value) +: attrs))
          if (flatten) {
            values
          } else {
            JsObject(Seq(name → values))
          }
        case (_, _) => JsObject(Seq(name -> toJsValue(value)))
      }
      case XNode(xs) => JsObject(mkFields(xs))
      case XArray(elems) => elems match {
        case (_: XValue) :: _ => JsArray(elems.map(y ⇒ toJsValue(y)))
        case _ => JsArray(elems.map(y ⇒ toJsValue(y, flatten=true)))
      }
    }

    def xValueToJsValue(xValue: XValue): JsValue = {
      (Try(xValue.value.toInt), Try(xValue.value.toBoolean)) match {
        case (Success(v), Failure(_)) => JsNumber(v)
        case (Failure(_), Success(v)) => JsBoolean(v)
        case _ => JsString(xValue.value)
      }
    }

    def mkFields(xs: List[(String, XElem)]): List[(String, JsValue)] =
      xs.flatMap { case (name, value) => (value, toJsValue(value)) match {
        case (XLeaf(_, _ :: _), o: JsObject) =>
          if(o.fields.map(_._1).contains(name)) o.fields
          else Seq(name -> o)
        case (_, json) => Seq(name -> json)
      }}

    def buildNodes(xml: NodeSeq): List[XElem] = xml match {
      case n: Node =>
        if (isEmpty(n)) XLeaf((nameOf(n), XValue("")), buildAttrs(n)) :: Nil
        else if (isLeaf(n)) XLeaf((nameOf(n), XValue(n.text)), buildAttrs(n)) :: Nil
        else {
          val children = directChildren(n).map(cn ⇒ (nameOf(cn), buildNodes(cn).head))
            .groupBy(_._1)
            .map({ case (k,v) => (k,if (v.length > 1)  XArray(v.toList.map(_._2)) else v.head._2)})
            .toList
          XNode(buildAttrs(n) ::: children) :: Nil
        }
      case nodes: NodeSeq =>
        val allLabels = nodes.map(_.label)
        if (isArray(allLabels)) {
          val arr = XArray(nodes.toList.flatMap { n =>
            if (isLeaf(n) && n.attributes.length == 0) XValue(n.text) :: Nil
            else buildNodes(n)
          })
          XLeaf((allLabels.head, arr), Nil) :: Nil
        } else nodes.toList.flatMap(buildNodes)
    }

    buildNodes(xml) match {
      case List(x@XLeaf(_, _ :: _)) => toJsValue(x)
      case List(x) => play.api.libs.json.Json.obj(nameOf(xml.head) -> toJsValue(x))
      case x => JsArray(x.map(y ⇒ toJsValue(y, flatten=true)))
    }
  }

//
//  val json: JsValue=  Json.parse("""{"person":{"name": {"fname":1,"lname":"Eggplant"},"address":{"city":" chennai","district":" Thiruvalur "}}}""".stripMargin)
//  print(toXml(json))
//  def toXml(json: JsValue): NodeSeq = {
//
//    def toXml(name: String, json: JsValue): NodeSeq = json match {
//      case JsObject(fields) =>
//        println("case Jsobject"+json)
//        val children = fields.toList.flatMap { case (n, v) => toXml(n, v) }
////        println("Xml node"+XmlNode(name, children))
//        XmlNode(name, children)
//
//      case JsArray(xs) =>
//        println("case JsArray"+json)
//        xs.flatMap { v => toXml(name, v) }
//      case JsNumber(v) =>
//        println("case JsNumber"+json)
////        println("XmlEle"+XmlElem(name, v.toString()))
//        XmlElem(name, v.toString())
//      case JsBoolean(v) =>
//        println("case JsBoolean"+json)
//        XmlElem(name, v.toString)
//      case JsString(v) =>
//        println("case String"+json)
////        println("XmlEle"+XmlElem(name,v))
//        XmlElem(name, v)
//      case JsNull =>
//        XmlElem(name, "null")
//    }
//
//    json match {
//      case JsObject(fields) =>
//        println(JsObject(fields))
//      println(fields.toList)
//        fields.toList.flatMap { case (n, v) => println;toXml(n, v) }
//      case x => toXml("root", x)
//    }
//  }
//  private[this] case class XmlNode(name: String, children: Seq[Node])
//    extends Elem(null, name, xml.Null, TopScope, true, children: _*)
//  private[this] case class XmlElem(name: String, value: String)
//    extends Elem(null, name, xml.Null, TopScope, true, Text(value))
}