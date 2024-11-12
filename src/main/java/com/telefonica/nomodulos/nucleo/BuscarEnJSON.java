package com.telefonica.nomodulos.nucleo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class BuscarEnJSON {

	private static String rutaINFANuc = "C:\\t718467\\workspace\\2_DF_INFA";
	//private static String rutaINFANucCliews = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\CLIEWS";
	
	private static String rutaPRTENuc = "C:\\t718467\\workspace\\2_DF_PRTE";
	
	private static String rutaTERCuc = "C:\\t718467\\workspace\\2_DF_TERC";
	//private static String rutaPRTENucCliews = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\CLIEWS";
	
	//private static String rutaPRTESOA = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\SOA";
	//private static String rutaINFASOA = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\SOA";
	
	private static Set<String> resultados = new HashSet<String>();
	
	public static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {

		
		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
			}
		} else {
			if(dir.getAbsolutePath().contains("trunk") && dir.getAbsolutePath().contains("json")){
	    		  
				// hemos llegado al json, lo abrimos y vemos si encontramos lo que buscamos
				BufferedReader br = new BufferedReader(new FileReader(dir));
				try {
				    StringBuilder sb = new StringBuilder();
				    String line = br.readLine();

				    while (line != null) {
				    	if(line.contains(("SupplierPartnerProductMng"))
				    			//|| line.toUpperCase().contains(("sPOrderMngServiceImpl.getSPOrder(").toUpperCase())
				    			//|| line.toUpperCase().contains(("sPOrderMngExpService.getSPOrder(").toUpperCase())
				    			//|| line.toUpperCase().contains(("sPOrderMngExpServiceImpl.getSPOrder(").toUpperCase())
				    			//|| line.toUpperCase().contains(("spOrderMng.getSPOrder(").toUpperCase())
				    			){
				    		//System.out.println(dir);
				    		resultados.add(dir.getAbsolutePath());
				    	}
				        /*sb.append(line);
				        sb.append(System.lineSeparator());*/
				        line = br.readLine();
				    }
				    //String everything = sb.toString();
				} finally {
				    br.close();
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				/*
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(dir.getAbsolutePath());
				Node node = document.getElementsByTagName("project").item(0);
				NodeList lista = node.getChildNodes();
				String version = "";
				String nombreServicio = "";
				for(int i=0; i<lista.getLength(); i++){
					if(lista.item(i).getNodeType() == Node.ELEMENT_NODE){// && lista.item(i).getLocalName().equals("version")){
						Element eElement = (Element) lista.item(i);
						String nombre = eElement.getNodeName();
						if(nombre.equals("version")){
							version = eElement.getTextContent().substring(0, eElement.getTextContent().indexOf("-"));;
							//System.out.println(version);
						}else if(nombre.equals("artifactId")){
							nombreServicio = eElement.getTextContent().substring(12);
							//System.out.println(nombreServicio);
						}
					}
				}
				if(!version.equals("")){
					System.out.println(nombreServicio);
					System.out.println("Version nucleo: " + version);	
				}
				
				// volvemos a llamar al método, pero esta vez pasando por parámetro la ruta de cliews, y el nombre del servicio 
				File file = new File(rutaPRTENucCliews + "\\cliews-" + nombreServicio);
				fetchFilesConNombreServicio(file, version, nombreServicio);
				*/
			}
		}
	}
	
	/*private static void fetchFilesConNombreServicio(File dir, String versionNucleo, String nombreServicio) throws SAXException, IOException, ParserConfigurationException {

		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFilesConNombreServicio(file1, versionNucleo, nombreServicio);
			}
		} else {
			if(dir.getAbsolutePath().contains("branches") 
					&& dir.getAbsolutePath().contains("1905")
					&& dir.getName().equalsIgnoreCase("pom.xml")){
	    		  
				// hemos llegado al pom, lo abrimos y vemos la versión que tiene
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(dir.getAbsolutePath());
				Node node = document.getElementsByTagName("project").item(0);
				
				NodeList lista = node.getChildNodes();
				String versionCliews = "";
				//String nombreServicio = "";
				for(int i=0; i<lista.getLength(); i++){
					if(lista.item(i).getNodeType() == Node.ELEMENT_NODE){// && lista.item(i).getLocalName().equals("version")){
						Element eElement = (Element) lista.item(i);
						String nombre = eElement.getNodeName();
						if(nombre.equals("version")){
							versionCliews = eElement.getTextContent().substring(0, eElement.getTextContent().indexOf("-"));;
						}
					}
				}
				if(!versionCliews.equals("")){
					System.out.println(nombreServicio);
					System.out.println("Version cliews: " + versionCliews + "\n");
				}
			}
		}
	}*/

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		
		
		List<String> lista = new ArrayList<String>();
		//lista.add(rutaINFANuc);
		lista.add(rutaPRTENuc);
		//lista.add(rutaTERCuc);
		
		File file = null;
		for(String ruta: lista){
			file = new File(ruta);
			fetchFiles(file);
		}
		
		for(String prueba: resultados){
			System.out.println(prueba);	
		}
		
		
	}
}