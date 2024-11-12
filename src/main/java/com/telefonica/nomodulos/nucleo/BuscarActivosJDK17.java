package com.telefonica.nomodulos.nucleo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BuscarActivosJDK17 {
	
	
	private static String RUTA_INFA_NUC_JEE = "C:\\T718467\\workspace\\1_CO\\INFA\\1_CO\\srv-nuc-jee";
	private static String RUTA_PRTE_NUC_JEE = "C:\\T718467\\workspace\\1_CO\\PRTE\\1_CO\\srv-nuc-jee";
	private static String RUTA_TERC_NUC_JEE = "C:\\T718467\\workspace\\1_CO\\TERC\\1_CO\\srv-nuc-jee";
	
	private static String ramaBuscar = "branches";
	private static String ramaBuscar2 = "trunk";
	private static String ramaNoBuscar2 = "tags";
	private static String rutaRecorriendo = null;
	
	private static Set<String> activosJDK17 = new LinkedHashSet<>();
	private static Set<String> activosJDK8 = new LinkedHashSet<>();
	private static Set<String> activosObsoletos = new LinkedHashSet<>();
	
	public static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {
		if(dir.getAbsolutePath().toUpperCase().contains("MIGRADO")) {
			activosObsoletos.add(dir.getAbsolutePath());
		}
		
		if (dir.isDirectory() && !dir.getAbsolutePath().contains(ramaNoBuscar2)) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
			}
		} else {
			if((dir.getAbsolutePath().contains(ramaBuscar) || dir.getAbsolutePath().contains(ramaBuscar2))
					&& (dir.getAbsolutePath().contains("jt-nuc-jee")
							|| dir.getAbsolutePath().contains("res-nuc-jee")
							|| dir.getAbsolutePath().contains("srv-nuc-jee")
							|| dir.getAbsolutePath().contains("srv-nuc-soa")
							|| dir.getAbsolutePath().contains("srv-pres")
						)
					&& !dir.getAbsolutePath().contains(ramaNoBuscar2)
					//&& !dir.getAbsolutePath().contains("tags")
					//&& !dir.getAbsolutePath().contains("1911")
					&& !dir.getAbsolutePath().contains("service")
					&& !dir.getAbsolutePath().contains("facade")
					&& !dir.getAbsolutePath().contains("msg")
					&& !dir.getAbsolutePath().contains("-webapp")
					&& dir.getName().equalsIgnoreCase("pom.xml")){
				
				try {
					buscarEnJava(dir);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void buscarEnJava(File dir) throws Exception{
		
		String nombreServicio = "";
		if(dir.getAbsolutePath().contains(ramaBuscar)) {
			nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);	
		}else if(dir.getAbsolutePath().contains(ramaBuscar2)) {
			nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar2)-1);
		}
		
		FileReader pom1Reader = new FileReader(dir);
		Model model = new MavenXpp3Reader().read(pom1Reader);
		
		Parent parent = model.getParent();
		if(parent.getVersion().startsWith("4.5")) {
			activosJDK17.add(nombreServicio);
		}else {
			activosJDK8.add(nombreServicio);
		}
	}
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		
		List<String> listaRutas = new ArrayList<String>();
		
		listaRutas.add(RUTA_INFA_NUC_JEE);
		listaRutas.add(RUTA_PRTE_NUC_JEE);
		listaRutas.add(RUTA_TERC_NUC_JEE);
		
		File file = null;
		for(String ruta: listaRutas){
			file = new File(ruta);
			rutaRecorriendo = ruta;
			fetchFiles(file);
		}
		
		System.out.println("Activos JDK 8");
		for(String activo: activosJDK8) {
			
			Optional<String> exists = activosObsoletos.stream()
	                .filter(dir -> dir.contains(activo+"\\")).findFirst();
			
			if(!activosJDK17.contains(activo) && !exists.isPresent()) {
				System.out.println(activo);	
			}
		}
		System.out.println("Activos JDK 17");
		for(String activo: activosJDK17) {
			
			Optional<String> exists = activosObsoletos.stream()
	                .filter(dir -> dir.contains(activo+"\\")).findFirst();
			
			if(!exists.isPresent()) {
				System.out.println(activo);	
			}	
		}
	}
}