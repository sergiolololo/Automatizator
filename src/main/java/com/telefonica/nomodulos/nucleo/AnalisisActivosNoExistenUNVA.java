package com.telefonica.nomodulos.nucleo;

import com.telefonica.nomodulos.beans.ActivoBean;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AnalisisActivosNoExistenUNVA {
	
	private static Map<String, ActivoBean> mapa = new LinkedHashMap<String, ActivoBean>();
	private static Set<String> activosExistentes = new HashSet<String>();

	public static void main(String[] args) throws IOException {
		leerExcel();
		crearExcelUNVA();
	}
	
	private static void leerExcel() throws IOException{
		
		//obtaining input bytes from a file  
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\20211013_InfoActivosGTER.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		
		int comparac = "PESP_2202_731323".compareTo("PESP_2109_692674");
		
		for(Row row: sheet){
			if(row.getRowNum() >= 1){
				String aplicacion = row.getCell(16)!=null?row.getCell(16).getStringCellValue():null;
				aplicacion = aplicacion!=null?aplicacion.substring(2):null;
				//if(!aplicacion.contains("SRMP") && !aplicacion.contains("PORT")){
					
					row.getCell(7).setCellType(CellType.STRING);
					String idUnva = row.getCell(7)!=null?row.getCell(7).getStringCellValue():null;
					String activo = row.getCell(11)!=null?row.getCell(11).getStringCellValue():null;
					String version = row.getCell(12)!=null?row.getCell(12).getStringCellValue():null;
					row.getCell(11).setCellType(CellType.STRING);
					String revision = null;
					String tipoActivo = row.getCell(5)!=null?row.getCell(5).getStringCellValue():null;
					String ruta = null;
					String pespActual = row.getCell(3)!=null?row.getCell(3).getStringCellValue():null;
					String guid = row.getCell(13)!=null?row.getCell(13).getStringCellValue():null;
					String fqn = row.getCell(10)!=null?row.getCell(10).getStringCellValue():null;
					
					ActivoBean activoBean = new ActivoBean(idUnva, activo, version, revision, tipoActivo, ruta,
							guid, fqn, aplicacion, pespActual);
					
					if(activo.equals("ME_displayMilestoneDetail")){
						System.out.println("");
					}
					
					if(idUnva != null && !idUnva.equals("")){
						activosExistentes.add(activo + "_" + tipoActivo);
					}else if(idUnva == null || idUnva.equals("")){
						if(pespActual.equals("PGLO_LB_EPR") || pespActual.compareTo("PESP_2105_692672") == 0 || pespActual.compareTo("PESP_2105_692672") < 0){
							if(mapa.get(activo+tipoActivo) != null){
								String pesp1 = mapa.get(activo+tipoActivo).getPespActual();
								String pesp2 = pespActual;
								
								if(pesp1.equals("PGLO_LB_EPR") && !pesp2.equals("PGLO_LB_EPR")){
									mapa.put(activo + "_" + tipoActivo, activoBean);
								}else if(!pesp1.equals("PGLO_LB_EPR") && pesp2.equals("PGLO_LB_EPR")){
									// nothing
								}else if(pesp1.compareTo(pesp2) < 0){
									mapa.put(activo + "_" + tipoActivo, activoBean);
								}
							}else{
								mapa.put(activo + "_" + tipoActivo, activoBean);	
							}
						}
					}
				//}
			}
		}
		wb.close();
		fis.close();
	}
	
	private static void crearExcelUNVA() throws IOException{
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet2 = workbook.createSheet();
		
		int i=0;
		for (Entry<String, ActivoBean> entry : mapa.entrySet()) {
			//if(!activosExistentes.contains(entry.getKey())){
				System.out.println(entry.getValue().getActivo());
				
				ActivoBean activoBean = entry.getValue();
				XSSFRow fila = sheet2.createRow(i);
				fila.createCell(0).setCellValue(activoBean.getGuid());
				fila.createCell(1).setCellValue(activoBean.getPesp());
				fila.createCell(2).setCellValue(activoBean.getPesp2());
				fila.createCell(3).setCellValue(activoBean.getActivo());
				fila.createCell(4).setCellValue(activoBean.getFqn());
				fila.createCell(5).setCellValue(activoBean.getTipoActivo());
				fila.createCell(6).setCellValue(activoBean.getVersion());//
				fila.createCell(7).setCellValue(activoBean.getComplejidad());
				fila.createCell(8).setCellValue(activoBean.getEsfuerzo());
				
				fila.createCell(9).setCellValue(activoBean.getEtapa());
				fila.createCell(10).setCellValue(activoBean.getSlb());
				fila.createCell(11).setCellValue(activoBean.getLote());
				fila.createCell(12).setCellValue(activoBean.getSuministrador());
				fila.createCell(15).setCellValue(activoBean.getTipoActuacion());
				fila.createCell(17).setCellValue(activoBean.getAplicacion());

				i++;
			//}
		}
		
		File dirDestino = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\CargaUnvaActivosNoExisten.xlsx");
		FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
	}
}