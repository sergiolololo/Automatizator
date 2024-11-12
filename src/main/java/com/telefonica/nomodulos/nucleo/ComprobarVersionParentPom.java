package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ComprobarVersionParentPom {

	private static String RUTA_INFA_NUC_JEE = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\NUCLEO";
	private static String RUTA_INFA_CGT = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\cgt";
	private static String RUTA_INFA_CGT_NODE = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\cgt-node";
	private static String RUTA_INFA_CNT = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\cnt";
	private static String RUTA_INFA_CNT_NODE = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\cnt-node";
	private static String RUTA_INFA_RES_NUC_JEE = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\res-nuc-jee";
	private static String RUTA_INFA_PRES = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\srv-pres";
	private static String rutaINFANucCliews = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\CLIEWS";
	
	
	private static String RUTA_PRTE_NUC_JEE = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\NUCLEO";
	private static String RUTA_PRTE_JT_NUC_JEE = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\jt-nuc-jee";
	private static String RUTA_PRTE_RES_NUC_JEE = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\res-nuc-jee";
	private static String RUTA_PRTE_PRES = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\srv-pres";
	private static String rutaPRTENucCliews = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\CLIEWS";
	
	private static String rutaPRTESOA = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\SOA";
	private static String rutaINFASOA = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\SOA";
	
	public static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {

		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
			}
		} else {
			if(dir.getAbsolutePath().contains("trunk") 
					&& !dir.getAbsolutePath().contains("tags")
					&& !dir.getAbsolutePath().contains("branches")
					&& !dir.getAbsolutePath().contains("service")
					&& !dir.getAbsolutePath().contains("facade")
					&& !dir.getAbsolutePath().contains("msg")
					&& !dir.getAbsolutePath().contains("-webapp")
					&& dir.getName().equalsIgnoreCase("pom.xml")){
	    		  
				// hemos llegado al pom, lo abrimos y vemos la versión que tiene
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(dir.getAbsolutePath());
				Node node = document.getElementsByTagName("project").item(0);
				NodeList lista = node.getChildNodes();
				String versionParent = "";
				String versionServicio = "";
				String nombreServicio = "";
				boolean mostrar = false;
				for(int i=0; i<lista.getLength(); i++){
					if(lista.item(i).getNodeType() == Node.ELEMENT_NODE){// && lista.item(i).getLocalName().equals("version")){
						Element eElement = (Element) lista.item(i);
						String nombre = eElement.getNodeName();
						if(nombre.equals("parent")){
							//versionServicio = eElement.getTextContent().substring(0, eElement.getTextContent().indexOf("-"));
							//System.out.println(version);
							
							NodeList lista2 = eElement.getChildNodes();
							for(int j=0; j<lista2.getLength(); j++){
								if(lista2.item(j).getNodeType() == Node.ELEMENT_NODE){// && lista.item(i).getLocalName().equals("version")){
									Element eElement2 = (Element) lista2.item(j);
									String nombre2 = eElement2.getNodeName();
									if(nombre2.equals("version")){
										versionParent = eElement2.getTextContent();
										if(versionParent.substring(0, 1).equals("3") && versionParent.compareTo("3.1.6") < 0){
											versionParent = eElement2.getTextContent();
											mostrar = true;
										}else if(versionParent.substring(0, 1).equals("4") && versionParent.compareTo("4.2.9") < 0){
											versionParent = eElement2.getTextContent();
											mostrar = true;
										}
										break;
									}
								}
							}
							
							
							
						}else if(nombre.equals("artifactId")){
							nombreServicio = eElement.getTextContent();
							if(nombreServicio.contains("srv-nuc-jee-")){
								nombreServicio = "SRV_" + nombreServicio.substring(12);
							}else if(nombreServicio.contains("cgt-")){
								nombreServicio = "CGT_" + nombreServicio.substring(4);
							}else if(nombreServicio.contains("cnt-")){
								nombreServicio = "CNT_" + nombreServicio.substring(4);
							}else if(nombreServicio.contains("res-nuc-jee-")){
								nombreServicio = "RES_" + nombreServicio.substring(12);
							}
							//System.out.println(nombreServicio);
						}else if(nombre.equals("version")){
							versionServicio = eElement.getTextContent().substring(0, eElement.getTextContent().indexOf("-"));
						}
					}
				}
				if(mostrar){
					//System.out.println(nombreServicio);
					System.out.println(nombreServicio + " - Parent: " + versionParent + " - Version: " + versionServicio);
				}else{
					//System.out.println(" - ");
				}
				// volvemos a llamar al método, pero esta vez pasando por parámetro la ruta de cliews, y el nombre del servicio 
				//File file = new File(rutaINFANucCliews + "\\cliews-" + nombreServicio);
				//fetchFilesConNombreServicio(file, version, nombreServicio);
			}
		}
	}
	
	private static void fetchFilesConNombreServicio(File dir, String versionNucleo, String nombreServicio) throws SAXException, IOException, ParserConfigurationException {

		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFilesConNombreServicio(file1, versionNucleo, nombreServicio);
			}
		} else {
			if(dir.getAbsolutePath().contains("trunk") 
					&& !dir.getAbsolutePath().contains("tags")
					&& !dir.getAbsolutePath().contains("branches")
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
		
		List<String> listaRutas = new ArrayList<String>();
		listaRutas.add(RUTA_INFA_NUC_JEE);
		/*listaRutas.add(RUTA_INFA_CGT);
		listaRutas.add(RUTA_INFA_CGT_NODE);*/
		listaRutas.add(RUTA_INFA_CNT);
		listaRutas.add(RUTA_INFA_CNT_NODE);
		listaRutas.add(RUTA_INFA_RES_NUC_JEE);
		listaRutas.add(RUTA_PRTE_NUC_JEE);
		listaRutas.add(RUTA_PRTE_JT_NUC_JEE);
		listaRutas.add(RUTA_PRTE_RES_NUC_JEE);
		/*listaRutas.add(RUTA_INFA_PRES);
		listaRutas.add(RUTA_PRTE_PRES);*/
		File file = null;
		for(String ruta: listaRutas){
			file = new File(ruta);
			//System.out.println("\nRuta inicial: " + ruta);
			fetchFiles(file);
		}
	}
}