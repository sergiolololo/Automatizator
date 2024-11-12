package com.telefonica.nomodulos.nucleo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BuscarEnPomsCascada {

	/*private static String RUTA_INFA_NUC_JEE = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\NUCLEO";
	private static String RUTA_INFA_CGT = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\cgt";
	private static String RUTA_INFA_CGT_NODE = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\cgt-node";
	private static String RUTA_INFA_CNT = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\cnt";
	private static String RUTA_INFA_CNT_NODE = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\cnt-node";
	private static String RUTA_INFA_RES_NUC_JEE = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\res-nuc-jee";
	private static String RUTA_INFA_PRES = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\srv-pres";
	private static String rutaINFANucCliews = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\CLIEWS";*/
	
	
	private static String RUTA_PRTE_NUC_JEE = "C:\\t718467\\workspace\\CODI\\PRTE\\srv-nuc-jee";
	private static String RUTA_PRTE_JT_NUC_JEE = "C:\\t718467\\workspace\\CODI\\PRTE\\jt-nuc-jee";
	private static String RUTA_PRTE_RES_NUC_JEE = "C:\\t718467\\workspace\\CODI\\PRTE\\res-nuc-jee";
	/*private static String RUTA_PRTE_PRES = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\srv-pres";
	private static String rutaPRTENucCliews = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\CLIEWS";*/
	
	/*private static String rutaPRTESOA = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\SOA";
	private static String rutaINFASOA = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\SOA";*/
	
	/*private static String RUTA_TERC_NUC_JEE = "C:\\t718467\\workspace\\CODIFICACION\\TERC\\srv-nuc-jee";
	private static String RUTA_TERC_EXP = "C:\\t718467\\workspace\\CODIFICACION\\TERC\\srv-exp-osb";*/
	
	private static String ramaBuscar = "trunk";
	private static String ramaNoBuscar = "tags";
	private static List<String> mapa = new ArrayList<String>();
	//private static List<String> listaServiciosBuscarEnPom = new ArrayList<String>();
	private static String rutaRecorriendo = null;
	
	private static List<String> listaRutas = new ArrayList<String>();
	private static Set<String> listaServicios = new HashSet<String>();
	private static List<String> listaServiciosBuscarEnPomInicial = new ArrayList<String>();
	
	private static String nombreDAO = "dao-SPOrder";
	
	public static void fetchFiles(File dir, List<String> listaServiciosBuscarEnPom) throws SAXException, IOException, ParserConfigurationException {
		
		if (dir.isDirectory() && !dir.getAbsolutePath().contains(ramaNoBuscar) && !dir.getAbsolutePath().contains("tags")) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1, listaServiciosBuscarEnPom);
			}
		} else if(dir.getAbsolutePath().contains(ramaBuscar) && listaServiciosBuscarEnPom.parallelStream().anyMatch(dir.getAbsolutePath()::contains)) {
			if(dir.getAbsolutePath().contains(ramaBuscar)
					&& 
					!dir.getAbsolutePath().contains(ramaNoBuscar)
					//&& !dir.getAbsolutePath().contains("tags")
					//&& !dir.getAbsolutePath().contains("1911")
					&& !dir.getAbsolutePath().contains("service")
					&& !dir.getAbsolutePath().contains("facade")
					&& !dir.getAbsolutePath().contains("msg")
					&& !dir.getAbsolutePath().contains("-webapp")
					&& dir.getName().equalsIgnoreCase("pom.xml")){
				
				try {
					buscarEnJava(dir, listaServiciosBuscarEnPom);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void fetchFilesHijas(File dir, String rutaServicio) throws SAXException, IOException, ParserConfigurationException {

		
		if (dir.isDirectory() && !dir.getAbsolutePath().contains(ramaNoBuscar) && !dir.getAbsolutePath().contains("tags")) {
			for (File file1 : dir.listFiles()) {
				fetchFilesHijas(file1, rutaServicio);
			}
		} else if(dir.getAbsolutePath().contains(ramaBuscar) && dir.getAbsolutePath().contains(rutaServicio + "\\")) {
			if(dir.getAbsolutePath().contains(ramaBuscar)
					&& 
					!dir.getAbsolutePath().contains(ramaNoBuscar)
					//&& !dir.getAbsolutePath().contains("tags")
					//&& !dir.getAbsolutePath().contains("1911")
					&& !dir.getAbsolutePath().contains("service")
					&& !dir.getAbsolutePath().contains("facade")
					&& !dir.getAbsolutePath().contains("msg")
					&& !dir.getAbsolutePath().contains("-webapp")
					&& dir.getName().equalsIgnoreCase("pom.xml")){
				
				try {
					buscarEnJavaHijas(dir, rutaServicio);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void buscarEnJava(File dir, List<String> listaServiciosBuscarEnPom) throws Exception{
		// hemos llegado al java, lo abrimos y vemos la versión que tiene
		BufferedReader br = new BufferedReader(new FileReader(dir));
		try {
			String line = br.readLine();
			boolean encontradoPadre = false;
			String nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);
			Set<String> listaDependenciasHijas = new HashSet<String>();
		    while (line != null) {
		    	if(line.contains("srv-nuc-jee-") && !line.contains(nombreServicio) && !listaServiciosBuscarEnPom.parallelStream().anyMatch(line::contains)){
		    		String nombreDependenciaHija = line.substring(line.indexOf(">")+1, line.indexOf("</"));
		    		if(!listaServicios.parallelStream().anyMatch(nombreDependenciaHija::contains) && !listaServiciosBuscarEnPomInicial.parallelStream().anyMatch(nombreDependenciaHija::contains)){
		    			listaDependenciasHijas.add(nombreDependenciaHija);
		    		}
		    	}
		    	
		    	if(listaServiciosBuscarEnPom.parallelStream().anyMatch(line::contains)){
		    		String nombreDependencia = line.substring(line.indexOf(">")+1, line.indexOf("</"));
		    		
		    		if(!dir.getAbsolutePath().contains(nombreDependencia)){
		    			String version = "";
			    		line = br.readLine();
			    		
			    		while(line != null) {
			    			/*if(line.toUpperCase().contains("<DEPENDENCIES")){
			    				encontradoTagDependencies = true;
			    			}*/
			    			
			    			if(line.toUpperCase().contains("<VERSION")){
			    				version = line.substring(line.indexOf(">")+1, line.indexOf("</"));
			    				break;
			    			}
			    			line = br.readLine();
			    		}
			    		if(!nombreDependencia.contains(nombreServicio) && !nombreServicio.contains("_tem")){
			    			encontradoPadre = true;
			    			mapa.add(nombreServicio + " -- " + nombreDependencia + "-V" + version);
			    			listaServicios.add(nombreServicio);
			    			
			    			//File parthRecorriendo = new File(rutaRecorriendo);
			    			List<String> listaServiciosBuscar = new ArrayList<String>();
			    			listaServiciosBuscar.add(nombreServicio);
			    			//fetchFiles(parthRecorriendo, listaServiciosBuscar);
			    			
			    			
			    			File file = null;
			    			mapa.add("--------- Tiene como padres ---------");
			    			int registrosAntes = mapa.size();
			    			for(String ruta: listaRutas){
			    				file = new File(ruta);
			    				rutaRecorriendo = ruta;
			    				//mapa.add(ruta);
			    				fetchFiles(file, listaServiciosBuscar);
			    				//mapa.add(" ");
			    			}
			    			if(registrosAntes < mapa.size()){
			    				mapa.add("--------- Terminan los padres ---------");
				    			//mapa.add("");
			    			}else{
			    				mapa.remove(mapa.size()-1);
			    			}
			    		}
			    		//mapa.add(nombreServicio + " -- " + nombreDependencia + "-V" + version);
		    		}
		    	}
		        line = br.readLine();
		    }
		    /*if(listaDependenciasHijas.size() > 0 && encontradoPadre){
		    	List<String> dependenciashijas = new ArrayList<String>();
		    	dependenciashijas.addAll(listaDependenciasHijas);
		    	File file = null;
		    	mapa.add("--------- Tiene como hijas ---------");
		    	for(String ruta: listaRutas){
    				file = new File(ruta);
    				rutaRecorriendo = ruta;
    				//mapa.add(ruta);
    				fetchFiles(file, dependenciashijas);
    				//mapa.add(" ");
    			}
    			mapa.add("--------- Terminan las hijas ---------");
		    }*/
		} finally {
		    br.close();
		}
	}
	
	
	private static void buscarEnJavaHijas(File dir, String rutaServicio) throws Exception{
		// hemos llegado al java, lo abrimos y vemos la versión que tiene
		BufferedReader br = new BufferedReader(new FileReader(dir));
		try {
			String line = br.readLine();
			boolean encontradoPadre = false;
			String nombreServicio = dir.getAbsolutePath().substring(rutaRecorriendo.length()+1, dir.getAbsolutePath().indexOf(ramaBuscar)-1);
			Set<String> listaDependenciasHijas = new HashSet<String>();
			//boolean encontradoDAO = false;
			mapa.add("<Dependencias>");
		    while (line != null) {
		    	if(line.contains("srv-nuc-jee-") && !line.contains(nombreServicio) && !line.contains(rutaServicio)){
		    		String nombreDependenciaHija = line.substring(line.indexOf(">")+1, line.indexOf("-service"));
		    		if(!listaServicios.parallelStream().anyMatch(nombreDependenciaHija::contains) && !listaServiciosBuscarEnPomInicial.parallelStream().anyMatch(nombreDependenciaHija::contains)){
		    			listaDependenciasHijas.add(nombreDependenciaHija);
		    			listaServicios.add(nombreDependenciaHija);
		    			mapa.add(nombreDependenciaHija);
		    			
		    			File file = null;
		    			
		    			for(String ruta: listaRutas){
		    				file = new File(ruta);
		    				rutaRecorriendo = ruta;
		    				fetchFilesHijas(file, nombreDependenciaHija);
		    			}
		    		}
		    	}
		    	if(line.contains(nombreDAO)){
		    		mapa.add(line.trim());
		    		//encontradoDAO = true;
		    	}
		    	
		    	/*if(listaServiciosBuscarEnPom.parallelStream().anyMatch(line::contains)){
		    		String nombreDependencia = line.substring(line.indexOf(">")+1, line.indexOf("</"));
		    		
		    		if(!dir.getAbsolutePath().contains(nombreDependencia)){
		    			String version = "";
			    		line = br.readLine();
			    		
			    		while(line != null) {
			    			
			    			if(line.toUpperCase().contains("<VERSION")){
			    				version = line.substring(line.indexOf(">")+1, line.indexOf("</"));
			    				break;
			    			}
			    			line = br.readLine();
			    		}
			    		if(!nombreDependencia.contains(nombreServicio) && !nombreServicio.contains("_tem")){
			    			encontradoPadre = true;
			    			mapa.add(nombreServicio + " -- " + nombreDependencia + "-V" + version);
			    			listaServicios.add(nombreServicio);
			    			
			    			//File parthRecorriendo = new File(rutaRecorriendo);
			    			List<String> listaServiciosBuscar = new ArrayList<String>();
			    			listaServiciosBuscar.add(nombreServicio);
			    			//fetchFiles(parthRecorriendo, listaServiciosBuscar);
			    			
			    			
			    			File file = null;
			    			mapa.add("--------- Tiene como padres ---------");
			    			int registrosAntes = mapa.size();
			    			for(String ruta: listaRutas){
			    				file = new File(ruta);
			    				rutaRecorriendo = ruta;
			    				//mapa.add(ruta);
			    				fetchFiles(file, listaServiciosBuscar);
			    				//mapa.add(" ");
			    			}
			    			if(registrosAntes < mapa.size()){
			    				mapa.add("--------- Terminan los padres ---------");
				    			//mapa.add("");
			    			}else{
			    				mapa.remove(mapa.size()-1);
			    			}
			    		}
			    		//mapa.add(nombreServicio + " -- " + nombreDependencia + "-V" + version);
		    		}
		    	}*/
		        line = br.readLine();
		    }
		    mapa.add("</Dependencias>");
		    
		    /*if(encontradoDAO){
	    		//encontradoDAO = true;
	    		mapa.add(nombreServicio + " -> Se ha encontrado DAO");
	    	}*/
		    
		    /*if(listaDependenciasHijas.size() > 0){
		    	//List<String> dependenciashijas = new ArrayList<String>();
		    	//dependenciashijas.addAll(listaDependenciasHijas);
		    	File file = null;
		    	//mapa.add("--------- " + nombreServicio + ": tiene en su pom.xml ---------");
		    	for(String ruta: listaRutas){
    				file = new File(ruta);
    				rutaRecorriendo = ruta;
    				//mapa.add(ruta);
    				for(String rutaServ: listaDependenciasHijas){
    					mapa.add("Analizando " + rutaServ);
    					mapa.add("<Dependencias>");
    					fetchFilesHijas(file, rutaServ);
    					mapa.add("</Dependencias>");
    				}
    				//mapa.add(" ");
    			}
		    }*/
		} finally {
		    br.close();
		}
	}
	
	

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		
		//List<String> listaServiciosBuscarEnPom = new ArrayList<String>();
		/*listaServiciosBuscarEnPom.add("srv-nuc-jee-BIFulfillmentMng");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-InitSPReqOrderFulfillment");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-InvokeExecuteRule");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-ManageRuleOpenIncByExcErr");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-ModifySPRepository");*/
		
		/*listaServiciosBuscarEnPomInicial.add("srv-nuc-jee-SPOrderItemMng");
		listaServiciosBuscarEnPomInicial.add("srv-nuc-jee-SPPortabilityOrderItem");*/
		
		
		/*listaServiciosBuscarEnPomInicial.add("srv-nuc-jee-SPOrderMng");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-SPOrderSpecItemMng");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-SPRequisitionOrquestationMng");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-SPServiceInteractionTask");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-SPServiceOrchestration");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-SPServiceTask");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-SPTestTask");
		listaServiciosBuscarEnPom.add("srv-nuc-jee-SupplierPartnerProductMng");*/
		
		
		listaServiciosBuscarEnPomInicial.add("srv-nuc-jee-TrackSPRequisitionOrders");
		listaServiciosBuscarEnPomInicial.add("srv-nuc-jee-TreatmentError");
		listaServiciosBuscarEnPomInicial.add("srv-nuc-jee-TreatmentErrorPortability");
		
		
		
		// RUTAS INFA
		/*listaRutas.add(RUTA_INFA_NUC_JEE);
		listaRutas.add(RUTA_INFA_CGT);
		listaRutas.add(RUTA_INFA_CGT_NODE);
		listaRutas.add(RUTA_INFA_CNT);
		listaRutas.add(RUTA_INFA_CNT_NODE);
		listaRutas.add(RUTA_INFA_RES_NUC_JEE);
		listaRutas.add(RUTA_INFA_PRES);
		listaRutas.add(rutaINFASOA);*/
		
		// RUTAS PRTE
		listaRutas.add(RUTA_PRTE_NUC_JEE);
		listaRutas.add(RUTA_PRTE_JT_NUC_JEE);
		listaRutas.add(RUTA_PRTE_RES_NUC_JEE);
		/*listaRutas.add(RUTA_PRTE_PRES);
		listaRutas.add(rutaPRTESOA);*/
		
		// RUTAS TERC
		/*listaRutas.add(RUTA_TERC_NUC_JEE);*/
		
		File file = null;
		//for(String ruta: listaRutas){
			/*file = new File(ruta);
			rutaRecorriendo = ruta;
			mapa.add(ruta);*/
			
			for(String servicio: listaServiciosBuscarEnPomInicial){
				mapa.add("Analizando padre " + servicio);
				//mapa.add("<Dependencias>");
				
				for(String ruta: listaRutas){
					file = new File(ruta);
					rutaRecorriendo = ruta;
					//mapa.add(ruta);
					fetchFilesHijas(file, servicio);
				}
				//mapa.add("</Dependencias>");
				mapa.add(" ");
			}
			
			//fetchFiles(file, listaServiciosBuscarEnPomInicial);
			//mapa.add(" ");
		//}
		
		for(String i : mapa){
			System.out.println(i);
		}
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		
		for(String i : listaServicios){
			System.out.println(i);
		}
		
	}
}