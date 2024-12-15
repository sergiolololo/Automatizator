package com.telefonica.modulos.despliegueInte.beans;

public class MaquinaBean {
    private String host;
    private String user;
    private String password;
    private String directorioWar;
    private String directorioDejarWar;
    private String directorioScriptDespliegue;
    private String nombreScriptsDespliegue;
    private String nombreScriptsArranque;
    private String urlPing;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDirectorioWar() {
        return directorioWar;
    }

    public void setDirectorioWar(String directorioWar) {
        this.directorioWar = directorioWar;
    }

    public String getDirectorioDejarWar() {
        return directorioDejarWar;
    }

    public void setDirectorioDejarWar(String directorioDejarWar) {
        this.directorioDejarWar = directorioDejarWar;
    }

    public String getDirectorioScriptDespliegue() {
        return directorioScriptDespliegue;
    }

    public void setDirectorioScriptDespliegue(String directorioScriptDespliegue) {
        this.directorioScriptDespliegue = directorioScriptDespliegue;
    }

    public String getNombreScriptsDespliegue() {
        return nombreScriptsDespliegue;
    }

    public void setNombreScriptsDespliegue(String nombreScriptsDespliegue) {
        this.nombreScriptsDespliegue = nombreScriptsDespliegue;
    }

    public String getNombreScriptsArranque() {
        return nombreScriptsArranque;
    }

    public void setNombreScriptsArranque(String nombreScriptsArranque) {
        this.nombreScriptsArranque = nombreScriptsArranque;
    }

    public String getUrlPing() {
        return urlPing;
    }

    public void setUrlPing(String urlPing) {
        this.urlPing = urlPing;
    }
}
