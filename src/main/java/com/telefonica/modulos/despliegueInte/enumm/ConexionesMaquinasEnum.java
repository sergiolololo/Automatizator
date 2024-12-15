package com.telefonica.modulos.despliegueInte.enumm;

public enum ConexionesMaquinasEnum {
	V0_REPO_DES(
			"10.159.5.30",
			"v0",
			"qENMLKJ8",
			"/var/opt/aat/d/v0/distribucion/servidor1/v0_tramitador.war",
			"/var/opt/aat/d/v0",
			"despliega0,despliega1",
			"",
			"10.159.5.101",
			"v0",
			"qENMLKJ8",
			"/var/opt/aat/d/v0/distribucion/servidor2/v0_portal.war",
			"/var/opt/aat/d/v0",
			"despliegaV0",
			"",
			"/var/opt/aat/d/v0/construccion",
			"/var/opt/aat/d/v0",
			"http://10.159.5.30:22320/v0/",
			"http://10.159.5.101:23660/v0/"
			),
	V1_REPO_DES(
			"10.159.5.30",
			"v1",
			"kvvuFEL0",
			"/var/opt/aat/d/v1/distribucion/servidor1/web/v1_tramitador.war",
			"/var/opt/aat/d/v1/bin",
			"despliega00,despliega01",
			"arrTRA0,arrTRA1",
			"10.159.5.101",
			"v1",
			"kvvuFEL0",
			"/var/opt/aat/d/v1/distribucion/servidor2/web/v1_portal.war",
			"/var/opt/aat/d/v1/bin",
			"despliegaPortal",
			"arrPOR0,arrPOR1",
			"/var/opt/aat/d/v1/construccion",
			"/home/t613050",
			"http://10.159.5.30:22326/v1/",
			"http://10.159.5.101:23665/v1/"
			);

	private final String hostTramitador;
	private final String userTramitador;
	private final String passwordTramitador;
	private final String directorioWarTramitador;
	private final String directorioScriptDespliegueTramitador;
	private final String nombreScriptsDespliegueTramitador;
	private final String nombreScriptsArranqueTramitador;
	private final String hostPortal;
	private final String userPortal;
	private final String passwordPortal;
	private final String directorioWarPortal;
	private final String directorioScriptDesplieguePortal;
	private final String nombreScriptsDesplieguePortal;
	private final String nombreScriptsArranquePortal;
	private final String directorioConstruccion;
	private final String directorioDejarWarPortal;
	private final String urlTramitador;
	private final String urlPortal;
	
	ConexionesMaquinasEnum(String hostTramitador, String userTramitador, String passwordTramitador, String directorioWarTramitador,
						   String directorioScriptDespliegueTramitador, String nombreScriptsDespliegueTramitador, String nombreScriptsArranqueTramitador,
						   String hostPortal, String userPortal, String passwordPortal, String directorioWarPortal,
						   String directorioScriptDesplieguePortal, String nombreScriptsDesplieguePortal, String nombreScriptsArranquePortal,
						   String directorioConstruccion, String directorioDejarWarPortal,
						   String urlTramitador, String urlPortal) {
		this.hostTramitador = hostTramitador;
		this.userTramitador = userTramitador;
		this.passwordTramitador = passwordTramitador;
		this.directorioWarTramitador = directorioWarTramitador;
		this.directorioScriptDespliegueTramitador = directorioScriptDespliegueTramitador;
		this.nombreScriptsDespliegueTramitador = nombreScriptsDespliegueTramitador;
		this.nombreScriptsArranqueTramitador = nombreScriptsArranqueTramitador;
		this.hostPortal = hostPortal;
		this.userPortal = userPortal;
		this.passwordPortal = passwordPortal;
		this.directorioWarPortal = directorioWarPortal;
		this.directorioScriptDesplieguePortal = directorioScriptDesplieguePortal;
		this.nombreScriptsDesplieguePortal = nombreScriptsDesplieguePortal;
		this.nombreScriptsArranquePortal = nombreScriptsArranquePortal;
		this.directorioConstruccion = directorioConstruccion;
		this.directorioDejarWarPortal = directorioDejarWarPortal;
		this.urlTramitador = urlTramitador;
		this.urlPortal = urlPortal;
	}

	public String getHostTramitador() {
		return hostTramitador;
	}

	public String getUserTramitador() {
		return userTramitador;
	}

	public String getPasswordTramitador() {
		return passwordTramitador;
	}

	public String getDirectorioWarTramitador() {
		return directorioWarTramitador;
	}

	public String getDirectorioScriptDespliegueTramitador() {
		return directorioScriptDespliegueTramitador;
	}

	public String getNombreScriptsDespliegueTramitador() {
		return nombreScriptsDespliegueTramitador;
	}

	public String getNombreScriptsArranqueTramitador() {
		return nombreScriptsArranqueTramitador;
	}

	public String getHostPortal() {
		return hostPortal;
	}

	public String getUserPortal() {
		return userPortal;
	}

	public String getPasswordPortal() {
		return passwordPortal;
	}

	public String getDirectorioWarPortal() {
		return directorioWarPortal;
	}

	public String getDirectorioScriptDesplieguePortal() {
		return directorioScriptDesplieguePortal;
	}

	public String getNombreScriptsDesplieguePortal() {
		return nombreScriptsDesplieguePortal;
	}

	public String getNombreScriptsArranquePortal() {
		return nombreScriptsArranquePortal;
	}

	public String getDirectorioConstruccion() {
		return directorioConstruccion;
	}

	public String getDirectorioDejarWarPortal() {
		return directorioDejarWarPortal;
	}

	public String getUrlTramitador() {
		return urlTramitador;
	}

	public String getUrlPortal() {
		return urlPortal;
	}

	public String getUserDavid() {
		return "t613050";
	}

	public String getPasswordDavid() {
		return "DLAlonso0924";
	}
}