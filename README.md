# xmltojson
This project helps to convert json to xml and vise versa
flow of convertion: 
        json file(string)=> case class => listBuffer => string of Xml format
        xml file(string) => Sequene =>json string format.
Input:
<root>
<person1>
		<fname>Preethi</fname>
		<name>bff</name>
		<address>bhares</address>
		<phonenumber>9941545658</phonenumber>
</person1>
</root>
output:
{"root":{row1:{"fname":Preethi,"lname":Anbhu,"address":bharathi salai,"phonenumber":9941545658}}}



Input:
[{"fname": "Preethi","lname": "Anbhu","address":"bharathi salai", "phonenumber":"9941545658"},
{"fname": "Teja","lname": "Anbu","address":"bharathi salai ", "phonenumber":"9941"}]
output:
<root><fname>Preethi</fname><lname>Anbhu</lname><address>bharathi salai</address><phonenumber>9941545658</phonenumber><fname>Teja</fname><lname>Anbu</lname><address>bharathi salai </address><phonenumber>9941</phonenumber></root>
