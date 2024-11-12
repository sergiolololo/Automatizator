package com.telefonica.nomodulos.nucleo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class GenerarFicherosDimensionamiento {

	private static String rutaFicheroDisney = "C:\\Users\\sherrerah\\Desktop\\dss_movistar_es_activation_notifications_20230629000000_UTC.csv";

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        
		PrintWriter writer = new PrintWriter(rutaFicheroDisney, "UTF-8");
		writer.println("is_activated,is_entitled,last_updated,subject_id");
		for(int i=1; i<1001; i++) {
			writer.println("1,1,2023-06-26 09:49:18.929,0000000000000" + i);
		}
		for(int i=1001; i<799751; i++) {
			writer.println("0,0,2023-06-26 09:49:18.929,0000000000000" + i);
		}
		
		
		// cambiar la fecha de los siguientes FOR a una mayor
		for(int i=799751; i<799951; i++) {
			writer.println("1,1,2023-06-27 21:54:18.929,0000000000000" + i);
		}
		// 50 filas existentes 0,0
		for(int i=799951; i<800001; i++) {
			writer.println("0,0,2023-06-27 21:54:18.929,0000000000000" + i);
		}
		// 250 filas nuevas
		for(int i=800001; i<800251; i++) {
			writer.println("1,1,2023-06-27 21:54:18.929,0000000000000" + i);
		}
	    writer.close();
    }
}