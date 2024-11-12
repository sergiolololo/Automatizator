package com.telefonica.nomodulos.nucleo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EliminarVariablesZK {

	private static final String RUTA_VAR_ZK = "C:\\Worskspace_spring\\var-conf_";
	private static final String RUTA_VARIABLES = "C:\\Worskspace_spring";
	
	private static List<String> listaPalabras = Arrays.asList("encrypt", "name", "path", "value");
	
	public static void main(String[] args) throws IOException {
		File dir = new File(RUTA_VARIABLES);
		for(File ruta: dir.listFiles()) {
			if(ruta.isDirectory() && ruta.getAbsolutePath().contains(RUTA_VAR_ZK)) {
				for(File ruta2: ruta.listFiles()) {
					procesarEliminacionVacios(ruta2);	
				}
			}
		}
	}

	private static void procesarEliminacionVacios(File ruta) throws IOException {
		if(ruta.isDirectory()) {
			for(File ruta2: ruta.listFiles()) {
				if(!ruta.getAbsolutePath().contains("svn")) {
					procesarEliminacionVacios(ruta2);	
				}
			}
		}else if(ruta.getName().equals("variables.conf")) {
			procesarArchivo(ruta);
		}
	}

	private static void procesarArchivo(File ruta) throws IOException {
		FileReader varZK = new FileReader(ruta);
		BufferedReader br = new BufferedReader(varZK);
		StringBuffer pomModificado = new StringBuffer();
		
		String inicioLinea = "";
		String encrypt = "";
		String name = "";
		String path = "";
		String value = "";
		String finLinea = "";
		
		List<String> lineas = new ArrayList<>();
		try {
			String line = br.readLine();
			pomModificado.append(line).append('\n');
			lineas.add(line);
			
			line = br.readLine();
			pomModificado.append(line).append('\n');
			lineas.add(line);
			
			line = br.readLine();
		    while (line != null) {
		    	if(line.contains("{") && !listaPalabras.stream().anyMatch(line::contains)) {
		    		// empieza variable
		    		inicioLinea = line;
		    		encrypt = br.readLine();
		    		name = br.readLine();
		    		path = br.readLine();
		    		value = br.readLine();
		    		finLinea = br.readLine();
		    		
		    		if(!value.contains("\"value\": \"\"")) {
		    			pomModificado.append(inicioLinea).append('\n');
		    			pomModificado.append(encrypt).append('\n');
		    			pomModificado.append(name).append('\n');
		    			pomModificado.append(path).append('\n');
		    			pomModificado.append(value).append('\n');
		    			pomModificado.append(finLinea).append('\n');
		    			
			    		lineas.addAll(Arrays.asList(inicioLinea, encrypt, name, path, value, finLinea));
			    	}else {
			    		System.out.println("Eliminada variable -> " + "path: " + path + "; name: " + name + "; value: " + value);
			    	}
		    	}
		    	line = br.readLine();
		    }
		    pomModificado.append("  ]").append('\n');
		    pomModificado.append("}").append('\n');
		    lineas.addAll(Arrays.asList("  ]", "}"));
		    
		    FileOutputStream fileOut = new FileOutputStream(ruta.getAbsolutePath());
	        fileOut.write(pomModificado.toString().getBytes());
	        fileOut.close();
		}catch(Exception e){
			System.out.println(e);
		}finally {
		    br.close();
		    varZK.close();
		}
	}

}
