package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ObtenerServiciosJavaDesplegables {

	private static String rutaINFANuc = "C:\\T718467\\workspace\\1_CO\\INFA\\1_CO\\srv-nuc-jee";
	private static String rutaPRTENuc = "C:\\T718467\\workspace\\1_CO\\PRTE\\1_CO\\srv-nuc-jee";
	private static String rutaTERCuc = "C:\\T718467\\workspace\\1_CO\\TERC\\1_CO\\srv-nuc-jee";
	
	private static Set<String> resultados = new LinkedHashSet<String>();
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        
        List<File> listaRutasbuscar = new ArrayList<File>();
        listaRutasbuscar.add(new File(rutaINFANuc));
        listaRutasbuscar.add(new File(rutaPRTENuc));
        listaRutasbuscar.add(new File(rutaTERCuc));
        
        for(File ruta: listaRutasbuscar){
        	for(File ruta2: ruta.listFiles()) {
        		fetchFiles(ruta2);	
        	}
        }
        
        for(String linea: resultados){
            System.out.println(linea);
        }
    }
	
	public static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException {
		if (dir.isDirectory()) {
			if(!dir.getAbsolutePath().toUpperCase().contains("LOCAL")) {
				resultados.add(dir.getName());
			}
		}
	}
}