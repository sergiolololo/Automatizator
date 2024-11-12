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

public class ExisteAdminEnFichero {
	
	private static Set<String> listaNombresActivos = new HashSet<String>();
	private static int vecesProcesadoExcel = 0;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//obtaining input bytes from a file  
		FileInputStream ficheroDisney = new FileInputStream(new File("C:\\Users\\sherrerah\\Desktop\\ficheroDisney.xlsx"));
		FileInputStream ficheroSQL = new FileInputStream(new File("C:\\Users\\sherrerah\\Desktop\\ficheroSQLDisney.xlsx"));
		
		XSSFWorkbook workbook_final = new XSSFWorkbook();
		XSSFSheet sheet_final_encontrados = workbook_final.createSheet("sheet_final_encontrados");
		
		//creating workbook instance that refers to .xls file  
		XSSFWorkbook wb = new XSSFWorkbook(ficheroSQL);
		XSSFWorkbook wb2 = new XSSFWorkbook(ficheroDisney);
		//creating a Sheet object to retrieve the object  
		XSSFSheet sheetFicheroSQL = wb.getSheetAt(0);
		XSSFSheet sheetFicheroDisney = wb2.getSheetAt(0);
		
		int i=0;
		int numeroFila_encontrados = 0;
		for(Row row2: sheetFicheroDisney){
			if(row2.getRowNum() >= 1){
				String activate = row2.getCell(0).getStringCellValue();
				String entitled = row2.getCell(1).getStringCellValue();
				String fecha = row2.getCell(2).getStringCellValue();
				String numAdmin = row2.getCell(3).getStringCellValue();
				for(Row row: sheetFicheroSQL){
					if(row.getRowNum() >= 1){
						String numAdmin2 = row.getCell(0).getStringCellValue();
						if(numAdmin.equals(numAdmin2)){
							XSSFRow fila = sheet_final_encontrados.createRow(numeroFila_encontrados);
							XSSFCell cell = fila.createCell(0);
							cell.setCellValue(numAdmin);
							XSSFCell cell2 = fila.createCell(1);
							cell2.setCellValue(fecha);
							XSSFCell cell3 = fila.createCell(1);
							cell3.setCellValue(entitled);
							XSSFCell cell4 = fila.createCell(1);
							cell4.setCellValue(activate);
							numeroFila_encontrados++;
							break;
						}
					}
					i++;
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
		
		File dirDestino = new File("C:\\Users\\sherrerah\\Desktop\\adminEncontradosEnFichero.xlsx");
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