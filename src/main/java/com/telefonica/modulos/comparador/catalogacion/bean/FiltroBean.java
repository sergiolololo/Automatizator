package com.telefonica.modulos.comparador.catalogacion.bean;

public class FiltroBean {
	private int numeroColumnaFiltrada;
	private String nombreColumnaFiltrada;
	private String filtroTabla;
	
	public int getNumeroColumnaFiltrada() {
		return numeroColumnaFiltrada;
	}
	public void setNumeroColumnaFiltrada(int numeroColumnaFiltrada) {
		this.numeroColumnaFiltrada = numeroColumnaFiltrada;
	}
	public String getNombreColumnaFiltrada() {
		return nombreColumnaFiltrada;
	}
	public void setNombreColumnaFiltrada(String nombreColumnaFiltrada) {
		this.nombreColumnaFiltrada = nombreColumnaFiltrada;
	}
	public String getFiltroTabla() {
		return filtroTabla;
	}
	public void setFiltroTabla(String filtroTabla) {
		this.filtroTabla = filtroTabla;
	}
}