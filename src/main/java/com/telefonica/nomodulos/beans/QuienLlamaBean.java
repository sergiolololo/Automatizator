package com.telefonica.nomodulos.beans;

import java.util.Map;

public class QuienLlamaBean {

	private String servicio;
	private String operacion; // afecta solo cuando es operación
	private String aplicacion;
	private String fqn;
	private String tipo;
	private String tipoRelacion; // afecta solo cuando es operación
	private String tecnologiaInterfaz; // afecta solo cuando es servicio
	private String tecnologiaInterfazCorrecta; // afecta solo cuando es servicio
	private Map<String, QuienLlamaBean> mapaDependencias; // afecta tanto para cuando es servicio (contendrá sus operaciones) como cuando es operación (contendrá sus dependencias: llamantes y/o llamados)
	private Map<String, QuienLlamaBean> mapaDependenciasLlamados; // afecta cuando es operación (contendrá las operaciones a las que llama)
	private Map<String, QuienLlamaBean> mapaDependenciasLlamantes; // afecta cuando es operación (contendrá las operaciones que la llaman)
	private String faseMigracion; // afecta solo cuando es operación
	private boolean seDesdobla; // afecta solo cuando es operación
	
	public QuienLlamaBean(String aplicacion, String operacion, String fqn, String tipo, String servicio, String tipoRelacion, String tecnologiaInterfaz, 
			Map<String, QuienLlamaBean> mapaDependencias, Map<String, QuienLlamaBean> mapaDependenciasLlamados, Map<String, QuienLlamaBean> mapaDependenciasLlamantes){
		this.aplicacion = aplicacion;
		this.operacion = operacion;
		this.fqn = fqn;
		this.tipo = tipo;
		this.servicio = servicio;
		this.tipoRelacion = tipoRelacion;
		this.tecnologiaInterfaz = tecnologiaInterfaz!=null?tecnologiaInterfaz:"";
		this.mapaDependencias = mapaDependencias;
		this.mapaDependenciasLlamados = mapaDependenciasLlamados;
		this.mapaDependenciasLlamantes = mapaDependenciasLlamantes;
	}
	
	// getters and setters
	public String getAplicacion() {
		return aplicacion;
	}
	public void setAplicacion(String aplicacion) {
		this.aplicacion = aplicacion;
	}
	public String getOperacion() {
		return operacion;
	}
	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}
	public String getFqn() {
		return fqn;
	}
	public void setFqn(String fqn) {
		this.fqn = fqn;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getServicio() {
		return servicio;
	}
	public void setServicio(String servicio) {
		this.servicio = servicio;
	}
	public Map<String, QuienLlamaBean> getMapaDependencias() {
		return mapaDependencias;
	}
	public void setMapaDependencias(Map<String, QuienLlamaBean> mapaDependencias) {
		this.mapaDependencias = mapaDependencias;
	}
	public String getTipoRelacion() {
		return tipoRelacion;
	}
	public void setTipoRelacion(String tipoRelacion) {
		this.tipoRelacion = tipoRelacion;
	}
	public String getTecnologiaInterfaz() {
		return tecnologiaInterfaz;
	}
	public void setTecnologiaInterfaz(String tecnologiaInterfaz) {
		this.tecnologiaInterfaz = tecnologiaInterfaz;
	}
	public String getFaseMigracion() {
		return faseMigracion;
	}
	public void setFaseMigracion(String faseMigracion) {
		this.faseMigracion = faseMigracion;
	}
	public boolean isSeDesdobla() {
		return seDesdobla;
	}
	public void setSeDesdobla(boolean seDesdobla) {
		this.seDesdobla = seDesdobla;
	}
	public String getTecnologiaInterfazCorrecta() {
		return tecnologiaInterfazCorrecta;
	}
	public void setTecnologiaInterfazCorrecta(String tecnologiaInterfazCorrecta) {
		this.tecnologiaInterfazCorrecta = tecnologiaInterfazCorrecta;
	}
	public Map<String, QuienLlamaBean> getMapaDependenciasLlamados() {
		return mapaDependenciasLlamados;
	}
	public void setMapaDependenciasLlamados(Map<String, QuienLlamaBean> mapaDependenciasLlamados) {
		this.mapaDependenciasLlamados = mapaDependenciasLlamados;
	}
	public Map<String, QuienLlamaBean> getMapaDependenciasLlamantes() {
		return mapaDependenciasLlamantes;
	}
	public void setMapaDependenciasLlamantes(Map<String, QuienLlamaBean> mapaDependenciasLlamantes) {
		this.mapaDependenciasLlamantes = mapaDependenciasLlamantes;
	}
}