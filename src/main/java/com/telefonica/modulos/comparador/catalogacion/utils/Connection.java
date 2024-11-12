package com.telefonica.modulos.comparador.catalogacion.utils;


import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class Connection {
	
	private static Map<String, java.sql.Connection> connectionMap;
	
	private static java.sql.Connection createConnection(String entorno, String usuario, String user, String password) throws SQLException, ClassNotFoundException{
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		java.sql.Connection connn;
		
		Properties prop = new Properties();
		try {
			prop.load(Connection.class.getClassLoader().getResourceAsStream("conexiones.properties"));
			
		} catch (IOException e) {
		}
		
		if(entorno.equals("EDC")){
			connn = DriverManager.getConnection(  
					prop.getProperty("edc.dataBaseUrl"), prop.getProperty("edc.userNamePrefix") + "_" + usuario, prop.getProperty("edc.password"));
		}else if(entorno.equals("EIN")){
			connn = DriverManager.getConnection(  
					prop.getProperty("ein.dataBaseUrl"), prop.getProperty("ein.userNamePrefix") + "_" + usuario, prop.getProperty("ein.password"));
		}else if(entorno.equals("ECE")){
			connn = DriverManager.getConnection(  
					prop.getProperty("ece.dataBaseUrl"), prop.getProperty("ece.userNamePrefix") + "_" + usuario, prop.getProperty("ece.password"));
		}else if(entorno.equals("ECO")){
			connn = DriverManager.getConnection(  
					prop.getProperty("eco.dataBaseUrl"), prop.getProperty("eco.userNamePrefix") + "_" + usuario, prop.getProperty("eco.password"));
		}else{
			connn = DriverManager.getConnection(  
					prop.getProperty("epr.dataBaseUrl"), user, password);
		}
		connectionMap.put(entorno + "_" + usuario, connn);
		return connn;
	}
	
	public static void closeConnection() {
		try {
			for(Entry<String, java.sql.Connection> conn: connectionMap.entrySet()){
				if(conn.getValue() != null && !conn.getValue().isClosed()) {
					conn.getValue().close();
				}
			}
		}catch(Exception e) {
		}finally{
			connectionMap = new HashMap<String, java.sql.Connection>();
		}
	}
	
	public static java.sql.Connection getConnection(String entorno, String usuario, String user, String password) throws ClassNotFoundException, SQLException {
		java.sql.Connection conn = null;
		
		if(connectionMap == null){
			connectionMap = new HashMap<String, java.sql.Connection>();
			conn = createConnection(entorno, usuario, user, password);
		}else{
			conn = connectionMap.get(entorno + "_" + usuario);
			if(conn == null || conn.isClosed()) {
				conn = createConnection(entorno, usuario, user, password);
			}
		}
		return conn;
	}
}
