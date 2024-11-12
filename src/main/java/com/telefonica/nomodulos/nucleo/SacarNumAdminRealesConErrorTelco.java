package com.telefonica.nomodulos.nucleo;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SacarNumAdminRealesConErrorTelco {
	
	private static Set<String> listaNombresActivos = new HashSet<String>();
	private static int vecesProcesadoExcel = 0;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//obtaining input bytes from a file  
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\numAdminEnErrorTelco.xlsx"));
		FileInputStream fis2 = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\numAdminEnError.xlsx"));
		
		XSSFWorkbook workbook_final = new XSSFWorkbook();
		XSSFSheet sheet_final_encontrados = workbook_final.createSheet("sheet_final_encontrados");
		XSSFSheet sheet_final_no_encontrados = workbook_final.createSheet("sheet_final_no_encontrados");
		
		//creating workbook instance that refers to .xls file  
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFWorkbook wb2 = new XSSFWorkbook(fis2);
		//creating a Sheet object to retrieve the object  
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFSheet sheet2 = wb2.getSheetAt(0);
		
		int i=0;
		int numeroFila_encontrados = 0;
		int numeroFila_no_encontrados = 0;
		for(Row row2: sheet2){
			if(row2.getRowNum() >= 1){
				
				String numAdmin = row2.getCell(0).getStringCellValue();
				String estado = row2.getCell(1).getStringCellValue();
				String fechaCreacion = row2.getCell(2).getStringCellValue();
				
				boolean encontrado = false;
				for(Row row: sheet){
					if(row.getRowNum() >= 1){
						
						String messageTerc = row.getCell(0).getStringCellValue();
						String estado2 = row.getCell(2).getStringCellValue();
						String numAdmin2 = row.getCell(4).getStringCellValue();
						String errorCode = row.getCell(5).getStringCellValue();
						
						if(numAdmin.equals(numAdmin2)){
							encontrado = true;
							XSSFRow fila = sheet_final_encontrados.createRow(numeroFila_encontrados);
							XSSFCell cell = fila.createCell(0);
							cell.setCellValue(numAdmin2);
							XSSFCell cell2 = fila.createCell(1);
							cell2.setCellValue(errorCode);
							
							numeroFila_encontrados++;
							break;
						}
					}
					i++;
				}
				if(!encontrado){
					XSSFRow fila = sheet_final_no_encontrados.createRow(numeroFila_no_encontrados);
					XSSFCell cell = fila.createCell(0);
					cell.setCellValue(numAdmin);
					XSSFCell cell2 = fila.createCell(1);
					cell2.setCellValue(estado);
					numeroFila_no_encontrados++;
				}
			}
		}
		
		
		/*
		List<String> hola = new ArrayList<String>();
		for (Entry<String, ServicioBean> entry : mapaActivos.entrySet()) {
			ServicioBean activo = entry.getValue();
			//System.out.println(activo.getTipo() + " -> " + activo.getNombre());
			hola.add(activo.getAplicacion() + " -> " + activo.getTipo() + " -> " + activo.getNombre());
		}
		
		Collections.sort(hola);
		for(String activo: hola){
			System.out.println(activo);
		}
		*/
		
		File dirDestino = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\numAdminEnError_PROCESADO.xlsx");
        FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
        workbook_final.write(fileOut);
        workbook_final.close();
        wb.close();
        wb2.close();
        
		/*List<String> list = new ArrayList<String>(listaActivosImpactados); 
	    Collections.sort(list); 
		for(String activo: list){
			System.out.println(activo);
		}*/
	}
}