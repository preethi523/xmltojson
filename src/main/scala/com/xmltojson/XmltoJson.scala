package com.xmltojson

import scala.xml.{Elem, NodeSeq, XML}

object Json extends App {
  val xml = XML.loadFile("/home/preethia/xmltojson/xml")
  val root = s"{\"${xml.label}\":{"

  def xmlToJson(xml: Elem) = {
    var resultEmpty = ""
    var personPrint = ""
    val s = xml.child
    if (s.nonEmpty) {
      val personLabel = xml.child.map(x => x.label).filterNot(x => x.matches("#PCDATA")).mkString
      val people = xml \\ personLabel
      people.foreach { person =>
        personPrint = s"${person.label}:{"
        val dataLabel = person.child.map(x => x.label).filterNot(x => x.matches("#PCDATA"))
        val dataValue = person.child.map(x => x.text.trim).zipWithIndex
       val dataValues= dataValue.filter(x=>x._2%2==1)
        val dataValuesFinal=dataValues.map(x=>x._1)
        val zippedLabelValue = dataLabel.zip(dataValuesFinal)
        val json = zippedLabelValue.map(x => s"\"${x._1}\":${x._2},").concat("}").mkString
        val reg = """[,][}]""".r
        val result = reg.replaceAllIn(json, "}")
        resultEmpty = result + resultEmpty
      }
      personPrint + resultEmpty + "}"
    }
    else{
     print("root has no children")
    }
  }

  print(root + xmlToJson(xml) + "}")
}



