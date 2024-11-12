package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class PrepararSOAPUI {

	private static String RUTA_WORKSPACE = "C:\\Users\\sergy\\OneDrive\\Documentos\\SOAP UI - Proyectos\\SOAP_UI\\Sergio2-workspace.xml";
	private static String RUTA_SOAPUI = "C:\\Users\\sergy\\OneDrive\\Documentos\\SOAP UI - Proyectos\\SOAP_UI";
	
	private static File rutaWork;
	
	private static String meterProyectoEnWorkspace(File dir) throws Exception{
		
		String nombreProyecto = "";
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(dir.getAbsolutePath());
			nombreProyecto = document.getChildNodes().item(0).getAttributes().getNamedItem("name").getTextContent();
		}catch(Exception e) {
			System.out.println(e);
		}
		
		String lineaWork = "  <con:project name=\"" + nombreProyecto + "\">" + dir.getAbsolutePath() + "</con:project>";
		return lineaWork;
	}
	
	public static void main(String[] args) throws Exception {
		rutaWork = new File(RUTA_WORKSPACE);
		
		List<String> lineasWorkspace2 = new ArrayList<>();
		lineasWorkspace2.addAll(
		Arrays.asList(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
				"<con:soapui-workspace name=\"Sergio2\" soapui-version=\"5.7.0\" xmlns:con=\"http://eviware.com/soapui/config\">",
				"  <con:settings/>")
		);
		
		File file = new File(RUTA_SOAPUI);
		for(File file2: file.listFiles()){
			if(!file2.getAbsolutePath().endsWith("workspace.xml")) {
				String linea = meterProyectoEnWorkspace(file2);
				lineasWorkspace2.add(linea);	
			}
		}
		
		lineasWorkspace2.addAll(
			Arrays.asList(
					"  <con:collectInfoForSupport>false</con:collectInfoForSupport>",
					"</con:soapui-workspace>")
		);
		
		Path pathTest = Paths.get(rutaWork.getAbsolutePath());
		Files.write(pathTest, lineasWorkspace2, StandardOpenOption.CREATE);
	}
}