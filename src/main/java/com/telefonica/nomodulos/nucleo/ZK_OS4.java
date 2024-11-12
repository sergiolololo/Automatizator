package com.telefonica.nomodulos.nucleo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ZK_OS4 {
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\ZK\\EDC_Nuevas_OS4.txt"));
		PrintWriter writer = new PrintWriter("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\ZK\\EDC_Nuevas_OS4_modificado.txt", "UTF-8");
		try {
		    String line = br.readLine();
		    while (line != null) {
		    	String nuevaLinea = line;
		    	if(line.contains(".request.timeout=")){
		    		// sustituir por "tap.web.client.read-timeout.nombreServicio"
		    		String rutaInicio = line.split("=")[0]; // /global/infa/cnt/cnt-OrchestrateSPInteractGuiCRed
		    		String lineaTimeout = line.split("=")[1]; // MediateYOrchSPInteractFromGUIExpService.request.timeout
		    		String valor = line.split("=")[2]; // 600
		    		
		    		String nuevaLineaTimeOut = "tap.web.client.read-timeout." + lineaTimeout.split("\\.")[0];
		    		
		    		nuevaLinea = rutaInicio + "=" + nuevaLineaTimeOut + "=" + valor;
		    		System.out.println(nuevaLinea);
		    	}else if(line.contains("system.transaction.timeout")){
		    		// sustituir por "spring.transaction.default-timeout"
		    		nuevaLinea = line.replace("=system.transaction.timeout=", "=spring.transaction.default-timeout=");
		    		System.out.println(nuevaLinea);
		    	}
		    	writer.println(nuevaLinea);
		        line = br.readLine();
		    }
		} finally {
		    br.close();
		}
		
		writer.close();
		br.close();
	}
}