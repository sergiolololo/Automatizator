package com.telefonica.modulos.comparador.catalogacion.utils;

import java.io.File;

public class Utilities {

	public static void borrarResultados() {
		// borramos la carpeta de resultados
		File dirOrigen = new File("Resultados");
		for(File fichero: dirOrigen.listFiles()) {
			if(!fichero.isDirectory()) {
				fichero.delete();
			}
		}
	}
}
