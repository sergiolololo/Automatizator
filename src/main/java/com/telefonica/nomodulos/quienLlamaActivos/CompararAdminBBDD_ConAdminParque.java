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

public class CompararAdminBBDD_ConAdminParque {

	private static XSSFCellStyle estiloCeldaTecnologiaCorregidaError;
	private static Set<String> listaObsoletos;
	
	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\Disney\\INFORMES_DISNEY\\numAdmin error_previamenteOK.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet_adminBBDD = wb.getSheetAt(0); // Activos
		XSSFSheet sheet_adminParque = wb.getSheetAt(1); // Activos
		
		Map<String, String> adminParqueFila1 = new HashMap<String, String>();
		Map<String, String> adminParqueFila2 = new HashMap<String, String>();
		
		Set<String> listaNOParqueFila1 = new HashSet<String>();
		Set<String> listaNOParqueFila2 = new HashSet<String>();
		
 		for(Row row: sheet_adminParque){
			if(row.getRowNum() > 0){
				if(row.getCell(0) != null){
					adminParqueFila1.put(row.getCell(0).getStringCellValue(), row.getCell(0).getStringCellValue());	
				}
				if(row.getCell(1) != null){
					adminParqueFila2.put(row.getCell(1).getStringCellValue(), row.getCell(1).getStringCellValue());	
				}
			}
		}
		for(Row row: sheet_adminBBDD){
			if(row.getRowNum() > 0){
				
				if(row.getCell(0) != null){
					String fila1 = row.getCell(0).getStringCellValue();
					if(adminParqueFila1.get(fila1) == null){
						// marcamos el admin
						listaNOParqueFila1.add(fila1);
					}
				}
				if(row.getCell(1) != null){
					String fila2 = row.getCell(1).getStringCellValue();
					if(adminParqueFila2.get(fila2) == null){
						// marcamos el admin
						listaNOParqueFila2.add(fila2);
					}
				}
			}
		}
		System.out.println("Lista de los que NO está en parque: Fila 1");
		for(String admin: listaNOParqueFila1){
			System.out.println(admin);
		}
		System.out.println("Lista de los que NO está en parque: Fila 2");
		for(String admin: listaNOParqueFila2){
			System.out.println(admin);
		}
		
		/*File dirDestino = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\MigracionOS4_AnalisisTodasFases_CON_OPS_JAVA_relleno.xlsx");
		FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
		wb.write(fileOut);
        fileOut.close();*/
		
		wb.close();
		//wbNuevo.close();
		fis.close();
	}
}