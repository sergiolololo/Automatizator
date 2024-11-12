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

public class Main2 {

	private static String rutaINFANucleo = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\NUCLEO";
	private static String rutaINFACliews = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\CLIEWS";
	
	private static String rutaPRTENucleo = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\NUCLEO";
	private static String rutaPRTECliews = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\CLIEWS";
	
	public static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {

		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
			}
		} else {
			if((dir.getAbsolutePath().contains("1905") || dir.getAbsolutePath().contains("trunk"))
					&& !dir.getAbsolutePath().contains("service")
					&& !dir.getAbsolutePath().contains("facade")
					&& !dir.getAbsolutePath().contains("msg")
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
						if(nombre.equals("dependencyManagement")){
							NodeList dependencias = lista.item(i).getChildNodes().item(1).getChildNodes();
							for(int j=0; j<dependencias.getLength(); j++){
								if(dependencias.item(j).getNodeName().equals("dependency")){
									NodeList listado = dependencias.item(j).getChildNodes();
									for(int k=0; k<listado.getLength(); k++){
										if(listado.item(k).getNodeType() == Node.ELEMENT_NODE){// && lista.item(i).getLocalName().equals("version")){
											Element elemento = (Element) listado.item(k);
											String nombree = elemento.getNodeName();
											if(nombree.equals("artifactId")){
												if(elemento.getTextContent().contains("ResponseFromGUIManageException") ||
														elemento.getTextContent().contains("OrcheSupPartPortSinInte")){
													System.out.println("lo contiene");
													System.out.println(dir.getAbsolutePath());
												}
											}
										}
									}
								}
							}
							
							
							
							
							
							
							//version = eElement.getTextContent().substring(0, eElement.getTextContent().indexOf("-"));;
							//System.out.println(version);
						}
					}
				}
				// volvemos a llamar al método, pero esta vez pasando por parámetro la ruta de cliews, y el nombre del servicio 
				//File file = new File(rutaINFACliews + "\\cliews-" + nombreServicio);
				//fetchFilesConNombreServicio(file, version, nombreServicio);
			}
		}
	}
	
	public static void fetchFilesConNombreServicio(File dir, String versionNucleo, String nombreServicio) throws SAXException, IOException, ParserConfigurationException {

		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFilesConNombreServicio(file1, versionNucleo, nombreServicio);
			}
		} else {
			if(dir.getAbsolutePath().contains("trunk") && !dir.getAbsolutePath().contains("service")
					&& !dir.getAbsolutePath().contains("facade")
					&& !dir.getAbsolutePath().contains("msg")
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
							
							if(!versionNucleo.equals(versionCliews)){
								System.out.println(nombreServicio);
								System.out.println("Version nucleo: " + versionNucleo);
								System.out.println("Version cliews: " + versionCliews + "\n");
							}
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		File file = new File(rutaINFANucleo);
		fetchFiles(file);
	}
}