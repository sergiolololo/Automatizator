package com.telefonica.nomodulos.quienLlamaActivos;

import com.telefonica.nomodulos.beans.QuienLlamaBean;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class QuienLlamaActivos {
	
	// SRV_ChangeCustomerAccount.OP_updateAppointmentData
	// Este llama por LOCAL a OP_generateSPOrder
	// Esto se cambia para que llame por cliews debido a condiciones de carrera
	// Tener en cuenta para migración de OS
	// para SRV_ChangeCustomerAccount.OP_changeCustomerAccount también debería llamarse igual
	
	// SRV_ChangeCustomerAccount.OP_updateAppointmentData llama a SRV_SPOrderMng.OP_traceBISPOrder por LOCAL, pero también se cambia a SOAP
	
	
	public static XSSFCellStyle estiloCeldaError = null;
	public static XSSFCellStyle estiloCeldaSepararPadreError = null;
	public static XSSFCellStyle estiloCeldaCabecera = null;
	public static XSSFCellStyle estiloCeldaSepararPadreCabecera = null;
	public static XSSFCellStyle estiloCeldaFinServicio = null;
	public static XSSFCellStyle estiloCeldaSepararPadreFinServicio = null;
	public static XSSFCellStyle estiloCeldaTecnologiaCorregida = null;
	public static XSSFCellStyle estiloCeldaTecnologiaCorregidaError = null;
	public static XSSFCellStyle estiloCeldaSepararPadre = null;
	public static XSSFCellStyle estiloCeldaOP = null;
	public static XSSFCellStyle estiloCeldaOPFinServicio = null;
	public static XSSFCellStyle estiloCeldaServicio = null;
	
	private static int numeroCeldasSheetQuienMeLlama = 11;
	private static int numeroCeldasSheetAQuienLlamo = 9;
	private static int numeroCeldasSheetMigracionOS = 7;
	
	// índices de columnas del hoja quienMeLlama
	private static int indiceAplicacion = 0;
	private static int indiceTecnologiaActual = 1;
	private static int indiceTecnologiaCorrecta = 2;
	private static int indiceServicio = 3;
	private static int indiceFqn = 4;
	private static int indiceTipo = 5;
	private static int indiceAplicacionPadre = 6;
	private static int indiceFqnPadre = 7;
	private static int indiceTipoPadre = 8;
	private static int indiceRelacionActual = 9;
	private static int indiceRelacionCorrecta = 10;
	
	// índices de columnas del hoja aQuienLlamo
	private static int indiceAplicacionAQuienLlamo = 0;
	private static int indiceServicioAQuienLlamo = 1;
	private static int indiceFqnAQuienLlamo = 2;
	private static int indiceTipoAQuienLlamo = 3;
	private static int indiceAplicacionPadreAQuienLlamo = 4;
	private static int indiceFqnPadreAQuienLlamo = 5;
	private static int indiceTipoPadreAQuienLlamo = 6;
	private static int indiceRelacionActualAQuienLlamo = 7;
	private static int indiceRelacionCorrectaAQuienLlamo = 8;
	
	protected static List<String> serviciosObsoletos = new ArrayList<String>();
	public static Map<String, QuienLlamaBean> mapaServicioBean = new LinkedHashMap<String, QuienLlamaBean>();
	
	public static void main(String[] args) throws IOException {
		
		serviciosObsoletos = obtenerObsoletos();
		
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\excelActivosModificado2.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet_relaciones = wb.getSheetAt(1); // Relaciones
		
		obtenerServiciosYOperaciones(wb);
		
		// recorremos la hoja de relaciones
		// crearemos un mapa que contendrá los servicios, dentro sus operaciones, y dentro de cada operación por quién es llamada dicha operación
		for(Row row: sheet_relaciones){
			if(row.getRowNum() >= 1){
				
				rellenarMapaQuienMeLlama(row);
				rellenarMapaAQuienLlamo(row);
			}
		}
		wb.close();
		
		XSSFWorkbook workbook_destino = new XSSFWorkbook();
		XSSFSheet sheet_quienMeLlama = workbook_destino.createSheet("Quien me llama");
		XSSFSheet sheet_aQuienLlamo = workbook_destino.createSheet("A quien llamo");
		XSSFSheet sheet_migracionOS = workbook_destino.createSheet("Migracion OS4");
		
		crearEstilosCelda(workbook_destino);
		crearCabecerasSheeQuienMeLlama(sheet_quienMeLlama);
		crearCabecerasSheeAQuienLlamo(sheet_aQuienLlamo, sheet_migracionOS);
		
		int indiceFilaQuienMeLlama=1;
		int indiceFilaAquienLlamo=1;
		XSSFRow filaQuienMeLlama = sheet_quienMeLlama.createRow(indiceFilaQuienMeLlama);
		XSSFRow filaAQuienLlamo = sheet_aQuienLlamo.createRow(indiceFilaAquienLlamo);
		int numeroFilaEmpiezaQuienMeLlama = 0;
		int numeroFilaEmpiezaAQuienLlamo = 0;
		
		// recorremos los servicios
		for(Entry<String, QuienLlamaBean> mapa: mapaServicioBean.entrySet()){
			
			QuienLlamaBean servicioBean = mapa.getValue();
			numeroFilaEmpiezaQuienMeLlama = sheet_quienMeLlama.getPhysicalNumberOfRows()-1;
			numeroFilaEmpiezaAQuienLlamo = sheet_aQuienLlamo.getPhysicalNumberOfRows()-1;
			
			Set<String> listaInterfazLlamadaOPsServicio = new HashSet<String>();
			Set<String> tipoRelacionesUsadas = new HashSet<String>();
			
			int numeroFilaEmpiezaQuienMeLlama2 = 0;
			int numeroFilaEmpiezaAQuienLlamo2 = 0;
			
			// recorremos las operaciones del servicio
			for(Entry<String, QuienLlamaBean> mapaOPs: servicioBean.getMapaDependencias().entrySet()){
				
				numeroFilaEmpiezaQuienMeLlama2 = sheet_quienMeLlama.getPhysicalNumberOfRows()-1;
				numeroFilaEmpiezaAQuienLlamo2 = sheet_aQuienLlamo.getPhysicalNumberOfRows()-1;
				
				QuienLlamaBean operacionBean = mapaOPs.getValue();
				
				indiceFilaQuienMeLlama = crearFilaQuienMeLlama(sheet_quienMeLlama, filaQuienMeLlama, servicioBean, operacionBean, listaInterfazLlamadaOPsServicio, indiceFilaQuienMeLlama, numeroFilaEmpiezaQuienMeLlama2);
				filaQuienMeLlama = sheet_quienMeLlama.getRow(sheet_quienMeLlama.getPhysicalNumberOfRows() - 1);
				indiceFilaAquienLlamo = crearFilaAQuienLlamo(sheet_aQuienLlamo, filaAQuienLlamo, servicioBean, operacionBean, tipoRelacionesUsadas, indiceFilaAquienLlamo, numeroFilaEmpiezaAQuienLlamo2);
				filaAQuienLlamo = sheet_aQuienLlamo.getRow(sheet_aQuienLlamo.getPhysicalNumberOfRows() - 1);
			}
			
			aplicarFormatoCeldasHojaQuienMeLlama(numeroFilaEmpiezaQuienMeLlama, sheet_quienMeLlama, indiceFilaQuienMeLlama, servicioBean, listaInterfazLlamadaOPsServicio);
			aplicarFormatoCeldasHojaAQuienLlamo(numeroFilaEmpiezaAQuienLlamo, sheet_aQuienLlamo, indiceFilaAquienLlamo, servicioBean, tipoRelacionesUsadas);
			
			if(servicioBean.getTipo().equals("SRNU") || servicioBean.getTipo().equals("SRUT")){
				for(Entry<String, QuienLlamaBean> mapa2: servicioBean.getMapaDependencias().entrySet()){
					QuienLlamaBean operacion = mapa2.getValue();
					
					XSSFRow filaMigracion = sheet_migracionOS.createRow(sheet_migracionOS.getPhysicalNumberOfRows());
					filaMigracion.createCell(0).setCellValue(servicioBean.getAplicacion());
					filaMigracion.createCell(1).setCellValue(servicioBean.getTecnologiaInterfazCorrecta());
					filaMigracion.createCell(2).setCellValue(servicioBean.getFaseMigracion());
					filaMigracion.createCell(3).setCellValue(servicioBean.getTipo());
					filaMigracion.createCell(4).setCellValue(servicioBean.getServicio());
					filaMigracion.createCell(5).setCellValue(operacion.getOperacion());
					
					// una operación solo se podrá desdoblar en caso de que su servicio sea SOAP+LOCAL, por ello lo comprobamos antes
					if(servicioBean.getTecnologiaInterfazCorrecta().equals("SOAP+LOCAL")){
						filaMigracion.createCell(6).setCellValue(operacion.isSeDesdobla()?"SI":"NO");
					}else{
						filaMigracion.createCell(6).setCellValue("NO");
					}
				}
			}
		}
		sheet_quienMeLlama.removeRow(sheet_quienMeLlama.getRow(sheet_quienMeLlama.getPhysicalNumberOfRows()-1));
		sheet_aQuienLlamo.removeRow(sheet_aQuienLlamo.getRow(sheet_aQuienLlamo.getPhysicalNumberOfRows()-1));
		
		for(int j=0; j<numeroCeldasSheetQuienMeLlama; j++){
			sheet_quienMeLlama.autoSizeColumn(j);
		}
		for(int j=0; j<numeroCeldasSheetAQuienLlamo; j++){
			sheet_aQuienLlamo.autoSizeColumn(j);
		}
		for(int j=0; j<numeroCeldasSheetMigracionOS; j++){
			sheet_migracionOS.autoSizeColumn(j);
		}
		
		sheet_quienMeLlama.setDisplayGridlines(false);
		sheet_aQuienLlamo.setDisplayGridlines(false);
		sheet_migracionOS.setDisplayGridlines(false);
		
		File dirDestino = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\QuienLlamaActivos_18Abril_ejemplo.xlsx");
		FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
		workbook_destino.write(fileOut);
        fileOut.close();
        workbook_destino.close();
	}
	
	
	private static void rellenarMapaQuienMeLlama(Row row) {
		// se preparan los datos de "quien me llama"
		String proyecto = row.getCell(16).getStringCellValue();
		String aplicacion = row.getCell(17).getStringCellValue();
		String fqn = row.getCell(20).getStringCellValue();
		String operacion = row.getCell(19).getStringCellValue();
		String servicio = fqn.contains(".")?fqn.substring(0, row.getCell(20).getStringCellValue().indexOf(".")):fqn;
		String tipo = row.getCell(22).getStringCellValue();
		
		if(proyecto.equals("GTER") && !serviciosObsoletos.contains(servicio)){
			
			String tipoServicio = "";
			if(tipo.equals("OPNJ")){
				tipoServicio = "SRNU";
			}else if(tipo.equals("OPNS")){
				tipoServicio = "SRNS";
			}else if(tipo.equals("OPPR")){
				tipoServicio = "SRPR";
			}else if(tipo.equals("OPEX")){
				tipoServicio = "SREX";
			}else if(tipo.equals("OPUT")){
				tipoServicio = "SRUT";
			}else if(tipo.equals("ME")){
				tipoServicio = "RES";
			}else{
				tipoServicio = tipo;
			}
			
			QuienLlamaBean servicioBean = null;
			
			if(mapaServicioBean.get(servicio + "&" + tipoServicio) != null){
				servicioBean = mapaServicioBean.get(servicio + "&" + tipoServicio);
			}else{
				servicioBean = new QuienLlamaBean(aplicacion, null, null, tipoServicio, servicio, null, null, new LinkedHashMap<String, QuienLlamaBean>(), null, null);
			}
			
			Map<String, QuienLlamaBean> mapaOperacionDependencias = new LinkedHashMap<String, QuienLlamaBean>();
			if(servicioBean.getMapaDependencias().size() > 0){
				mapaOperacionDependencias = servicioBean.getMapaDependencias();
			}
			
			QuienLlamaBean quienLlamaBean = null;
			if(mapaOperacionDependencias.get(fqn) != null){
				quienLlamaBean = mapaOperacionDependencias.get(fqn);
			}else{
				quienLlamaBean = new QuienLlamaBean(aplicacion, operacion, fqn, tipo, servicio, null, null, null, new LinkedHashMap<String, QuienLlamaBean>(), new LinkedHashMap<String, QuienLlamaBean>());
			}
			
			String fqnPadre = row.getCell(6).getStringCellValue();
			if(quienLlamaBean.getMapaDependenciasLlamantes().get(fqnPadre) == null){
				String aplicacionPadre = row.getCell(3).getStringCellValue();
				String operacionPadre = row.getCell(5).getStringCellValue();
				String tipoPadre = row.getCell(8).getStringCellValue();
				String servicioPadre = fqnPadre.contains(".")?fqnPadre.substring(0, fqnPadre.indexOf(".")):fqnPadre;
				
				String tipoRelacion = row.getCell(26)!=null?row.getCell(26).getStringCellValue():"";
				
				QuienLlamaBean dependencia = new QuienLlamaBean(aplicacionPadre, operacionPadre, fqnPadre, tipoPadre, servicioPadre, tipoRelacion, null, null, null, null);
				
				Map<String, QuienLlamaBean> mapa = quienLlamaBean.getMapaDependenciasLlamantes();
				mapa.put(fqnPadre, dependencia);
				quienLlamaBean.setMapaDependenciasLlamantes(mapa);
			}
			mapaOperacionDependencias.put(fqn, quienLlamaBean);
			servicioBean.setMapaDependencias(mapaOperacionDependencias);
			
			mapaServicioBean.put(servicio + "&" + tipoServicio, servicioBean);
		}
	}
	
	
	private static void rellenarMapaAQuienLlamo(Row row) {
		// se preparan los datos de "a quien llamo"
		String proyecto2 = row.getCell(2).getStringCellValue();
		String aplicacion2 = row.getCell(3).getStringCellValue();
		String operacion2 = row.getCell(5).getStringCellValue();
		String fqn2 = row.getCell(6).getStringCellValue();
		String tipo2 = row.getCell(8).getStringCellValue();
		String servicio2 = fqn2.contains(".")?fqn2.substring(0, fqn2.indexOf(".")):fqn2;
		
		String tipoRelacion = row.getCell(26)!=null?row.getCell(26).getStringCellValue():"";
		String fqnDependencia = row.getCell(20).getStringCellValue();
		
		if(proyecto2.equals("GTER") && !QuienLlamaActivos.serviciosObsoletos.contains(servicio2)){
			
			String tipoServicio = "";
			if(tipo2.equals("OPNJ")){
				tipoServicio = "SRNU";
			}else if(tipo2.equals("OPNS")){
				tipoServicio = "SRNS";
			}else if(tipo2.equals("OPPR")){
				tipoServicio = "SRPR";
			}else if(tipo2.equals("OPEX")){
				tipoServicio = "SREX";
			}else if(tipo2.equals("OPUT")){
				tipoServicio = "SRUT";
			}else if(tipo2.equals("ME")){
				tipoServicio = "RES";
			}else{
				tipoServicio = tipo2;
			}
			
			QuienLlamaBean servicioBean = null;
			
			if(QuienLlamaActivos.mapaServicioBean.get(servicio2 + "&" + tipoServicio) != null){
				servicioBean = QuienLlamaActivos.mapaServicioBean.get(servicio2 + "&" + tipoServicio);
			}else{
				servicioBean = new QuienLlamaBean(aplicacion2, null, null, tipoServicio, servicio2, null, null, new LinkedHashMap<String, QuienLlamaBean>(), null, null);
			}
			
			Map<String, QuienLlamaBean> mapaOperacionDependencias = new LinkedHashMap<String, QuienLlamaBean>();
			if(servicioBean.getMapaDependencias().size() > 0){
				mapaOperacionDependencias = servicioBean.getMapaDependencias();
			}
			
			QuienLlamaBean quienLlamaBean = null;
			if(mapaOperacionDependencias.get(fqn2) != null){	
				quienLlamaBean = mapaOperacionDependencias.get(fqn2);
			}else{
				quienLlamaBean = new QuienLlamaBean(aplicacion2, operacion2, fqn2, tipo2, servicio2, null, null, null, new LinkedHashMap<String, QuienLlamaBean>(), new LinkedHashMap<String, QuienLlamaBean>());
			}
			
			if(quienLlamaBean.getMapaDependenciasLlamados().get(fqnDependencia) == null){
				String aplicacionHijo = row.getCell(17).getStringCellValue();
				String fqnHijo = fqnDependencia;
				String operacionHijo = row.getCell(19).getStringCellValue();
				String servicioHijo = fqnHijo.contains(".")?fqnHijo.substring(0, fqnHijo.indexOf(".")):fqnHijo;
				String tipoHijo = row.getCell(22).getStringCellValue();
				
				QuienLlamaBean dependencia = new QuienLlamaBean(aplicacionHijo, operacionHijo, fqnHijo, tipoHijo, servicioHijo, tipoRelacion, null, null, null, null);
				
				Map<String, QuienLlamaBean> mapa = quienLlamaBean.getMapaDependenciasLlamados();
				mapa.put(fqnDependencia, dependencia);
				quienLlamaBean.setMapaDependenciasLlamados(mapa);
			}
			mapaOperacionDependencias.put(fqn2, quienLlamaBean);
			servicioBean.setMapaDependencias(mapaOperacionDependencias);
			QuienLlamaActivos.mapaServicioBean.put(servicio2 + "&" + tipoServicio, servicioBean);
		}
	}


	private static void crearCabecerasSheeAQuienLlamo(XSSFSheet sheet_aQuienLlamo, XSSFSheet sheet_migracionOS) {
		XSSFRow fila = sheet_aQuienLlamo.createRow(0);
		fila.createCell(indiceAplicacionAQuienLlamo).setCellValue("Aplicacion");
		
		fila.createCell(indiceServicioAQuienLlamo).setCellValue("Servicio");
		
		fila.createCell(indiceFqnAQuienLlamo).setCellValue("FQN");
		
		fila.createCell(indiceTipoAQuienLlamo).setCellValue("Tipo");
		
		fila.createCell(indiceAplicacionPadreAQuienLlamo).setCellValue("Aplicacion dependencia");
		
		fila.createCell(indiceFqnPadreAQuienLlamo).setCellValue("FQN dependencia");
		
		fila.createCell(indiceTipoPadreAQuienLlamo).setCellValue("Tipo dependencia");
		
		fila.createCell(indiceRelacionActualAQuienLlamo).setCellValue("Tipo relación actual");
		
		fila.createCell(indiceRelacionCorrectaAQuienLlamo).setCellValue("Tipo relación correcta");
		
		for(int i=0; i<numeroCeldasSheetAQuienLlamo; i++){
			fila.getCell(i).setCellStyle(QuienLlamaActivos.estiloCeldaCabecera);
		}
		fila.getCell(indiceAplicacionPadreAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaSepararPadreCabecera);
		
		
		XSSFRow fila2 = sheet_migracionOS.createRow(0);
		fila2.createCell(0).setCellValue("APLICACIÓN");
		fila2.getCell(0).setCellStyle(QuienLlamaActivos.estiloCeldaCabecera);
		
		fila2.createCell(1).setCellValue("TECNOLOGIA");
		fila2.getCell(1).setCellStyle(QuienLlamaActivos.estiloCeldaCabecera);
		
		fila2.createCell(2).setCellValue("FASE MIGRACION");
		fila2.getCell(2).setCellStyle(QuienLlamaActivos.estiloCeldaCabecera);
		
		fila2.createCell(3).setCellValue("TIPO");
		fila2.getCell(3).setCellStyle(QuienLlamaActivos.estiloCeldaCabecera);
		
		fila2.createCell(4).setCellValue("SERVICIO");
		fila2.getCell(4).setCellStyle(QuienLlamaActivos.estiloCeldaCabecera);
		
		fila2.createCell(5).setCellValue("OPERACIÓN");
		fila2.getCell(5).setCellStyle(QuienLlamaActivos.estiloCeldaCabecera);
		
		fila2.createCell(6).setCellValue("SE DESDOBLA OP");
		fila2.getCell(6).setCellStyle(QuienLlamaActivos.estiloCeldaCabecera);
	}


	private static void aplicarFormatoCeldasHojaAQuienLlamo(int numeroFilaEmpiezaAQuienLlamo, XSSFSheet sheet_destinoHoja, int indiceFilaAQuienLlamo, QuienLlamaBean servicioBean, Set<String> tipoRelacionesUsadas) {
		sheet_destinoHoja.getRow(indiceFilaAQuienLlamo-1).getCell(indiceFqnAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaOPFinServicio);
		sheet_destinoHoja.getRow(indiceFilaAQuienLlamo-1).getCell(indiceTipoAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaOPFinServicio);
		
		try{
			sheet_destinoHoja.addMergedRegion(new CellRangeAddress(numeroFilaEmpiezaAQuienLlamo, indiceFilaAQuienLlamo-1, indiceAplicacionAQuienLlamo,indiceAplicacionAQuienLlamo));
			sheet_destinoHoja.addMergedRegion(new CellRangeAddress(numeroFilaEmpiezaAQuienLlamo, indiceFilaAQuienLlamo-1, indiceServicioAQuienLlamo,indiceServicioAQuienLlamo));	
		}catch(Exception e){
			
		}
		
		String tecnologiaQueDeberiaSer = "SOAP+LOCAL";
		if(servicioBean.getTipo().equals("RT") || servicioBean.getTipo().equals("JTNU") || servicioBean.getTipo().equals("MO") || 
				servicioBean.getTipo().equals("DAOB") || servicioBean.getTipo().equals("CGT") || servicioBean.getTipo().equals("CNT") || 
				servicioBean.getTipo().equals("CONF") || servicioBean.getTipo().equals("CJ")){
			tecnologiaQueDeberiaSer = "";
		}else if(servicioBean.getTipo().equals("RES") || servicioBean.getTipo().equals("SRPR")){
			tecnologiaQueDeberiaSer = "REST";
		}else{
			if("SRNU".equals(servicioBean.getTipo())){
				if(servicioBean.getTecnologiaInterfazCorrecta().equals("SOAP")){
					if(tipoRelacionesUsadas.size() == 1){
						tecnologiaQueDeberiaSer = tipoRelacionesUsadas.iterator().next();
						if(tecnologiaQueDeberiaSer.equals("SOAP")){
							servicioBean.setFaseMigracion("Fase 1");
						}else{
							servicioBean.setFaseMigracion("Fase 2");
						}
					}else if(tipoRelacionesUsadas.size() == 0){
						servicioBean.setFaseMigracion("Fase 1");
					}else if(tipoRelacionesUsadas.size() > 1){
						servicioBean.setFaseMigracion("Fase 2");
					}
				}else{
					servicioBean.setFaseMigracion("Fase 2");
				}
			}else if("SRUT".equals(servicioBean.getTipo())){
				servicioBean.setFaseMigracion("Fase 2");
			}
		}
		
		XSSFRow filaAplicarFormato = sheet_destinoHoja.getRow(indiceFilaAQuienLlamo-1);
		boolean celdaInterfazMal = false;
		
		for(int k=4; k<numeroCeldasSheetAQuienLlamo; k++){
			XSSFCell celda = filaAplicarFormato.getCell(k);
			if(celda == null){
				celda = filaAplicarFormato.createCell(k);
				celda.setCellStyle(QuienLlamaActivos.estiloCeldaFinServicio);
			}else if(k==indiceRelacionCorrectaAQuienLlamo){
				// si el tipo de relación actual es distinto al tipo de relación correcta, pintamos error
				if(!filaAplicarFormato.getCell(indiceRelacionActualAQuienLlamo).getStringCellValue().equals(celda.getStringCellValue())){
					celdaInterfazMal = true;
					filaAplicarFormato.getCell(indiceAplicacionPadreAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaSepararPadreFinServicio);
					filaAplicarFormato.getCell(indiceRelacionActualAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaSepararPadreFinServicio);
				}
				celda.setCellStyle(QuienLlamaActivos.estiloCeldaFinServicio);
			}else if(k!=indiceTipoAQuienLlamo){
				celda.setCellStyle(QuienLlamaActivos.estiloCeldaFinServicio);
			}
		}
		if(!celdaInterfazMal){
			filaAplicarFormato.getCell(indiceAplicacionPadreAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaSepararPadreFinServicio);
			filaAplicarFormato.getCell(indiceRelacionActualAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaSepararPadreFinServicio);
		}
	}


	private static void aplicarFormatoCeldasHojaQuienMeLlama(int numeroFilaEmpiezaQuienMeLlama, XSSFSheet sheet_quienMeLlama, int indiceFilaQuienMeLlama, QuienLlamaBean servicioBean, Set<String> listaInterfazLlamadaOPsServicio) {
		sheet_quienMeLlama.getRow(indiceFilaQuienMeLlama-1).getCell(indiceTipo).setCellStyle(estiloCeldaOPFinServicio);
		sheet_quienMeLlama.getRow(indiceFilaQuienMeLlama-1).getCell(indiceFqn).setCellStyle(estiloCeldaOPFinServicio);
		
		try{
			sheet_quienMeLlama.addMergedRegion(new CellRangeAddress(numeroFilaEmpiezaQuienMeLlama, indiceFilaQuienMeLlama-1, indiceAplicacion,indiceAplicacion));
			sheet_quienMeLlama.addMergedRegion(new CellRangeAddress(numeroFilaEmpiezaQuienMeLlama, indiceFilaQuienMeLlama-1, indiceServicio,indiceServicio));	
		}catch(Exception e){
			
		}
		
		// calculamos cuál debería ser la tecnología de interfaz correcta del servicio,
		// en función de las relaciones de sus operaciones con los llamantes
		String tecnologiaServicio = servicioBean.getTecnologiaInterfaz();
		String tecnologiaQueDeberiaSer = "SOAP+LOCAL";
		if(servicioBean.getTipo().equals("RT") || servicioBean.getTipo().equals("JTNU") || servicioBean.getTipo().equals("MO") || 
				servicioBean.getTipo().equals("DAOB") || servicioBean.getTipo().equals("CGT") || servicioBean.getTipo().equals("CNT") || 
				servicioBean.getTipo().equals("CONF") || servicioBean.getTipo().equals("CJ")){
			tecnologiaQueDeberiaSer = "";
		}else if(servicioBean.getTipo().equals("RES") || servicioBean.getTipo().equals("SRPR")){
			tecnologiaQueDeberiaSer = "REST";
		}else{
			if(listaInterfazLlamadaOPsServicio.size() == 1){
				tecnologiaQueDeberiaSer = listaInterfazLlamadaOPsServicio.iterator().next();
			}
		}
		servicioBean.setTecnologiaInterfazCorrecta(tecnologiaQueDeberiaSer);
		
		// comprobamos que la tecnología actual del servicio es la misma que la que debería ser
		boolean tecnologiaCorrecta = true;
		if(tecnologiaServicio != null && !tecnologiaServicio.equals(tecnologiaQueDeberiaSer)){
			tecnologiaCorrecta = false;
		}
		
		for(int k=numeroFilaEmpiezaQuienMeLlama; k<indiceFilaQuienMeLlama; k++){
			if(sheet_quienMeLlama.getRow(k).getCell(indiceTecnologiaCorrecta) == null){
				sheet_quienMeLlama.getRow(k).createCell(indiceTecnologiaCorrecta).setCellValue(tecnologiaQueDeberiaSer);
			}
			sheet_quienMeLlama.getRow(k).getCell(indiceTecnologiaActual).setCellStyle(estiloCeldaTecnologiaCorregida);
			sheet_quienMeLlama.getRow(k).getCell(indiceTecnologiaCorrecta).setCellStyle(tecnologiaCorrecta?estiloCeldaTecnologiaCorregida:estiloCeldaTecnologiaCorregidaError);
		}
		
		
		XSSFRow filaAplicarFormato = sheet_quienMeLlama.getRow(indiceFilaQuienMeLlama-1);
		boolean celdaInterfazMal = false;
		for(int k=4; k<numeroCeldasSheetQuienMeLlama; k++){
			XSSFCell celda = filaAplicarFormato.getCell(k);
			if(celda == null){
				celda = filaAplicarFormato.createCell(k);
				celda.setCellStyle(estiloCeldaFinServicio);
			}else if(k==indiceRelacionCorrecta){
				// si el tipo de relación actual es distinto al tipo de relación correcta, pintamos error
				if(!filaAplicarFormato.getCell(indiceRelacionActual).getStringCellValue().equals(celda.getStringCellValue())){
					celdaInterfazMal = true;
					filaAplicarFormato.getCell(indiceAplicacionPadre).setCellStyle(estiloCeldaSepararPadreFinServicio);
					filaAplicarFormato.getCell(indiceRelacionActual).setCellStyle(estiloCeldaSepararPadreFinServicio);
				}
				celda.setCellStyle(estiloCeldaFinServicio);
			}else if(k!=indiceTipo && k!=indiceTecnologiaActual){
				celda.setCellStyle(estiloCeldaFinServicio);
			}
		}
		if(!celdaInterfazMal){
			filaAplicarFormato.getCell(indiceAplicacionPadre).setCellStyle(estiloCeldaSepararPadreFinServicio);
			filaAplicarFormato.getCell(indiceRelacionActual).setCellStyle(estiloCeldaSepararPadreFinServicio);
		}
		
		if(numeroFilaEmpiezaQuienMeLlama > 0){
			int numeroFilaTermina = indiceFilaQuienMeLlama-1;
			try{
				sheet_quienMeLlama.addMergedRegion(new CellRangeAddress(numeroFilaEmpiezaQuienMeLlama, numeroFilaTermina, indiceTecnologiaActual,indiceTecnologiaActual));
				sheet_quienMeLlama.addMergedRegion(new CellRangeAddress(numeroFilaEmpiezaQuienMeLlama, numeroFilaTermina, indiceTecnologiaCorrecta,indiceTecnologiaCorrecta));
			}catch(Exception e){
			}finally{
				if(tecnologiaCorrecta){
					sheet_quienMeLlama.getRow(numeroFilaEmpiezaQuienMeLlama).getCell(indiceTecnologiaCorrecta).setCellStyle(estiloCeldaTecnologiaCorregida);	
				}else{
					sheet_quienMeLlama.getRow(numeroFilaEmpiezaQuienMeLlama).getCell(indiceTecnologiaCorrecta).setCellStyle(estiloCeldaTecnologiaCorregidaError);
				}
			}
		}
	}


	private static int crearFilaAQuienLlamo(XSSFSheet sheet_aQuienLlamo, XSSFRow filaQuienAQuienLlamo, QuienLlamaBean servicioBean, QuienLlamaBean operacionBean, Set<String> tipoRelacionesUsadas, int indiceFilaAquienLlamo, int numeroFilaEmpiezaAQuienLlamo) {
		if(operacionBean.getMapaDependenciasLlamados().size() == 0){
			
			filaQuienAQuienLlamo.createCell(indiceAplicacionAQuienLlamo).setCellValue(operacionBean.getAplicacion());
			filaQuienAQuienLlamo.getCell(indiceAplicacionAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaServicio);
			
			filaQuienAQuienLlamo.createCell(indiceServicioAQuienLlamo).setCellValue(servicioBean.getServicio());
			filaQuienAQuienLlamo.getCell(indiceServicioAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaServicio);
			
			filaQuienAQuienLlamo.createCell(indiceFqnAQuienLlamo).setCellValue(operacionBean.getOperacion());
			filaQuienAQuienLlamo.getCell(indiceFqnAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaOP);
			
			filaQuienAQuienLlamo.createCell(indiceTipoAQuienLlamo).setCellValue(operacionBean.getTipo());
			filaQuienAQuienLlamo.getCell(indiceTipoAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaOP);
			
			filaQuienAQuienLlamo.createCell(indiceAplicacionPadreAQuienLlamo).setCellValue("");
			filaQuienAQuienLlamo.getCell(indiceAplicacionPadreAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaSepararPadre);
			
			filaQuienAQuienLlamo.createCell(indiceRelacionActualAQuienLlamo).setCellValue("");
			filaQuienAQuienLlamo.getCell(indiceRelacionActualAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaSepararPadre);
			
			indiceFilaAquienLlamo++;
			filaQuienAQuienLlamo = sheet_aQuienLlamo.createRow(indiceFilaAquienLlamo);
			
		}else{
			for(Entry<String, QuienLlamaBean> mapaDependenciaBean: operacionBean.getMapaDependenciasLlamados().entrySet()){
				String dependencia = mapaDependenciaBean.getKey();
				
				filaQuienAQuienLlamo.createCell(indiceAplicacionAQuienLlamo).setCellValue(operacionBean.getAplicacion());
				filaQuienAQuienLlamo.getCell(indiceAplicacionAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaServicio);
				
				filaQuienAQuienLlamo.createCell(indiceServicioAQuienLlamo).setCellValue(servicioBean.getServicio());
				filaQuienAQuienLlamo.getCell(indiceServicioAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaServicio);
				
				filaQuienAQuienLlamo.createCell(indiceFqnAQuienLlamo).setCellValue(operacionBean.getOperacion());
				filaQuienAQuienLlamo.getCell(indiceFqnAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaOP);
				
				filaQuienAQuienLlamo.createCell(indiceTipoAQuienLlamo).setCellValue(operacionBean.getTipo());
				filaQuienAQuienLlamo.getCell(indiceTipoAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaOP);
				
				filaQuienAQuienLlamo.createCell(indiceAplicacionPadreAQuienLlamo).setCellValue(mapaDependenciaBean.getValue().getAplicacion());
				filaQuienAQuienLlamo.getCell(indiceAplicacionPadreAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaSepararPadre);
				
				filaQuienAQuienLlamo.createCell(indiceFqnPadreAQuienLlamo).setCellValue(dependencia);
				
				filaQuienAQuienLlamo.createCell(indiceTipoPadreAQuienLlamo).setCellValue(mapaDependenciaBean.getValue().getTipo());
				
				filaQuienAQuienLlamo.createCell(indiceRelacionActualAQuienLlamo).setCellValue(mapaDependenciaBean.getValue().getTipoRelacion());
				filaQuienAQuienLlamo.getCell(indiceRelacionActualAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaSepararPadre);
				
				String tipoRelacionCorrecta = calcularTipoRelacionCorrectaAQuienLlamo(mapaDependenciaBean.getValue(), operacionBean, filaQuienAQuienLlamo);
				if(!tipoRelacionCorrecta.equals("")){
					tipoRelacionesUsadas.add(tipoRelacionCorrecta);	
				}
				
				indiceFilaAquienLlamo++;
				filaQuienAQuienLlamo = sheet_aQuienLlamo.createRow(indiceFilaAquienLlamo);
			}
		}
		
		int numeroFilaTermina2 = indiceFilaAquienLlamo-1;
		try{
			sheet_aQuienLlamo.addMergedRegion(new CellRangeAddress(numeroFilaEmpiezaAQuienLlamo, numeroFilaTermina2, indiceFqnAQuienLlamo,indiceFqnAQuienLlamo));
			sheet_aQuienLlamo.addMergedRegion(new CellRangeAddress(numeroFilaEmpiezaAQuienLlamo, numeroFilaTermina2, indiceTipoAQuienLlamo,indiceTipoAQuienLlamo));	
		}catch(Exception e){
			
		}
		return indiceFilaAquienLlamo;
	}


	private static int crearFilaQuienMeLlama(XSSFSheet sheet_quienMeLlama, XSSFRow filaQuienMeLlama, QuienLlamaBean servicioBean, QuienLlamaBean operacionBean, 
			Set<String> listaInterfazLlamadaOPsServicio, int indiceFilaQuienMeLlama, int numeroFilaEmpieza2) {
		if(operacionBean.getMapaDependenciasLlamantes().size() == 0){
			
			filaQuienMeLlama.createCell(indiceAplicacion).setCellValue(operacionBean.getAplicacion());
			filaQuienMeLlama.getCell(indiceAplicacion).setCellStyle(estiloCeldaServicio);
			
			filaQuienMeLlama.createCell(indiceTecnologiaActual).setCellValue(servicioBean.getTecnologiaInterfaz());
			
			filaQuienMeLlama.createCell(indiceServicio).setCellValue(servicioBean.getServicio());
			filaQuienMeLlama.getCell(indiceServicio).setCellStyle(estiloCeldaServicio);
			
			filaQuienMeLlama.createCell(indiceFqn).setCellValue(operacionBean.getOperacion());
			filaQuienMeLlama.getCell(indiceFqn).setCellStyle(estiloCeldaOP);
			
			filaQuienMeLlama.createCell(indiceTipo).setCellValue(operacionBean.getTipo());
			filaQuienMeLlama.getCell(indiceTipo).setCellStyle(estiloCeldaOP);
			
			filaQuienMeLlama.createCell(indiceAplicacionPadre).setCellValue("");
			filaQuienMeLlama.getCell(indiceAplicacionPadre).setCellStyle(estiloCeldaSepararPadre);
			
			filaQuienMeLlama.createCell(indiceRelacionActual).setCellValue("");
			filaQuienMeLlama.getCell(indiceRelacionActual).setCellStyle(estiloCeldaSepararPadre);
			
			indiceFilaQuienMeLlama++;
			filaQuienMeLlama = sheet_quienMeLlama.createRow(indiceFilaQuienMeLlama);
			
		}else{
			Set<String> listaTipoRelacionesOperacion = new HashSet<String>();
			// recorremos los llamantes de la operación
			for(Entry<String, QuienLlamaBean> mapaDependenciaBean: operacionBean.getMapaDependenciasLlamantes().entrySet()){
				String dependencia = mapaDependenciaBean.getKey();
				QuienLlamaBean dependenciaBean = mapaDependenciaBean.getValue();
				
				filaQuienMeLlama.createCell(indiceAplicacion).setCellValue(operacionBean.getAplicacion());
				filaQuienMeLlama.getCell(indiceAplicacion).setCellStyle(estiloCeldaServicio);
				
				filaQuienMeLlama.createCell(indiceTecnologiaActual).setCellValue(servicioBean.getTecnologiaInterfaz());
				
				filaQuienMeLlama.createCell(indiceServicio).setCellValue(servicioBean.getServicio());
				filaQuienMeLlama.getCell(indiceServicio).setCellStyle(estiloCeldaServicio);
				
				filaQuienMeLlama.createCell(indiceFqn).setCellValue(operacionBean.getOperacion());
				filaQuienMeLlama.getCell(indiceFqn).setCellStyle(estiloCeldaOP);
				
				filaQuienMeLlama.createCell(indiceTipo).setCellValue(operacionBean.getTipo());
				filaQuienMeLlama.getCell(indiceTipo).setCellStyle(estiloCeldaOP);
				
				filaQuienMeLlama.createCell(indiceAplicacionPadre).setCellValue(dependenciaBean.getAplicacion());
				filaQuienMeLlama.getCell(indiceAplicacionPadre).setCellStyle(estiloCeldaSepararPadre);
				
				filaQuienMeLlama.createCell(indiceFqnPadre).setCellValue(dependencia);
				
				filaQuienMeLlama.createCell(indiceTipoPadre).setCellValue(dependenciaBean.getTipo());
				
				filaQuienMeLlama.createCell(indiceRelacionActual).setCellValue(dependenciaBean.getTipoRelacion());
				filaQuienMeLlama.getCell(indiceRelacionActual).setCellStyle(estiloCeldaSepararPadre);
				
				// calculamos el tipo de relación que debería ser
				String tipoRelacionCorrecta = calcularTipoRelacionCorrectaQuienMeLlama(dependenciaBean, operacionBean, filaQuienMeLlama);
				if(!tipoRelacionCorrecta.equals("")){
					listaInterfazLlamadaOPsServicio.add(tipoRelacionCorrecta);
					if(operacionBean.getTipo().equals("OPNJ")){
						listaTipoRelacionesOperacion.add(tipoRelacionCorrecta);
					}
				}
				
				indiceFilaQuienMeLlama++;
				filaQuienMeLlama = sheet_quienMeLlama.createRow(indiceFilaQuienMeLlama);
			}
			
			// si la relación correcta entre OP -> llamante es SOAP, entonces esa OP es propensa a desdoblarse. Eso dependerá
			// de si el servicio es SOAP+LOCAL, cosa qeu no sabremos hasta más adelante, cuando hayamos evaluado todas las relaciones
			// de todas sus operaciones
			if(listaTipoRelacionesOperacion.size() > 0 && listaTipoRelacionesOperacion.contains("SOAP")){
				operacionBean.setSeDesdobla(true);
        	}
		}
		
		int numeroFilaTermina2 = indiceFilaQuienMeLlama-1;
		
		try{
			sheet_quienMeLlama.addMergedRegion(new CellRangeAddress(numeroFilaEmpieza2, numeroFilaTermina2, indiceFqn,indiceFqn));
			sheet_quienMeLlama.addMergedRegion(new CellRangeAddress(numeroFilaEmpieza2, numeroFilaTermina2, indiceTipo,indiceTipo));	
		}catch(Exception e){
			
		}
		return indiceFilaQuienMeLlama;
	}


	private static void obtenerServiciosYOperaciones(XSSFWorkbook wb) {
		
		Set<String> listaTipoServicio = new HashSet<String>();
		//listaTipoServicio.add("CGT");
		//listaTipoServicio.add("CJ");
		//listaTipoServicio.add("CNT");
		//listaTipoServicio.add("CONF");
		//listaTipoServicio.add("DAOB");
		//listaTipoServicio.add("JTNU");
		//listaTipoServicio.add("MO");
		listaTipoServicio.add("RES");
		//listaTipoServicio.add("RT");
		listaTipoServicio.add("SREX");
		listaTipoServicio.add("SRNS");
		listaTipoServicio.add("SRNU");
		listaTipoServicio.add("SRPR");
		listaTipoServicio.add("SRUT");
		
		XSSFSheet sheet_operaciones = wb.getSheetAt(0);
		Map<String, String> mapaServicioTecnologia = new LinkedHashMap<String, String>();
		
		for(Row filaActivo: sheet_operaciones){
			if(filaActivo.getRowNum() > 0){
				String aplicacion = filaActivo.getCell(3).getStringCellValue();
				String fqn = filaActivo.getCell(4).getStringCellValue();
				String operacion = filaActivo.getCell(5).getStringCellValue();
				String servicio = fqn.contains(".")?fqn.substring(0, fqn.indexOf(".")):fqn;
				String tipo = filaActivo.getCell(11).getStringCellValue();
				
				if(!serviciosObsoletos.contains(servicio)){
					if(listaTipoServicio.contains(tipo)){
						String servicioHojaActivos = filaActivo.getCell(5).getStringCellValue();
						String tipoHojaActivos = filaActivo.getCell(11).getStringCellValue();
						String tecnologiaInterfaz = filaActivo.getCell(27).getStringCellValue();
						
						if(!serviciosObsoletos.contains(servicioHojaActivos)){
							if(mapaServicioTecnologia.get(servicioHojaActivos + "&" + tipoHojaActivos) == null){
								if(servicioHojaActivos.equals("SRV_SPInteractionMng")){
									mapaServicioTecnologia.put(servicioHojaActivos + "&" + tipoHojaActivos, "REST+SOAP");
								}else if(servicioHojaActivos.equals("SRV_SPConnectionXML")){
									mapaServicioTecnologia.put(servicioHojaActivos + "&" + tipoHojaActivos, "SOAP+LOCAL");
								}else if(!tipoHojaActivos.equals("CGT") && !tipoHojaActivos.equals("CNT") && !tipoHojaActivos.equals("CONF") && !tipoHojaActivos.equals("MO") && !tipoHojaActivos.equals("RT") && !tipoHojaActivos.equals("JTNU")){
									mapaServicioTecnologia.put(servicioHojaActivos + "&" + tipoHojaActivos, tecnologiaInterfaz);
								}
							}
						}
					}else{
						String tipoServicio = "";
						if(tipo.equals("OPNJ")){
							tipoServicio = "SRNU";
						}else if(tipo.equals("OPNS")){
							tipoServicio = "SRNS";
						}else if(tipo.equals("OPPR")){
							tipoServicio = "SRPR";
						}else if(tipo.equals("OPEX")){
							tipoServicio = "SREX";
						}else if(tipo.equals("OPUT")){
							tipoServicio = "SRUT";
						}else if(tipo.equals("ME")){
							tipoServicio = "RES";
						}else{
							tipoServicio = tipo;
						}
						
						QuienLlamaBean servicioBean = null;
						if(mapaServicioBean.get(servicio + "&" + tipoServicio) != null){
							servicioBean = mapaServicioBean.get(servicio + "&" + tipoServicio);
						}else{
							servicioBean = new QuienLlamaBean(aplicacion, null, null, tipoServicio, servicio, null, mapaServicioTecnologia.get(servicio + "&" + tipoServicio), new LinkedHashMap<String, QuienLlamaBean>(), null, null);
						}
						
						Map<String, QuienLlamaBean> mapaOperacionDependencias = new LinkedHashMap<String, QuienLlamaBean>();
						if(servicioBean.getMapaDependencias().size() > 0){
							mapaOperacionDependencias = servicioBean.getMapaDependencias();
						}
						
						QuienLlamaBean quienLlamaBean = null;
						if(mapaOperacionDependencias.get(fqn) != null){
							quienLlamaBean = mapaOperacionDependencias.get(fqn);
						}else{
							quienLlamaBean = new QuienLlamaBean(aplicacion, operacion, fqn, tipo, servicio, null, null, null, new LinkedHashMap<String, QuienLlamaBean>(), new LinkedHashMap<String, QuienLlamaBean>());
						}
						
						mapaOperacionDependencias.put(fqn, quienLlamaBean);
						servicioBean.setMapaDependencias(mapaOperacionDependencias);
						mapaServicioBean.put(servicio + "&" + tipoServicio, servicioBean);
					}
				}
			}
		}
		mapaServicioTecnologia = null;
	}


	private static void crearCabecerasSheeQuienMeLlama(XSSFSheet sheet_destino){
		XSSFRow fila = sheet_destino.createRow(0);
		fila.createCell(indiceAplicacion).setCellValue("Aplicacion");
		
		fila.createCell(indiceTecnologiaActual).setCellValue("Tecnología actual SRV");
		
		fila.createCell(indiceTecnologiaCorrecta).setCellValue("Tecnología correcta SRV");
		
		fila.createCell(indiceServicio).setCellValue("Servicio");
		
		fila.createCell(indiceFqn).setCellValue("FQN");
		
		fila.createCell(indiceTipo).setCellValue("Tipo");
		
		fila.createCell(indiceAplicacionPadre).setCellValue("Aplicacion padre");
		
		fila.createCell(indiceFqnPadre).setCellValue("FQN padre");
		
		fila.createCell(indiceTipoPadre).setCellValue("Tipo padre");
		
		fila.createCell(indiceRelacionActual).setCellValue("Tipo relación actual");
		
		fila.createCell(indiceRelacionCorrecta).setCellValue("Tipo relación correcta");
		
		
		for(int i=0; i<numeroCeldasSheetQuienMeLlama; i++){
			fila.getCell(i).setCellStyle(estiloCeldaCabecera);
		}
		fila.getCell(indiceAplicacionPadre).setCellStyle(estiloCeldaSepararPadreCabecera);
		fila.getCell(indiceRelacionActual).setCellStyle(estiloCeldaSepararPadreCabecera);
	}
	
	
	private static String calcularTipoRelacionCorrectaQuienMeLlama(QuienLlamaBean beanPadre, QuienLlamaBean beanHijo, XSSFRow fila) {
		String tipoRelacionCorrecta = "SOAP";
		if(beanHijo.getTipo().equals("RT") || beanHijo.getTipo().equals("CGT") || beanHijo.getTipo().equals("CNT") || beanHijo.getTipo().equals("CONF")
				|| beanPadre.getTipo().equals("RT") || beanPadre.getTipo().equals("CGT") || beanPadre.getTipo().equals("CNT") || beanPadre.getTipo().equals("CONF")){
			fila.createCell(indiceRelacionCorrecta).setCellValue("");
			tipoRelacionCorrecta = "";
		}else if(beanHijo.getTipo().equals("MO") || beanPadre.getTipo().equals("MO") || beanHijo.getTipo().equals("DAO") || beanPadre.getTipo().equals("DAO")){
			fila.createCell(indiceRelacionCorrecta).setCellValue("LOCAL");
			tipoRelacionCorrecta = "LOCAL";
		}else{
			if(beanHijo.getAplicacion().equals(beanPadre.getAplicacion()) && !beanHijo.getTipo().equals("OPNS") && !beanPadre.getTipo().equals("OPNS")){
				if(beanHijo.getTipo().equals(beanPadre.getTipo()) || (beanHijo.getTipo().equals("OPNJ") && beanPadre.getTipo().equals("JTNU")) || (beanHijo.getTipo().equals("JTNU") && beanPadre.getTipo().equals("OPNJ")) || 
						(beanHijo.getTipo().equals("OPNJ") && beanPadre.getTipo().equals("ME")) || (beanHijo.getTipo().equals("ME") && beanPadre.getTipo().equals("OPNJ")
								|| (beanHijo.getTipo().equals("OPNJ") && beanPadre.getTipo().equals("OPUT")) || (beanHijo.getTipo().equals("OPUT") && beanPadre.getTipo().equals("OPNJ")))	){
					fila.createCell(indiceRelacionCorrecta).setCellValue("LOCAL");
					tipoRelacionCorrecta = "LOCAL";
				}else if(!beanHijo.getTipo().equals(beanPadre.getTipo())){
					fila.createCell(indiceRelacionCorrecta).setCellValue("SOAP");
				}
			}else{
				fila.createCell(indiceRelacionCorrecta).setCellValue("SOAP");
			}
			if(!fila.getCell(indiceRelacionActual).getStringCellValue().equals(fila.getCell(indiceRelacionCorrecta).getStringCellValue())){
				fila.getCell(indiceRelacionCorrecta).setCellStyle(estiloCeldaError);
			}
		}
		return tipoRelacionCorrecta;
	}
	
	
	private static String calcularTipoRelacionCorrectaAQuienLlamo(QuienLlamaBean beanPadre, QuienLlamaBean beanHijo, XSSFRow fila) {
		
		String tipoRelacionCorrecta = "SOAP";
		if(beanHijo.getTipo().equals("RT") || beanHijo.getTipo().equals("CGT") || beanHijo.getTipo().equals("CNT") || beanHijo.getTipo().equals("CONF")
				|| beanPadre.getTipo().equals("CGT") || beanPadre.getTipo().equals("CNT") || beanPadre.getTipo().equals("CONF")){
			fila.createCell(indiceRelacionCorrectaAQuienLlamo).setCellValue("");
			tipoRelacionCorrecta = "";
		}else if(beanHijo.getTipo().equals("MO") || beanPadre.getTipo().equals("MO") || beanHijo.getTipo().equals("DAO") || beanPadre.getTipo().equals("DAO")){
			fila.createCell(indiceRelacionCorrectaAQuienLlamo).setCellValue("LOCAL");
			tipoRelacionCorrecta = "LOCAL";
		}else if(beanPadre.getTipo().equals("RT")){
			fila.createCell(indiceRelacionCorrectaAQuienLlamo).setCellValue("SOAP");
			tipoRelacionCorrecta = "SOAP";
		}else{
			if(beanHijo.getAplicacion().equals(beanPadre.getAplicacion()) && !beanHijo.getTipo().equals("OPNS") && !beanPadre.getTipo().equals("OPNS")){
				if(beanHijo.getTipo().equals(beanPadre.getTipo()) || (beanHijo.getTipo().equals("OPNJ") && beanPadre.getTipo().equals("JTNU")) || (beanHijo.getTipo().equals("JTNU") && beanPadre.getTipo().equals("OPNJ")) || 
						(beanHijo.getTipo().equals("OPNJ") && beanPadre.getTipo().equals("ME")) || (beanHijo.getTipo().equals("ME") && beanPadre.getTipo().equals("OPNJ"))	){
					fila.createCell(indiceRelacionCorrectaAQuienLlamo).setCellValue("LOCAL");
					tipoRelacionCorrecta = "LOCAL";
				}else if(!beanHijo.getTipo().equals(beanPadre.getTipo())){
					fila.createCell(indiceRelacionCorrectaAQuienLlamo).setCellValue("SOAP");
				}
			}else{
				fila.createCell(indiceRelacionCorrectaAQuienLlamo).setCellValue("SOAP");
			}
			if(!fila.getCell(indiceRelacionActualAQuienLlamo).getStringCellValue().equals(fila.getCell(indiceRelacionCorrectaAQuienLlamo).getStringCellValue())){
				fila.getCell(indiceRelacionCorrectaAQuienLlamo).setCellStyle(QuienLlamaActivos.estiloCeldaError);
			}
		}
		
		return tipoRelacionCorrecta;
	}
	
	
	private static void crearEstilosCelda(XSSFWorkbook workbook_destino){
		XSSFFont font = workbook_destino.createFont();
		font.setBold(true);
		font.setItalic(true);
		font.setFontHeight(12);
		estiloCeldaError = workbook_destino.createCellStyle();
		estiloCeldaError.setFillForegroundColor(new XSSFColor(new java.awt.Color(205,222,180), new DefaultIndexedColorMap()));
		estiloCeldaError.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		estiloCeldaSepararPadreError = workbook_destino.createCellStyle();
		estiloCeldaSepararPadreError.setFillForegroundColor(new XSSFColor(new java.awt.Color(205,222,180), new DefaultIndexedColorMap()));
		estiloCeldaSepararPadreError.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloCeldaSepararPadreError.setBorderLeft(BorderStyle.MEDIUM);
		
		XSSFFont font2 = workbook_destino.createFont();
		font2.setBold(true);
		font2.setFontHeight(14);
		estiloCeldaCabecera = workbook_destino.createCellStyle();
		estiloCeldaCabecera.setFont(font2);
		estiloCeldaCabecera.setBorderBottom(BorderStyle.MEDIUM);
		
		estiloCeldaSepararPadreCabecera = workbook_destino.createCellStyle();
		estiloCeldaSepararPadreCabecera.setFont(font2);
		estiloCeldaSepararPadreCabecera.setBorderBottom(BorderStyle.MEDIUM);
		estiloCeldaSepararPadreCabecera.setBorderLeft(BorderStyle.MEDIUM);
		
		estiloCeldaFinServicio = workbook_destino.createCellStyle();
		estiloCeldaFinServicio.setBorderBottom(BorderStyle.THIN);
		
		estiloCeldaSepararPadreFinServicio = workbook_destino.createCellStyle();
		estiloCeldaSepararPadreFinServicio.setBorderBottom(BorderStyle.THIN);
		estiloCeldaSepararPadreFinServicio.setBorderLeft(BorderStyle.MEDIUM);
		
		estiloCeldaSepararPadre = workbook_destino.createCellStyle();
		estiloCeldaSepararPadre.setBorderLeft(BorderStyle.MEDIUM);
		
		estiloCeldaTecnologiaCorregida = workbook_destino.createCellStyle();
		estiloCeldaTecnologiaCorregida.setAlignment(HorizontalAlignment.CENTER);
		estiloCeldaTecnologiaCorregida.setVerticalAlignment(VerticalAlignment.CENTER);
		estiloCeldaTecnologiaCorregida.setBorderBottom(BorderStyle.THIN);
		
		estiloCeldaTecnologiaCorregidaError = workbook_destino.createCellStyle();
		estiloCeldaTecnologiaCorregidaError.setFillForegroundColor(new XSSFColor(new java.awt.Color(205,222,180), new DefaultIndexedColorMap()));
		estiloCeldaTecnologiaCorregidaError.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloCeldaTecnologiaCorregidaError.setAlignment(HorizontalAlignment.CENTER);
		estiloCeldaTecnologiaCorregidaError.setVerticalAlignment(VerticalAlignment.CENTER);
		estiloCeldaTecnologiaCorregidaError.setBorderBottom(BorderStyle.THIN);
		
		estiloCeldaOP = workbook_destino.createCellStyle();
		estiloCeldaOP.setAlignment(HorizontalAlignment.LEFT);
		estiloCeldaOP.setVerticalAlignment(VerticalAlignment.TOP);
		
		estiloCeldaOPFinServicio = workbook_destino.createCellStyle();
		estiloCeldaOPFinServicio.setAlignment(HorizontalAlignment.LEFT);
		estiloCeldaOPFinServicio.setVerticalAlignment(VerticalAlignment.TOP);
		estiloCeldaOPFinServicio.setBorderBottom(BorderStyle.THIN);
		
		estiloCeldaServicio = workbook_destino.createCellStyle();
		estiloCeldaServicio.setAlignment(HorizontalAlignment.LEFT);
		estiloCeldaServicio.setVerticalAlignment(VerticalAlignment.TOP);
		estiloCeldaServicio.setBorderBottom(BorderStyle.THIN);
		
	}
	
	
	private static List<String> obtenerObsoletos(){
		List<String> listaObsoletos = new ArrayList<String>();
		listaObsoletos.add("SRV_FindBISIActionbyBISpecType");
		listaObsoletos.add("SRV_FindBusServSpecBySPMsgAction");
		//listaObsoletos.add("SRV_FindSpecInfo");
		listaObsoletos.add("SRV_FindSPOrdeSpecItemRelatToCat");
		listaObsoletos.add("SRV_FindSPPorOrdSItBySPPorByOrdSI");
		listaObsoletos.add("SRV_FindSPPortOrdSpeIteRelaToCtg");
		listaObsoletos.add("SRV_GenerateSPReqOrderBillInvoice");
		listaObsoletos.add("SRV_GenerateSPReqOrderFulfillment"); // no movido en ARIS
		listaObsoletos.add("SRV_GenerateSPReqOrderResCapDeliv");
		listaObsoletos.add("SRV_GeneSPReqOrderRepairsAndWorks");
		listaObsoletos.add("SRV_GenSPReqOrderNumberMana");
		listaObsoletos.add("SRV_GetSPCharacteristicValue");
		listaObsoletos.add("SRV_IdenSPartIntDFRecConsul");
		listaObsoletos.add("SRV_IdenSupPartIntDatForNumMan");
		listaObsoletos.add("SRV_IdenSupPartIntDFPChaSt");
		//listaObsoletos.add("SRV_IdentifyPRTE");
		listaObsoletos.add("SRV_IdentifySPBillInvoice");
		listaObsoletos.add("SRV_IdentifySPFulfillment");
		listaObsoletos.add("SRV_IdentifySPInteractDataFormChSt");
		listaObsoletos.add("SRV_IdentifySPIntResCapDel");
		listaObsoletos.add("SRV_IdentifySPIntResCapDelCancel");
		listaObsoletos.add("SRV_IdentifySPResCapDelivery");
		listaObsoletos.add("SRV_IdentSPInteractDataFormatFulfm");
		listaObsoletos.add("SRV_IdentSPInteractionDFPorta");
		//listaObsoletos.add("SRV_MediatePortaINFA");
		listaObsoletos.add("SRV_MediateSPInteractDataFormatBC");
		listaObsoletos.add("SRV_MediateSPInteractFulfillment");
		listaObsoletos.add("SRV_MediateSPInteractFulfmtChngSt");
		//listaObsoletos.add("SRV_MediateSPInteraction");
		listaObsoletos.add("SRV_MedSupPartIntFPortaAsin");
		listaObsoletos.add("SRV_MedSupPartIntMobPorLis");
		listaObsoletos.add("SRV_MedSupPartIntMPConsulta");
		listaObsoletos.add("SRV_MedSupPartIntMPFullfill");
		listaObsoletos.add("SRV_MedSupPartIntNumberManan");
		listaObsoletos.add("SRV_ModBIRelshiPortaByCustOrder"); // no movido en ARIS
		listaObsoletos.add("SRV_ModifyFinancialSPOrder");
		listaObsoletos.add("SRV_ModifySPOrder");
		listaObsoletos.add("SRV_OrchestrateSPInteracWebSrvResp");
		listaObsoletos.add("SRV_OrcheSupPartNumManaIntLis");
		listaObsoletos.add("SRV_OrcheSupPartPortInteLis");
		listaObsoletos.add("SRV_OrcheSupPartPortSinInte");
		//listaObsoletos.add("SRV_OrcheSupPortaINFA");
		listaObsoletos.add("SRV_PrePortSupParIntRes");
		listaObsoletos.add("SRV_RulesSPMsgMng");
		listaObsoletos.add("SRV_SPProductMng");
		listaObsoletos.add("SRV_SPProductTelcoOUTMng");
		listaObsoletos.add("SRV_UpdateSPPortaOrderBIRelShip");
		listaObsoletos.add("SRV_UpdateSPPortOrdItemBIIRefBII");
		
		listaObsoletos.add("SRV_IdenSupPartIntDFRecCanc");
		listaObsoletos.add("SRV_ExtInfoPortaPRTE"); // no estoy seguro
		listaObsoletos.add("SRV_ManageSPUserAPI"); // no estoy seguro
		listaObsoletos.add("SRV_OperacionesBBDD");
		listaObsoletos.add("SRV_SingleSignOn");
		listaObsoletos.add("SRV_SPRequisitionFromBBDD");
		
		return listaObsoletos;
	}
}