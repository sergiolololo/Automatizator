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

public class ObtenerRepositoriosDeXMI {
	
	public static List<String> obtenerRepositorios() throws SAXException, IOException, ParserConfigurationException {
		
		List<String> listaRepositorios = new ArrayList<String>();
		File dir = new File("C:\\Users\\usuario\\Desktop\\repositorios.xml");
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(dir.getAbsolutePath());
		Node node = document.getElementsByTagName("repositoryModel").item(0);
		NodeList lista = node.getChildNodes();
		
		for(int i=0; i<lista.getLength(); i++){
			if(lista.item(i).getNodeType() == Node.ELEMENT_NODE){
				Element eElement = (Element) lista.item(i);
				String nombre = eElement.getNodeName();
				if(nombre.equals("repositories")){
					listaRepositorios.add(eElement.getAttribute("repositoryName"));
				}
			}
		}
		return listaRepositorios;
	}
}