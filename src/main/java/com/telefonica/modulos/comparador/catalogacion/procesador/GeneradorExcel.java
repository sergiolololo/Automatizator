package com.telefonica.modulos.comparador.catalogacion.procesador;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import com.telefonica.modulos.comparador.catalogacion.pantalla.Interfaz;
import com.telefonica.modulos.comparador.catalogacion.pantalla.PanelTablas;
import com.telefonica.modulos.comparador.catalogacion.utils.Connection;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.SAXException;

public class GeneradorExcel {

	private static XSSFWorkbook workbook;
	private static XSSFCellStyle estiloColumnaCabecera;
	private static XSSFCellStyle estiloColumnaValorEDCNormal;
	private static XSSFCellStyle estiloColumnaValorEPRNormal;
	private static XSSFCellStyle estiloColumnaValorEDCDiferencia;
	private static XSSFCellStyle estiloColumnaValorEPRDiferencia;
	private static XSSFCellStyle estiloColumnaValorECEDiferencia;
	private static XSSFCellStyle estiloColumnaValorEINDiferencia;
	
	private static XSSFCellStyle estiloColumnaEntornoEDC;
	private static XSSFCellStyle estiloColumnaEntornoEPR;
	private static XSSFCellStyle estiloColumnaEntornoEIN;
	private static XSSFCellStyle estiloColumnaEntornoECE;
	
	private static XSSFCellStyle estiloCabeceraResumenDerecha;
	private static XSSFCellStyle estiloCabeceraResumenIzquierda;
	
	private static XSSFCellStyle estiloCabeceraTablaResumenDerecha;
	private static XSSFCellStyle estiloCabeceraTablaResumenIzquierda;
	
	private static XSSFCellStyle estiloNormalResumenBordeIzquierdo;
	private static XSSFCellStyle estiloNormalResumenBordeDerecho;
	
	private static XSSFCellStyle estiloNormalResumenBordeIzquierdoAbajo;
	private static XSSFCellStyle estiloNormalResumenBordeDerechoAbajo;
	
	private static Map<String, Map<String, Integer>> mapaTablaColumnas;
	private static Map<String, Integer> mapaColumnaNumeroDiscrepancias;
	
	public static void generarLibroExcel(File dirDestino) throws ClassNotFoundException, SQLException, SAXException, IOException, ParserConfigurationException {
		
		try{
			List<String> listaFicherosTablas = new ArrayList<String>();
			listaFicherosTablas.add("Tablas_completas/tablas_INFTER1");
			listaFicherosTablas.add("Tablas_completas/tablas_PRVTER1");
			listaFicherosTablas.add("Tablas_completas/tablas_TERCBS1");
			
			workbook = new XSSFWorkbook();
			createStylesExcel();
			workbook.createSheet("Resumen");
			for(String fichero: listaFicherosTablas) {
				String usuarioBBDD = fichero.substring(fichero.lastIndexOf("_") + 1);
				processCompare(fichero, usuarioBBDD);
			}
			
			// Write the output to a file
	        FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
	        workbook.write(fileOut);
	        fileOut.close();

	        // Closing the workbook
	        workbook.close();
			
		}catch(Exception e){
		}
		finally{
			Connection.closeConnection();
		}
	}
		
	private static void processCompare(String fichero, String usuarioBBDD) throws ClassNotFoundException, SQLException {
		try{
			mapaTablaColumnas = new LinkedHashMap<String, Map<String,Integer>>();
			InputStream hola = Interfaz.class.getClassLoader().getResourceAsStream(fichero);
			if(hola != null) {
				InputStreamReader streamReader = new InputStreamReader(hola);
				BufferedReader in = new BufferedReader(streamReader);
				String line = in.readLine();
				
				while(line != null && !line.startsWith("--")) {
	            	
	            	String lineaPartida = line.substring(line.indexOf("FROM ")).substring(line.substring(line.indexOf("FROM ")).indexOf(" ") + 1);
	            	String nombreTabla = lineaPartida.substring(0, lineaPartida.indexOf(" ORDER"));
	            	
	            	if(PanelTablas.listaTablasSeleccionadas.contains(nombreTabla)) {
	            		if(mapaTablaColumnas.get(nombreTabla) != null){
							mapaColumnaNumeroDiscrepancias = mapaTablaColumnas.get(nombreTabla);
						}else{
							mapaColumnaNumeroDiscrepancias = new LinkedHashMap<String, Integer>();
						}
		            	
		            	XSSFSheet sheet = workbook.createSheet(nombreTabla);
		            	
		            	Map<Long, Map<String, String>> mapaResultSetEDC = getResults(line, "EDC", usuarioBBDD);
		            	Map<Long, Map<String, String>> mapaResultSetEIN = getResults(line, "EIN", usuarioBBDD);
		            	Map<Long, Map<String, String>> mapaResultSetECE = getResults(line, "ECE", usuarioBBDD);
		            	//Map<Long, Map<String, String>> mapaResultSetEPR = mapaResultadosEPR.get(nombreTabla);
		    			
		            	//SortedSet<Long> idsFilasEDC = new TreeSet<>(mapaResultSetEDC.keySet());
		            	SortedSet<Long> idsFilasEIN = new TreeSet<>(mapaResultSetEIN.keySet());
		    			
		            	int posicionFila=0;
		            	int numeroColumnas = 0;
		            	// recorremos cada fila de la tabla
		    			for (Long idFilaEIN : idsFilasEIN) {
		    				
		    				numeroColumnas = crearFilas(sheet, idFilaEIN, mapaResultSetEDC, mapaResultSetEIN, mapaResultSetECE, nombreTabla, true, posicionFila, numeroColumnas, mapaResultSetEIN);
							posicionFila++;
		    			}
		    			
		    			int rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
		    			// creamos una fila vacía para separar las discrepancias con las filas que no existen en EIN
		    			XSSFRow filaSeparacion = sheet.createRow(rowTotal);
		    			List<Long> listaIdsNoEncontradosEnEIN = new ArrayList<Long>();
		    			for (Long idFilaEDC : mapaResultSetEDC.keySet()) {
		    				if(mapaResultSetEIN.get(idFilaEDC) == null){
		    					boolean encontrado = false;
		    					for(int i=0; i<listaIdsNoEncontradosEnEIN.size() && !encontrado; i++){
		    						if(listaIdsNoEncontradosEnEIN.get(i).equals(idFilaEDC)){
		    							encontrado = true;
		    						}
		    					}
		    					if(!encontrado){
		    						listaIdsNoEncontradosEnEIN.add(idFilaEDC);
		    						//crearFilas(sheet, idFilaEDC, mapaResultSetEDC, mapaResultSetEIN, mapaResultSetECE, mapaResultSetEPR, nombreTabla, false, out, posicionFila, numeroColumnas, mapaResultSetEDC);
		    						crearFilas(sheet, idFilaEDC, mapaResultSetEDC, mapaResultSetEIN, mapaResultSetECE, nombreTabla, false, posicionFila, numeroColumnas, mapaResultSetEDC);
		    					}
		    				}
		    			}
		    			
		    			//rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
		    			// creamos una fila vacía para separar las discrepancias con las filas que no existen en EIN
		    			//sheet.createRow(rowTotal);
		    			for (Long idFilaECE : mapaResultSetECE.keySet()) {
		    				if(mapaResultSetEIN.get(idFilaECE) == null){
		    					boolean encontrado = false;
		    					for(int i=0; i<listaIdsNoEncontradosEnEIN.size() && !encontrado; i++){
		    						if(listaIdsNoEncontradosEnEIN.get(i).equals(idFilaECE)){
		    							encontrado = true;
		    						}
		    					}
		    					if(!encontrado){
		    						listaIdsNoEncontradosEnEIN.add(idFilaECE);
		    						//crearFilas(sheet, idFilaECE, mapaResultSetEDC, mapaResultSetEIN, mapaResultSetECE, mapaResultSetEPR, nombreTabla, false, out, posicionFila, numeroColumnas, mapaResultSetECE);
		    						crearFilas(sheet, idFilaECE, mapaResultSetEDC, mapaResultSetEIN, mapaResultSetECE, nombreTabla, false, posicionFila, numeroColumnas, mapaResultSetECE);
		    					}
		    				}
		    			}
		    			
		    			
		    			if(listaIdsNoEncontradosEnEIN.size() == 0){
		    				sheet.removeRow(filaSeparacion);
		    			}else{
		    				for(int i=1; i<numeroColumnas+1; i++){
		    					filaSeparacion.createCell(i);
		    					filaSeparacion.getCell(i).setCellValue("Filas que no existen en EIN, pero sí en otros entornos");
		    				}
		    				
		    				sheet.addMergedRegion(new CellRangeAddress(filaSeparacion.getRowNum(), filaSeparacion.getRowNum(), 1, numeroColumnas+1));
		    				XSSFCellStyle style = workbook.createCellStyle();
		    				style.setVerticalAlignment(VerticalAlignment.CENTER);
		    				style.setAlignment(HorizontalAlignment.CENTER);
		    				
		    				//style.setFillForegroundColor(new XSSFColor(new java.awt.Color(188,203,207)));
		    				
		    				setFillForegroundColor(style, new java.awt.Color(188,203,207));
		    				
		    				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    				XSSFFont fuente = workbook.createFont();
		    				fuente.setBold(true);
		    				fuente.setFontHeight(14);
		    				style.setFont(fuente);
		    				filaSeparacion.getCell(1).setCellStyle(style);
		    			}
		    			
		    			sheet.autoSizeColumn(0);
		    			
		    			// combinamos las celdas que corresponden al nombre de la tabla
		    			rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
		    			//int startRowIndx = rowTotal-(idsFilasEIN.size() * 4);
		    			int endRowIndx = rowTotal-1;
		    			int startColIndx = 0;
		    			int endColIndx = 0;
		    			
		    			sheet.addMergedRegion(new CellRangeAddress(1, endRowIndx, startColIndx,endColIndx));
		    			
		    			// centra vertical y horizontal el nombre de la tabla
		    			XSSFCellStyle style = workbook.createCellStyle();
						style.setVerticalAlignment(VerticalAlignment.CENTER);
						style.setAlignment(HorizontalAlignment.CENTER);
						
						//style.setFillForegroundColor(new XSSFColor(new java.awt.Color(188,203,207)));
						
						setFillForegroundColor(style, new java.awt.Color(188,203,207));
						
						style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						style.setBorderBottom(BorderStyle.MEDIUM);
						style.setBorderTop(BorderStyle.MEDIUM);
						style.setBorderLeft(BorderStyle.MEDIUM);
						style.setBorderRight(BorderStyle.MEDIUM);
		    			sheet.getRow(1).getCell(startColIndx).setCellStyle(style);
		    			
						//sheet.createRow(rowTotal);
						
						for(int i=1; i<numeroColumnas+2; i++){
			    			sheet.autoSizeColumn(i);
						}
						
						mapaTablaColumnas.put(nombreTabla, mapaColumnaNumeroDiscrepancias);
	            	}
	            	line = in.readLine();
	            }
	            
	            createSheetResumen();
	            
				in.close();
				//out.close();
			}
		}catch(Exception e){
		}
	}
	
	
	private static int crearFilas(XSSFSheet sheet, Long idFila, Map<Long, Map<String, String>> mapaResultSetEDC, Map<Long, Map<String, String>> mapaResultSetEIN, 
			Map<Long, Map<String, String>> mapaResultSetECE, String nombreTabla, boolean valorarDiscrepancias, int posicionFila, int numeroColumnas, Map<Long, 
			Map<String, String>> mapaResultSetRecorrer) throws IOException{
		int rowTotal = sheet.getLastRowNum()>0?sheet.getLastRowNum()+1:sheet.getPhysicalNumberOfRows();
		
		XSSFRow filaEDC = sheet.createRow(rowTotal);
		XSSFRow filaEIN = sheet.createRow(rowTotal+1);
		XSSFRow filaECE = sheet.createRow(rowTotal+2);
		XSSFRow filaEPR = sheet.createRow(rowTotal+3);
		
		if(posicionFila==0){
			filaEDC = sheet.createRow(rowTotal+1);
			filaEIN = sheet.createRow(rowTotal+2);
			filaECE = sheet.createRow(rowTotal+3);
			filaEPR = sheet.createRow(rowTotal+4);
		}
		
		filaEDC.createCell(0).setCellValue(nombreTabla);
		filaECE.createCell(0).setCellValue(nombreTabla);
		filaEIN.createCell(0).setCellValue(nombreTabla);
		filaEPR.createCell(0).setCellValue(nombreTabla);
		filaEDC.createCell(1).setCellValue("EDC");
		filaECE.createCell(1).setCellValue("ECE");
		filaEIN.createCell(1).setCellValue("EIN");
		filaEPR.createCell(1).setCellValue("EPR");
		
		filaEDC.getCell(1).setCellStyle(estiloColumnaEntornoEDC);
		filaECE.getCell(1).setCellStyle(estiloColumnaEntornoECE);
		filaEIN.getCell(1).setCellStyle(estiloColumnaEntornoEIN);
		filaEPR.getCell(1).setCellStyle(estiloColumnaEntornoEPR);
								
		Map<String, String> mapaColumnaValorEDC = mapaResultSetEDC.get(idFila);
		Map<String, String> mapaColumnaValorEIN = mapaResultSetEIN.get(idFila);
		Map<String, String> mapaColumnaValorECE = mapaResultSetECE.get(idFila);
		//Map<String, String> mapaColumnaValorEPR = mapaResultSetEPR.get(idFilaEDC);
		
		numeroColumnas = mapaResultSetRecorrer.get(idFila).size()>numeroColumnas?mapaResultSetRecorrer.get(idFila).size():numeroColumnas;
								
		// recorremos las columnas de la fila
		int numeroColumna = 2;
		for (Entry<String, String> mapaColumnaValor : mapaResultSetRecorrer.get(idFila).entrySet()) {
			
			String valorColumnaEDC = mapaColumnaValorEDC!=null?mapaColumnaValorEDC.get(mapaColumnaValor.getKey()):"------";
			String valorColumnaECE = mapaColumnaValorECE!=null?mapaColumnaValorECE.get(mapaColumnaValor.getKey()):"------";
			String valorColumnaEIN = mapaColumnaValorEIN!=null?mapaColumnaValorEIN.get(mapaColumnaValor.getKey()):"------";
			//String valorColumnaEPR = mapaColumnaValorEPR!=null?mapaColumnaValorEPR.get(mapaColumnaValor.getKey()):"------";
			String valorColumnaEPR = "--- NO SE ---";
			
			if(posicionFila==0){
				sheet.getRow(rowTotal).createCell(numeroColumna).setCellValue(mapaColumnaValor.getKey());
    			sheet.getRow(rowTotal).getCell(numeroColumna).setCellStyle(estiloColumnaCabecera);
			}
			
			int columnTotal = filaEDC.getLastCellNum()>0?filaEDC.getLastCellNum():filaEDC.getPhysicalNumberOfCells();
			if(valorarDiscrepancias){
				filaEDC.createCell(columnTotal).setCellValue(valorColumnaEDC);
				filaEDC.getCell(columnTotal).setCellStyle(estiloColumnaValorEDCNormal);
				filaECE.createCell(columnTotal).setCellValue(valorColumnaECE);
				filaEIN.createCell(columnTotal).setCellValue(valorColumnaEIN);
				filaEPR.createCell(columnTotal).setCellValue(valorColumnaEPR);
				filaEPR.getCell(columnTotal).setCellStyle(estiloColumnaValorEPRNormal);
				
				boolean discrepancia = false;
				discrepancia = compararEntornos(valorColumnaEIN, valorColumnaECE, discrepancia, mapaColumnaNumeroDiscrepancias,
						mapaColumnaValor.getKey(), idFila, filaEIN, filaECE, columnTotal,
						"EIN", "ECE", estiloColumnaValorEINDiferencia, estiloColumnaValorECEDiferencia);
				
				discrepancia = compararEntornos(valorColumnaEIN, valorColumnaEDC, discrepancia, mapaColumnaNumeroDiscrepancias,
						mapaColumnaValor.getKey(), idFila, filaEIN, filaEDC, columnTotal,
						"EIN", "EDC", estiloColumnaValorEINDiferencia, estiloColumnaValorEDCDiferencia);
				
				discrepancia = compararEntornos(valorColumnaEIN, valorColumnaEPR, discrepancia, mapaColumnaNumeroDiscrepancias,
						mapaColumnaValor.getKey(), idFila, filaEIN, filaEPR, columnTotal,
						"EIN", "EPR", estiloColumnaValorEINDiferencia, estiloColumnaValorEPRDiferencia);
				
				discrepancia = false;
			}else{
				filaEDC.createCell(columnTotal).setCellValue(valorColumnaEDC);
				filaEDC.getCell(columnTotal).setCellStyle(mapaColumnaValorEDC!=null?estiloColumnaValorEDCDiferencia:estiloColumnaValorEDCNormal);
				filaECE.createCell(columnTotal).setCellValue(valorColumnaECE);
				filaECE.getCell(columnTotal).setCellStyle(mapaColumnaValorECE!=null?estiloColumnaValorECEDiferencia:null);
				filaEIN.createCell(columnTotal).setCellValue(valorColumnaEIN);
				filaEIN.getCell(columnTotal).setCellStyle(mapaColumnaValorEIN==null?null:estiloColumnaValorEINDiferencia);
				filaEPR.createCell(columnTotal).setCellValue(valorColumnaEPR);
				//filaEPR.getCell(columnTotal).setCellStyle(mapaColumnaValorEPR!=null?estiloColumnaValorEPRDiferencia:estiloColumnaValorEPRNormal);
				filaEPR.getCell(columnTotal).setCellStyle(estiloColumnaValorEPRNormal);
			}
			
			numeroColumna++;
		}
		//posicionFila++;
		return numeroColumnas;
	}

	private static boolean compararEntornos(String valorColumna1, String valorColumna2, boolean discrepancia,
			Map<String, Integer> mapaColumnaNumeroDiscrepancias, String key, Long idFila, XSSFRow fila1,
			XSSFRow fila2, int columnTotal,
			String entorno1, String entorno2, XSSFCellStyle estilo1, XSSFCellStyle estilo2) throws IOException {

		if((valorColumna1 == null && valorColumna2 != null) || 
				(valorColumna1 != null && valorColumna2 == null) ||
				(valorColumna1 != null && valorColumna2 != null && !valorColumna1.equals(valorColumna2))){
			
			if(!discrepancia){
				int numeroDiscrepancias = 0;
				if(mapaColumnaNumeroDiscrepancias.get(key) != null){
					numeroDiscrepancias = mapaColumnaNumeroDiscrepancias.get(key) + 1;
				}else{
					numeroDiscrepancias = 1;
				}
				mapaColumnaNumeroDiscrepancias.put(key, numeroDiscrepancias);
				discrepancia = true;
			}
			
			fila1.getCell(columnTotal).setCellStyle(estilo1);
			fila2.getCell(columnTotal).setCellStyle(estilo2);
		}else if(!discrepancia){
			if(mapaColumnaNumeroDiscrepancias.get(key) == null){
				mapaColumnaNumeroDiscrepancias.put(key, 0);
			}
		}
		return discrepancia;
	}

	private static void createSheetResumen() {
		
		XSSFSheet sheetResumen = workbook.getSheet("Resumen");
		XSSFRow filaCabeceras = sheetResumen.createRow(1);
		XSSFCell cell = filaCabeceras.createCell(1);
		cell.setCellValue("Tabla/Columna");
		cell.setCellStyle(estiloCabeceraResumenIzquierda);
		
		XSSFCell cell2 = filaCabeceras.createCell(2);
		cell2.setCellValue("Discrepancias");
		cell2.setCellStyle(estiloCabeceraResumenDerecha);
        
        for(String nombreTabla: mapaTablaColumnas.keySet()){
        	
			int rowTotal = sheetResumen.getLastRowNum()>0?sheetResumen.getLastRowNum()+1:sheetResumen.getPhysicalNumberOfRows();
			XSSFRow nuevaFila = sheetResumen.createRow(rowTotal);
			XSSFCell celda = nuevaFila.createCell(1);
			celda.setCellValue(nombreTabla);
			celda.setCellStyle(estiloCabeceraTablaResumenIzquierda);
			
			CreationHelper createHelper = workbook.getCreationHelper();
			Hyperlink link2 = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
			link2.setAddress("'" + nombreTabla + "'!A1");
			celda.setHyperlink(link2);
			
        	Map<String, Integer> mapaColumnaNumeroDiscrepancias = mapaTablaColumnas.get(nombreTabla);
        	int i=1;
        	int contadorTotalDiscrepancias = 0;
        	for(String nombreColumna: mapaColumnaNumeroDiscrepancias.keySet()){
        		
        		if(mapaColumnaNumeroDiscrepancias.get(nombreColumna) > 0){
        			XSSFRow nuevaFila2 = sheetResumen.createRow(rowTotal+i);
    				XSSFCell celda3 = nuevaFila2.createCell(1);
    				celda3.setCellValue(nombreColumna);
    				celda3.setCellStyle(estiloNormalResumenBordeIzquierdo);
    				
    				XSSFCell celda4 = nuevaFila2.createCell(2);
    				celda4.setCellValue(mapaColumnaNumeroDiscrepancias.get(nombreColumna));
    				celda4.setCellStyle(estiloNormalResumenBordeDerecho);
    				
    				contadorTotalDiscrepancias += mapaColumnaNumeroDiscrepancias.get(nombreColumna);
    				i++;
        		}
        	}
        	XSSFCell celda2 = nuevaFila.createCell(2);
			celda2.setCellValue(contadorTotalDiscrepancias);
			celda2.setCellStyle(estiloCabeceraTablaResumenDerecha);
        }
        
        sheetResumen.getRow(sheetResumen.getLastRowNum()).getCell(1).setCellStyle(estiloNormalResumenBordeIzquierdoAbajo);
        sheetResumen.getRow(sheetResumen.getLastRowNum()).getCell(2).setCellStyle(estiloNormalResumenBordeDerechoAbajo);
        
        sheetResumen.autoSizeColumn(1);
        sheetResumen.autoSizeColumn(2);
	}

	/*private static Map<String, Map<Long, Map<String, String>>> crearMapaResultadosEPR(File file, String usuarioBBDD) throws IOException, SQLException, InterruptedException, ClassNotFoundException {
		BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
		String line = in.readLine();
		
		Map<String, Map<Long, Map<String, String>>> mapaResultadosEPR = new LinkedHashMap<String, Map<Long, Map<String, String>>>();
		while(line != null && !line.startsWith("--")) {
			
			String lineaPartida = line.substring(line.indexOf("FROM ")).substring(line.substring(line.indexOf("FROM ")).indexOf(" ") + 1);
        	String nombreTabla = lineaPartida.substring(0, lineaPartida.indexOf(" ORDER"));
			
        	Map<Long, Map<String, String>> mapaResultSetEPR = getResults(line, "EPR", usuarioBBDD);
        	mapaResultadosEPR.put(nombreTabla, mapaResultSetEPR);
			line = in.readLine();
		}
		
		in.close();
		return mapaResultadosEPR;
	}*/
	
	private static Map<Long, Map<String, String>> getResults(String consulta, String entorno, String usuario) throws SQLException, InterruptedException, ClassNotFoundException{
		
		Map<String, String> mapaColumnaValor = null;
		ResultSet rsSPMessageSpec = Connection.getConnection(entorno, usuario, null, null).createStatement().executeQuery(consulta);
		ResultSetMetaData rsmd = rsSPMessageSpec.getMetaData();
		int numeroColumnas = rsmd.getColumnCount();
		Map<Long, Map<String, String>> mapaResultSet = new LinkedHashMap<Long, Map<String, String>>();
		while(rsSPMessageSpec.next()){
			mapaColumnaValor = new LinkedHashMap<String, String>();
			for(int i=1; i<=numeroColumnas; i++){
				mapaColumnaValor.put(rsmd.getColumnName(i), rsSPMessageSpec.getString(i));
			}
			mapaResultSet.put(rsSPMessageSpec.getLong(1), mapaColumnaValor);
		}
		rsSPMessageSpec.close();
		return mapaResultSet;
	}
	
	private static void createStylesExcel(){
		
		estiloColumnaCabecera = workbook.createCellStyle();
		//estiloColumnaCabecera.setFillForegroundColor(new XSSFColor(new java.awt.Color(188,203,207)));
		
		setFillForegroundColor(estiloColumnaCabecera, new java.awt.Color(188,203,207));
		
		estiloColumnaCabecera.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloColumnaCabecera.setBorderBottom(BorderStyle.MEDIUM);
		
		estiloColumnaValorEPRNormal = workbook.createCellStyle();
		estiloColumnaValorEPRNormal.setBorderBottom(BorderStyle.MEDIUM);
		
		estiloColumnaValorEDCNormal = workbook.createCellStyle();
		estiloColumnaValorEDCNormal.setBorderTop(BorderStyle.MEDIUM);
		
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setItalic(true);
		font.setFontHeight(12);
		estiloColumnaValorEDCDiferencia = workbook.createCellStyle();
		estiloColumnaValorEDCDiferencia.setFont(font);
		//estiloColumnaValorEDCDiferencia.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,165,163)));
		setFillForegroundColor(estiloColumnaValorEDCDiferencia, new java.awt.Color(255,165,163));
		
		estiloColumnaValorEDCDiferencia.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloColumnaValorEDCDiferencia.setBorderTop(BorderStyle.MEDIUM);
		
		estiloColumnaValorEPRDiferencia = workbook.createCellStyle();
		estiloColumnaValorEPRDiferencia.setFont(font);
		//estiloColumnaValorEPRDiferencia.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,165,163)));
		setFillForegroundColor(estiloColumnaValorEPRDiferencia, new java.awt.Color(255,165,163));
		estiloColumnaValorEPRDiferencia.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloColumnaValorEPRDiferencia.setBorderBottom(BorderStyle.MEDIUM);
		
		estiloColumnaValorECEDiferencia = workbook.createCellStyle();
		estiloColumnaValorECEDiferencia.setFont(font);
		//estiloColumnaValorECEDiferencia.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,165,163)));
		setFillForegroundColor(estiloColumnaValorECEDiferencia, new java.awt.Color(255,165,163));
		estiloColumnaValorECEDiferencia.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		estiloColumnaValorEINDiferencia = workbook.createCellStyle();
		estiloColumnaValorEINDiferencia.setFont(font);
		//estiloColumnaValorEINDiferencia.setFillForegroundColor(new XSSFColor(new java.awt.Color(205,222,180)));
		setFillForegroundColor(estiloColumnaValorEINDiferencia, new java.awt.Color(205,222,180));
		estiloColumnaValorEINDiferencia.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		estiloColumnaEntornoEDC = workbook.createCellStyle();
		//estiloColumnaEntornoEDC.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,216,166)));
		setFillForegroundColor(estiloColumnaEntornoEDC, new java.awt.Color(255,216,166));
		estiloColumnaEntornoEDC.setBorderTop(BorderStyle.MEDIUM);
		estiloColumnaEntornoEDC.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloColumnaEntornoEDC.setBorderRight(BorderStyle.MEDIUM);
		estiloColumnaEntornoEDC.setBorderLeft(BorderStyle.MEDIUM);
		
		estiloColumnaEntornoEPR = workbook.createCellStyle();
		//estiloColumnaEntornoEPR.setFillForegroundColor(new XSSFColor(new java.awt.Color(159,210,232)));
		setFillForegroundColor(estiloColumnaEntornoEPR, new java.awt.Color(159,210,232));
		estiloColumnaEntornoEPR.setBorderBottom(BorderStyle.MEDIUM);
		estiloColumnaEntornoEPR.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloColumnaEntornoEPR.setBorderRight(BorderStyle.MEDIUM);
		estiloColumnaEntornoEPR.setBorderLeft(BorderStyle.MEDIUM);
		
		estiloColumnaEntornoECE = workbook.createCellStyle();
		//estiloColumnaEntornoECE.setFillForegroundColor(new XSSFColor(new java.awt.Color(250,180,232)));
		setFillForegroundColor(estiloColumnaEntornoECE, new java.awt.Color(250,180,232));
		estiloColumnaEntornoECE.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloColumnaEntornoECE.setBorderRight(BorderStyle.MEDIUM);
		estiloColumnaEntornoECE.setBorderLeft(BorderStyle.MEDIUM);
		
		estiloColumnaEntornoEIN = workbook.createCellStyle();
		//estiloColumnaEntornoEIN.setFillForegroundColor(new XSSFColor(new java.awt.Color(205,222,180)));
		setFillForegroundColor(estiloColumnaEntornoEIN, new java.awt.Color(205,222,180));
		estiloColumnaEntornoEIN.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloColumnaEntornoEIN.setBorderRight(BorderStyle.MEDIUM);
		estiloColumnaEntornoEIN.setBorderLeft(BorderStyle.MEDIUM);
		
		XSSFFont font2 = workbook.createFont();
		font2.setBold(true);
		font2.setFontHeight(14);
		estiloCabeceraResumenDerecha = workbook.createCellStyle();
		estiloCabeceraResumenDerecha.setFont(font2);
		//estiloCabeceraResumenDerecha.setFillForegroundColor(new XSSFColor(new java.awt.Color(188,203,207)));
		setFillForegroundColor(estiloCabeceraResumenDerecha, new java.awt.Color(188,203,207));
		estiloCabeceraResumenDerecha.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloCabeceraResumenDerecha.setBorderTop(BorderStyle.MEDIUM);
		estiloCabeceraResumenDerecha.setBorderRight(BorderStyle.MEDIUM);
		
		estiloCabeceraResumenIzquierda = workbook.createCellStyle();
		estiloCabeceraResumenIzquierda.setFont(font2);
		//estiloCabeceraResumenIzquierda.setFillForegroundColor(new XSSFColor(new java.awt.Color(188,203,207)));
		setFillForegroundColor(estiloCabeceraResumenIzquierda, new java.awt.Color(188,203,207));
		estiloCabeceraResumenIzquierda.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloCabeceraResumenIzquierda.setBorderTop(BorderStyle.MEDIUM);
		estiloCabeceraResumenIzquierda.setBorderLeft(BorderStyle.MEDIUM);
		
		XSSFFont font3 = workbook.createFont();
		font3.setBold(true);
		font3.setFontHeight(12);
		estiloCabeceraTablaResumenDerecha = workbook.createCellStyle();
		estiloCabeceraTablaResumenDerecha.setFont(font3);
		//estiloCabeceraTablaResumenDerecha.setFillForegroundColor(new XSSFColor(new java.awt.Color(159,210,232)));
		setFillForegroundColor(estiloCabeceraTablaResumenDerecha, new java.awt.Color(159,210,232));
		estiloCabeceraTablaResumenDerecha.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloCabeceraTablaResumenDerecha.setBorderTop(BorderStyle.MEDIUM);
		estiloCabeceraTablaResumenDerecha.setBorderRight(BorderStyle.MEDIUM);
		
		estiloCabeceraTablaResumenIzquierda = workbook.createCellStyle();
		estiloCabeceraTablaResumenIzquierda.setFont(font3);
		//estiloCabeceraTablaResumenIzquierda.setFillForegroundColor(new XSSFColor(new java.awt.Color(159,210,232)));
		setFillForegroundColor(estiloCabeceraTablaResumenIzquierda, new java.awt.Color(159,210,232));
		estiloCabeceraTablaResumenIzquierda.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloCabeceraTablaResumenIzquierda.setBorderTop(BorderStyle.MEDIUM);
		estiloCabeceraTablaResumenIzquierda.setBorderLeft(BorderStyle.MEDIUM);
		
		estiloNormalResumenBordeIzquierdo = workbook.createCellStyle();
		estiloNormalResumenBordeIzquierdo.setBorderLeft(BorderStyle.MEDIUM);
		estiloNormalResumenBordeDerecho = workbook.createCellStyle();
		estiloNormalResumenBordeDerecho.setBorderRight(BorderStyle.MEDIUM);
		
		estiloNormalResumenBordeIzquierdoAbajo = workbook.createCellStyle();
		estiloNormalResumenBordeIzquierdoAbajo.setBorderLeft(BorderStyle.MEDIUM);
		estiloNormalResumenBordeIzquierdoAbajo.setBorderBottom(BorderStyle.MEDIUM);
		estiloNormalResumenBordeDerechoAbajo = workbook.createCellStyle();
		estiloNormalResumenBordeDerechoAbajo.setBorderRight(BorderStyle.MEDIUM);
		estiloNormalResumenBordeDerechoAbajo.setBorderBottom(BorderStyle.MEDIUM);
	}
	
	private static void setFillForegroundColor(XSSFCellStyle style, Color color) {
		IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();
		style.setFillForegroundColor(new XSSFColor(color, colorMap));
	}
}