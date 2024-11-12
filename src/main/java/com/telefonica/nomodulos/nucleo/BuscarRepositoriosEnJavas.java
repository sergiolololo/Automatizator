package com.telefonica.nomodulos.nucleo;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BuscarRepositoriosEnJavas {

	private static String rutaINFANuc = "C:\\t718467\\workspace\\CODIFICACION\\INFA";
	//private static String rutaINFANucCliews = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\CLIEWS";
	
	private static String rutaPRTENuc = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\NUCLEO";
	//private static String rutaPRTENucCliews = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\CLIEWS";
	
	//private static String rutaPRTESOA = "C:\\t718467\\workspace\\CODIFICACION\\PRTE\\SOA";
	//private static String rutaINFASOA = "C:\\t718467\\workspace\\CODIFICACION\\INFA\\SOA";
	
	private static Set<String> mapa = new HashSet<String>();
	private static Map<String, Set<String>> mapaServicioRepositorios = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> mapaRepositorioConsultas = new HashMap<String, Set<String>>();
	
	private static Map<String, Map<String, Set<String>>> mapita = new HashMap<String, Map<String,Set<String>>>();
	
	private static List<String> repositorios;
	
	public static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {

		
		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
			}
		} else {
			if(dir.getAbsolutePath().contains("trunk") && dir.getAbsolutePath().contains("service") 
					&& dir.getAbsolutePath().contains("impl")
					&& dir.getName().contains(".java")){
	    		  
				// hemos llegado al java, lo abrimos y vemos la versión que tiene
				BufferedReader br = new BufferedReader(new FileReader(dir));
				try {
				    String line = br.readLine();

				    while (line != null) {
				    	
				    	for(String repositorio: repositorios){
				    		
				    		Set<String> listaConsultas = new HashSet<String>();
				    		
				    		// sacamos el nombre del servicio
			    			String nombreServicio = dir.getAbsolutePath().substring(dir.getAbsolutePath().indexOf("srv-nuc-jee-"));
			    			nombreServicio = nombreServicio.substring(0, nombreServicio.indexOf("\\"));
				    		if(line.contains(("." + repositorio))){
				    			if(line.contains("//") && line.indexOf("//") < line.indexOf("." + repositorio)){
				    				System.out.println("Repositorio comentado en código -> " + nombreServicio);
				    			}else{
				    				// comprobamos si el servicio ya está añadido, y en ese caso solo añadimos el nuevo repositorio
					    			Set<String> repositorios = new HashSet<String>();
					    			if(mapaServicioRepositorios.get(nombreServicio) != null){
					    				repositorios = mapaServicioRepositorios.get(nombreServicio);
					    			}
					    			repositorios.add(repositorio);
				    				mapaServicioRepositorios.put(nombreServicio, repositorios);
				    			}
					    	}else if(line.toLowerCase().contains(repositorio.toLowerCase() + ".")){
					    		if(line.contains("//") && line.indexOf("//") < line.indexOf(repositorio.toLowerCase() + ".")){
				    				System.out.println("Acceso a consulta comentado en código -> " + nombreServicio);
				    			}else{
				    				
				    			}
					    	}
				    	}
				    	line = br.readLine();
				    }
				}catch(Exception e) {
					System.out.println(e.getMessage());
				}
				finally {
				    br.close();
				}
			}
		}
	}

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		
		repositorios = ObtenerRepositoriosDeXMI.obtenerRepositorios();
		
		
		
		/*Set<String> hola = new HashSet<String>();
		hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_SPPortabilityOrder");hola.add("SRV_SPPortabilityOrder");hola.add("SRV_SPPortabilityOrder");hola.add("SRV_SPPortabilityOrderItem");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderMng");hola.add("SRV_SPOrderMng");hola.add("SRV_SPOrderMng");hola.add("SRV_SPOrderMng");hola.add("SRV_SPPortabilityOrder");hola.add("SRV_SPPortabilityOrder");hola.add("SRV_SPPortabilityOrder");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_ModifySPRepository");hola.add("SRV_TrackSPRequisitionOrders");hola.add("SRV_SPOrderSpecItemMng");hola.add("SRV_BIFulfillmentMng");hola.add("SRV_ModifySPRepository");hola.add("SRV_SPServiceInteractionTask");hola.add("SRV_SPServiceInteractionTask");hola.add("SRV_SPRequisitionOrquestationTask");hola.add("SRV_SPRequisitionOrquestationTask");hola.add("SRV_SendCustomerMail");hola.add("SRV_SendCustomerMail");hola.add("SRV_ModifySPOrder");hola.add("SRV_ModifyFinancialSPOrder");hola.add("SRV_ModifyFinancialSPOrder");hola.add("SRV_BIFulfillmentMng");hola.add("SRV_BIFulfillmentMng");hola.add("SRV_GetProductOwnerOPHandling");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_ConSPReqPorOrderStatus");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_ResourceManagement");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SPOrderMng");hola.add("SRV_SPOrderMng");hola.add("SRV_SPOrderMng");hola.add("SRV_SPOrderMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_SupplierPartnerProductMng");hola.add("SRV_IdentifySPFulfillment");hola.add("SRV_RegularizationSPOrderItem");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPOrderItemMng");hola.add("SRV_SPServiceInteractionTask");hola.add("SRV_SPServiceInteractionTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_GenerateSPReqOrderFulfillment");hola.add("SRV_GenerateSPReqOrderFulfillment");hola.add("SRV_GenerateSPReqOrderFulfillment");hola.add("SRV_GenerateSPReqOrderFulfillment");hola.add("SRV_GenerateSPReqOrderFulfillment");hola.add("SRV_GenerateSPReqOrderFulfillment");hola.add("SRV_GenerateSPReqOrderFulfillment");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_GenSPReqOrderPortability");hola.add("SRV_TrackSPReqOrdersPortLsnr");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");hola.add("SRV_SPServiceTask");
		
		for(String prueba: hola){
			System.out.println(prueba);	
		}*/
		
		/*File file = new File(rutaPRTENuc);
		fetchFiles(file);*/
		
		File file2 = new File(rutaINFANuc);
		fetchFiles(file2);
		
		
		SortedSet<String> keys = new TreeSet<>(mapaServicioRepositorios.keySet());
		for(String key: keys){
			System.out.println(key);
			
			Set<String> listado = mapaServicioRepositorios.get(key);
			List<String> list = new ArrayList<String>(listado);
	        Collections.sort(list);
	        
	        for(String repositorio: list){
				System.out.println(repositorio);
			}
	        System.out.println(" ");
		}
		
		
		
		
		/*for (Entry<String, Set<String>> entry : mapaServicioRepositorios.entrySet()) {
			System.out.println(entry.getKey());
			for(String repositorio: entry.getValue()){
				System.out.println(repositorio);
			}
			System.out.println(" ");
		}*/
		
		
		
		
		for(String i : mapa){
			System.out.println(i);
		}
	}
}