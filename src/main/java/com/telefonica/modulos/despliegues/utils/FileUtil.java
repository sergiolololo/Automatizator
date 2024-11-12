package com.telefonica.modulos.despliegues.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class FileUtil {
	
	public String seleccionarDirectorioOFichero(int fileSelectionMode) {
		JFileChooser chooser = new JFileChooser();
	    chooser.setAcceptAllFileFilterUsed(false);
	    chooser.setFileSelectionMode(fileSelectionMode);
	    
	    String directorio = "";
	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	directorio = chooser.getSelectedFile().getAbsolutePath();
	    }
	    return directorio;
	}
	
	public void guardarFichero(String nombre, Workbook workbook, String directorio) throws IOException {
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
	}
}
