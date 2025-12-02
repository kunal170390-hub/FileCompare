package com.example.demo;

import java.io.StringWriter;
import java.util.Collections;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;	

public class JSONPrettifier {
	public static void main(String[] args) {
		//String jsonString = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\",\"skills\":[\"Java\",\"Python\",\"JavaScript\"]}";
		//String jsonString = "{\"menu\": {\"id\": \"file\",\"value\": \"File\",\"popup\": {\"menuitem\": [{\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},{\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},{\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}]}}}";
		//String jsonString = "{\"glossary\": {\"title\": \"example glossary\",\"GlossDiv\": {\"title\": \"S\",\"GlossList\": {\"GlossEntry\": {\"ID\": \"SGML\",\"SortAs\": \"SGML\",\"GlossTerm\": \"Standard Generalized Markup Language\",\"Acronym\": \"SGML\",\"Abbrev\": \"ISO 8879:1986\",\"GlossDef\": {\"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\"GlossSeeAlso\": [\"GML\", \"XML\"]},\"GlossSee\": \"markup\"}}}}}}";
		//String jsonString = "{\"employees\":[{\"firstName\":\"John\",\"lastName\":\"Doe\"},{\"firstName\":\"Anna\",\"lastName\":\"Smith\"},{\"firstName\":\"Peter\",\"lastName\":\"Jones\"}]}";
		String jsonString = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";
		System.out.println(jsonString);
		
		String prettyJson = prettifyJSON(jsonString);
		System.out.println(prettyJson);
	}

	private static String prettifyJSON(String jsonString) {
		// Using built-in Java libraries to prettify JSON
		try {
			JsonReader jsonReader = Json.createReader(new java.io.StringReader(jsonString));
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();

			StringWriter stringWriter = new StringWriter();
			JsonWriter jsonWriter = Json.createWriterFactory(
					Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true))
					.createWriter(stringWriter);
			jsonWriter.writeObject(jsonObject);
			jsonWriter.close();

			return stringWriter.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonString;
	}

}
