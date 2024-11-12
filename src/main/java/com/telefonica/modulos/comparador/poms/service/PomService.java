package com.telefonica.modulos.comparador.poms.service;

import com.telefonica.modulos.comparador.poms.bean.DependencyBean;
import com.telefonica.modulos.comparador.poms.bean.PomBean;
import com.telefonica.modulos.comparador.poms.utils.Constants;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PomService extends ComparatorService {
	@Override
	protected Map<String, String> getSortedDependenciesSpecialized(FileReader pomFile) throws IOException, XmlPullParserException {

		Model model = new MavenXpp3Reader().read(pomFile);

		Map<String, String> dependenciesMap = new TreeMap<>();
		List<Dependency> dependencies = getSortedDependenciesList(model);
		dependencies
				.forEach(dp -> dependenciesMap.put(dp.getArtifactId() + "_" + dp.getGroupId(), dp.getVersion()));

		if(model.getParent() != null && model.getParent().getVersion() != null) {
			dependenciesMap.put(model.getParent().getArtifactId() + "_" + model.getParent().getGroupId(), model.getParent().getVersion());
		}
		if(model.getVersion() != null) {
			dependenciesMap.put("version", model.getVersion());
		}
		return dependenciesMap;
	}

	private List<Dependency> getSortedDependenciesList(Model model) {
		List<Dependency> dependencies = new ArrayList<>();
		if (model.getDependencyManagement() != null) {
			dependencies.addAll(model.getDependencyManagement().getDependencies());
		}
		if (model.getDependencies() != null) {
			dependencies.addAll(model.getDependencies());
		}
		dependencies = dependencies.stream().filter(d->d.getVersion() != null).collect(Collectors.toList());
		return dependencies;
	}

	@Override
	protected void mergear(String rutaPom, JTable tabla, String comboValue) {
		try (FileReader pom1Reader = new FileReader(rutaPom)) {
			BufferedReader br = new BufferedReader(pom1Reader);
			List<String> pomModificado = new ArrayList<>();
			try {
				String lineaAnterior = "";
				String line = br.readLine();
				while (line != null) {
					line = obtenerSiguienteLineaValida(line, br);
					if(line.contains("</dependencies>")){
						// se añaden las nuevas dependencias al final
						aniadirNuevasDependencias(pomModificado, tabla);
					}
					if(line.contains("<artifactId>")){
						String dependencia = line.replace("<artifactId>", "").replace("</artifactId>", "").trim();
						dependencia = obtenerNombreDependenciaSinTipo(dependencia);
						String finalDependencia = dependencia;
						Entry<String, PomBean> entry = mapaDependencias.entrySet().stream().filter(e -> e.getValue().getDependenciaMerge() != null
								&& e.getValue().getDependencia1CO() != null
								&& obtenerNombreDependenciaSinTipo(e.getValue().getDependenciaMerge().getArtifactId()).equals(finalDependencia)
						).findFirst().orElse(null);
						if(entry != null){
							PomBean pomBean = entry.getValue();
							lineaAnterior = lineaAnterior.replace(pomBean.getDependencia1CO().getGroupId(), pomBean.getDependenciaMerge().getGroupId());
							pomModificado.remove(pomModificado.size()-1);
							pomModificado.add(lineaAnterior);

							line = line.replace(pomBean.getDependencia1CO().getArtifactId(), pomBean.getDependenciaMerge().getArtifactId());
							pomModificado.add(line);

							line = br.readLine();
							line = obtenerSiguienteLineaValida(line, br);
							line = line.replace(pomBean.getDependencia1CO().getVersion(), pomBean.getDependenciaMerge().getVersion());
						}else {
							if(dependencia.equals(obtenerNombreDependenciaSinTipo(comboValue))
									&& mapaDependencias.get("version") != null
									&& mapaDependencias.get("version").getDependenciaMerge() != null){
								pomModificado.add(line);
								System.out.println(line);

								line = br.readLine();
								line = obtenerSiguienteLineaValida(line, br);
								line = line.replace(mapaDependencias.get("version").getDependencia1CO().getVersion(), mapaDependencias.get("version").getDependenciaMerge().getVersion());
							}
						}
					}
					pomModificado.add(line);
					lineaAnterior = line;
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

	private String obtenerSiguienteLineaValida(String line, BufferedReader br) throws IOException {
		if(line.contains("<!--") && line.contains("-->") && (!line.contains("Infrastructure")  && !line.contains("Application-specific"))){
			line = br.readLine();
		}
		if(line.contains("<!--") && !line.contains("-->")){
			do{
				line = br.readLine();
			}while(!line.contains("-->"));
			line = br.readLine();
		}
		return line;
	}

	@Override
	protected PomBean crearPomBean(Entry<String, String> entrySetPom1, String nombreDependencia) {
		PomBean pomBean = new PomBean();
		pomBean.setNombreDependencia(nombreDependencia);
		pomBean.setTipoDependencia(getType(entrySetPom1.getKey()));
		pomBean.setDependencia1CO(crearDependencyBean(entrySetPom1));
		return pomBean;
	}

	@Override
	protected DependencyBean crearDependencyBean(Entry<String, String> entrySetPom1) {
		String groupId = getGroupId(entrySetPom1.getKey());
		String artifactId = removeGroupId(entrySetPom1.getKey());
		String version = entrySetPom1.getValue();
		return new DependencyBean(groupId, artifactId, version);
	}

	@Override
	protected String removeGroupId(String key) {
        String[] split = key.split("_");
        return split[0];
    }

	@Override
	protected String getGroupId(String key) {
		String[] split = key.split("_");
		return split[1];
	}

	@Override
	protected String getName(String key) {
		String name = removeGroupId(key);
		return name.substring(name.lastIndexOf("-")+1);
	}

	@Override
	protected String getType(String key) {
		String name = removeGroupId(key);
		return name.substring(0, name.lastIndexOf("-"));
	}

	@Override
	protected String obtenerNombreDependenciaSinTipoSpecialized(String nombreDependencia) {
		if(nombreDependencia.equals("version")){
			return nombreDependencia;
		}else {
			return nombreDependencia.substring(nombreDependencia.lastIndexOf("-")+1);
		}
	}

	@Override
	protected void aniadirNuevasDependencias(List<String> pomModificado, JTable tabla) throws IOException {
		for(int i=0; i<tabla.getRowCount(); i++){
			String dependencia = obtenerNombreDependenciaSinTipo(tabla.getValueAt(i, 0).toString());
			Entry<String, PomBean> entry = mapaDependencias.entrySet().stream().filter(e -> e.getValue().getDependenciaMerge() != null
					&& e.getValue().getDependencia1CO() == null
					&& obtenerNombreDependenciaSinTipo(e.getValue().getDependenciaMerge().getArtifactId()).equals(dependencia)).findFirst().orElse(null);
			if(entry != null){
				PomBean pomBean = entry.getValue();
				String nuevaLinea = Constants.PLANTILLA_DEPENDENCIA_POM.replace("${GROUP_ID}", pomBean.getDependenciaMerge().getGroupId())
						.replace("${ARTIFACT_ID}", pomBean.getDependenciaMerge().getArtifactId())
						.replace("${VERSION}", pomBean.getDependenciaMerge().getVersion());
				pomModificado.add(nuevaLinea);
				break;
			}
		}
	}

	@Override
	protected List<String> isDependencyInClariveSpecialized(JTable tabla, boolean comparando1CO) {
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

					boolean response = exists(URLName);
					if(response) {
						String urlCheckSum = "https://tclar30.es.telefonica/artifacts/repo/group1/" + path + "/" + dependencia + "/" + version
								+ "/maven-metadata.xml";

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
}
