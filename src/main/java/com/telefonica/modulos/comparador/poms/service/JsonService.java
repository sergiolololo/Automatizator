package com.telefonica.modulos.comparador.poms.service;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.modulos.comparador.poms.bean.DependencyBean;
import com.telefonica.modulos.comparador.poms.bean.PomBean;
import com.telefonica.modulos.comparador.poms.model.JsonDependency;
import com.telefonica.modulos.comparador.poms.utils.Constants;

import javax.swing.*;

public class JsonService extends ComparatorService {
	protected Map<String, String> getSortedDependenciesSpecialized(FileReader jsonFile) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readTree(jsonFile);

		Map<String, String> dependenciesMap = new TreeMap<String, String>();
		List<JsonDependency> dependencies = getSortedDependenciesList(node);
		dependencies.forEach(dp -> dependenciesMap.put(dp.getName(),
				dp.getVersion()));

		return dependenciesMap;
	}

	private List<JsonDependency> getSortedDependenciesList(JsonNode node) {

		List<JsonDependency> dependencies = new ArrayList<>();
		JsonNode versionJson = node.get(Constants.NAME_JSON_VERSION);
		JsonNode dependenciesJson = node.get(Constants.NAME_JSON_DEPENDENCIES);

		dependencies.add(new JsonDependency(Constants.NAME_JSON_VERSION, versionJson.textValue()));
		if (dependenciesJson != null) {
			dependenciesJson.fields().forEachRemaining(entry -> {
				dependencies.add(
						new JsonDependency(entry.getKey().replace(Constants.TELEFONICA_PREFIX, ""),
								entry.getValue().asText()));
			});
		}

		/// dependencies = dependencies.stream().filter(d -> d.getVersion() != null).collect(Collectors.toList());
		return dependencies;
	}

	@Override
	protected void mergear(String rutaPom, JTable tabla, String comboValue) {
		try (FileReader pom1Reader = new FileReader(rutaPom)) {
			BufferedReader br = new BufferedReader(pom1Reader);
			List<String> pomModificado = new ArrayList<>();
			try {
				boolean empiezaDependencias = false;
				boolean terminaDependencias = false;
				String lineaAnterior = "";
				String line = br.readLine();
				while (line != null) {
					if(line.contains("\"dependencies\"") && !terminaDependencias){
						empiezaDependencias = true;
						pomModificado.add(line);
						line = br.readLine();
					}
					if(!empiezaDependencias){
						if(line.contains("\"version\":") && mapaDependencias.get("version") != null && mapaDependencias.get("version").getDependenciaMerge() != null){
							line = line.replace(mapaDependencias.get("version").getDependencia1CO().getVersion(), mapaDependencias.get("version").getDependenciaMerge().getVersion());
						}
					}
					if(empiezaDependencias && !terminaDependencias && line.contains("}")){
						terminaDependencias = true;
						pomModificado.remove(pomModificado.size()-1);
						pomModificado.add(lineaAnterior.concat(","));
						aniadirNuevasDependencias(pomModificado, tabla);
					}
					if(empiezaDependencias && !terminaDependencias){
						String dependencia = line.substring(line.indexOf(".")+1);
						dependencia = dependencia.substring(0, dependencia.indexOf("\"")).trim();
						String finalDependencia = dependencia;
						Map.Entry<String, PomBean> entry = mapaDependencias.entrySet().stream().filter(e -> e.getValue().getDependenciaMerge() != null
								&& e.getValue().getDependencia1CO() != null
								&& obtenerNombreDependenciaSinTipo(e.getValue().getDependenciaMerge().getArtifactId()).equals(finalDependencia)
						).findFirst().orElse(null);
						if(entry != null){
							PomBean pomBean = entry.getValue();
							line = line.replace(pomBean.getDependencia1CO().getVersion(), pomBean.getDependenciaMerge().getVersion());
						}
					}
					lineaAnterior = line;
					pomModificado.add(line);
					line = br.readLine();
				}
			} finally {
				br.close();
			}
			FileOutputStream fileOut = new FileOutputStream(rutaPom);
			fileOut.write(String.join("\n", pomModificado).getBytes());
			fileOut.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected PomBean crearPomBean(Map.Entry<String, String> entrySetPom1, String nombreDependencia) {
		PomBean pomBean = new PomBean();
		pomBean.setNombreDependencia(nombreDependencia);
		pomBean.setTipoDependencia(getType(entrySetPom1.getKey()));
		pomBean.setDependencia1CO(crearDependencyBean(entrySetPom1));
		return pomBean;
	}

	@Override
	protected DependencyBean crearDependencyBean(Map.Entry<String, String> entrySetPom1) {
		String groupId = getGroupId(entrySetPom1.getKey());
		String artifactId = removeGroupId(entrySetPom1.getKey());
		String version = entrySetPom1.getValue();
		return new DependencyBean(groupId, artifactId, version);
	}

	@Override
	protected String removeGroupId(String key) {
		String[] split = key.split("\\.");
		return split[1];
	}

	@Override
	protected String getGroupId(String key) {
		String[] split = key.split("\\.");
		return split[0];
	}

	@Override
	protected String getName(String key) {
		return removeGroupId(key);
	}

	@Override
	protected String getType(String key) {
		return key.startsWith(Constants.JSON_LIB_COCO) ? "coco" : "cgt";
	}

	@Override
	protected void aniadirNuevasDependencias(List<String> pomModificado, JTable tabla) throws IOException {
		boolean existeDependencia = false;
		for(int i=0; i<tabla.getRowCount(); i++){
			String dependencia = obtenerNombreDependenciaSinTipo(tabla.getValueAt(i, 0).toString());
			Map.Entry<String, PomBean> entry = mapaDependencias.entrySet().stream().filter(e -> e.getValue().getDependenciaMerge() != null
					&& e.getValue().getDependencia1CO() == null
					&& obtenerNombreDependenciaSinTipo(e.getValue().getDependenciaMerge().getArtifactId()).equals(dependencia)).findFirst().orElse(null);
			if(entry != null){
				PomBean pomBean = entry.getValue();
				String nuevaLinea = Constants.PLANTILLA_DEPENDENCIA_PACKAGE_JSON.replace("${GROUP_ID}", pomBean.getDependenciaMerge().getGroupId())
						.replace("${ARTIFACT_ID}", pomBean.getDependenciaMerge().getArtifactId())
						.replace("${VERSION}", pomBean.getDependenciaMerge().getVersion());
				pomModificado.add(nuevaLinea);
				existeDependencia = true;
				break;
			}
		}
		//if(existeDependencia){
			String ultimaDependencia = pomModificado.get(pomModificado.size()-1).substring(0, pomModificado.get(pomModificado.size()-1).length()-1);
			pomModificado.set(pomModificado.size()-1, ultimaDependencia);
		//}
	}

	@Override
	protected List<String> isDependencyInClariveSpecialized(JTable tabla, boolean comparando1CO) {
		List<DependencyBean> dependencias;
		if(comparando1CO){
			dependencias = mapaDependencias.values().stream().map(PomBean::getDependencia1CO).filter(Objects::nonNull).toList();
		}else{
			dependencias = mapaDependencias.values().stream().map(PomBean::getDependencia0AT).filter(Objects::nonNull).toList();
		}


		return null;
	}
}
