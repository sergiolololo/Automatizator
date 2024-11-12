package com.telefonica.nomodulos.nucleo;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ConsultaMiguel {
	
	private static Set<String> listaNombresActivos = new HashSet<String>();
	private static int vecesProcesadoExcel = 0;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//obtaining input bytes from a file  
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\consultaMiguel.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		
		Set<String> numAdminList = new HashSet<String>();
		for(Row row: sheet){
			if(row.getRowNum() >= 1){
				String numAdmin = row.getCell(0).getStringCellValue();
				if(numAdminList.contains(numAdmin)){
					System.out.println(numAdmin);
				}
				numAdminList.add(numAdmin);
			}
		}
        wb.close();
	}
}