package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.nomodulos.utilidades.Constantes;

public class CrearUNVA_SVN_MigracionOS4 {

	// org.apache.commons.lang3.StringUtils.replaceOnce("coast-to-coast", "coast", "") = "-to-coast" -> para reemplazar primera ocurrencia
	
	private static String rutaInfoOPs = "C:\\Users\\Sergio\\Documents\\ARIS 10\\Descarga Servicios.xls";
	private static String rutaJSONs = "C:\\Users\\Sergio\\Documents\\ARIS 10\\JSON\\MIGRACION_OS4";
	
	// C:\t718467\workspace\2_DF_PRTE\srv-nuc\srv-nuc-BIFulfillmentMng
	private static final String RUTA_2DF = "C:\\t718467\\workspace\\2_DF_?\\srv-nuc\\srv-nuc-?";
	
	private static String pesp1 = "PESP_2205_731325";
	private static String pesp2 = "PPRO_GTER_v0.0.180.1";
	private static String drs_infa = "735191";
	private static String drs_prte = "735195";
	private static String drs_terc = "735200";
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {

		File ruta = new File(rutaJSONs);
		
		XSSFWorkbook workbook_cargaUnva = new XSSFWorkbook();
		XSSFSheet sheet_cargaUnva = workbook_cargaUnva.createSheet();
		
		XSSFWorkbook workbook_asignarDRS = new XSSFWorkbook();
		XSSFSheet sheet_asignarDRS = workbook_asignarDRS.createSheet();
		
		
		FileInputStream fis = new FileInputStream(new File(rutaInfoOPs));
		HSSFWorkbook wb_infoOPs = new HSSFWorkbook(fis);
		HSSFSheet sheet_infoOPs = wb_infoOPs.getSheetAt(0);
		
		ObjectMapper objectMapper = new ObjectMapper();
		for(File archivo: ruta.listFiles()){
			if(!archivo.isDirectory()){
				
				JsonNode node = objectMapper.readTree(archivo);
				String aplicacion = node.get("name").asText().toUpperCase();
				
				String drs = "";
				switch(aplicacion){
					case "INFA":
						drs = drs_infa + " - AINF - GTER";
						break;
					case "PRTE":
						drs = drs_prte + " - PRTE - GTER";
						break;
					case "TERC":
						drs = drs_terc + " - TERC - GTER";
						break;
				}
				
				Iterator<JsonNode> iterator = node.get("srv-nuc-v4").elements();
				JsonNode nodeInfoSRV = iterator.next();
				
				String nombreServicio = nodeInfoSRV.get("name").asText();
				String version = nodeInfoSRV.get("version").asText();
				String revision = nodeInfoSRV.get("revision").asText();
				String arquitectura = nodeInfoSRV.get("versionAT").asText();
				String guidServicio = nodeInfoSRV.get("guid").asText();
				//boolean hasExpEndPoint = nodeInfoSRV.get("hasExpEndPoints").asBoolean();
				String fqnServicio = "SRV_" + nombreServicio;
				
				// creamos fila de carga en UNVA del SRV
				crearFilaExcelCargaUnva(sheet_cargaUnva, guidServicio, fqnServicio, fqnServicio, "SRNU", version + "." + revision, aplicacion);
				crearFilaExcelASignarDRS(sheet_asignarDRS, fqnServicio, "SRNU", version + "." + revision, drs);
				
				// creamos fila de carga en UNVA del DTD
				String nombreDTD = Constantes.NOMBRE_DTD;
				nombreDTD = StringUtils.replaceOnce(nombreDTD, "?", aplicacion);
				nombreDTD = StringUtils.replaceOnce(nombreDTD, "?", nombreServicio);
				nombreDTD = StringUtils.replaceOnce(nombreDTD, "?", version);
				nombreDTD = StringUtils.replaceOnce(nombreDTD, "?", revision);
				
				String guidDTD = Constantes.GUID_DTD;
				guidDTD = StringUtils.replaceOnce(guidDTD, "?", nombreServicio);
				guidDTD = StringUtils.replaceOnce(guidDTD, "?", version);
				guidDTD = StringUtils.replaceOnce(guidDTD, "?", revision);
				guidDTD = StringUtils.replaceOnce(guidDTD, "?", pesp1);
				guidDTD = StringUtils.replaceOnce(guidDTD, "?", arquitectura);
				guidDTD = StringUtils.replaceOnce(guidDTD, "?", nombreDTD);
				
				crearFilaExcelCargaUnva(sheet_cargaUnva, guidDTD, nombreDTD, nombreDTD, "DTD", version + "-" + revision + "-1", aplicacion);
				crearFilaExcelASignarDRS(sheet_asignarDRS, nombreDTD, "DTD", version + "-" + revision + "-1", drs);
				
				Iterator<JsonNode> nodeOPs = nodeInfoSRV.get("op").elements();
				while(nodeOPs.hasNext()){
					JsonNode nodeOP = nodeOPs.next();
					String nombreOP = nodeOP.get("name").asText();
					String fqnOP = Constantes.FQN_OP;
					fqnOP = StringUtils.replaceOnce(fqnOP, "?", nombreServicio);
					fqnOP = StringUtils.replaceOnce(fqnOP, "?", nombreOP);
					
					// buscamos el GUID de la OP en el excel de informaci�n descargado de UNVA
					String guidOP = "";
					for(Row row: sheet_infoOPs){
						if(row.getRowNum() >= 1){
							String guid_excel = row.getCell(0).getStringCellValue();
							String nombreOP_excel = row.getCell(1).getStringCellValue();
							String tipoActivo_excel = row.getCell(3).getStringCellValue();
							
							if(tipoActivo_excel.equals("Operaci�n N�cleo") && nombreOP_excel.equals("OP_" + nombreOP)){
								guidOP = guid_excel;
								break;
							}
						}
					}
					
					// creamos fila de carga en UNVA de la OP
					crearFilaExcelCargaUnva(sheet_cargaUnva, guidOP, "OP_" + nombreOP, fqnOP, "OPNJ", version + "." + revision, aplicacion);
					crearFilaExcelASignarDRS(sheet_asignarDRS, fqnOP, "OPNJ", version + "." + revision, drs);
				}
				
				crearDirectorio2DF(archivo, aplicacion, nombreServicio, version, revision, arquitectura, nombreDTD);
			}
		}
		
		File dirDestino_cargaUnva = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\Carga_UNVA_MigracionOS4_Java.xlsx");
		FileOutputStream fileOut_cargaUnva = new FileOutputStream(dirDestino_cargaUnva.getAbsolutePath());
		workbook_cargaUnva.write(fileOut_cargaUnva);
		
        File dirDestino_asignarDRS = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\Carga_UNVA_MigracionOS4_Java_asignarDRS.xlsx");
		FileOutputStream fileOut_asignarDRS = new FileOutputStream(dirDestino_asignarDRS.getAbsolutePath());
		workbook_asignarDRS.write(fileOut_asignarDRS);
		
		fileOut_cargaUnva.close();
        workbook_cargaUnva.close();
        fileOut_asignarDRS.close();
		workbook_asignarDRS.close();
		wb_infoOPs.close();
		fis.close();
	}
	
	
	private static void crearDirectorio2DF(File json, String aplicacion, String nombreServicio, String version, String revision, 
			String arquitectura, String nombreDTD) throws IOException{
		//List<String> listaRutasCrear = new ArrayList<String>();
		
		// se crea el directorio en 2-DF
		String ruta2DF = RUTA_2DF;
		ruta2DF = StringUtils.replaceOnce(ruta2DF, "?", aplicacion);
		ruta2DF = StringUtils.replaceOnce(ruta2DF, "?", nombreServicio);
		
		// rutas Branch
		String ruta2DFBranch = ruta2DF + "\\branches\\v" + version + "." + revision + "_" + pesp1 + "_v" + arquitectura;
		// rutas Tag
		String ruta2DFTag = ruta2DF + "\\tags\\" + version + "-" + revision;
		
		File directory = new File(ruta2DF + "\\tags\\");
		
		int majorMaxima = 0;
		int minorMaxima = 0;
		int revisionMaxima = 0;
		
		for(File direc: directory.listFiles()){
			String versionRecorriendo = direc.getCanonicalPath().substring(direc.getCanonicalPath().lastIndexOf("\\") + 1);
			int majorRecorriendo = Integer.parseInt(versionRecorriendo.split("-|\\.")[0]);
			int minorRecorriendo = Integer.parseInt(versionRecorriendo.split("-|\\.")[1]);
			int revisionRecorriendo = Integer.parseInt(versionRecorriendo.split("-|\\.")[2]);
			if(majorMaxima == 0){
				majorMaxima = majorRecorriendo;
				minorMaxima = minorRecorriendo;
				revisionMaxima = revisionRecorriendo;
			}else{
				if(majorRecorriendo > majorMaxima ){
					majorMaxima = majorRecorriendo;
					minorMaxima = minorRecorriendo;
					revisionMaxima = revisionRecorriendo;
				}else if(majorRecorriendo == majorMaxima && minorRecorriendo > minorMaxima){
					minorMaxima = minorRecorriendo;
					revisionMaxima = revisionRecorriendo;
				}else if(majorRecorriendo == majorMaxima && minorRecorriendo == minorMaxima && revisionRecorriendo > revisionMaxima){
					revisionMaxima = revisionRecorriendo;
				}
			}
		}
		String versionTagDondeCopiar = majorMaxima + "." + minorMaxima + "-" + revisionMaxima;
		
		File directorioCopiarCarpetas = new File(ruta2DF + "\\tags\\" + versionTagDondeCopiar);
		File directorioTag = new File(ruta2DFTag);
		File directorioBranch = new File(ruta2DFBranch);
		FileUtils.copyDirectory(directorioCopiarCarpetas, directorioTag);
		FileUtils.copyDirectory(directorioCopiarCarpetas, directorioBranch);
		
		File rutaDTDTag = new File(ruta2DFTag + "\\doc");
		File rutaDTDBranch = new File(ruta2DFBranch + "\\doc");
		int numeroArchivos = rutaDTDTag.listFiles().length;
		for(int i=0; i<numeroArchivos-1; i++){
			rutaDTDTag.listFiles()[0].delete();
			rutaDTDBranch.listFiles()[0].delete();
		}
		
		rutaDTDTag.listFiles()[0].renameTo(new File(rutaDTDTag + "\\" + nombreDTD + ".docx"));
		rutaDTDBranch.listFiles()[0].renameTo(new File(rutaDTDBranch + "\\" + nombreDTD + ".docx"));
		
		String nombreJsonNuevo = json.getAbsolutePath().substring(json.getAbsolutePath().lastIndexOf("\\") + 1);
		
		// eliminar el json que se ha copiado en la carpeta json tanto de branch como tag, y copiar el nuevo
		File rutaJsonBranch = new File(ruta2DFBranch + "\\json");
		if(!rutaJsonBranch.exists()){
			rutaJsonBranch.mkdir();
		}else{
			rutaJsonBranch.listFiles()[0].delete();	
		}
		Files.copy(json.toPath(), (new File(ruta2DFBranch + "\\json\\" + nombreJsonNuevo)).toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		File rutaJsonTag = new File(ruta2DFTag + "\\json");
		if(!rutaJsonTag.exists()){
			rutaJsonTag.mkdir();
		}else{
			rutaJsonTag.listFiles()[0].delete();	
		}
		Files.copy(json.toPath(), (new File(ruta2DFTag + "\\json\\" + nombreJsonNuevo)).toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	
	private static void crearFilaExcelCargaUnva(XSSFSheet sheet, String guid, String nombre, String fqn, String tipoActivo, String version, String aplicacion){
		int rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
		XSSFRow fila = sheet.createRow(rowTotal);
		
		XSSFCell guidCelda = fila.createCell(0);
		guidCelda.setCellValue(guid);
		
		XSSFCell pesp = fila.createCell(1);
		pesp.setCellValue(pesp1);
		
		XSSFCell codAplicacion = fila.createCell(2);
		codAplicacion.setCellValue(pesp2);
		
		XSSFCell nombreCelda = fila.createCell(3);
		nombreCelda.setCellValue(nombre);
		
		XSSFCell fqnCelda = fila.createCell(4);
		fqnCelda.setCellValue(fqn);
		
		XSSFCell celdaTipo = fila.createCell(5);
		celdaTipo.setCellValue(tipoActivo);
		
		XSSFCell versionCelda = fila.createCell(6);
		versionCelda.setCellValue(version);
		
		XSSFCell complejidad = fila.createCell(7);
		complejidad.setCellValue("Media");
		
		XSSFCell nivelEsfuerzo = fila.createCell(8);
		nivelEsfuerzo.setCellValue("Baja");
		
		XSSFCell etapa = fila.createCell(9);
		etapa.setCellValue("DISE_E21");
		
		XSSFCell slb = fila.createCell(10);
		slb.setCellValue("SLB1");
		
		XSSFCell lote = fila.createCell(11);
		lote.setCellValue("Lote 1");
		
		XSSFCell suministrador = fila.createCell(12);
		suministrador.setCellValue("INDRA");
		
		XSSFCell tipoActuacion = fila.createCell(15);
		tipoActuacion.setCellValue("Modificacion");
		
		XSSFCell aplicacionCelda = fila.createCell(17);
		aplicacionCelda.setCellValue(aplicacion);
	}
	
	// formato DRS: 735191 - AINF - GTER
	private static void crearFilaExcelASignarDRS(XSSFSheet sheet, String fqn, String tipoActivo, String version, String drs){
		int rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
		if(rowTotal == 0) {
			sheet.createRow(0);
			rowTotal = 1;
		}
		XSSFRow fila = sheet.createRow(rowTotal);
		
		XSSFCell pespCelda = fila.createCell(0);
		pespCelda.setCellValue(pesp1);
		
		XSSFCell pdpCelda = fila.createCell(1);
		pdpCelda.setCellValue(Constantes.PDP);
		
		XSSFCell fqnCelda = fila.createCell(2);
		fqnCelda.setCellValue(fqn);
		
		XSSFCell tipoCelda = fila.createCell(3);
		tipoCelda.setCellValue(tipoActivo);
		
		XSSFCell versionCelda = fila.createCell(4);
		versionCelda.setCellValue(version);
		
		XSSFCell drsCelda = fila.createCell(5);
		drsCelda.setCellValue(drs);
	}
}
