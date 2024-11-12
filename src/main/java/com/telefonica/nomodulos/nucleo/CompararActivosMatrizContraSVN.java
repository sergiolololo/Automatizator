package com.telefonica.nomodulos.nucleo;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CompararActivosMatrizContraSVN {
	
	private static Set<String> listaNombresActivos = new HashSet<String>();
	private static int vecesProcesadoExcel = 0;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//obtaining input bytes from a file  
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\excelActivosMatriz_SVN_ultimo.xlsx"));
		FileInputStream fis2 = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\T3G_PG_CYS_DFIS_Matriz_Uso_Servicio_Tecnico_Ola2_20211027 (1)\\T3G_PG_CYS_DFIS_Matriz_Uso_Servicio_Tecnico_Ola2_20211027 (1)_SIN_COLUMNAS.xlsx"));
		
		/*XSSFWorkbook workbook_final = new XSSFWorkbook();
		XSSFSheet sheet_final_encontrados = workbook_final.createSheet("sheet_final_encontrados");
		XSSFSheet sheet_final_no_encontrados = workbook_final.createSheet("sheet_final_no_encontrados");*/
		
		//creating workbook instance that refers to .xls file  
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		
		//creating a Sheet object to retrieve the object  
		XSSFSheet sheet1 = wb.getSheetAt(0); // MATRIZ
		XSSFSheet sheet2 = wb.getSheetAt(1); // SVN
		
		
		int i=0;
		int numeroFila_encontrados = 0;
		int numeroFila_no_encontrados = 0;
		Set<String> activosINFA = new HashSet<String>();
		Set<String> activosPRTE = new HashSet<String>();
		Set<String> activosTERC = new HashSet<String>();
		Set<String> activosSinAplicacion = new HashSet<String>();
		Set<String> activosLocalORest = new HashSet<String>();
		for(Row row1: sheet1){
			if(row1.getRowNum() >= 1){
				
				String activo1 = row1.getCell(0).getStringCellValue();
				String interfaz = row1.getCell(3).getStringCellValue();
				if(!interfaz.equals("LOCAL") && !interfaz.equals("REST+SOAP")){
					boolean encontrado = false;
					for(Row row2: sheet2){
						if(row2.getRowNum() >= 1){
							String activo2 = row2.getCell(0).getStringCellValue();
							if(activo1.equals(activo2)){
								String aplicacion = row2.getCell(1).getStringCellValue();
								if(aplicacion.contains("INFA")){
									activosINFA.add(activo1);
								}else if(aplicacion.contains("PRTE")){
									activosPRTE.add(activo1);
								}else{
									activosTERC.add(activo1);
								}
								
								break;
							}
						}
						i++;
					}
				}else{
					activosLocalORest.add(activo1);
				}
			}
		}
		
		wb.close();
		
		XSSFWorkbook wb2 = new XSSFWorkbook(fis2);
		XSSFSheet sheet3 = wb2.getSheetAt(0);
		for(Row row1: sheet3){
			if(row1.getRowNum() >= 1){
				String aplicacion = row1.getCell(3)!=null?row1.getCell(3).getStringCellValue():"";
				String activo = row1.getCell(5).getStringCellValue();
				
				if(aplicacion == null || aplicacion.equals("")){
					if(!activosINFA.contains(activo) && !activosPRTE.contains(activo) 
							&& !activosTERC.contains(activo) && !activosLocalORest.contains(activo)){
						activosSinAplicacion.add(activo);
					}
				}
			}
		}
		wb2.close();
		
		
		System.out.println("Activos sin aplicacion:");
		for(String activo: activosSinAplicacion){
			System.out.println(activo);
		}
		
		System.out.println("");
		System.out.println("INFA -> " + activosINFA.size());
		System.out.println("PRTE -> " + activosPRTE.size());
		System.out.println("TERC -> " + activosTERC.size());
		System.out.println("Activos con interfaz LOCAL o REST -> " + activosTERC.size());
		
		
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
		
        
        
		/*List<String> list = new ArrayList<String>(listaActivosImpactados); 
	    Collections.sort(list); 
		for(String activo: list){
			System.out.println(activo);
		}*/
	}
}