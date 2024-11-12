package com.telefonica.modulos.busqueda.enumm;

import java.util.List;

public enum TipoActivoEnum {

	CGT_NODE(
			"\\1_CO\\cgt-node\\",
            List.of(".js")
			),
	CNT(
			"\\1_CO\\cnt\\",
            List.of(".java")
			),
	CNT_NODE(
			"\\1_CO\\cnt-node\\",
            List.of("package.json")
			),
	DAO(
			"\\1_CO\\dao\\",
			List.of(".java")
			),
	JT(
			"\\1_CO\\jt-nuc-jee\\",
            List.of(".java")
			),
	RES(
			"\\1_CO\\res-nuc-jee\\",
			List.of("Mapper.java", "Command.java")
			),
	SRV_NUC(
			"\\1_CO\\srv-nuc-jee\\",
			List.of("Mapper.java", "Command.java", "Impl.java", "Operation.java")
			),
	SRV_SOA(
			"\\1_CO\\srv-nuc-soa\\",
			List.of(".bpel", ".xsl")
			),
	SRV_PRES(
			"\\1_CO\\srv-pres\\",
			List.of("Impl.java")
			),
	SRV_EXP(
			"\\1_CO\\srv-exp-osb\\",
			List.of(".bix", ".pipeline", ".proxy", ".xqy")
			);
	
	private final String ruta1CO;
	private final List<String> archivoDondeBuscar;
	
	TipoActivoEnum(String ruta1CO, List<String> archivoDondeBuscar){
		this.ruta1CO = ruta1CO;
		this.archivoDondeBuscar = archivoDondeBuscar;
	}

	public String getRuta1CO() {
		return ruta1CO;
	}
	public List<String> getArchivoDondeBuscar() {
		return archivoDondeBuscar;
	}
}