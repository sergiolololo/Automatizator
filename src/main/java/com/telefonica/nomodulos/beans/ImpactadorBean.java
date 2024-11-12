package com.telefonica.nomodulos.beans;

import java.util.ArrayList;
import java.util.List;

public class ImpactadorBean {

	private String nombre;
	private String tipo;
	private String aplicacion;
	private String proyecto;
	private String version;
	private String key;
	private List<ImpactadorBean> listaOperaciones = new ArrayList<>();
	private List<ImpactadorBean> listaImpactados = new ArrayList<>();
	
	public ImpactadorBean(String nombre, String tipo, String aplicacion, String proyecto, String version) {
		this.nombre = nombre;
		this.tipo = tipo;
		this.aplicacion = aplicacion;
		this.proyecto = proyecto;
		this.version = version;
		this.key = aplicacion + "-" + tipo + "-" + nombre;
	}
	
	public String getProyecto() {
		return proyecto;
	}
	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getAplicacion() {
		return aplicacion;
	}
	public void setAplicacion(String aplicacion) {
		this.aplicacion = aplicacion;
	}
	public List<ImpactadorBean> getListaImpactados() {
		return listaImpactados;
	}
	public void setListaImpactados(List<ImpactadorBean> listaImpactados) {
		this.listaImpactados = listaImpactados;
	}
	public List<ImpactadorBean> getListaOperaciones() {
		return listaOperaciones;
	}
	public void setListaOperaciones(List<ImpactadorBean> listaOperaciones) {
		this.listaOperaciones = listaOperaciones;
	}
}