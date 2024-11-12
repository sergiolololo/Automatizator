package com.telefonica.modulos.busqueda.service;

import com.telefonica.modulos.busqueda.enumm.TipoActivoEnum;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class BusquedaActivos {
	private static Set<String> listaActivos = new HashSet<>();
	private String vcAnalisis;
	private static boolean encontrado = false;
	private static List<String> rutasDondeBuscar = null;
	private List<String> impactosManuales;

	public List<String> ejecutarBusqueda(List<String> impactosManuales, List<File> listaRutasbuscar, String vcAnalisis, String rutaInfa, String rutaPrte, String rutaTerc){
		this.vcAnalisis = vcAnalisis;
		this.impactosManuales = impactosManuales;
		listaRutasbuscar.forEach(ruta -> Arrays.stream(ruta.listFiles()).forEach(ruta2 -> Arrays.stream(ruta2.listFiles()).forEach(ruta3 -> {
            try {
                fetchFiles(ruta3);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        })));

		List<String> listaActivosEncontrados = new ArrayList<>();
		for(String activo : listaActivos) {
			listaActivosEncontrados.add(obtenerAplicacion(activo) + "|" + obtenerNombreActivo(activo, rutaInfa, rutaPrte, rutaTerc) + "|" + activo);
		}
		Collections.sort(listaActivosEncontrados);
		return listaActivosEncontrados;
	}

	private String obtenerNombreActivo(String ruta, String rutaInfa, String rutaPrte, String rutaTerc) {
		String inicioRuta = rutaInfa;
		if(ruta.contains(rutaPrte)) {
			inicioRuta = rutaPrte;
		}else if(ruta.contains(rutaTerc))
			inicioRuta = rutaTerc;

		String nombreActivo = ruta.substring(inicioRuta.length() + 1);
		nombreActivo = nombreActivo.substring(nombreActivo.indexOf("\\") + 1);
		nombreActivo = nombreActivo.substring(0, nombreActivo.indexOf("\\"));
		return nombreActivo;
	}

	public void inicializarListaRutas(boolean buscarJavas, boolean buscarSoas, boolean buscarPantallas, boolean buscarOsbs) {
		rutasDondeBuscar = new ArrayList<>();
		if(buscarJavas){
			rutasDondeBuscar.addAll(Arrays.asList(
					TipoActivoEnum.CNT.getRuta1CO(),
					TipoActivoEnum.DAO.getRuta1CO(),
					TipoActivoEnum.JT.getRuta1CO(),
					TipoActivoEnum.RES.getRuta1CO(),
					TipoActivoEnum.SRV_NUC.getRuta1CO(),
					TipoActivoEnum.SRV_PRES.getRuta1CO()
			));
		}
		if(buscarSoas){
			rutasDondeBuscar.add(TipoActivoEnum.SRV_SOA.getRuta1CO());
		}
		if(buscarPantallas){
			rutasDondeBuscar.addAll(Arrays.asList(
					TipoActivoEnum.CGT_NODE.getRuta1CO(),
					TipoActivoEnum.CNT_NODE.getRuta1CO()
			));
		}
		if(buscarOsbs){
			rutasDondeBuscar.add(TipoActivoEnum.SRV_EXP.getRuta1CO());
		}
		listaActivos = new HashSet<>();
    }
	
	private void fetchFiles(File dir) {
		if (rutasDondeBuscar.stream().anyMatch(dir.getAbsolutePath()::contains)){
			if(dir.isDirectory() && dir.getName().equalsIgnoreCase("BRANCHES")) {
				try {
					fetchLastVersion(dir);
				}catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}else if(dir.isDirectory() && dir.getName().equalsIgnoreCase("TRUNK")) {
				if(!encontrado) {
					Arrays.stream(dir.listFiles()).forEach(file -> {
						try {
							fetchFiles(file);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					});
				}
				encontrado = false;
			}else if(dir.isDirectory()) {
				Arrays.stream(dir.listFiles()).forEach(file -> {
					try {
						fetchFiles(file);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				});
			}else {
				redirigirTipoActivo(dir);
			}
		}
	}
	
	private void redirigirTipoActivo(File dir) {
		if(!listaActivos.contains(dir.getAbsolutePath())){
			if(dir.getAbsolutePath().contains(TipoActivoEnum.CGT_NODE.getRuta1CO())) {

			}else if(dir.getAbsolutePath().contains(TipoActivoEnum.CNT.getRuta1CO())) {
				if(TipoActivoEnum.CNT.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
					buscarPalabras(dir);
				}
			}else if(dir.getAbsolutePath().contains(TipoActivoEnum.CNT_NODE.getRuta1CO())) {
				if(TipoActivoEnum.CNT_NODE.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
					buscarPalabras(dir);
				}
			}else if(dir.getAbsolutePath().contains(TipoActivoEnum.JT.getRuta1CO())) {
				if(TipoActivoEnum.JT.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
					buscarPalabras(dir);
				}
			}else if(dir.getAbsolutePath().contains(TipoActivoEnum.RES.getRuta1CO())) {
				if(TipoActivoEnum.RES.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
					buscarPalabras(dir);
				}
			}else if(dir.getAbsolutePath().contains(TipoActivoEnum.SRV_NUC.getRuta1CO())) {
				File rutaAnterior = dir.getParentFile();
				boolean encuentraCommand = Arrays.stream(rutaAnterior.list()).anyMatch(p -> p.endsWith("Command.java"));
				if(encuentraCommand) {
					buscarPalabras(dir);
				}
			}else if(dir.getAbsolutePath().contains(TipoActivoEnum.SRV_SOA.getRuta1CO())) {
				if(TipoActivoEnum.SRV_SOA.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
					buscarPalabras(dir);
				}
			}else if(dir.getAbsolutePath().contains(TipoActivoEnum.SRV_PRES.getRuta1CO())) {
				if(TipoActivoEnum.SRV_PRES.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
					buscarPalabras(dir);
				}
			}else if(dir.getAbsolutePath().contains(TipoActivoEnum.SRV_EXP.getRuta1CO())) {
				if(TipoActivoEnum.SRV_EXP.getArchivoDondeBuscar().stream().anyMatch(dir.getName()::endsWith)) {
					buscarPalabras(dir);
				}
			}
		}
	}
	
	private void fetchLastVersion(File dir) {
		File opFile = null;
		if(dir.listFiles().length > 0) {
			vcAnalisis = vcAnalisis.substring(vcAnalisis.indexOf("_")+1);
			int vcAnalisisInt = Integer.parseInt(vcAnalisis);
			
			File[] branches = dir.listFiles();
			int j=0;
			int versionFinal = 0;
			while(j<branches.length) {
				if(branches[j].getName().contains("_PESP_")) {
					String version = branches[j].getName().substring(branches[j].getName().indexOf("PESP_"), branches[j].getName().lastIndexOf("_"));
					version = version.substring(version.indexOf("_")+1);
					int versionInt = Integer.parseInt(version);
					if(versionInt <= vcAnalisisInt && versionInt > versionFinal) {
						opFile = branches[j];
						versionFinal = versionInt;
					}
				}
				j++;
			}
			if(opFile != null) {
		        fetchFiles(opFile);
		        encontrado = true;
			}else {
                encontrado = branches.length == 1 && branches[0].getName().contains("MIGRADO");
		    }
    	}
	}

	private void buscarPalabras(File dir){
		try (BufferedReader br = new BufferedReader(new FileReader(dir))) {
			AtomicBoolean terminarBucle = new AtomicBoolean();
			terminarBucle.set(false);
			String line = br.readLine();
			while (line != null && !terminarBucle.get()) {
				String finalLine = line;
				String ocurrencia = impactosManuales.stream().filter(e -> finalLine.trim().toUpperCase().contains(e.toUpperCase())).findFirst().orElse(null);
				if(ocurrencia != null) {
					listaActivos.add(dir.getAbsolutePath());
					terminarBucle.set(true);
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public String seleccionarDirectorio(int fileSelectionMode, String ruta) {
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new File(ruta));
	    chooser.setAcceptAllFileFilterUsed(false);
	    chooser.setFileSelectionMode(fileSelectionMode);
	    
	    String directorio = "";
	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	directorio = chooser.getSelectedFile().getAbsolutePath();
	    }
	    return directorio;
	}

	public String obtenerAplicacion(String ruta) {
		String aplicacion = "INFA";
		if(ruta.contains("\\PRTE\\")) {
			aplicacion = "PRTE";
		}else if(ruta.contains("\\TERC\\")) {
			aplicacion = "TERC";
		}
		return aplicacion;
	}

	public void mostrarMensajeInformativo(String mensaje) {
		JOptionPane.showMessageDialog(null, mensaje);
	}
}
