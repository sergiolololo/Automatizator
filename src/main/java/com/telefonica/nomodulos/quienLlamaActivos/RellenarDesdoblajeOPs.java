package com.telefonica.nomodulos.quienLlamaActivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RellenarDesdoblajeOPs {

	private static XSSFCellStyle estiloCeldaTecnologiaCorregidaError;
	private static Set<String> listaObsoletos;
	
	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\MigracionOS4_AnalisisTodasFases_CON_OPS_JAVA.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet_activos = wb.getSheetAt(0); // Activos
		XSSFSheet sheet_desdoblaje = wb.getSheetAt(1); // Activos
		
		//XSSFWorkbook wbNuevo = new XSSFWorkbook();
		
		
		//rellenarListaObsoletos();
		
		for(int i=1; i<sheet_activos.getPhysicalNumberOfRows(); i++){
			Row row = sheet_activos.getRow(i);
			
			String tecnologia = row.getCell(1)!=null?row.getCell(1).getStringCellValue():"";
			String faseMigracion = row.getCell(2).getStringCellValue();
			String servicio = row.getCell(3).getStringCellValue();
			String operacion = row.getCell(4).getStringCellValue();
			
			if(tecnologia.equals("SOAP+LOCAL") && faseMigracion.equals("Fase 2")){
				boolean encontrado = false;
				for(int j=1; j<150; j++){
					Row row2 = sheet_desdoblaje.getRow(j);
					String fqn = row2.getCell(1).getStringCellValue();
					String servicio2 = fqn.split("\\.")[0];
					String operacion2 = fqn.split("\\.")[3];
					
					if(servicio2.equals(servicio) && operacion2.equals(operacion)){
						row.createCell(5).setCellValue("SI");
						encontrado = true;
						break;
					}
				}
				if(!encontrado){
					row.createCell(5).setCellValue("NO");
				}
			}else{
				row.createCell(5).setCellValue("NO");
			}
		}
		
		File dirDestino = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\MigracionOS4_AnalisisTodasFases_CON_OPS_JAVA_relleno.xlsx");
		FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
		wb.write(fileOut);
        fileOut.close();
		
		wb.close();
		//wbNuevo.close();
		fis.close();
	}
}