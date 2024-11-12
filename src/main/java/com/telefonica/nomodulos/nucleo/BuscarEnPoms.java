package com.telefonica.nomodulos.nucleo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BuscarEnPoms {

	private static String RUTA_PRTE_TODO = "C:\\T718467\\CODI\\PRTE";
	private static String RUTA_INFA_TODO = "C:\\T718467\\CODI\\INFA";
	private static String RUTA_TERC_TODO = "C:\\T718467\\CODI\\TERC";
	
	private static String RUTA_INFA_CNT = "C:\\T718467\\CODI\\INFA\\1_CO\\cnt";
	
	
	private static String RUTA_INFA_NUC_JEE = "C:\\T718467\\CODI\\INFA\\1_CO\\srv-nuc-jee";
	private static String RUTA_INFA_JT_NUC_JEE = "C:\\T718467\\CODI\\INFA\\1_CO\\jt-nuc-jee";
	private static String RUTA_INFA_CGT = "C:\\T718467\\CODI\\INFA\\1-CO\\cgt";
	private static String RUTA_INFA_CGT_NODE = "C:\\T718467\\CODI\\INFA\\1-CO\\cgt-node";
	private static String RUTA_INFA_CNT_NODE = "C:\\T718467\\CODI\\INFA\\1-CO\\cnt-node";
	private static String RUTA_INFA_RES_NUC_JEE = "C:\\T718467\\CODI\\INFA\\1_CO\\res-nuc-jee";
	private static String RUTA_INFA_PRES = "C:\\T718467\\CODI\\INFA\\1_CO\\srv-pres";
	private static String rutaINFANucCliews = "C:\\T718467\\CODI\\INFA\\1-CO\\CLIEWS";
	
	
	private static String RUTA_PRTE_NUC_JEE = "C:\\T718467\\CODI\\PRTE\\1_CO\\srv-nuc-jee";
	private static String RUTA_PRTE_JT_NUC_JEE = "C:\\T718467\\CODI\\PRTE\\1_CO\\jt-nuc-jee";
	private static String RUTA_PRTE_RES_NUC_JEE = "C:\\T718467\\CODI\\PRTE\\1_CO\\res-nuc-jee";
	/*private static String RUTA_PRTE_PRES = "C:\\T718467\\CODI\\CODIFICACION\\PRTE\\srv-pres";
	private static String rutaPRTENucCliews = "C:\\T718467\\CODI\\CODIFICACION\\PRTE\\CLIEWS";*/
	
	private static String rutaPRTESOA = "C:\\T718467\\CODI\\PRTE\\1_CO\\srv-nuc-soa";
	private static String rutaINFASOA = "C:\\T718467\\CODI\\INFA\\1_CO\\srv-nuc-soa";
	private static String rutaTERCSOA = "C:\\T718467\\CODI\\TERC\\1_CO\\srv-nuc-soa";
	
	private static String RUTA_TERC_NUC_JEE = "C:\\T718467\\CODI\\TERC\\1_CO\\srv-nuc-jee";
	private static String RUTA_TERC_JT_NUC_JEE = "C:\\T718467\\CODI\\TERC\\1_CO\\jt-nuc-jee";
	private static String RUTA_TERC_RES_NUC_JEE = "C:\\T718467\\CODI\\TERC\\1_CO\\res-nuc-jee";
	
	/*private static String RUTA_TERC_NUC_JEE = "C:\\T718467\\CODI\\CODIFICACION\\TERC\\srv-nuc-jee";
	private static String RUTA_TERC_EXP = "C:\\T718467\\CODI\\CODIFICACION\\TERC\\srv-exp-osb";*/
	
	private static String ramaBuscar = "branches";
	private static String ramaBuscar2 = "trunk";
	private static String ramaNoBuscar2 = "tags";
	//private static List<String> mapa = new ArrayList<String>();
	private static List<String> listaServiciosBuscarEnPom = new ArrayList<String>();
	private static String rutaRecorriendo = null;
	
	
	//private static Map<String, Set<String>> mapaActivoLlamantes = new LinkedHashMap<String, Set<String>>();
	private static Map<String, Map<String, String>> mapaActivoLlamantes = new LinkedHashMap<String, Map<String, String>>();
	
	private static boolean encontrado2 = false;
	
	public static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {

		
		if (dir.isDirectory() && !dir.getAbsolutePath().contains(ramaNoBuscar2)) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
			}
		} else {
			if(dir.getAbsolutePath().contains("SOA")
					&& (dir.getAbsolutePath().contains(ramaBuscar) || dir.getAbsolutePath().contains(ramaBuscar2)) 
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
					&& dir.getName().equalsIgnoreCase("composite.xml")){
				
				try {
					buscarEnSOA(dir);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
			} else if((dir.getAbsolutePath().contains(ramaBuscar) || dir.getAbsolutePath().contains(ramaBuscar2))
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
			} else if(dir.getAbsolutePath().contains("TERC") && dir.getAbsolutePath().contains("bs")
					&& dir.getAbsolutePath().contains(ramaBuscar) 
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
					//&& !dir.getName().contains("SPServiceSPIntOrchestration")
					&& dir.getName().contains(".bix")){
				
				try {
					buscarEnTerc(dir);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			} else if(dir.getAbsolutePath().contains(ramaBuscar)
					&& (dir.getAbsolutePath().contains("jt-nuc-jee")
							|| dir.getAbsolutePath().contains("res-nuc-jee")
							|| dir.getAbsolutePath().contains("srv-nuc-jee")
							|| dir.getAbsolutePath().contains("srv-nuc-soa")
							|| dir.getAbsolutePath().contains("srv-pres")
						)
					&& !dir.getAbsolutePath().contains(ramaNoBuscar2)
					//&& !dir.getAbsolutePath().contains("tags")
					//&& !dir.getAbsolutePath().contains("1911")
					&& dir.getAbsolutePath().contains("cnt")
					&& !dir.getAbsolutePath().contains("service")
					&& !dir.getAbsolutePath().contains("facade")
					&& !dir.getAbsolutePath().contains("msg")
					&& dir.getAbsolutePath().contains("-webapp")
					&& dir.getName().equalsIgnoreCase("pom.xml")){
				
				try {
					buscarEnCNT(dir);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	private static void buscarEnSOA(File dir) throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader(dir));
		try {
			String line = br.readLine();

		    while (line != null) {
		    	for(String activo2: listaServiciosBuscarEnPom){
		    		
		    		String activo = activo2.contains(".")?activo2.substring(0, activo2.indexOf(".")):activo2;
					activo = activo.substring(4);
		    		
		    		if(line.contains(activo+"-")){
		    			
		    			String linea = line;
		    			
		    			String nombreServicio = "";
		    			if(dir.getAbsolutePath().contains(ramaBuscar)) {
		    				nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);
		    			}else {
		    				nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar2)-1);
		    			}
		    			
		    			 
			    		//mapa.add(nombreServicio + " -- " + line);
			    		
			    		//Set<String> llamantes = new HashSet<String>();
			    		Map<String, String> llamantes = new LinkedHashMap<String, String>();
			    		if(mapaActivoLlamantes.get(activo2) != null){
			    			llamantes = mapaActivoLlamantes.get(activo2);
			    		}
			    		llamantes.put(nombreServicio, linea);
		    			mapaActivoLlamantes.put(activo2, llamantes);
		    		}
		    	}
		    	/*if(listaServiciosBuscarEnPom.parallelStream().anyMatch(line::contains) && line.contains("<import")){
		    		String nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);
		    		mapa.add(nombreServicio + " -- " + line);
		    	}*/
		        line = br.readLine();
		    }
		} finally {
		    br.close();
		}
	}
	
	private static void buscarEnJava(File dir) throws Exception{
		
		String nombreServicio = "";
		/*if(dir.getAbsolutePath().contains(ramaBuscar)) {
			nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);	
		}else if(dir.getAbsolutePath().contains(ramaBuscar2)) {
			nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar2)-1);
		}*/
		
		FileReader pom1Reader = new FileReader(dir);
		Model model = new MavenXpp3Reader().read(pom1Reader);
		
		nombreServicio = model.getArtifactId();
		
		boolean encontrado = false;
		if (model.getDependencyManagement() != null) {
			for(Dependency dependency: model.getDependencyManagement().getDependencies()) {
				if(dependency.getArtifactId().toUpperCase().contains("EXECUTERULE")) {
					System.out.println("");
				}
				if (listaServiciosBuscarEnPom.stream().anyMatch(dependency.getArtifactId()::contains)){
					Map<String, String> llamantes = new LinkedHashMap<String, String>();
		    		if(mapaActivoLlamantes.get(dependency.getArtifactId()) != null){
		    			llamantes = mapaActivoLlamantes.get(dependency.getArtifactId());
		    		}
		    		llamantes.put(nombreServicio, dependency.getArtifactId() + " -> " + dependency.getVersion());
	    			mapaActivoLlamantes.put(dependency.getArtifactId(), llamantes);
	    			encontrado = true;
	    			System.out.println(dir + " -> " + dependency.getVersion());
					break;
				}
			}
		}
		if (!encontrado && model.getDependencies() != null) {
			for(Dependency dependency: model.getDependencies()) {
				if (listaServiciosBuscarEnPom.stream().anyMatch(dependency.getArtifactId()::contains)){
					Map<String, String> llamantes = new LinkedHashMap<String, String>();
		    		if(mapaActivoLlamantes.get(dependency.getArtifactId()) != null){
		    			llamantes = mapaActivoLlamantes.get(dependency.getArtifactId());
		    		}
		    		llamantes.put(nombreServicio, dependency.getArtifactId() + " -> " + dependency.getVersion());
	    			mapaActivoLlamantes.put(dependency.getArtifactId(), llamantes);
	    			System.out.println(dir + " -> " + dependency.getVersion());
					break;
				}
			}
		}
	}
	
	private static void buscarEnCNT(File dir) throws Exception{
		// hemos llegado al java, lo abrimos y vemos la versión que tiene
		BufferedReader br = new BufferedReader(new FileReader(dir));
		try {
			String line = br.readLine();
			
		    while (line != null) {
		    	
		    	for(String activo: listaServiciosBuscarEnPom){
		    		if(line.contains(activo) && !line.contains("com.telefonica.") && !line.contains("-facade") && !line.contains("-msg") && !line.contains("-service")){
		    			
		    			String linea = line;
		    			
		    			String nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);
			    		nombreServicio = nombreServicio.substring(nombreServicio.indexOf("\\") + 1);
			    		String nombreDependencia = line.substring(line.indexOf(">")+1, line.indexOf("</"));
			    		
			    		if(!nombreServicio.equals(nombreDependencia)){
			    			String version = "";
				    		line = br.readLine();
				    		while(line != null) {
				    			if(line.toUpperCase().contains("<VERSION")){
				    				version = line.substring(line.indexOf(">")+1, line.indexOf("</"));
				    				break;
				    			}
				    			line = br.readLine();
				    		}
				    		if(!nombreServicio.contains(nombreDependencia) && !nombreServicio.contains("_temp")){
				    			//mapa.add(nombreServicio + " -- " + nombreDependencia + "-V" + version);
				    		}
				    		//mapa.add(nombreServicio + " -- " + nombreDependencia + "-V" + version);
			    		}
		    			
			    		Map<String, String> llamantes = new LinkedHashMap<String, String>();
			    		if(mapaActivoLlamantes.get(activo) != null){
			    			llamantes = mapaActivoLlamantes.get(activo);
			    		}
			    		//llamantes.add(nombreServicio);
			    		llamantes.put(nombreServicio, linea);
		    			mapaActivoLlamantes.put(activo, llamantes);
		    		}
		    	}
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	/*if(listaServiciosBuscarEnPom.parallelStream().anyMatch(line::contains)){
		    		String nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);
		    		nombreServicio = nombreServicio.substring(nombreServicio.indexOf("\\") + 1);
		    		String nombreDependencia = line.substring(line.indexOf(">")+1, line.indexOf("</"));
		    		
		    		if(!nombreServicio.equals(nombreDependencia)){
		    			String version = "";
			    		line = br.readLine();
			    		while(line != null) {
			    			if(line.toUpperCase().contains("<VERSION")){
			    				version = line.substring(line.indexOf(">")+1, line.indexOf("</"));
			    				break;
			    			}
			    			line = br.readLine();
			    		}
			    		if(!nombreServicio.contains(nombreDependencia) && !nombreServicio.contains("_temp")){
			    			mapa.add(nombreServicio + " -- " + nombreDependencia + "-V" + version);
			    		}
			    		//mapa.add(nombreServicio + " -- " + nombreDependencia + "-V" + version);
		    		}
		    	}*/
		        line = br.readLine();
		    }
		} finally {
		    br.close();
		}
	}
	
	private static void buscarEnTerc(File dir) throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader(dir));
		try {
		    String line = br.readLine();

		    while (line != null) {
		    	
		    	for(String activo: listaServiciosBuscarEnPom){
		    		if(line.contains(activo) && !line.contains("com.telefonica.") && !line.contains("-facade") && !line.contains("-msg") && !line.contains("-service")){
		    			String linea = line;
		    			
		    			String nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);
			    		//mapa.add(nombreServicio + " -- " + line);
			    		
			    		Map<String, String> llamantes = new LinkedHashMap<String, String>();
			    		if(mapaActivoLlamantes.get(activo) != null){
			    			llamantes = mapaActivoLlamantes.get(activo);
			    		}
			    		//llamantes.add(nombreServicio);
			    		llamantes.put(nombreServicio, linea);
		    			mapaActivoLlamantes.put(activo, llamantes);
		    		}
		    	}
		    	
		    	
		    	
		    	
		    	/*if(listaServiciosBuscarEnPom.parallelStream().anyMatch(line::contains) && line.contains("namespace>")){
		    		String nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);
		    		mapa.add(nombreServicio + " -- " + line);
		    	}*/
		        line = br.readLine();
		    }
		} finally {
		    br.close();
		}
	}
	
	
	private static void prueba(File dir) throws Exception {
		if(dir.getAbsolutePath().contains("InitSPReqOrderFulfillment")) {
			System.out.println("");
		}
		if(!dir.getAbsolutePath().toUpperCase().contains("MIGRADO") && !dir.getAbsolutePath().toUpperCase().contains("_TEMP")) {
			if(dir.isDirectory() && dir.getName().toUpperCase().equals("BRANCHES")) {
				try {
					File opFile = null;
					if(dir.listFiles().length > 1) {
						
						File[] hola = dir.listFiles();
						opFile = hola[0];
						
						String versionAlta = hola[0].getName();
						versionAlta = versionAlta.substring(1, versionAlta.indexOf("_"));
						
						int majorAlta = Integer.parseInt(versionAlta.split("\\.")[0]);
						int minorAlta = Integer.parseInt(versionAlta.split("\\.")[1]);
						
						for(int i=1; i<hola.length; i++) {
							File file = hola[i];
							String version2 = file.getName().substring(1, file.getName().indexOf("_"));
							
							int majorRecorriendo = Integer.parseInt(version2.split("-|\\.")[0]);
							int minorRecorriendo = Integer.parseInt(version2.split("-|\\.")[1]);
							
							if(majorRecorriendo > majorAlta){
								majorAlta = majorRecorriendo;
								minorAlta = minorRecorriendo;
								opFile = file;
							}else if(majorRecorriendo == majorAlta && minorRecorriendo > minorAlta){
								minorAlta = minorRecorriendo;
								opFile = file;
							}
						}
		    	        prueba(opFile);
		    	        encontrado2 = true;
			    	}else if(dir.listFiles().length == 1) {
			    		opFile = dir.listFiles()[0];
			    		prueba(opFile);
			    		encontrado2 = true;
				    }else {
				    	encontrado2 = false;
				    }
				}catch(Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
			}else if(dir.isDirectory() && dir.getName().toUpperCase().equals("TRUNK")) {
				if(!encontrado2) {
					for (File file : dir.listFiles()) {
						prueba(file);
					}
				}
				encontrado2 = false; 
			}else if(dir.isDirectory()) {
				for(File file : dir.listFiles()) {
					prueba(file);
				}
			}else {
				if(dir.getAbsolutePath().contains("SOA")
						&& dir.getAbsolutePath().contains("srv-nuc-soa")
						//&& !dir.getAbsolutePath().contains("tags")
						//&& !dir.getAbsolutePath().contains("1911")
						&& !dir.getAbsolutePath().contains("service")
						&& !dir.getAbsolutePath().contains("facade")
						&& !dir.getAbsolutePath().contains("msg")
						&& !dir.getAbsolutePath().contains("-webapp")
						&& dir.getName().equalsIgnoreCase("composite.xml")){
					
					try {
						System.out.println("SOA - " + dir.getAbsolutePath());
						buscarEnSOA(dir);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					
				} else if((dir.getAbsolutePath().contains("jt-nuc-jee")
								|| dir.getAbsolutePath().contains("res-nuc-jee")
								|| dir.getAbsolutePath().contains("srv-nuc-jee")
								|| dir.getAbsolutePath().contains("srv-pres")
							)
						//&& !dir.getAbsolutePath().contains("tags")
						//&& !dir.getAbsolutePath().contains("1911")
						&& !dir.getAbsolutePath().contains("service")
						&& !dir.getAbsolutePath().contains("facade")
						&& !dir.getAbsolutePath().contains("msg")
						&& !dir.getAbsolutePath().contains("-webapp")
						&& dir.getName().equalsIgnoreCase("pom.xml")){
					try {
						System.out.println("JAVA - " + dir.getAbsolutePath());
						buscarEnJava(dir);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				} else if(dir.getAbsolutePath().contains("TERC") && dir.getAbsolutePath().contains("bs")
						&& (dir.getAbsolutePath().contains("jt-nuc-jee")
								|| dir.getAbsolutePath().contains("res-nuc-jee")
								|| dir.getAbsolutePath().contains("srv-nuc-jee")
								|| dir.getAbsolutePath().contains("srv-nuc-soa")
								|| dir.getAbsolutePath().contains("srv-pres")
							)
						//&& !dir.getAbsolutePath().contains("tags")
						//&& !dir.getAbsolutePath().contains("1911")
						&& !dir.getAbsolutePath().contains("service")
						&& !dir.getAbsolutePath().contains("facade")
						&& !dir.getAbsolutePath().contains("msg")
						&& !dir.getAbsolutePath().contains("-webapp")
						//&& !dir.getName().contains("SPServiceSPIntOrchestration")
						&& dir.getName().contains(".bix")){
					
					try {
						System.out.println("TERC - " + dir.getAbsolutePath());
						buscarEnTerc(dir);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				} else if((dir.getAbsolutePath().contains("jt-nuc-jee")
								|| dir.getAbsolutePath().contains("res-nuc-jee")
								|| dir.getAbsolutePath().contains("srv-nuc-jee")
								|| dir.getAbsolutePath().contains("srv-nuc-soa")
								|| dir.getAbsolutePath().contains("srv-pres")
							)
						//&& !dir.getAbsolutePath().contains("tags")
						//&& !dir.getAbsolutePath().contains("1911")
						&& dir.getAbsolutePath().contains("cnt")
						&& !dir.getAbsolutePath().contains("service")
						&& !dir.getAbsolutePath().contains("facade")
						&& !dir.getAbsolutePath().contains("msg")
						&& dir.getAbsolutePath().contains("-webapp")
						&& dir.getName().equalsIgnoreCase("pom.xml")){
					
					try {
						System.out.println("CNT - " + dir.getAbsolutePath());
						buscarEnCNT(dir);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				}
				
				
				//buscarEnSOA(dir);
				//buscarEnJava(dir);
				//fetchFiles(dir);
				//System.out.println(dir.getAbsolutePath());
			}
		}
	}
	
	

	public static void main(String[] args) throws Exception {
		
		// SI SE QUIERE BUSCAR EN ALGO QUE NO SEA SOA, LLAMAR A fetchFiles(file) EN VEZ DE prueba(file)
		
		listaServiciosBuscarEnPom.add("dao-SPOrderSpec");
		
		List<String> listaRutas = new ArrayList<String>();
		// RUTAS INFA
		//listaRutas.add(RUTA_INFA_NUC_JEE);
		/*listaRutas.add(RUTA_INFA_CGT);
		listaRutas.add(RUTA_INFA_CGT_NODE);
		listaRutas.add(RUTA_INFA_CNT);
		listaRutas.add(RUTA_INFA_CNT_NODE);
		listaRutas.add(rutaINFASOA);
		listaRutas.add(RUTA_INFA_RES_NUC_JEE);
		listaRutas.add(RUTA_INFA_PRES);
		//listaRutas.add(rutaINFASOA);
		listaRutas.add(RUTA_INFA_NUC_JEE);
		listaRutas.add(RUTA_INFA_JT_NUC_JEE);
		listaRutas.add(RUTA_INFA_RES_NUC_JEE);*/
		
		listaRutas.add(RUTA_PRTE_NUC_JEE);
		//listaRutas.add(RUTA_PRTE_JT_NUC_JEE);
		//listaRutas.add(RUTA_PRTE_RES_NUC_JEE);
		//listaRutas.add(rutaPRTESOA);
		//listaRutas.add(rutaTERCSOA);
		
		//listaRutas.add(RUTA_INFA_CNT);
		
		// RUTAS PRTE
		//listaRutas.add(RUTA_PRTE_TODO);
		//listaRutas.add(RUTA_INFA_TODO);
		//listaRutas.add(RUTA_TERC_TODO);
		/*listaRutas.add(RUTA_PRTE_PRES);
		listaRutas.add(rutaPRTESOA);*/
		//listaRutas.add(rutaINFASOA);
		//listaRutas.add(rutaINFASOA);
		
//		listaRutas.add(RUTA_INFA_TODO);
//		listaRutas.add(RUTA_PRTE_TODO);
//		listaRutas.add(RUTA_TERC_TODO);
		
		//listaRutas.add(RUTA_INFA_NUC_JEE);
		/*listaRutas.add(RUTA_INFA_JT_NUC_JEE);
		listaRutas.add(RUTA_INFA_RES_NUC_JEE);
		
		listaRutas.add(RUTA_PRTE_NUC_JEE);
		listaRutas.add(RUTA_PRTE_JT_NUC_JEE);
		listaRutas.add(RUTA_PRTE_RES_NUC_JEE);
		
		listaRutas.add(RUTA_TERC_NUC_JEE);
		listaRutas.add(RUTA_TERC_JT_NUC_JEE);
		listaRutas.add(RUTA_TERC_RES_NUC_JEE);*/
		
		// RUTAS TERC
		/*listaRutas.add(RUTA_TERC_NUC_JEE);*/
		
		File file = null;
		for(String ruta: listaRutas){
			file = new File(ruta);
			rutaRecorriendo = ruta;
			//mapa.add(ruta);
			//fetchFiles(file);
			prueba(file);
			//mapa.add(" ");
		}
		
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		
		int i=0;
		XSSFRow fila = sheet.createRow(i);
		for (Entry<String, Map<String, String>> entry : mapaActivoLlamantes.entrySet()) {
			
			//
			fila.createCell(0).setCellValue(entry.getKey());
			
			//System.out.println(entry.getKey());
			for(Entry<String, String> mapa: entry.getValue().entrySet()){
				
			//}
			//for(String activo: entry.getValue()){
				
				fila.createCell(1).setCellValue(mapa.getKey());
				fila.createCell(2).setCellValue(mapa.getValue());
				i++;
				fila = sheet.createRow(i);
				
				//System.out.println(activo);
			}
			System.out.println("");
		}
		
		File dirDestino = new File("C:\\Users\\sergy\\OneDrive\\Documentos\\busquedaDAO_SPOrderSpec.xlsx");
		if(!dirDestino.exists()) {
			dirDestino.createNewFile();
		}
		FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
		
		
		
//		for(String i : mapa){
//			System.out.println(i);
//		}
	}
}