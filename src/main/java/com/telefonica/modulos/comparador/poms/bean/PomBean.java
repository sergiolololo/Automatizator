package com.telefonica.modulos.comparador.poms.bean;

public class PomBean {
    private String nombreDependencia;
    private String tipoDependencia;
    private DependencyBean dependencia1CO;
    private DependencyBean dependencia0AT;
    private DependencyBean dependenciaMerge;

    public PomBean(){
    }
    public PomBean(String nombreDependencia, String tipoDependencia, DependencyBean dependencia1CO, DependencyBean dependencia0AT){
        this.nombreDependencia = nombreDependencia;
        this.tipoDependencia = tipoDependencia;
        this.dependencia1CO = dependencia1CO;
        this.dependencia0AT = dependencia0AT;
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }
    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }
    public String getTipoDependencia() {
        return tipoDependencia;
    }
    public void setTipoDependencia(String tipoDependencia) {
        this.tipoDependencia = tipoDependencia;
    }
    public DependencyBean getDependencia1CO() {
        return dependencia1CO;
    }
    public void setDependencia1CO(DependencyBean dependencia1CO) {
        this.dependencia1CO = dependencia1CO;
    }
    public DependencyBean getDependencia0AT() {
        return dependencia0AT;
    }
    public void setDependencia0AT(DependencyBean dependencia0AT) {
        this.dependencia0AT = dependencia0AT;
    }
    public DependencyBean getDependenciaMerge() {
        return dependenciaMerge;
    }
    public void setDependenciaMerge(DependencyBean dependenciaMerge) {
        this.dependenciaMerge = dependenciaMerge;
    }
}
