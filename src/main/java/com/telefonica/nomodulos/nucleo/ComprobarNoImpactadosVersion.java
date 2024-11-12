package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.SAXException;

public class ComprobarNoImpactadosVersion {
	
	private static String ruta_2DF = "C:\\T718467\\workspace\\2_DF_APLICACION\\TIPO\\ACTIVO\\branches";
	
	private static String rutaFicheroAnalisis = "C:\\Users\\sherrerah\\OneDrive\\Documentos\\Analisis dependencias\\PESP_2306\\Dependencias_2306_Analisis.xlsx";
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        
		FileInputStream fis = new FileInputStream(new File(rutaFicheroAnalisis));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		
		for(int i=0; i<wb.getNumberOfSheets(); i++) {
			XSSFSheet sheet = wb.getSheetAt(i);
			
			for(Row row: sheet){
				if(row.getRowNum() >= 3){
					String activo = row.getCell(1).getStringCellValue();
					String aplicacion = row.getCell(2).getStringCellValue();
					String tipo = row.getCell(3).getStringCellValue();
					
					String rutaBuscar = ruta_2DF.replace("APLICACION", aplicacion);
					
					if(tipo.trim().equals("RES")) {
						rutaBuscar = rutaBuscar.replace("TIPO", "res-nuc");
						rutaBuscar = rutaBuscar.replace("ACTIVO", activo.replace(tipo.trim() + "_", "res-nuc-"));
					}else if(tipo.trim().equals("SRNU")) {
						rutaBuscar = rutaBuscar.replace("TIPO", "srv-nuc");
						rutaBuscar = rutaBuscar.replace("ACTIVO", activo.replace("SRV_", "srv-nuc-"));
					}else if(tipo.trim().equals("SRPR")) {
						rutaBuscar = rutaBuscar.replace("TIPO", "srv-pres");
						rutaBuscar = rutaBuscar.replace("ACTIVO", activo.replace("SRV_", "srv-pres-"));
					}else if(tipo.trim().equals("JTNU")) {
						rutaBuscar = rutaBuscar.replace("TIPO", "jt-nuc");
						rutaBuscar = rutaBuscar.replace("ACTIVO", activo.replace("JT_", "jt-nuc-"));
					}else if(tipo.trim().equals("CNT")) {
						rutaBuscar = rutaBuscar.replace("TIPO", "cnt");
						rutaBuscar = rutaBuscar.replace("ACTIVO", activo.replace("CNT_", "cnt-"));
					}else if(tipo.trim().equals("CGT")) {
						rutaBuscar = rutaBuscar.replace("TIPO", "cgt");
						rutaBuscar = rutaBuscar.replace("ACTIVO", activo.replace("CGT_", "cgt-"));
					}else {
						System.out.println("no se tiene en cuenta");
					}
					
					boolean encontrado = false;
					File ruta = new File(rutaBuscar);
					for(File ruta2: ruta.listFiles()) {
						if(ruta2.isDirectory() && ruta2.getAbsolutePath().contains("2306")) {
							encontrado = true;
							break;
						}
					}
					if(!encontrado) {
						if(row.getCell(5) == null) {
							row.createCell(5);
						}
						row.getCell(5).setCellValue("NO Existe rama 2306");
					}
				}
			}
		}
		
		guardarFichero("Dependencias_2306_Analisis_FINAL", wb);
		
		wb.close();
		fis.close();
    }
	
	private static void guardarFichero(String nombre, Workbook workbook) throws IOException {
		
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setAcceptAllFileFilterUsed(false);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Libro de Excel 97-2003 (*.xlsx)", "xlsx");
	    chooser.setFileFilter(filter);
	    chooser.setSelectedFile(new File(nombre));
	    
	    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	// Write the output to a file
	    	File dirDestino = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + chooser.getSelectedFile().getName() + ".xlsx");
	        FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
	        workbook.write(fileOut);
	        fileOut.close();
	    }
	}
}