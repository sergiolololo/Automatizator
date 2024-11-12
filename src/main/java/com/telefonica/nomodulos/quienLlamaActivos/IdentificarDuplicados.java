package com.telefonica.nomodulos.quienLlamaActivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class IdentificarDuplicados {

	private static XSSFCellStyle estiloCeldaTecnologiaCorregidaError;
	private static Set<String> listaObsoletos;
	
	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\T3G_PG_CYS_DFIS_Matriz_Uso_Servicio_Tecnico_Ola2_20220118\\T3G_PG_CYS_DFIS_Matriz_Uso_Servicio_Tecnico_Ola2_20220118 __PROVISIONAL.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet_activos = wb.getSheetAt(0); // Activos
		
		XSSFWorkbook wbNuevo = new XSSFWorkbook();
		XSSFSheet sheetNueva = wbNuevo.createSheet();
		
		estiloCeldaTecnologiaCorregidaError = wbNuevo.createCellStyle();
		estiloCeldaTecnologiaCorregidaError.setFillForegroundColor(new XSSFColor(new java.awt.Color(205,222,180), new DefaultIndexedColorMap()));
		estiloCeldaTecnologiaCorregidaError.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		rellenarListaObsoletos();
		
		Map<String, String> mapaOP_SRV = new LinkedHashMap<String, String>();
		Set<String> listaOPsRepetidas = new HashSet<String>();
		
		for(int i=1; i<sheet_activos.getPhysicalNumberOfRows(); i++){
			Row row = sheet_activos.getRow(i);
			
			String fqn = row.getCell(4).getStringCellValue();
			String op = row.getCell(5).getStringCellValue();
			String servicio = fqn.split("\\.")[0];
			
			if(mapaOP_SRV.get(op) != null && !mapaOP_SRV.get(op).equals(fqn)){
				// detectada OP duplicada, guardamos el FQN
				listaOPsRepetidas.add(fqn);
				Row filaNueva = sheetNueva.createRow(sheetNueva.getPhysicalNumberOfRows());
				filaNueva.createCell(0).setCellValue(op);
				filaNueva.createCell(1).setCellValue(fqn);
				filaNueva.createCell(2).setCellValue(servicio);
				if(listaObsoletos.contains(servicio)){
					filaNueva.getCell(0).setCellStyle(estiloCeldaTecnologiaCorregidaError);
					filaNueva.getCell(1).setCellStyle(estiloCeldaTecnologiaCorregidaError);
					filaNueva.getCell(2).setCellStyle(estiloCeldaTecnologiaCorregidaError);
				}
				
				Row filaNueva2 = sheetNueva.createRow(sheetNueva.getPhysicalNumberOfRows());
				filaNueva2.createCell(0).setCellValue(op);
				filaNueva2.createCell(1).setCellValue(mapaOP_SRV.get(op));
				filaNueva2.createCell(2).setCellValue(mapaOP_SRV.get(op).split("\\.")[0]);
				if(listaObsoletos.contains(mapaOP_SRV.get(op).split("\\.")[0])){
					filaNueva2.getCell(0).setCellStyle(estiloCeldaTecnologiaCorregidaError);
					filaNueva2.getCell(1).setCellStyle(estiloCeldaTecnologiaCorregidaError);
					filaNueva2.getCell(2).setCellStyle(estiloCeldaTecnologiaCorregidaError);
				}
			}else{
				mapaOP_SRV.put(op, fqn);
			}
		}
		
		for(String operacion: listaOPsRepetidas){
			System.out.println(operacion);
		}
		
		File dirDestino = new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\IdentificarDuplicados.xlsx");
		FileOutputStream fileOut = new FileOutputStream(dirDestino.getAbsolutePath());
		wbNuevo.write(fileOut);
        fileOut.close();
		
		wb.close();
		wbNuevo.close();
		fis.close();
	}
	
	private static void rellenarListaObsoletos(){
		listaObsoletos = new HashSet<String>();
		listaObsoletos.add("SRV_FindBISIActionbyBISpecType");
		listaObsoletos.add("SRV_FindBusServSpecBySPMsgAction");
		//listaObsoletos.add("SRV_FindSpecInfo");
		listaObsoletos.add("SRV_FindSPOrdeSpecItemRelatToCat");
		listaObsoletos.add("SRV_FindSPPorOrdSItBySPPorByOrdSI");
		listaObsoletos.add("SRV_FindSPPortOrdSpeIteRelaToCtg");
		listaObsoletos.add("SRV_GenerateSPReqOrderBillInvoice");
		listaObsoletos.add("SRV_GenerateSPReqOrderFulfillment");
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
		listaObsoletos.add("SRV_ModBIRelshiPortaByCustOrder");
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
	}
}