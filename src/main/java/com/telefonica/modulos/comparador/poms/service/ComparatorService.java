package com.telefonica.modulos.comparador.poms.service;

import com.telefonica.modulos.comparador.poms.bean.DependencyBean;
import com.telefonica.modulos.comparador.poms.bean.PomBean;
import com.telefonica.modulos.comparador.poms.utils.Constants;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ComparatorService {

	protected Map<String, PomBean> mapaDependencias;

	public Map<String, PomBean> compare(Map<String, String> pom1, Map<String, String> pom2) {
		Map<String, PomBean> pomBeanMap = new HashMap<>();
		for (Entry<String, String> entrySetPom1 : pom1.entrySet()) {
			if(!entrySetPom1.getKey().contains("version")){
				String nombreDependencia = getName(entrySetPom1.getKey());
				PomBean pomBean = crearPomBean(entrySetPom1, nombreDependencia);
				pomBeanMap.put(nombreDependencia, pomBean);
			}else{
				PomBean pomBean = new PomBean();
				pomBean.setNombreDependencia(entrySetPom1.getKey());
				DependencyBean dependencyBean = new DependencyBean("", entrySetPom1.getKey(), entrySetPom1.getValue());
				pomBean.setDependencia1CO(dependencyBean);
				pomBeanMap.put(entrySetPom1.getKey(), pomBean);
			}
		}
		for (Entry<String, String> entrySetPom2 : pom2.entrySet()) {
			if(!entrySetPom2.getKey().contains("version")){
				String nombreDependencia = getName(entrySetPom2.getKey());
				PomBean pomBean = pomBeanMap.containsKey(nombreDependencia)? pomBeanMap.get(nombreDependencia) : new PomBean();
				if(pomBean.getDependencia0AT() == null){
					pomBean.setDependencia0AT(crearDependencyBean(entrySetPom2));
				}
				pomBeanMap.put(nombreDependencia, pomBean);
			}else{
				PomBean pomBean = pomBeanMap.get(entrySetPom2.getKey());
				DependencyBean dependencyBean = new DependencyBean("", entrySetPom2.getKey(), entrySetPom2.getValue());
				pomBean.setDependencia0AT(dependencyBean);
				pomBeanMap.put(entrySetPom2.getKey(), pomBean);
			}
		}
		mapaDependencias = pomBeanMap;
		return pomBeanMap;
	}

	public void merge(String rutaPom, JTable tabla, String comboValue) throws IOException {
		mergear(rutaPom, tabla, comboValue);
	}

	public Map<String, String> getSortedDependencies(FileReader pomFile) throws IOException, XmlPullParserException {
		return getSortedDependenciesSpecialized(pomFile);
	}

	public List<Object[]> crearListaFilas(Map<String, PomBean> mapaDependencias) {
		List<Object[]> tabla = new ArrayList<>();
		for(Entry<String, PomBean> mapa : mapaDependencias.entrySet()){
			Object[] fila = new Object[5];
			if(mapa.getValue().getDependencia1CO() != null){
				fila[0] = mapa.getValue().getDependencia1CO().getArtifactId();
				fila[1] = mapa.getValue().getDependencia1CO().getVersion();
				if(mapa.getValue().getDependencia0AT() != null){
					if(mapa.getValue().getDependencia0AT().getArtifactId().equals(mapa.getValue().getDependencia1CO().getArtifactId())){
						fila[2] = mapa.getValue().getDependencia0AT().getVersion();
					}else{
						fila[2] = "null";
						// creamos nueva fila informando solo la version de 0-AT
						Object[] fila2 = new Object[5];
						fila2[0] = mapa.getValue().getDependencia0AT().getArtifactId();
						fila2[1] = "null";
						fila2[2] = mapa.getValue().getDependencia0AT().getVersion();
						fila2[3] = true;
						fila2[4] = true;
						tabla.add(fila2);
					}
				}else{
					fila[2] = "null";
				}
            }else{
				fila[0] = mapa.getValue().getDependencia0AT().getArtifactId();
				fila[1] = "null";
				fila[2] = mapa.getValue().getDependencia0AT().getVersion();
            }
            fila[3] = true;
            fila[4] = true;
            tabla.add(fila);
		}
		return tabla;
	}

	public List<String> isDependencyInClarive(JTable tabla, boolean comparando1CO) {
		List<String> noExistingDependencies = new ArrayList<>();
		for(Entry<String, PomBean> entrySet: mapaDependencias.entrySet()){
			if(!entrySet.getKey().contains("coco.") && !entrySet.getKey().contains("parent") && !entrySet.getKey().contains("version")){
				String version = null;
				String dependencia = null;
				String groupId = null;
				if(comparando1CO && entrySet.getValue().getDependencia1CO() != null){
					version = entrySet.getValue().getDependencia1CO().getVersion();
					dependencia = entrySet.getValue().getDependencia1CO().getArtifactId();
					groupId = entrySet.getValue().getDependencia1CO().getGroupId();
				}else if (!comparando1CO && entrySet.getValue().getDependencia0AT() != null){
					version = entrySet.getValue().getDependencia0AT().getVersion();
					dependencia = entrySet.getValue().getDependencia0AT().getArtifactId();
					groupId = entrySet.getValue().getDependencia0AT().getGroupId();
				}
				if(version != null && dependencia != null){
					String path = StringUtils.join(groupId.split("\\."), "/");
					String artefactName = dependencia + "-" + version + ".jar";
					String URLName = "https://tclar30.es.telefonica/artifacts/repo/group1/" + path + "/" + dependencia + "/" + version
							+ "/" + artefactName;

					if(entrySet.getValue().getTipoDependencia().equals("cgt")){
						URLName = "https://tclar30.es.telefonica/artifacts/repo/group1/" + Constants.GROUP_TELEFONICA + groupId + "/cgt/" + dependencia + "/" + version
								+ "/" + artefactName;
					}

					boolean response = exists(URLName);
					if(response) {
						String urlCheckSum = "https://tclar30.es.telefonica/artifacts/repo/group1/" + path + "/" + dependencia + "/" + version
								+ "/maven-metadata.xml";

						if(entrySet.getValue().getTipoDependencia().equals("cgt")){
							urlCheckSum = "https://tclar30.es.telefonica/artifacts/repo/group1/" + Constants.GROUP_TELEFONICA + groupId + "/cgt/" + dependencia + "/" + version
									+ "/maven-metadata.xml";
						}

						response = checkSumValidation(urlCheckSum);
					}
					if(!response) {
						noExistingDependencies.add(entrySet.getKey() + " - " + version);
						DefaultTableModel model = (DefaultTableModel) tabla.getModel();
						for (int i=0; i<model.getRowCount(); i++) {
							String dependencia2 = model.getValueAt(i, 0).toString();
							if(dependencia.equals(dependencia2)) {
								model.setValueAt(false, i, comparando1CO ? 3 : 4);
								break;
							}
						}
					}
				}
			}
		}
		return noExistingDependencies;
	}

	public String obtenerNombreDependenciaSinTipo(String nombreDependencia) {
		return obtenerNombreDependenciaSinTipoSpecialized(nombreDependencia);
	}

    protected boolean exists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }

	protected boolean checkSumValidation(String URLName) {
    	try {
            //HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            //con.setRequestMethod("HEAD");
            if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            	BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
            	String line = br.readLine();
            	br.close();
                return line.startsWith("<?xml");
            }
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }

	protected String obtenerNombreDependenciaSinTipoSpecialized(String nombreDependencia){
		return nombreDependencia;
	}

	protected Map<String, String> getSortedDependenciesSpecialized(FileReader pomFile) throws IOException, XmlPullParserException{
		return null;
	}
	protected PomBean crearPomBean(Entry<String, String> entrySetPom1, String nombreDependencia){
		return null;
	}
	protected DependencyBean crearDependencyBean(Entry<String, String> entrySetPom1){
		return null;
	}
	protected String removeGroupId(String key){
		return null;
	}
	protected String getGroupId(String key){
		return null;
	}
	protected String getName(String key){
		return null;
	}
	protected String getType(String key){
		return null;
	}
	protected void mergear(String rutaPom, JTable tabla, String comboValue) {
	}
	protected void aniadirNuevasDependencias(List<String> pomModificado, JTable tabla) throws IOException {
	}
	protected List<String> isDependencyInClariveSpecialized(JTable tabla, boolean comparando1CO) {
		return null;
	}
}
