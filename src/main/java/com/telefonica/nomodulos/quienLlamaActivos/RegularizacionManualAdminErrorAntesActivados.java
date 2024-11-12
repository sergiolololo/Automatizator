package com.telefonica.nomodulos.quienLlamaActivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RegularizacionManualAdminErrorAntesActivados {
	
	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\Disney\\INFORMES_DISNEY\\adminEstadoError_conMensajeAnteriorOK_contraParque_soloAdmin.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet_adminEstado12 = wb.getSheetAt(0); // Activos
		//XSSFSheet sheet_adminEstado5 = wb.getSheetAt(1); // Activos
		
		List<String> lines = new ArrayList<String>();
		//lines.add("-- Actualizacion estado admin con error 12 --");
		int i=0;
		int contador = 0;
 		for(Row row: sheet_adminEstado12){
			if(row.getRowNum() > 0){
				if(row.getCell(0) != null && !row.getCell(0).getStringCellValue().equals("")){
					
					String admin = row.getCell(0).getStringCellValue();
					if(!admin.equals("")){
						contador++;
						String updateEstado = "UPDATE TERCR_SP_CON_MES_HAS_STATUS SET CMHS_TI_END_VALIDITY = SYSDATE WHERE CMHS_TI_END_VALIDITY = TO_DATE('31/12/9999 23:59:59', 'dd/mm/rrrr hh24:mi:ss') AND ROEN_ID_SP_CON_MESSAGE = (SELECT MAX(BB.ROEN_ID_SP_CON_MESSAGE) FROM TERCP_SP_CONNECTION_MESSAGE BB, TERCP_CHARAC_VALUE CC WHERE CC.ROEN_ID_SP_CON_MESSAGE = BB.ROEN_ID_SP_CON_MESSAGE AND CC.CHVA_NA_CHARAC_VALUE = '" + admin + "');";
						String insertEstado = "INSERT INTO TERCR_SP_CON_MES_HAS_STATUS (ROEN_ID_SP_CON_MESSAGE,CMST_ID_SPCON_MES_STATUS,CMHS_TI_START_VALIDITY,SPCF_DA_FILE_CREATION_DATE,CMHS_TI_END_VALIDITY, USER_ID_CREATOR_PARTY, AUDI_TI_CREATION) VALUES((SELECT MAX(BB.ROEN_ID_SP_CON_MESSAGE) FROM TERCP_SP_CONNECTION_MESSAGE BB,TERCP_CHARAC_VALUE CC WHERE CC.ROEN_ID_SP_CON_MESSAGE = BB.ROEN_ID_SP_CON_MESSAGE AND CC.CHVA_NA_CHARAC_VALUE = '" + admin + "'),15,SYSDATE,(SELECT SPCF_DA_FILE_CREATION_DATE FROM TERCP_SP_CONNECTION_MESSAGE WHERE ROEN_ID_SP_CON_MESSAGE = (SELECT MAX(BB.ROEN_ID_SP_CON_MESSAGE) FROM TERCP_SP_CONNECTION_MESSAGE BB, TERCP_CHARAC_VALUE CC WHERE CC.ROEN_ID_SP_CON_MESSAGE = BB.ROEN_ID_SP_CON_MESSAGE AND CC.CHVA_NA_CHARAC_VALUE = '" + admin + "')),TO_TIMESTAMP('9999-12-31 23:59:59.000000000', 'YYYY-MM-DD HH24:MI:SS.FF'),0,SYSDATE);";
						String updateTablaDiseny = "UPDATE TERCP_DISNEY_CUSTOMER SET DICU_TI_PROCESS = SYSDATE WHERE DICU_CO_DISNEY_SHARED_CUSTOMER = '" + admin + "';";
						
						lines.add(updateEstado);
						lines.add(insertEstado);
						lines.add(updateTablaDiseny);
					}
					if(i==50){
						lines.add("commit;");
						i = 0;
					}
				}
			}
			i++;
		}
 		System.out.println(contador);
 		/*lines.add("-- Actualizacion estado admin con error 5 --");
		for(Row row: sheet_adminEstado5){
			if(row.getRowNum() > 0){
				
				if(row.getCell(0) != null && !row.getCell(0).getStringCellValue().equals("")){
					String admin = row.getCell(0).getStringCellValue();
					if(!admin.equals("")){
						String updateEstado = "UPDATE TERCR_SP_CON_MES_HAS_STATUS SET CMHS_TI_END_VALIDITY = SYSDATE WHERE CMHS_TI_END_VALIDITY = TO_DATE('31/12/9999 23:59:59', 'dd/mm/rrrr hh24:mi:ss') AND ROEN_ID_SP_CON_MESSAGE = (SELECT MAX(BB.ROEN_ID_SP_CON_MESSAGE) FROM TERCP_SP_CONNECTION_MESSAGE BB, TERCP_CHARAC_VALUE CC WHERE CC.ROEN_ID_SP_CON_MESSAGE = BB.ROEN_ID_SP_CON_MESSAGE AND CC.CHVA_NA_CHARAC_VALUE = '" + admin + "');";
						String insertEstado = "INSERT INTO TERCR_SP_CON_MES_HAS_STATUS (ROEN_ID_SP_CON_MESSAGE,CMST_ID_SPCON_MES_STATUS,CMHS_TI_START_VALIDITY,SPCF_DA_FILE_CREATION_DATE,CMHS_TI_END_VALIDITY, USER_ID_CREATOR_PARTY, AUDI_TI_CREATION) VALUES((SELECT MAX(BB.ROEN_ID_SP_CON_MESSAGE) FROM TERCP_SP_CONNECTION_MESSAGE BB,TERCP_CHARAC_VALUE CC WHERE CC.ROEN_ID_SP_CON_MESSAGE = BB.ROEN_ID_SP_CON_MESSAGE AND CC.CHVA_NA_CHARAC_VALUE = '" + admin + "'),15,SYSDATE,(SELECT SPCF_DA_FILE_CREATION_DATE FROM TERCP_SP_CONNECTION_MESSAGE WHERE ROEN_ID_SP_CON_MESSAGE = (SELECT MAX(BB.ROEN_ID_SP_CON_MESSAGE) FROM TERCP_SP_CONNECTION_MESSAGE BB, TERCP_CHARAC_VALUE CC WHERE CC.ROEN_ID_SP_CON_MESSAGE = BB.ROEN_ID_SP_CON_MESSAGE AND CC.CHVA_NA_CHARAC_VALUE = '" + admin + "')),TO_TIMESTAMP('9999-12-31 23:59:59.000000000', 'YYYY-MM-DD HH24:MI:SS.FF'),0,SYSDATE);";
						
						lines.add(updateEstado);
						lines.add(insertEstado);
					}
				}
			}
		}*/
		
		Path file = Paths.get("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\regularizacionAdminEstadoOKManual_EPR_prueba.sql");
		Files.write(file, lines, StandardCharsets.UTF_8);
		
		wb.close();
		fis.close();
	}
}