package com.telefonica.modulos.despliegueCerti.beans;

public class LineaComandosBean {
    private String directorioConstruccion;
    private String usuarioDavid;
    private String passwordDavid;
    private MaquinaBean maquinaTramitador;
    private MaquinaBean maquinaPortal;


    public String getDirectorioConstruccion() {
        return directorioConstruccion;
    }

    public void setDirectorioConstruccion(String directorioConstruccion) {
        this.directorioConstruccion = directorioConstruccion;
    }

    public String getUsuarioDavid() {
        return usuarioDavid;
    }

    public void setUsuarioDavid(String usuarioDavid) {
        this.usuarioDavid = usuarioDavid;
    }

    public String getPasswordDavid() {
        return passwordDavid;
    }

    public void setPasswordDavid(String passwordDavid) {
        this.passwordDavid = passwordDavid;
    }

    public MaquinaBean getMaquinaTramitador() {
        return maquinaTramitador;
    }

    public void setMaquinaTramitador(MaquinaBean maquinaTramitador) {
        this.maquinaTramitador = maquinaTramitador;
    }

    public MaquinaBean getMaquinaPortal() {
        return maquinaPortal;
    }

    public void setMaquinaPortal(MaquinaBean maquinaPortal) {
        this.maquinaPortal = maquinaPortal;
    }
}
