package com.telefonica.modulos.despliegues.service;

import com.telefonica.modulos.despliegues.pantalla.PanelConsola;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
public class FuentesService {
    @Autowired
    private GitService gitService;

    public void copiaFuentes(String directorioOrigen, String directorioDestino, List<String> listaActivos, PanelConsola panelConsola) {
        for (String activoAux : listaActivos) {
            String activo = activoAux.replace("Modificado - ", "").replace("Nuevo - ", "");
            String directory = directorioDestino + "/" + activo.substring(0, activo.lastIndexOf("/"));
            File file = new File(directory);
            if(!file.exists()) {
                file.mkdirs();
            }
            try {
                Files.copy(new File(directorioOrigen + "/" + activo).toPath(),
                        new File(directorioDestino + "/" + activo).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copiado en: " + directorioDestino + "/" + activo);
            } catch (IOException e) {
                panelConsola.addText("ERROR - " + e.getMessage() + "\n");
            }
        }
    }

    public List<String> cargarFicherosDesdeGit(String path, String primerCommit, String ultimoCommit) throws Exception {
        return gitService.getCommitComparison(path, primerCommit, ultimoCommit);
    }

    public List<String> cargarFicherosDesdeArchivo(String rutaArchivo, String anagrama) throws Exception {
        List<String> listaActivos = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(rutaArchivo));
        String line = br.readLine();
        while (line != null) {
            line = line.trim();
            if(line.contains("/") && line.contains(anagrama)) {
                String estado = line.trim().split(",")[0];
                String archivo = line.trim().split(",")[1].substring(line.trim().split(",")[1].indexOf("/") + 1);
                listaActivos.add(estado + " - " + archivo);
            }
            line = br.readLine();
        }
        return listaActivos;
    }
}