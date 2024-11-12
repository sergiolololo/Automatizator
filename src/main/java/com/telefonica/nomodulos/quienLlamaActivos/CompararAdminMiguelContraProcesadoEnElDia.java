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

public class CompararAdminMiguelContraProcesadoEnElDia {

	private static XSSFCellStyle estiloCeldaTecnologiaCorregidaError;
	private static Set<String> listaObsoletos;
	
	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\admin_en_parqueYProcesados.xlsx"));
		//FileInputStream fis2 = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\adminProcesadoDia.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet_adminParque = wb.getSheetAt(0); // Activos
		XSSFSheet sheet_adminProcesados = wb.getSheetAt(1); // Activos
		
		String adminParque = null;
		String adminProcesado = null;
 		for(Row row: sheet_adminParque){
			if(row.getRowNum() > 0){
				if(row.getCell(0) != null){
					adminParque = row.getCell(0).getStringCellValue();
					
					for(Row row2: sheet_adminProcesados){
						if(row2.getRowNum() > 0){
							if(row2.getCell(0) != null){
								adminProcesado = row2.getCell(0).getStringCellValue();
								if(adminProcesado.equals(adminParque)){
									// marcamos el admin
									System.out.println(adminProcesado);
									break;
								}
							}
						}
					}
				}
			}
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