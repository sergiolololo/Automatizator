package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CompararZK {

	public static void main(String[] args) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		File svnJson = new File("C:\\Users\\sherrerah\\Downloads\\variables2.json");
		JsonNode node = objectMapper.readTree(svnJson);
		
		List<String> listaVarJsonSVN = new ArrayList<>();
		
		Iterator<JsonNode> iterator = node.get("properties").elements();
		while(iterator.hasNext()) {
			JsonNode variable = iterator.next();
			String name = variable.get("name").asText();
			String path = variable.get("path").asText();
			
			String var = name + "&&" + path;
			listaVarJsonSVN.add(var);
		}
		
		
		ObjectMapper objectMapper2 = new ObjectMapper();
		File exportJson= new File("C:\\Users\\sherrerah\\Downloads\\export_EDC_INFA.json");
		JsonNode node2 = objectMapper2.readTree(exportJson);
		
		List<String> listaVarNoExisteEnSVN = new ArrayList<>();
		
		List<String> listaVarJsonExport = new ArrayList<>();
		Iterator<JsonNode> iterator2 = node2.get("properties").elements();
		while(iterator2.hasNext()) {
			JsonNode variable = iterator2.next();
			String name = variable.get("name").asText();
			String path = variable.get("path").asText();
			
			String var = name + "&&" + path;
			listaVarJsonExport.add(var);
			
			if(!listaVarJsonSVN.contains(var)) {
				listaVarNoExisteEnSVN.add(var);
			}
		}
		
		
		
		node = objectMapper.readTree(svnJson);
		
		List<String> listaVarNoExisteEnExport = new ArrayList<>();
		
		iterator = node.get("properties").elements();
		while(iterator.hasNext()) {
			JsonNode variable = iterator.next();
			String name = variable.get("name").asText();
			String path = variable.get("path").asText();
			
			String var = name + "&&" + path;
			
			if(!listaVarJsonExport.contains(var)) {
				listaVarNoExisteEnExport.add(var);
			}
		}
		
		
		for(String var: listaVarNoExisteEnSVN) {
			System.out.println(var);
		}
		for(String var: listaVarNoExisteEnExport) {
			System.out.println(var);
		}
		
	}

}
