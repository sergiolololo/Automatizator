package com.telefonica.nomodulos.beans;

public class ActivoBean {

	private String idUnva;
	private String activo;
	private String version;
	private String tipoActivo;
	private String ruta;
	private String guid;
	private String fqn;
	private String pesp;
	private String pesp2;
	private String complejidad;
	private String esfuerzo;
	private String etapa;
	private String slb;
	private String lote;
	private String suministrador;
	private String tipoActuacion;
	private String aplicacion;
	private String pespActual;

	public ActivoBean(String idUnva, String activo, String version, String revision, String tipoActivo, String ruta, String guid, String fqn, String aplicacion, String pespActual){
		this.idUnva = idUnva;
		this.activo = activo;
		this.version = version + "." + revision;
		this.tipoActivo = tipoActivo;
		this.ruta = ruta;
		this.guid = guid;
		this.fqn = fqn;
		this.aplicacion = aplicacion;
		this.pespActual = pespActual;
		
		this.pesp = "PESP_2105_692672";
		this.pesp2 = "PPRO_GTER_v0.0.105.1";
		this.complejidad = "Media";
		this.esfuerzo = "Media";
		this.etapa = "DISE_E21";
		this.slb = "SLB1";
		this.lote = "Lote 1";
		this.suministrador = "INDRA";
		this.tipoActuacion = "Nuevo";
	}
	
	public String getIdUnva() {
		return idUnva;
	}
	public void setIdUnva(String idUnva) {
		this.idUnva = idUnva;
	}
	public String getActivo() {
		return activo;
	}
	public void setActivo(String activo) {
		this.activo = activo;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getTipoActivo() {
		return tipoActivo;
	}
	public void setTipoActivo(String tipoActivo) {
		this.tipoActivo = tipoActivo;
	}
	public String getRuta() {
		return ruta;
	}
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getFqn() {
		return fqn;
	}
	public void setFqn(String fqn) {
		this.fqn = fqn;
	}
	public String getPesp() {
		return pesp;
	}
	public void setPesp(String pesp) {
		this.pesp = pesp;
	}
	public String getPesp2() {
		return pesp2;
	}
	public void setPesp2(String pesp2) {
		this.pesp2 = pesp2;
	}
	public String getComplejidad() {
		return complejidad;
	}
	public void setComplejidad(String complejidad) {
		this.complejidad = complejidad;
	}
	public String getEsfuerzo() {
		return esfuerzo;
	}
	public void setEsfuerzo(String esfuerzo) {
		this.esfuerzo = esfuerzo;
	}
	public String getEtapa() {
		return etapa;
	}
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	public String getSlb() {
		return slb;
	}
	public void setSlb(String slb) {
		this.slb = slb;
	}
	public String getLote() {
		return lote;
	}
	public void setLote(String lote) {
		this.lote = lote;
	}
	public String getSuministrador() {
		return suministrador;
	}
	public void setSuministrador(String suministrador) {
		this.suministrador = suministrador;
	}
	public String getTipoActuacion() {
		return tipoActuacion;
	}
	public void setTipoActuacion(String tipoActuacion) {
		this.tipoActuacion = tipoActuacion;
	}
	public String getAplicacion() {
		return aplicacion;
	}
	public void setAplicacion(String aplicacion) {
		this.aplicacion = aplicacion;
	}
	public String getPespActual() {
		return pespActual;
	}
	public void setPespActual(String pespActual) {
		this.pespActual = pespActual;
	}
}