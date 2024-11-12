package com.telefonica.nomodulos.nucleo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.nomodulos.beans.ZKBean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ZKCompararVariablesOS4Faltan {

	
	private static final String rutaFicheroZK = "C:\\Users\\sherrerah\\Desktop\\ZK_OS4_PRTE";
	
	public static void main(String[] args) throws IOException {
		
		File archivo1 = new File("C:\\Worskspace_spring\\var-conf - PRTE\\ECE\\variables.conf");
		//File archivo2 = new File("C:\\Users\\sherrerah\\Desktop\\ZK_SVN_EIN.json");
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		JsonNode nodeEDC = objectMapper.readTree(archivo1);
		//JsonNode nodeEIN = objectMapper.readTree(archivo2);

		Map<String, ZKBean> mapaZKEDC = crearMapaZK(nodeEDC);
		//Map<String, ZKBean> mapaZKEIN = crearMapaZK(nodeEIN);
		
		/*for(Entry<String, ZKBean> mapa1: mapaZKEDC.entrySet()) {
			System.out.println("nombre variable -> " + mapa1.getKey());
		}*/
		buscarVariableOS4(nodeEDC, mapaZKEDC);
	}

	private static Map<String, ZKBean> crearMapaZK(JsonNode node) {
		
		Map<String, ZKBean> mapaZK = new HashMap<>();
		Iterator<JsonNode> elementsEDC = node.get("properties").elements();
		while(elementsEDC.hasNext()){
			JsonNode elementEDC = elementsEDC.next();
			
			String name = elementEDC.get("name").asText();
			if(name.contains("tap.web.client.read-timeout") || name.contains("spring.transaction.default-timeout")) {
				ZKBean zkBean = new ZKBean();
				zkBean.setEncrypt(elementEDC.get("encrypt").asText());
				zkBean.setName(name);
				zkBean.setPath(elementEDC.get("path").asText());
				zkBean.setValue(elementEDC.get("value").asText());
				
				mapaZK.put(zkBean.getPath()+"&"+zkBean.getName(), zkBean);
			}
			
			
			
			
			
			/*
			if(elementEDC.get("name").asText().contains("system.ws.request.timeout") || 
					elementEDC.get("name").asText().contains(".request.timeout") || 
					elementEDC.get("name").asText().contains("system.transaction.timeout")) {
				
				String variableOS4DeberiaSer = "";
				if(elementEDC.get("name").asText().contains("system.ws.request.timeout")) {
					variableOS4DeberiaSer = "tap.web.client.read-timeout";
				}else if(elementEDC.get("name").asText().contains(".request.timeout")) {
					
					String nombreServicio = elementEDC.get("name").asText().split(".")[0];
					
					variableOS4DeberiaSer = "tap.web.client.read-timeout." + nombreServicio;
				}else if(elementEDC.get("name").asText().contains("system.transaction.timeout")) {
					variableOS4DeberiaSer = "spring.transaction.default-timeout";
				}
				
				
				ZKBean zkBean = new ZKBean();
				zkBean.setEncrypt(elementEDC.get("encrypt").asText());
				zkBean.setName(variableOS4DeberiaSer);
				zkBean.setPath(elementEDC.get("path").asText());
				zkBean.setValue(elementEDC.get("value").asText());
				
				mapaZK.put(zkBean.getPath()+"&"+zkBean.getName(), zkBean);
			}*/
		}
		return mapaZK;
	}
	
	private static void buscarVariableOS4(JsonNode node, Map<String, ZKBean> mapaZKEDC) throws IOException {
		
		FileWriter myWriter = new FileWriter(rutaFicheroZK);
		
		Iterator<JsonNode> elementsEDC = node.get("properties").elements();
		while(elementsEDC.hasNext()){
			JsonNode elementEDC = elementsEDC.next();
			
			String name = "";
			String path = elementEDC.get("path").asText();
			String encrypt = elementEDC.get("encrypt").asText();
			String value = elementEDC.get("value").asText();
			
			if(elementEDC.get("name").asText().contains("system.ws.request.timeout") || 
					elementEDC.get("name").asText().contains(".request.timeout") || 
					elementEDC.get("name").asText().contains("system.transaction.timeout")) {
				
				if(elementEDC.get("name").asText().contains("system.ws.request.timeout")) {
					name = "tap.web.client.read-timeout";
				}else if(elementEDC.get("name").asText().contains(".request.timeout")) {
					
					String nombreServicio = elementEDC.get("name").asText().split("\\.")[0];
					
					name = "tap.web.client.read-timeout." + nombreServicio;
				}else if(elementEDC.get("name").asText().contains("system.transaction.timeout")) {
					name = "spring.transaction.default-timeout";
				}
				
				if(mapaZKEDC.get(path+"&"+name) == null) {
					System.out.println("No existe variable en OS4 para: " + path + " - " + name);
					
					myWriter.write("	{\n");
					myWriter.write("      \"encrypt\": " + encrypt + ",\n");
					myWriter.write("      \"name\": \"" + name + "\",\n");
					myWriter.write("      \"path\": \"" + path + "\",\n");
					myWriter.write("      \"value\": \"" + value + "\"\n");
					myWriter.write("    },\n");
				}
			}
		}
		myWriter.close();
	}
}
