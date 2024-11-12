package com.telefonica.nomodulos.nucleo;

import com.google.common.collect.Lists;
import com.telefonica.nomodulos.beans.DisneyCustomerDTO;
import com.telefonica.nomodulos.beans.DisneyCustomer_DTO_IN;
import com.telefonica.nomodulos.beans.SPConnectionFile_DTO_IN;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class AnalizarProblemaDisney {

	private static Map<String, Long> mapaAdmin = new HashMap<String, Long>();
	
	public static void main(String[] args) throws IOException, ParseException {
		
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\Sergio\\OneDrive - Telefonica\\Documentos\\export_TablaDisneyPrueba.xlsx"));
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0); // Activos
		
		Integer windowsSize = 40;
        Integer delay = 60;

        List<DisneyCustomerDTO> disneyCustomersToActivate = getDisneyCustomersToActivate(sheet);
        wb.close();
        fis.close();

        List<List<DisneyCustomerDTO>> allCustomersToActivated = Lists.partition(disneyCustomersToActivate, windowsSize);
        disneyCustomersToActivate = new ArrayList<DisneyCustomerDTO>();
        int i=0;
        for (List<DisneyCustomerDTO> batchCustomersToActivate : allCustomersToActivated) {
            i += callBPMToActivateCustomer(batchCustomersToActivate);
            //TimeUnit.SECONDS.sleep(delay);
        }
		
        System.out.println("Numero de filas en local: " + i);
	}
	
	// 661851
	private static List<DisneyCustomerDTO> getDisneyCustomersToActivate(XSSFSheet sheet) throws ParseException {
        List<DisneyCustomerDTO> listOfCustomers = new ArrayList<DisneyCustomerDTO>();
        int i=0;
        for(Row row: sheet){
        	if(row.getRowNum() > 0){
        		i++;
            	BigDecimal id = new BigDecimal(row.getCell(0).getStringCellValue());
            	String admin = row.getCell(1).getStringCellValue();
            	Long idFile = Long.parseLong(row.getCell(2).getStringCellValue());
            	DisneyCustomerDTO disneyCustomerDTO = new DisneyCustomerDTO();
            	
                disneyCustomerDTO.setDicuIdDisneyCustomer(id);
                disneyCustomerDTO.setDicuCoDisneySharedCustomer(admin);
                disneyCustomerDTO.setSpcfIdSpConnectionFile(idFile);
                
                listOfCustomers.add(disneyCustomerDTO);
        	}
        }
        System.out.println("Numero de filas en el excel: " + i);
        
        return listOfCustomers;
    }
	
	
	private static int callBPMToActivateCustomer(List<DisneyCustomerDTO> batchCustomersToActivate) {

        Map<Long, List<DisneyCustomerDTO>> customersGroupByFile = batchCustomersToActivate.stream()
                .collect(Collectors.groupingBy(DisneyCustomerDTO::getSpcfIdSpConnectionFile));

        int i = 0;
        List<SPConnectionFile_DTO_IN> connectionFiles = new ArrayList<>();
        for (Entry<Long, List<DisneyCustomerDTO>> customers : customersGroupByFile.entrySet()) {
        	
            SPConnectionFile_DTO_IN connectionFile = new SPConnectionFile_DTO_IN();
            connectionFile.setId(customers.getKey());
            connectionFile.setDisneyCustomers(customers.getValue().stream().map(customer -> {
                DisneyCustomer_DTO_IN newCustomer = new DisneyCustomer_DTO_IN();
                newCustomer.setSubjectId(customer.getDicuCoDisneySharedCustomer());
                newCustomer.setLastUpdated(customer.getDicuTiLastUpdated());
                return newCustomer;
            }).toArray(DisneyCustomer_DTO_IN[]::new));
            connectionFiles.add(connectionFile);
            
            for(DisneyCustomer_DTO_IN admin: connectionFile.getDisneyCustomers()){
            	if(mapaAdmin.get(admin.getSubjectId()) != null){
            		System.out.println(admin.getSubjectId());
            	}else{
            		mapaAdmin.put(admin.getSubjectId(), customers.getKey());
            	}
            }
        }
        
        for(SPConnectionFile_DTO_IN hola: connectionFiles){
        	for(DisneyCustomer_DTO_IN adios: hola.getDisneyCustomers()){
        		i++;
        	}
        }
        return i;
    }
}
