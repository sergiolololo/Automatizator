package com.telefonica.modulos.despliegueCerti.utils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class FileUtil {
	
	public String seleccionarDirectorioOFichero(int fileSelectionMode, String directorioRaiz) {
		JFileChooser chooser = new JFileChooser();
	    chooser.setAcceptAllFileFilterUsed(false);
	    chooser.setFileSelectionMode(fileSelectionMode);
		chooser.setCurrentDirectory(new File(directorioRaiz));
	    
	    String directorio = "";
	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	directorio = chooser.getSelectedFile().getAbsolutePath();
	    }
	    return directorio;
	}
	
	/*public void guardarFichero(String nombre, Workbook workbook, String directorio) throws IOException {
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new File(directorio));
	    chooser.setAcceptAllFileFilterUsed(false);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Libro de Excel 97-2003 (*.xlsx)", "xlsx");
	    chooser.setFileFilter(filter);
	    chooser.setSelectedFile(new File(nombre));
	    
	    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	File dirDestino = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + chooser.getSelectedFile().getName() + ".xlsx");
	        FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
	        workbook.write(fileOut);
	        fileOut.close();
	        workbook.close();
	    }
	}*/

	public String crearDirectorio(String directorio) {
		try {
			File dir = new File(directorio);
			if (!dir.exists()) {
				dir.mkdirs();
			}
            return dir.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
