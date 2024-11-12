package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

public class BorrarDirectorio {

	public static void deleteFolder(File dir) {
		if(!dir.isDirectory()) {
			dir.delete();
		}else {
	        for(File f: dir.listFiles()) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
		}
	}
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		
		List<String> listaRutas = new ArrayList<String>();
		listaRutas.add("C:\\Users\\sherrerah\\Desktop\\DEPENDENCIAS_2309_SH\\1-CO\\cliers");
		listaRutas.add("C:\\Users\\sherrerah\\Desktop\\DEPENDENCIAS_2309_SH\\1-CO\\cliews");
		
		for(String rutaRaiz: listaRutas){
			File file = new File(rutaRaiz);
			for(File rutaActivo: file.listFiles()) {
				for(File rutaArchivosActivo: rutaActivo.listFiles()) {
					if(!rutaArchivosActivo.getName().contains(".svn")) {
						deleteFolder(rutaArchivosActivo);
						
						String ruta0AT = rutaArchivosActivo.getAbsolutePath().replace("1-CO", "0-AT");
						File ruta0ATDondeCoger = new File(ruta0AT);
						
						if(rutaArchivosActivo.isDirectory()) {
							FileUtils.copyDirectory(ruta0ATDondeCoger, rutaArchivosActivo);	
						}else {
							FileUtils.copyFile(ruta0ATDondeCoger, rutaArchivosActivo);
						}
					}
				}
			}
		}
	}
}