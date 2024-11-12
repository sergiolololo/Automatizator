package com.telefonica.nomodulos.nucleo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class BuscarEnJavas {

	private static String rutaINFANuc = "C:\\T718467\\CODI\\INFA\\1_CO\\srv-nuc-jee";
	private static String rutaINFAPres = "C:\\T718467\\CODI\\INFA\\1_CO\\srv-pres";
	private static String rutaPRTENuc = "C:\\T718467\\CODI\\PRTE\\1_CO\\srv-nuc-jee";
	private static String rutaPRTEDao = "C:\\T718467\\REGENERACIONES\\PRTE\\1_CO\\dao\\dao-SPOrder\\tags";
	private static String rutaTERCuc = "C:\\T718467\\CODI\\TERC\\1_CO\\srv-nuc-jee";
	
	private static Set<String> resultados = new LinkedHashSet<String>();
	private static Set<String> resultadosCompletos = new LinkedHashSet<String>();
	private static Set<String> listaRutasEncontradas = new LinkedHashSet<String>();
	
	private static String ramaBuscar = "trunk";
	private static String ramaBuscar2 = "branches";
	private static String ramaNoBuscar = "tags";
	
	private static boolean encontrado2 = false;
	private static boolean buscarRecursivo = false;
	
	private static List<File> listaRutasbuscar = new ArrayList<File>();
	
	private static List<String> LISTA_PALABRAS_BUSCAR = Arrays.asList(
			"findSpinaSpMessageByCharIdAndCharValue".toUpperCase());
	
	public static void main(String[] args) throws Exception {
        
		String palabraBuscar = "findSpinaSpMessageByCharIdAndCharValue";
		//listaRutasEncontradas.add("C:\\T718467\\CODI\\INFA\\1_CO\\srv-nuc-jee\\srv-nuc-jee-SPMessageMngLocal\\branches\\v4.0.0_PESP_2309_v1\\src\\main\\java\\com\\telefonica\\infa\\srv\\nuc\\spmessagemnglocal\\service\\getspmessage\\GetSPMessageCommand.java");
		
		listaRutasbuscar.add(new File(rutaINFANuc));
        //listaRutasbuscar.add(new File(rutaINFAPres));
        //listaRutasbuscar.add(new File(rutaINFAPres));
        //listaRutasbuscar.add(new File(rutaPRTENuc));
        //listaRutasbuscar.add(new File(rutaTERCuc));
        
        for(File ruta: listaRutasbuscar){
        	fetchFiles2(ruta, palabraBuscar, "");   
        }
        
        for(String linea: resultados){
            System.out.println(linea);
        }
        for(String linea: resultadosCompletos){
            System.out.println(linea);
        }
        
        List<String> lineasPintar = new ArrayList<>();
        lineasPintar.addAll(resultados);
        lineasPintar.add("");
        lineasPintar.addAll(resultadosCompletos);
        
        File file = new File("C:\\Users\\sergy\\OneDrive\\Documentos\\resultadosBusqueda_findSPOrderDataByData.txt");
        if(!file.exists()) {
        	file.createNewFile();
        }
        Path pathTest = Paths.get(file.getAbsolutePath());
        
        Files.write(pathTest, lineasPintar, StandardOpenOption.WRITE);
        System.out.println("C:\\Users\\sergy\\OneDrive\\Documentos\\resultadosBusqueda_findSPOrderDataByData.txt");
    }
	
	
	private static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {

		if (dir.isDirectory() && !dir.getAbsolutePath().contains(ramaNoBuscar) && !dir.getAbsolutePath().contains("tags")) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
			}
		} else {
			if((dir.getAbsolutePath().contains(ramaBuscar) || dir.getAbsolutePath().contains(ramaBuscar2)) 
					&& ((dir.getAbsolutePath().contains("service") 
					&& dir.getAbsolutePath().endsWith(".java")))
					){
	    		  
				// hemos llegado al java, lo abrimos y vemos la versión que tiene
				BufferedReader br = new BufferedReader(new FileReader(dir));
				try {
				    String line = br.readLine();
				    
				    while (line != null) {
				    	if (LISTA_PALABRAS_BUSCAR.stream().anyMatch(line.toUpperCase()::contains)){
				    		
				    		String ruta = "";
				    		if(dir.getAbsolutePath().contains(rutaINFANuc)){
				    			ruta = dir.getAbsolutePath().substring(rutaINFANuc.length() + 1);
				    		}else if(dir.getAbsolutePath().contains(rutaPRTENuc)){
				    			ruta = dir.getAbsolutePath().substring(rutaPRTENuc.length() + 1);
				    		}else if(dir.getAbsolutePath().contains(rutaTERCuc)){
				    			ruta = dir.getAbsolutePath().substring(rutaTERCuc.length() + 1);
				    		}else{
				    			ruta = dir.getAbsolutePath().substring(rutaINFAPres.length() + 1);
				    		}
				    		if(dir.getAbsolutePath().contains(ramaBuscar)){
				    		    ruta = ruta.substring(0, ruta.indexOf("\\" + ramaBuscar));    
				    		}else{
				    		    ruta = ruta.substring(0, ruta.indexOf("\\" + ramaBuscar2));
				    		}
				    		//ruta = dir.getAbsolutePath();
				    		
				    		resultados.add(ruta);
				    		resultadosCompletos.add(dir.getAbsolutePath());
				    	}
				        line = br.readLine();
				    }
				} finally {
				    br.close();
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				/*
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
				*/
			}
		}
	}
	
	/*private static void fetchFilesConNombreServicio(File dir, String versionNucleo, String nombreServicio) throws SAXException, IOException, ParserConfigurationException {

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
	}*/

	private static void fetchFiles2(File dir, String palabraBuscar, String nivelLlamada) throws Exception {
		if(!dir.getAbsolutePath().toUpperCase().contains("MIGRADO")) {
			if(dir.isDirectory() && dir.getName().toUpperCase().equals("BRANCHES")) {
				try {
					fetchLastVersion(dir, palabraBuscar, nivelLlamada);
				}catch(Exception e) {
					System.out.println(dir.getAbsolutePath() + " -> " + e.getLocalizedMessage());
				}
			}else if(dir.isDirectory() && dir.getName().toUpperCase().equals("TRUNK")) {
				if(!encontrado2) {
					System.out.println(dir.getAbsolutePath());
					Arrays.asList(dir.listFiles()).stream().forEach(file -> {
						try {
							fetchFiles2(file, palabraBuscar, nivelLlamada);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
				encontrado2 = false; 
			}else if(dir.isDirectory()) {
				Arrays.asList(dir.listFiles()).stream().forEach(file -> {
					try {
						fetchFiles2(file, palabraBuscar, nivelLlamada);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}else if((dir.getName().endsWith("Command.java") || dir.getName().endsWith("Mapper.java") || dir.getName().endsWith("Impl.java")) 
					&& !listaRutasEncontradas.stream().anyMatch(dir.getAbsolutePath()::contains)) {
				buscarEnJava(dir, palabraBuscar, nivelLlamada);
			}
		}
	}
	
	private static void fetchLastVersion(File dir, String palabraBuscar, String nivelLlamada) throws Exception {
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
			System.out.println(opFile.getAbsolutePath());
	        fetchFiles2(opFile, palabraBuscar, nivelLlamada);
	        encontrado2 = true;
    	}else if(dir.listFiles().length == 1) {
    		opFile = dir.listFiles()[0];
    		fetchFiles2(opFile, palabraBuscar, nivelLlamada);
    		encontrado2 = true;
	    }else {
	    	encontrado2 = false;
	    }
	}
	
	private static void buscarEnJava(File dir, String palabraBuscar, String nivelLlamada) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(dir));
		try {
			String operacionDondeLoEncuentra = "";
			
		    String line = br.readLine();
		    if(line.startsWith("package")) {
		    	operacionDondeLoEncuentra = line.substring(line.indexOf("com"), line.length()-1) + ".";
		    	operacionDondeLoEncuentra = operacionDondeLoEncuentra.replace(".service.", ".msg.");
		    }
		    
		    if(dir.getAbsolutePath().contains("FindBasicSPOrderItem")) {
		    	System.out.println("");
		    }
		    
		    boolean encontrado = false;
		    while (line != null) {
		    	
		    	if(line.toUpperCase().contains(palabraBuscar.toUpperCase()) && !encontrado) {
		    		
		    		
		    		
		    		encontrado = true;
		    		//break;
		    	}
		    	if(encontrado) {
		    		String ruta = "";
		    		if(dir.getAbsolutePath().contains(rutaINFANuc)){
		    			ruta = dir.getAbsolutePath().substring(rutaINFANuc.length() + 1);
		    		}else if(dir.getAbsolutePath().contains(rutaPRTENuc)){
		    			ruta = dir.getAbsolutePath().substring(rutaPRTENuc.length() + 1);
		    		}else if(dir.getAbsolutePath().contains(rutaTERCuc)){
		    			ruta = dir.getAbsolutePath().substring(rutaTERCuc.length() + 1);
		    		}else{
		    			ruta = dir.getAbsolutePath().substring(rutaINFAPres.length() + 1);
		    		}
		    		ruta = ruta.substring(0, ruta.indexOf("\\")) + "." + dir.getAbsolutePath().substring(dir.getAbsolutePath().lastIndexOf("\\")+1);
		    		
		    		if(dir.getAbsolutePath().endsWith("Command.java")) {
		    			ruta = ruta.substring(0, ruta.lastIndexOf("Command.java"));	
		    		}else if(dir.getAbsolutePath().endsWith("Mapper.java")) {
		    			ruta = ruta.substring(0, ruta.lastIndexOf("Mapper.java"));
		    		}
		    		
		    		resultados.add(nivelLlamada + ruta);
		    		resultadosCompletos.add(nivelLlamada + dir.getAbsolutePath());
		    		
		    		String ruta1 = dir.getAbsolutePath().substring(0, dir.getAbsolutePath().lastIndexOf("\\"));
		    		listaRutasEncontradas.add(ruta1);
		    		break;
		    	}
		        line = br.readLine();
		    }
		    if(encontrado && buscarRecursivo
		    		) {
		    	
		    	for(File ruta: listaRutasbuscar) {
		    		fetchFiles2(ruta, operacionDondeLoEncuentra, nivelLlamada + "   ");	
		    	}
		    }
		} finally {
		    br.close();
		}
	}
}