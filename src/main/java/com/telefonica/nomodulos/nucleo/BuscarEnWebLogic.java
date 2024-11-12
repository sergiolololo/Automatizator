package com.telefonica.nomodulos.nucleo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BuscarEnWebLogic {

	private static String rutaINFANuc = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\NUCLEO";
	//private static String rutaINFANucCliews = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\CLIEWS";
	
	private static String rutaPRTENuc = "C:\\Oracle\\Middleware\\Oracle_Home";
	//private static String rutaPRTENucCliews = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\CLIEWS";
	
	//private static String rutaPRTESOA = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\SOA";
	//private static String rutaINFASOA = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\SOA";
	
	private static Set<String> mapa = new HashSet<String>();
	
	public static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {

		
		if (dir.isDirectory()) {
			//System.out.println(dir.getAbsolutePath());
			try{
				for (File file1 : dir.listFiles()) {
					fetchFiles(file1);
				}
			}
			catch(Exception e){
				
			}
		} else {
			if(dir.getName().contains(".xml") && !dir.getAbsolutePath().contains("appmergegen_")){
	    		  
				// hemos llegado al java, lo abrimos y vemos la versión que tiene
				BufferedReader br = new BufferedReader(new FileReader(dir));
				try {
				    StringBuilder sb = new StringBuilder();
				    String line = br.readLine();

				    while (line != null) {
				    	if(line.contains(("UOD_PRVTER1"))
				    			//|| line.toUpperCase().contains(("sPOrderMngServiceImpl.getSPOrder(").toUpperCase())
				    			//|| line.toUpperCase().contains(("sPOrderMngExpService.getSPOrder(").toUpperCase())
				    			//|| line.toUpperCase().contains(("sPOrderMngExpServiceImpl.getSPOrder(").toUpperCase())
				    			//|| line.toUpperCase().contains(("spOrderMng.getSPOrder(").toUpperCase())
				    			){
				    		//System.out.println(dir);
				    		mapa.add(dir.getAbsolutePath());
				    		System.out.println(dir.getAbsolutePath());
				    	}
				        /*sb.append(line);
				        sb.append(System.lineSeparator());*/
				        line = br.readLine();
				    }
				} finally {
				    br.close();
				}
			}
		}
	}

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		File file = new File(rutaPRTENuc);
		fetchFiles(file);
		
		/*File file2 = new File(rutaPRTENuc);
		fetchFiles(file2);*/
		
		for(String i : mapa){
			System.out.println(i);
		}
	}
}