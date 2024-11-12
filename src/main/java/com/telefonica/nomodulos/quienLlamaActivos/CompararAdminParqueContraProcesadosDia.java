package com.telefonica.nomodulos.quienLlamaActivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
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

public class CompararAdminParqueContraProcesadosDia {

	private static XSSFCellStyle estiloCeldaTecnologiaCorregidaError;
	private static Set<String> listaObsoletos;
	
	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\adminProcesados18FebreroYParque.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet_adminBBDD = wb.getSheetAt(0); // Activos
		XSSFSheet sheet_adminParque = wb.getSheetAt(1); // Activos
		
		int contador = 0;
		for(Row row: sheet_adminParque){
			contador++;
			if(row.getCell(0) != null){
				String adminParque = row.getCell(0).getStringCellValue();
				
				boolean encontrado = false;
				for(Row row2: sheet_adminBBDD){
					if(row2.getRowNum() > 0){
						if(row2.getCell(0) != null){
							String admin = row2.getCell(0).getStringCellValue();
							if(admin.equals(adminParque)){
								encontrado = true;
								break;
							}
						}
					}
				}
				if(!encontrado){
					System.out.println(adminParque);
				}
			}
		}
		
		
		
		
		
		/*for(Row row: sheet_adminBBDD){
			if(row.getRowNum() > 0){
				contador++;
				if(row.getCell(0) != null){
					String admin = row.getCell(0).getStringCellValue();
					String estado = row.getCell(1).getStringCellValue();
					
					for(Row row2: sheet_adminParque){
						if(row2.getCell(0) != null){
							String adminParque = row2.getCell(0).getStringCellValue();
							if(admin.equals(adminParque)){
								if(!estado.equals("15")){
									System.out.println(admin);	
								}
								break;
							}
						}
					}
				}
			}
		}*/
		System.out.println(contador);
		
		
		/*File dirDestino = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\MigracionOS4_AnalisisTodasFases_CON_OPS_JAVA_relleno.xlsx");
		FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
		wb.write(fileOut);
        fileOut.close();*/
		
		wb.close();
		//wbNuevo.close();
		fis.close();
	}
}