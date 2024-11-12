package com.telefonica.modulos.comparador.catalogacion.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CambioBean {

	private boolean soloDiferencias;
	private Map<String, FiltroBean> mapaColumnaFiltro = new HashMap<>();
	private List<Object[]> listaFilas;
	
	
	public boolean isSoloDiferencias() {
		return soloDiferencias;
	}
	public void setSoloDiferencias(boolean soloDiferencias) {
		this.soloDiferencias = soloDiferencias;
	}

	public Map<String, FiltroBean> getMapaColumnaFiltro() {
		return mapaColumnaFiltro;
	}
	public void setMapaColumnaFiltro(Map<String, FiltroBean> mapaColumnaFiltro) {
		this.mapaColumnaFiltro = mapaColumnaFiltro;
	}
	public List<Object[]> getListaFilas() {
		return listaFilas;
	}
	public void setListaFilas(List<Object[]> listaFilas) {
		this.listaFilas = listaFilas;
	}
}