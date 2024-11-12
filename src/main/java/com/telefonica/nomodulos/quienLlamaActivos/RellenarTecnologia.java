package com.telefonica.nomodulos.quienLlamaActivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RellenarTecnologia {

	private static XSSFCellStyle estiloColumnaEmpiezaServicio;
	
	public static void main(String[] args) throws IOException {
		//FileInputStream fis_ANTIGUA_RUTA_FUNCIONANDO = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\T3G_PG_CYS_DFIS_Matriz_Uso_Servicio_Tecnico_Ola2_20211027 (1)\\matrizModificadaSoloOPNJGter_final_FINAL.xlsx"));
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\MigracionOS4_AnalisisTodasFases.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet_activos = wb.getSheetAt(0); // Activos
		
		FileInputStream fis2 = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\T3G_PG_CYS_DFIS_Matriz_Uso_Servicio_Tecnico_Ola2_20220118\\T3G_PG_CYS_DFIS_Matriz_Uso_Servicio_Tecnico_Ola2_20220118 _SoloNuc.xlsx"));
		XSSFWorkbook wb2 = new XSSFWorkbook(fis2);
		XSSFSheet sheet_activos2 = wb2.getSheetAt(0); // Activos
		
		String nombreServicioRepetido = "";
		int posicionSegundaExcel = 1;
		int recorrerHasta = sheet_activos.getPhysicalNumberOfRows();
		
		Map<String, Set<String>> mapaActivos = new LinkedHashMap<String, Set<String>>();
		crearEstilosExcel(wb);
		
		// sheet_destino.addMergedRegion(new CellRangeAddress(numeroFilaEmpieza2, numeroFilaTermina2, indiceFqn,indiceFqn));
		int mergeFilaEmpieza = -1;
		int mergeFilaTermina = -1;
		for(int i=1; i<recorrerHasta; i++){
			mergeFilaTermina = i-1;
			if(mergeFilaEmpieza != -1 && mergeFilaTermina != -1 && mergeFilaEmpieza < mergeFilaTermina){
				sheet_activos.addMergedRegion(new CellRangeAddress(mergeFilaEmpieza, mergeFilaTermina, 0,0));
				sheet_activos.addMergedRegion(new CellRangeAddress(mergeFilaEmpieza, mergeFilaTermina, 1,1));
				sheet_activos.addMergedRegion(new CellRangeAddress(mergeFilaEmpieza, mergeFilaTermina, 2,2));
				sheet_activos.addMergedRegion(new CellRangeAddress(mergeFilaEmpieza, mergeFilaTermina, 3,3));
			}
			mergeFilaEmpieza = i;
			
			boolean algunProcesado = false;
			Row row = sheet_activos.getRow(i);
			String nombreSRV = row.getCell(3).getStringCellValue();
			String aplicacion = row.getCell(0).getStringCellValue();
			String tecnologia = row.getCell(1).getStringCellValue();
			String fase = row.getCell(2).getStringCellValue();
			
			row.getCell(0).setCellStyle(estiloColumnaEmpiezaServicio);
			row.getCell(1).setCellStyle(estiloColumnaEmpiezaServicio);
			row.getCell(2).setCellStyle(estiloColumnaEmpiezaServicio);
			row.getCell(3).setCellStyle(estiloColumnaEmpiezaServicio);
			
			Set<String> listaOPs = mapaActivos.get(nombreSRV)!=null?mapaActivos.get(nombreSRV):new HashSet<String>();
			
			for(int j=posicionSegundaExcel; j<sheet_activos2.getPhysicalNumberOfRows(); j++){
				Row row2 = sheet_activos2.getRow(j);
				String fqnOP = row2.getCell(4).getStringCellValue();
				//if(!listaOPs.contains(fqnOP)){
					//listaOPs.add(fqnOP);
					//mapaActivos.put(nombreSRV, listaOPs);
					String nombreSRV2 = fqnOP.split("\\.")[0];
					if(nombreSRV.toUpperCase().equals(nombreSRV2.toUpperCase())){
						if(fqnOP.contains("OP_createSPMsgRespFromGUI")){
							System.out.println("");
						}
						if(!listaOPs.contains(fqnOP.toUpperCase())){
							listaOPs.add(fqnOP.toUpperCase());
							mapaActivos.put(nombreSRV, listaOPs);
							if(nombreServicioRepetido.toUpperCase().equals(nombreSRV.toUpperCase())){
								int lastRow = sheet_activos.getPhysicalNumberOfRows();
								sheet_activos.shiftRows(i+1, lastRow, 1, true, true);
								
								Row filaNueva = sheet_activos.createRow(i+1);
								
								filaNueva.createCell(0).setCellValue(aplicacion);
								filaNueva.createCell(1).setCellValue(tecnologia);
								filaNueva.createCell(2).setCellValue(fase);
								filaNueva.createCell(3).setCellValue(nombreSRV);
								
								filaNueva.createCell(4).setCellValue(row2.getCell(5).getStringCellValue());
								i++;
								recorrerHasta++;
								algunProcesado = true;
							}else{
								nombreServicioRepetido = nombreSRV;
								if(algunProcesado){
									i--;
									posicionSegundaExcel = j;
									break;
								}else{
									row.createCell(4).setCellValue(row2.getCell(5).getStringCellValue());
									row.getCell(4).setCellStyle(estiloColumnaEmpiezaServicio);
									algunProcesado = true;
								}
							}
						}
					}else{
						//posicionSegundaExcel = j;
						//break;
					}
				//}
			}
		}
		
		File dirDestino = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\MigracionOS4_AnalisisTodasFases_CON_OPS.xlsx");
		FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
		wb.write(fileOut);
        fileOut.close();
		
		wb.close();
		wb2.close();
	}
	
	
	private static void crearEstilosExcel(XSSFWorkbook wb){
		estiloColumnaEmpiezaServicio = wb.createCellStyle();
		estiloColumnaEmpiezaServicio.setBorderTop(BorderStyle.MEDIUM);
	}
}