package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.nomodulos.beans.ZKBean;

public class JSONComparator {

	public static void main(String[] args) throws IOException {
		
		File archivo1 = new File("C:\\Users\\sherrerah\\Desktop\\ZK_SVN_EDC.json");
		File archivo2 = new File("C:\\Users\\sherrerah\\Desktop\\ZK_SVN_EIN.json");
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		JsonNode nodeEDC = objectMapper.readTree(archivo1);
		JsonNode nodeEIN = objectMapper.readTree(archivo2);

		Map<String, ZKBean> mapaZKEDC = crearMapaZK(nodeEDC);
		Map<String, ZKBean> mapaZKEIN = crearMapaZK(nodeEIN);
		
		for(Entry<String, ZKBean> mapa1: mapaZKEDC.entrySet()) {
			String valueEDC = mapa1.getValue().getValue();
			if(mapaZKEIN.get(mapa1.getKey())  == null) {
				System.out.println("No existe la variable en EIN -> " + mapa1.getKey());
			}else {
				String valueEIN = mapaZKEIN.get(mapa1.getKey()).getValue();
				if(!valueEDC.equals(valueEIN)) {
					System.out.println("Distinto valor en EIN (" + mapa1.getValue().getName() + ") -> EDC: " + valueEDC + " , EIN: " + valueEIN);
				}
			}
		}
		
		for(Entry<String, ZKBean> mapa2: mapaZKEIN.entrySet()) {
			if(mapaZKEDC.get(mapa2.getKey()) == null) {
				System.out.println("No existe la variable en EDC -> " + mapa2.getKey());
			}
		}
	}

	private static Map<String, ZKBean> crearMapaZK(JsonNode node) {
		
		Map<String, ZKBean> mapaZK = new HashMap<>();
		Iterator<JsonNode> elementsEDC = node.get("properties").elements();
		while(elementsEDC.hasNext()){
			JsonNode elementEDC = elementsEDC.next();
			
			ZKBean zkBean = new ZKBean();
			zkBean.setEncrypt(elementEDC.get("encrypt").asText());
			zkBean.setName(elementEDC.get("name").asText());
			zkBean.setPath(elementEDC.get("path").asText());
			zkBean.setValue(elementEDC.get("value").asText());
			
			mapaZK.put(zkBean.getPath()+"&"+zkBean.getName(), zkBean);
		}
		return mapaZK;
	}
	
}
