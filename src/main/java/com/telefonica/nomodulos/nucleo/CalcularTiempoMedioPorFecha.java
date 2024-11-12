package com.telefonica.nomodulos.nucleo;

import com.telefonica.nomodulos.beans.ActivoBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CalcularTiempoMedioPorFecha {
	
	private static Map<String, ActivoBean> mapa = new LinkedHashMap<String, ActivoBean>();
	private static Set<String> activosExistentes = new HashSet<String>();

	public static void main(String[] args) throws IOException {
		leerExcel();
		//crearExcelUNVA();
	}
	
	private static void leerExcel() throws IOException{
		
		Path path = Paths.get("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\tiemposEstadosDisney.txt");
	    List<String> read = Files.readAllLines(path);

	    List<Integer> listaSegundosDiferencia = new ArrayList<Integer>();
	    int totalSegundosDiferencia = 0;
	    for(int i=0; i<read.size(); i++){
	    	
	    	String hora1 = read.get(i);
	    	
	    	if(read.size() <= i+1){
	    		break;
	    	}
	    	String hora2 = read.get(i+1);
	    	
	    	String[] arrayHora1 = hora1.split(":");
	    	int segundosHora1_1 = Integer.parseInt(arrayHora1[0])*3600;
	    	int segundosHora1_2 = Integer.parseInt(arrayHora1[1])*60;
	    	int segundosHora1_3 = Integer.parseInt(arrayHora1[2]);
	    	int totalSegundosHora1 = segundosHora1_1 + segundosHora1_2 + segundosHora1_3;
	    	
	    	String[] arrayHora2 = hora2.split(":");
	    	int segundosHora2_1 = Integer.parseInt(arrayHora2[0])*3600;
	    	int segundosHora2_2 = Integer.parseInt(arrayHora2[1])*60;
	    	int segundosHora2_3 = Integer.parseInt(arrayHora2[2]);
	    	int totalSegundosHora2 = segundosHora2_1 + segundosHora2_2 + segundosHora2_3;
	    	
	    	int segundosDiferencia = totalSegundosHora2 - totalSegundosHora1;
	    	
	    	if(segundosDiferencia > 45){
	    		continue;
	    	}
	    	
	    	listaSegundosDiferencia.add(segundosDiferencia);
	    	totalSegundosDiferencia+=  segundosDiferencia;
	    }
	    
		double mediaSegundos = totalSegundosDiferencia/(listaSegundosDiferencia.size());
		
		System.out.println(mediaSegundos);
	}
}