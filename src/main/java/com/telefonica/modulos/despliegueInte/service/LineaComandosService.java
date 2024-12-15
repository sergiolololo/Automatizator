package com.telefonica.modulos.despliegueInte.service;

import com.jcraft.jsch.*;
import com.telefonica.modulos.despliegueInte.beans.LineaComandosBean;
import com.telefonica.modulos.despliegueInte.pantalla.PanelConsola;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LineaComandosService {

    private Session session = null;
    private ChannelSftp channelSftp = null;
    private ChannelExec channelExec = null;
    private final LineaComandosBean lineaComandosBean;
    private final PanelConsola panelConsola;

    public LineaComandosService(LineaComandosBean lineaComandosBean, PanelConsola panelConsola) {
        this.lineaComandosBean = lineaComandosBean;
        this.panelConsola = panelConsola;
    }

    private void crearConexion(String host, String user, String password) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(user, host, 22);
        session.setConfig("kex", "diffie-hellman-group1-sha1");
        session.setConfig("server_host_key", "ssh-rsa");
        session.setConfig("cipher.c2s", "aes128-cbc");
        session.setConfig("cipher.s2c", "aes128-cbc");
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();
    }

    private void crearConexionSFTP(String host, String user, String password) throws JSchException {
        crearConexion(host, user, password);
        channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
    }

    private void crearConexionSSH(String host, String user, String password) throws JSchException {
        crearConexion(host, user, password);
        channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err, true);
    }

    public void moverFicherosAMaquina(List<String> listaActivos, String directorioLocalFicheros) throws Exception {
        try {
            crearConexionSFTP(lineaComandosBean.getMaquinaTramitador().getHost(), lineaComandosBean.getMaquinaTramitador().getUser(),
                    lineaComandosBean.getMaquinaTramitador().getPassword());
            for (String activoAux : listaActivos) {
                if (activoAux.contains("Eliminado")) {
                    String activo = activoAux.replace("Eliminado - ", "");
                    String rutaActivoOrigen = lineaComandosBean.getDirectorioConstruccion() + "/" + activo;
                    try{
                        channelSftp.rm(rutaActivoOrigen);
                        System.out.println("Fichero eliminado: " + lineaComandosBean.getDirectorioConstruccion() + "/" + activo);
                        panelConsola.addText("Fichero eliminado: " + lineaComandosBean.getDirectorioConstruccion() + "/" + activo + "\n");
                    }catch(Exception e){
                        panelConsola.addText("Error al eliminar fichero, posiblemente se haya eliminado previamente: " + rutaActivoOrigen + "\n");
                    }
                }else{
                    String activo = activoAux.replace("Modificado - ", "").replace("Nuevo - ", "");
                    String rutaActivoOrigen = directorioLocalFicheros + "/" + activo;
                    String rutaDestino = lineaComandosBean.getDirectorioConstruccion() + "/" + activo.substring(0, activo.lastIndexOf("/"));

                    crearDirectorioEnMaquinaSiNoExiste(rutaDestino);

                    channelSftp.put(rutaActivoOrigen, rutaDestino);
                    System.out.println("Fichero añadido: " + lineaComandosBean.getDirectorioConstruccion() + "/" + activo);
                    panelConsola.addText("Fichero añadido: " + lineaComandosBean.getDirectorioConstruccion() + "/" + activo + "\n");
                }
            }
        } catch (Exception e) {
            panelConsola.addText("ERROR - " + e.getMessage() + "\n");
            throw e;
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private void crearDirectorioEnMaquinaSiNoExiste(String rutaDestino) throws SftpException {
        try{
            StringBuilder directorio = new StringBuilder("/");
            channelSftp.cd("/");
            String[] folders = rutaDestino.split( "/" );
            for ( String folder : folders ) {
                if (!folder.isEmpty()) {
                    try {
                        channelSftp.cd(folder);
                        directorio.append(folder).append("/");
                    }
                    catch ( SftpException e ) {
                        channelSftp.mkdir(folder);
                        channelSftp.cd(folder);
                        directorio.append(folder).append("/");
                        System.out.println("Directorio creado: " + directorio);
                        panelConsola.addText("Directorio creado: " + directorio + "\n");
                    }
                }
            }
        }catch(Exception e){
            channelSftp.mkdir(rutaDestino);
        }
    }

    public void compilar(String directorioLocalFicheros) throws Exception {
        String comando1 = "cd " + lineaComandosBean.getDirectorioConstruccion();
        String comando2 = " && ./build.sh";
        ejecutarComando(comando1 + comando2, 1);

        moverWarCarpetaLocal(directorioLocalFicheros);
        moverWarPortalAMaquina(directorioLocalFicheros);
    }

    private void moverWarCarpetaLocal(String directorioLocalFicheros) throws Exception {
        try {
            panelConsola.addText("Moviendo war del portal de la máquina a la carpeta local...\n");
            crearConexionSFTP(lineaComandosBean.getMaquinaTramitador().getHost(), lineaComandosBean.getMaquinaTramitador().getUser(),
                    lineaComandosBean.getMaquinaTramitador().getPassword());
            String rutaActivoOrigen = lineaComandosBean.getMaquinaPortal().getDirectorioWar();
            channelSftp.get(rutaActivoOrigen, directorioLocalFicheros);
            panelConsola.addText("Se ha movido el war del portal de la máquina a la carpeta local: " + directorioLocalFicheros + "\n");
        } catch (Exception e) {
            panelConsola.addText("ERROR - " + e.getMessage() + "\n");
            throw e;
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private void moverWarPortalAMaquina(String directorioLocalFicheros) throws Exception {
        try {
            crearConexionSFTP(lineaComandosBean.getMaquinaPortal().getHost(), lineaComandosBean.getUsuarioDavid(), lineaComandosBean.getPasswordDavid());
            String rutaLocalWar = directorioLocalFicheros + lineaComandosBean.getMaquinaPortal().getDirectorioWar().substring(lineaComandosBean.getMaquinaPortal().getDirectorioWar().lastIndexOf("/"));
            String nombreWar = lineaComandosBean.getMaquinaPortal().getDirectorioDejarWar() + "/" + rutaLocalWar.substring(rutaLocalWar.lastIndexOf("/") + 1);
            String nombreWarAntiguo = nombreWar + ".backup";
            panelConsola.addText("Renombrando war existente como backup...\n");

            try{
                channelSftp.rm(nombreWarAntiguo);
            }catch(Exception e){
                System.out.println("No existe backup del war, por eso no se borra");
            }
            try{
                channelSftp.rename(nombreWar, nombreWarAntiguo);
            }catch(Exception e){
                System.out.println("No existe war, por eso no se crea backup");
            }
            panelConsola.addText("Moviendo war del portal de la carpeta local a la máquina...\n");
            channelSftp.put(rutaLocalWar, lineaComandosBean.getMaquinaPortal().getDirectorioDejarWar());
            panelConsola.addText("Se ha movido el war del portal de la carpeta local a la máquina: " + lineaComandosBean.getMaquinaPortal().getDirectorioDejarWar() + "\n");
        } catch (Exception e) {
            panelConsola.addText("ERROR - " + e.getMessage() + "\n");
            throw e;
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public void desplegarYReiniciar(boolean desplegarSSNN, boolean desplegarPortal) throws Exception {
        if(desplegarSSNN){
            panelConsola.addText("Desplegando ficheros SSN/fases/tramitador y reiniciando servidores...\n");
            // desplegar
            ejecutarScrips(1, lineaComandosBean.getMaquinaTramitador().getDirectorioScriptDespliegue(), lineaComandosBean.getMaquinaTramitador().getNombreScriptsDespliegue());
            // reiniciar
            ejecutarScrips(1, lineaComandosBean.getMaquinaTramitador().getDirectorioScriptDespliegue(), lineaComandosBean.getMaquinaTramitador().getNombreScriptsArranque());
            panelConsola.addText("Ficheros SSN/fases/tramitador desplegados y servidores reiniciados\n");
        }
        if(desplegarPortal){
            panelConsola.addText("Desplegando ficheros portal y reiniciando servidores...\n");
            // desplegar
            ejecutarScrips(2, lineaComandosBean.getMaquinaPortal().getDirectorioScriptDespliegue(), lineaComandosBean.getMaquinaPortal().getNombreScriptsDespliegue());
            // reiniciar
            ejecutarScrips(2, lineaComandosBean.getMaquinaPortal().getDirectorioScriptDespliegue(), lineaComandosBean.getMaquinaPortal().getNombreScriptsArranque());
            panelConsola.addText("Ficheros portal desplegados y servidores reiniciados\n");
        }
    }

    private void ejecutarScrips(int opcion, String directorioScripts, String nombreScripts) throws Exception {
        String comando1 = "cd " + directorioScripts;
        String[] scriptsDespliegue = nombreScripts.split(",");

        for (String script : scriptsDespliegue) {
            if (!script.isEmpty()) {
                String comando2 = " && ./"+script;
                ejecutarComando(comando1 + comando2, opcion);
            }
        }
    }

    private void ejecutarComando(String comando, int opcion) throws Exception {
        try {
            if(opcion == 1){
                crearConexionSSH(lineaComandosBean.getMaquinaTramitador().getHost(), lineaComandosBean.getMaquinaTramitador().getUser(), lineaComandosBean.getMaquinaTramitador().getPassword());
            }else{
                crearConexionSSH(lineaComandosBean.getMaquinaPortal().getHost(), lineaComandosBean.getMaquinaPortal().getUser(), lineaComandosBean.getMaquinaPortal().getPassword());
            }
            channelExec.setCommand(comando);
            channelExec.connect();
            pintarTrazas();
        } catch (Exception e) {
            panelConsola.addText("ERROR - " + e.getMessage() + "\n");
            throw e;
        } finally {
            if (channelExec != null) {
                channelExec.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private void pintarTrazas() throws IOException, InterruptedException {
        InputStream in = channelExec.getInputStream();
        byte[] tmp = new byte[1024];
        while(true){
            while(in.available()>0){
                int i = in.read(tmp, 0, 1024);
                if(i < 0)break;
                String text = new String(tmp, 0, i);
                System.out.print(text);
                panelConsola.addText(text);
            }
            if(channelExec.isClosed()){
                int exitStatus = channelExec.getExitStatus();
                InputStream error = channelExec.getErrStream();
                if (error.available() > 0) {
                    byte[] errorTmp = new byte[1024];
                    int j = error.read(errorTmp, 0, 1024);
                    if (j < 0) break;
                    String errorText = new String(errorTmp, 0, j);
                    System.out.print(errorText);
                    panelConsola.addText(errorText);
                }
                System.out.println("Before Disconnected Here exit-status: " + exitStatus);
                channelExec.disconnect();
                System.out.println("Disconnected Here exit-status: " + exitStatus);
                break;
            }
            Thread.sleep(100);
        }
        System.out.println("***** out of infinite loop");
    }
}