package com.telefonica.modulos.comparador.catalogacion.enumm;

public enum ConectionEnum {

	EDC(
			"jdbc:oracle:thin:@//enhol01v-scan.serv.test.dc.es.telefonica:50001/DTLCO0",
			"UOD",
			"temporal1"
			),
	EIN(
			"jdbc:oracle:thin:@//enhol01v-scan.serv.test.dc.es.telefonica:50001/ITLCO0",
			"UBD",
			"temporal1"
			),
	ECE(
			"jdbc:oracle:thin:@//enhol01v-scan.serv.test.dc.es.telefonica:50001/CTLCO0",
			"UBD",
			"temporal1"
			),
	ECO(
			"jdbc:oracle:thin:@//enhol01v-scan.serv.test.dc.es.telefonica:50001/I2TLCO0",
			"UBD",
			"temporal1"
			),
	EPR(
			"jdbc:oracle:thin:@//ephol01v-scan.serv.dc.es.telefonica:50001/PTLCO0",
			"UBD",
			"temporal1"
			);
	
	private final String dataBaseUrl;
	private final String userNamePrefix;
	private final String password;
	
	private ConectionEnum(String dataBaseUrl, String userNamePrefix, String password){
		this.dataBaseUrl = dataBaseUrl;
		this.userNamePrefix = userNamePrefix;
		this.password = password;
	}

	
	public String getDataBaseUrl() {
		return dataBaseUrl;
	}
	public String getUserNamePrefix() {
		return userNamePrefix;
	}
	public String getPassword() {
		return password;
	}
}