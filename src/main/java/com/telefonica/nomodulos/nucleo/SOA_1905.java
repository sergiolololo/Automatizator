package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SOA_1905 {

	private static String rutaINFANuc = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\NUCLEO";
	private static String rutaINFANucCliews = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\CLIEWS";
	
	private static String rutaPRTENuc = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\NUCLEO";
	private static String rutaPRTENucCliews = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\CLIEWS";
	
	private static String rutaPRTESOA = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\SOA";
	private static String rutaINFASOA = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\SOA";
	
	public static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {

		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
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
			}
		}
	}
	
	public static void fetchFilesConNombreServicio(File dir, String versionNucleo, String nombreServicio) throws SAXException, IOException, ParserConfigurationException {

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
	}

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		File file = new File(rutaPRTESOA);
		fetchFiles(file);
	}
}