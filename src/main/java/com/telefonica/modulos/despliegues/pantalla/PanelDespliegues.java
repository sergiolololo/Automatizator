package com.telefonica.modulos.despliegues.pantalla;

import com.telefonica.modulos.despliegues.beans.LineaComandosBean;
import com.telefonica.modulos.despliegues.beans.MaquinaBean;
import com.telefonica.modulos.despliegues.service.FuentesService;
import com.telefonica.modulos.despliegues.service.GitService;
import com.telefonica.modulos.despliegues.service.LineaComandosService;
import com.telefonica.modulos.despliegues.utils.Constantes;
import com.telefonica.modulos.despliegues.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings({"rawtypes" })
@Component
public class PanelDespliegues extends JPanel {

	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private FuentesService fuentesService;
	private final FileUtil fileUtil = new FileUtil();

	private final JComboBox<String> cmbAnagramas;
	private final JTextField txtDirectorioLocalRepos;
	private final JTextField txtDirectorioActivosModificados;
	private final JTextField txtPrimerCommit, txtUltimoCommit, txtRutaFicheroActivosModificados;

	private final JButton btnCopiarFicheros, btnCompilar, btnDesplegar, btnAbrirConsola;
	private JButton btnCopiarResult;
	private JButton btnCompilarResult;
	private JButton btnDesplegarResult;

	private final JCheckBox chkSSNN;
	private final JCheckBox chkPortal;

	private final JLabel lblNumFicheros;
	private PanelConsola panelConsola;
	private PanelHistoricoLogs panelHistoricoLogs;

	private final JTable tablaActivos;

	private final String rutaLocalRepositorios, rutaLocalDestinoFuentesModificados, rutaLocalCarpetaLogs;
	private String anagramaSeleccionado;
	private Map<String, LineaComandosBean> mapaAnagramas;
	private List<String> listaActivos = new ArrayList<>();
	
	public PanelDespliegues() throws IOException {

		Properties prop = new Properties();
		prop.load(new FileReader("application.properties"));

		rutaLocalRepositorios = prop.getProperty("rutaLocal.Repositorios");
		rutaLocalDestinoFuentesModificados = fileUtil.crearDirectorio(prop.getProperty("rutaLocal.destinoFuentesModificados"));
		rutaLocalCarpetaLogs = fileUtil.crearDirectorio(prop.getProperty("rutaLocal.carpetaLogs"));

        JLabel lblRutaDirectorio = new JLabel("* Ruta directorio local repositorios");
        lblRutaDirectorio.setBounds(15, 10, 235, 14);
		add(lblRutaDirectorio);
        
        txtDirectorioLocalRepos = new JTextField();
        txtDirectorioLocalRepos.setText(rutaLocalRepositorios);
        txtDirectorioLocalRepos.setName("txtRuta1COInfa");
        txtDirectorioLocalRepos.setBounds(15, 30, 250, 20);
        txtDirectorioLocalRepos.setEnabled(false);
        txtDirectorioLocalRepos.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
				txtDirectorioLocalRepos.setText(fileUtil.seleccionarDirectorioOFichero(JFileChooser.DIRECTORIES_ONLY, rutaLocalRepositorios));
				txtDirectorioLocalRepos.setToolTipText(txtDirectorioLocalRepos.getText());
        	}
        });
        txtDirectorioLocalRepos.setColumns(10);
		add(txtDirectorioLocalRepos);
        
        JLabel lblRutaDestinoLocalFuentes = new JLabel("* Ruta directorio local destino fuentes modificados");
        lblRutaDestinoLocalFuentes.setBounds(15, 60, 283, 14);
		add(lblRutaDestinoLocalFuentes);
        
        txtDirectorioActivosModificados = new JTextField();
        txtDirectorioActivosModificados.setText(rutaLocalDestinoFuentesModificados);
        txtDirectorioActivosModificados.setName("txtRuta1COPrte");
        txtDirectorioActivosModificados.setBounds(15, 80, 250, 20);
        txtDirectorioActivosModificados.setEnabled(false);
        txtDirectorioActivosModificados.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		txtDirectorioActivosModificados.setText(fileUtil.seleccionarDirectorioOFichero(JFileChooser.DIRECTORIES_ONLY, rutaLocalDestinoFuentesModificados));
        		txtDirectorioActivosModificados.setToolTipText(txtDirectorioActivosModificados.getText());
        	}
        });
        txtDirectorioActivosModificados.setColumns(10);
		add(txtDirectorioActivosModificados);
        
        JLabel lblAnagrama = new JLabel("* Anagrama a desplegar");
        lblAnagrama.setBounds(15, 110, 235, 14);
		add(lblAnagrama);

		cmbAnagramas = new JComboBox<>();
		cmbAnagramas.setBounds(15, 130, 250, 21);
		cmbAnagramas.addActionListener(e -> anagramaSeleccionado = Objects.requireNonNull(cmbAnagramas.getSelectedItem()).toString());
		add(cmbAnagramas);

		JLabel lblQueDesplegar = new JLabel("* Selecciona qué desplegar");
		lblQueDesplegar.setBounds(308, 10, 198, 14);
		add(lblQueDesplegar);

		chkSSNN = new JCheckBox("SSNN / fases / tramitador");
		chkSSNN.setBounds(318, 30, 162, 21);
		add(chkSSNN);

		chkPortal = new JCheckBox("Portal");
		chkPortal.setBounds(318, 53, 162, 21);
		add(chkPortal);

		JPanel panelImpactoDesdeGIT = new JPanel();
		panelImpactoDesdeGIT.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelImpactoDesdeGIT.setBounds(537, 0, 276, 216);
		panelImpactoDesdeGIT.setLayout(null);
		add(panelImpactoDesdeGIT);

		JLabel lblImpactosGIT = new JLabel("Cargar impactos desde GIT");
		lblImpactosGIT.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblImpactosGIT.setBounds(10, 10, 185, 13);
		panelImpactoDesdeGIT.add(lblImpactosGIT);

		JLabel lblHashPrimerCommit = new JLabel("Hash commit del que partió la rama");
		lblHashPrimerCommit.setBounds(34, 91, 241, 14);
		panelImpactoDesdeGIT.add(lblHashPrimerCommit);

		txtPrimerCommit = new JTextField();
		txtPrimerCommit.setText("ff90a995bc8f9a67cabbb863775a0f137485b9cc");
		txtPrimerCommit.setName("txtRutaficheroDependencias");
		txtPrimerCommit.setBounds(34, 111, 221, 20);
		txtPrimerCommit.setColumns(10);
		panelImpactoDesdeGIT.add(txtPrimerCommit);

		JLabel lblHashUltCommit = new JLabel("Hash último commit branch");
		lblHashUltCommit.setBounds(34, 41, 198, 13);
		panelImpactoDesdeGIT.add(lblHashUltCommit);

		txtUltimoCommit = new JTextField();
		txtUltimoCommit.setText("cf0dd1d1ed887df852b0291bfc0ef1f539f7a376");
		txtUltimoCommit.setName("txtPesp");
		txtUltimoCommit.setBounds(34, 61, 221, 20);
		txtUltimoCommit.setColumns(10);
		panelImpactoDesdeGIT.add(txtUltimoCommit);

		JButton btnIdentificar = new JButton("IDENTIFICAR FICHEROS");
		btnIdentificar.setBounds(34, 157, 175, 33);
		btnIdentificar.addActionListener(e -> {
			if (txtPrimerCommit.getText().isEmpty() || txtUltimoCommit.getText().isEmpty()) {
				mostrarMensaje("Debe rellenar los hash antes de continuar", JOptionPane.INFORMATION_MESSAGE);
			}else {
				identificarFicherosModificados(1);
			}
		});
		panelImpactoDesdeGIT.add(btnIdentificar);

		JPanel panelImpactoDesdeFichero = new JPanel();
		panelImpactoDesdeFichero.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelImpactoDesdeFichero.setBounds(817, 0, 276, 216);
		panelImpactoDesdeFichero.setLayout(null);
		add(panelImpactoDesdeFichero);

        JLabel lblImpactosFichero = new JLabel("Cargar impactos desde fichero");
		lblImpactosFichero.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblImpactosFichero.setBounds(10, 10, 178, 13);
		panelImpactoDesdeFichero.add(lblImpactosFichero);

        JLabel lblRutaFicheroActivos = new JLabel("Ruta fichero activos modificados");
		lblRutaFicheroActivos.setBounds(34, 41, 198, 13);
		panelImpactoDesdeFichero.add(lblRutaFicheroActivos);

        txtRutaFicheroActivosModificados = new JTextField();
		txtRutaFicheroActivosModificados.setName("txtPesp");
		txtRutaFicheroActivosModificados.setColumns(10);
		txtRutaFicheroActivosModificados.setEnabled(false);
		txtRutaFicheroActivosModificados.setBounds(34, 61, 221, 20);
		txtRutaFicheroActivosModificados.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtRutaFicheroActivosModificados.setText(fileUtil.seleccionarDirectorioOFichero(JFileChooser.FILES_ONLY, rutaLocalDestinoFuentesModificados));
				txtRutaFicheroActivosModificados.setToolTipText(txtRutaFicheroActivosModificados.getText());
			}
		});
		panelImpactoDesdeFichero.add(txtRutaFicheroActivosModificados);

		JButton btnCargar = new JButton("CARGAR");
		btnCargar.setBounds(34, 100, 107, 33);
		btnCargar.addActionListener(e -> {
			if (!txtRutaFicheroActivosModificados.getText().isEmpty()) {
				identificarFicherosModificados(2);
			}else {
				mostrarMensaje("Debe seleccionar un fichero antes de continuar", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		panelImpactoDesdeFichero.add(btnCargar);

		btnCopiarFicheros = new JButton("1 - COPIAR FICHEROS A MÁQUINA");
		btnCopiarFicheros.setHorizontalAlignment(SwingConstants.LEFT);
		btnCopiarFicheros.addActionListener(e -> {
            int opcion = mostrarMensaje("¿Estás seguro de que deseas copiar los ficheros a la máquina?", JOptionPane.OK_CANCEL_OPTION);
            if (opcion == 0){
				String anagrama = anagramaSeleccionado.replace("-", "_");
				if (mapaAnagramas.get(anagrama) != null) {
					btnCompilarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/warning.png"))));
					btnDesplegarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/warning.png"))));
					ejecutarAccion(1);
				}else {
					btnCopiarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/NOK.png"))));
					mostrarMensaje("No se ha encontrado el anagrama en el archivo de propiedades", JOptionPane.ERROR_MESSAGE);
				}
            }
        });
		btnCopiarFicheros.setBounds(15, 174, 221, 33);
		add(btnCopiarFicheros);

		btnCopiarResult = new JButton("");
		btnCopiarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/warning.png"))));
		btnCopiarResult.setOpaque(true);
		btnCopiarResult.setHorizontalAlignment(SwingConstants.RIGHT);
		btnCopiarResult.setForeground(Color.BLACK);
		btnCopiarResult.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnCopiarResult.setFocusPainted(false);
		btnCopiarResult.setContentAreaFilled(false);
		btnCopiarResult.setBorderPainted(false);
		btnCopiarResult.setBounds(240, 174, 40, 33);
		add(btnCopiarResult);

		btnCompilar = new JButton("2 - COMPILAR");
		btnCompilar.setHorizontalAlignment(SwingConstants.LEFT);
		btnCompilar.addActionListener(e -> {
            int opcion = mostrarMensaje("¿Estás seguro de que deseas compilar los ficheros?", JOptionPane.OK_CANCEL_OPTION);
            if (opcion == 0){
				String anagrama = anagramaSeleccionado.replace("-", "_");
				if (mapaAnagramas.get(anagrama) != null) {
					btnDesplegarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/warning.png"))));
					ejecutarAccion(2);
				}else {
					btnCompilarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/NOK.png"))));
					mostrarMensaje("No se ha encontrado el anagrama en el archivo de propiedades", JOptionPane.ERROR_MESSAGE);
				}
            }
        });
		btnCompilar.setBounds(15, 214, 109, 33);
		add(btnCompilar);

		btnCompilarResult = new JButton("");
		btnCompilarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/warning.png"))));
		btnCompilarResult.setOpaque(true);
		btnCompilarResult.setHorizontalAlignment(SwingConstants.RIGHT);
		btnCompilarResult.setForeground(Color.BLACK);
		btnCompilarResult.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnCompilarResult.setFocusPainted(false);
		btnCompilarResult.setContentAreaFilled(false);
		btnCompilarResult.setBorderPainted(false);
		btnCompilarResult.setBounds(130, 214, 40, 33);
		add(btnCompilarResult);

		btnDesplegar = new JButton("3 - DESPLEGAR Y REINICIAR SERVIDORES");
		btnDesplegar.setHorizontalAlignment(SwingConstants.LEFT);
		btnDesplegar.addActionListener(e -> {
            if (chkSSNN.isSelected() || chkPortal.isSelected()){
                int opcion = mostrarMensaje("¿Estás seguro de que deseas desplegar y reiniciar los servidores?", JOptionPane.OK_CANCEL_OPTION);
				if (opcion == 0){
					String anagrama = anagramaSeleccionado.replace("-", "_");
					if (mapaAnagramas.get(anagrama) != null) {
						ejecutarAccion(3);
					}else {
						mostrarMensaje("No se ha encontrado el anagrama en el archivo de propiedades", JOptionPane.ERROR_MESSAGE);
					}
				}
            }else {
                mostrarMensaje("Debe seleccionar al menos una opción de despliegue", JOptionPane.INFORMATION_MESSAGE);
            }
        });
		btnDesplegar.setBounds(15, 254, 265, 33);
		add(btnDesplegar);

		btnDesplegarResult = new JButton("");
		btnDesplegarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/warning.png"))));
		btnDesplegarResult.setOpaque(true);
		btnDesplegarResult.setHorizontalAlignment(SwingConstants.RIGHT);
		btnDesplegarResult.setForeground(Color.BLACK);
		btnDesplegarResult.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnDesplegarResult.setFocusPainted(false);
		btnDesplegarResult.setContentAreaFilled(false);
		btnDesplegarResult.setBorderPainted(false);
		btnDesplegarResult.setBounds(285, 254, 40, 33);
		add(btnDesplegarResult);

		btnAbrirConsola = new JButton("ABRIR CONSOLA");
		btnAbrirConsola.addActionListener(e -> {
            if (panelConsola == null) {
                panelConsola = new PanelConsola(appContext);
            }
            panelConsola.setVisible(true);
        });
		btnAbrirConsola.setBounds(809, 267, 137, 33);
		add(btnAbrirConsola);

		tablaActivos = crearTabla();
		JScrollPane scrollPane = new JScrollPane(tablaActivos);
        
        JPanel panelTabla = new JPanel();
        panelTabla.setBounds(15, 310, 1078, 375);
		panelTabla.setLayout(new GridLayout(0, 1, 0, 0));
		panelTabla.add(scrollPane);
		add(panelTabla);

        setLayout(null);
        
        lblNumFicheros = new JLabel("Número de ficheros modificados: ");
        lblNumFicheros.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblNumFicheros.setBounds(431, 287, 240, 13);
        add(lblNumFicheros);
        
        JLabel lblDisponibilidadDeServidores = new JLabel("Disponibilidad de servidores");
        lblDisponibilidadDeServidores.setBounds(308, 150, 198, 14);
        add(lblDisponibilidadDeServidores);
        
        JButton btnComprobarSsnn = new JButton("COMPROBAR SSNN");
        btnComprobarSsnn.addActionListener(e -> {
			String anagrama = anagramaSeleccionado.replace("-", "_");
			if (mapaAnagramas.get(anagrama) != null) {
				if (pingURL(mapaAnagramas.get(anagrama).getMaquinaTramitador().getUrlPing())) {
					mostrarMensaje("El servidor de SSNN está disponible", JOptionPane.INFORMATION_MESSAGE);
				} else {
					mostrarMensaje("El servidor de SSNN no está disponible", JOptionPane.ERROR_MESSAGE);
				}
			}else {
				mostrarMensaje("No se ha encontrado el anagrama en el archivo de propiedades", JOptionPane.ERROR_MESSAGE);
			}
        });
        btnComprobarSsnn.setBounds(306, 174, 162, 33);
        add(btnComprobarSsnn);
        
        JButton btnComprobarPortal = new JButton("COMPROBAR PORTAL");
        btnComprobarPortal.addActionListener(e -> {
			String anagrama = anagramaSeleccionado.replace("-", "_");
            if (mapaAnagramas.get(anagrama) != null) {
                if (pingURL(mapaAnagramas.get(anagrama).getMaquinaPortal().getUrlPing())) {
                    mostrarMensaje("El servidor de Portal está disponible", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    mostrarMensaje("El servidor de Portal no está disponible", JOptionPane.ERROR_MESSAGE);
                }
            }else {
                mostrarMensaje("No se ha encontrado el anagrama en el archivo de propiedades", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnComprobarPortal.setBounds(306, 214, 162, 33);
        add(btnComprobarPortal);
        
        JButton btnGuardarLog = new JButton("GUARDAR LOG");
        btnGuardarLog.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				if (panelConsola == null || panelConsola.textTA.getText().isEmpty()) {
					mostrarMensaje("No hay información en la consola para guardar", JOptionPane.INFORMATION_MESSAGE);
				}else {
					try {
						String anagrama = anagramaSeleccionado.replace("-", "_");
						String nombreFichero = "log_" + anagrama + "_" + new Date().getTime() + ".txt";
						File fichero = new File(rutaLocalCarpetaLogs + "/" + nombreFichero);
						FileWriter fw = new FileWriter(fichero);
						fw.write(panelConsola.textTA.getText());
						fw.close();
						mostrarMensaje("Log guardado correctamente en " + fichero.getAbsolutePath(), JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException ex) {
						mostrarMensaje("Error al guardar el log - " + ex.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
					}
				}
        	}
        });
        btnGuardarLog.setBounds(956, 267, 137, 33);
        add(btnGuardarLog);
        
        JButton btnHistricoLogs = new JButton("HISTÓRICO LOGS");
        btnHistricoLogs.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		panelHistoricoLogs = new PanelHistoricoLogs(appContext, rutaLocalCarpetaLogs);
        		panelHistoricoLogs.setVisible(true);
        	}
        });
        btnHistricoLogs.setBounds(956, 226, 137, 33);
        add(btnHistricoLogs);

		cargarComboRepositorios();

		// crear mapa de anagramas desde archivo de properties
        try {
            crearMapaAnagramas();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		System.out.println("PanelDespliegues - Constructor");
    }

	private void crearMapaAnagramas() throws IOException {
		mapaAnagramas = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("maquinas.properties"))) {
            String line = br.readLine();
            while (line != null) {
                if (!line.contains("#")) {
                    String anagrama = line.split("\\.")[0];
                    LineaComandosBean lineaComandosBean = mapaAnagramas.get(anagrama) != null ? mapaAnagramas.get(anagrama) : new LineaComandosBean();

                    String nombreMetodoOriginal = "set" + line.split("=")[0].split("\\.")[1];
                    if (nombreMetodoOriginal.contains("Tramitador") || nombreMetodoOriginal.contains("Portal")) {
                        MaquinaBean maquinaBean = nombreMetodoOriginal.contains("Tramitador") ? lineaComandosBean.getMaquinaTramitador() : lineaComandosBean.getMaquinaPortal();
                        if (maquinaBean == null) {
                            maquinaBean = new MaquinaBean();
                        }
                        String nombreMetodo = nombreMetodoOriginal.replace("Tramitador", "").replace("Portal", "");
                        Method metodoInvocar = MaquinaBean.class.getMethod(nombreMetodo, String.class);
                        String[] partes = line.split("=");
                        String valor = partes.length > 1 ? partes[1] : "";
                        metodoInvocar.invoke(maquinaBean, valor);
                        if (nombreMetodoOriginal.contains("Portal")) {
                            lineaComandosBean.setMaquinaPortal(maquinaBean);
                        } else {
                            lineaComandosBean.setMaquinaTramitador(maquinaBean);
                        }
                    } else {
                        Method metodoInvocar = LineaComandosBean.class.getMethod(nombreMetodoOriginal, String.class);
                        String valor = line.split("=")[1];
                        metodoInvocar.invoke(lineaComandosBean, valor);
                    }
                    mapaAnagramas.put(anagrama, lineaComandosBean);
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	private void cargarComboRepositorios(){
		File file = new File("O:\\git_repositories\\");
		for(File file2: Objects.requireNonNull(file.listFiles())) {
			if(file2.isDirectory()) {
				cmbAnagramas.addItem(file2.getName());
			}
		}
	}

	private void identificarFicheros(int opcion) {
		btnDesplegar.setEnabled(false);
		btnCompilar.setEnabled(false);
		clearTable((DefaultTableModel) tablaActivos.getModel());
		try {
			if (opcion == 1) { // desde GIT
				String repoPath = txtDirectorioLocalRepos.getText() + "\\" + anagramaSeleccionado + "\\.git";
				listaActivos = fuentesService.cargarFicherosDesdeGit(repoPath, txtPrimerCommit.getText(), txtUltimoCommit.getText());
			}else { // desde fichero
				listaActivos = fuentesService.cargarFicherosDesdeArchivo(txtRutaFicheroActivosModificados.getText(), anagramaSeleccionado);
			}
			rellenarTabla();
		} catch (Exception e) {
			mostrarMensaje("Error al identificar los ficheros modificados: " + e, JOptionPane.ERROR_MESSAGE);
		}
	}

	private void rellenarTabla() {
		if (listaActivos.isEmpty()) {
			mostrarMensaje("No se han encontrado ficheros modificados para el anagrama seleccionado", JOptionPane.INFORMATION_MESSAGE);
		}else {
			for(String activo: listaActivos) {
				String estado = activo.split(" - ")[0];
				String archivo = activo.split(" - ")[1];

				String[] fila = new String[2];
				fila[0] = estado;
				fila[1] = archivo;
				((DefaultTableModel) tablaActivos.getModel()).addRow(fila);
			}
			btnCopiarFicheros.setEnabled(!listaActivos.isEmpty());
			btnCopiarFicheros.setEnabled(true);
			lblNumFicheros.setText("Número de ficheros modificados: " + listaActivos.size());
		}
	}

	private void copiarFicherosAMaquina() {
		String anagrama = anagramaSeleccionado.replace("-", "_");
		fuentesService.copiaFuentes(txtDirectorioLocalRepos.getText() + "/" + anagramaSeleccionado,
				txtDirectorioActivosModificados.getText() + "/" + anagramaSeleccionado, listaActivos, panelConsola);

		try {
			LineaComandosService lineaComandos = new LineaComandosService(mapaAnagramas.get(anagrama), panelConsola);
			lineaComandos.moverFicherosAMaquina(listaActivos, txtDirectorioActivosModificados.getText() + "/" + anagramaSeleccionado);
			btnCompilar.setEnabled(true);
			btnDesplegar.setEnabled(false);
		} catch (Exception e) {
			mostrarMensaje("Error al copiar los ficheros a la máquina - " + e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void compilar() {
		String anagrama = anagramaSeleccionado.replace("-", "_");
		try {
			LineaComandosService lineaComandosService = new LineaComandosService(mapaAnagramas.get(anagrama), panelConsola);
			lineaComandosService.compilar(txtDirectorioActivosModificados.getText() + "/" + anagramaSeleccionado);
			btnDesplegar.setEnabled(true);
		} catch (Exception e) {
			mostrarMensaje("Error al compilar los ficheros - " + e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void desplegarYReiniciar() {
		String anagrama = anagramaSeleccionado.replace("-", "_");
		try {
			LineaComandosService lineaComandosService = new LineaComandosService(mapaAnagramas.get(anagrama), panelConsola);
			lineaComandosService.desplegarYReiniciar(chkSSNN.isSelected(), chkPortal.isSelected());
			btnDesplegar.setEnabled(false);
			btnCompilar.setEnabled(false);
			btnCopiarFicheros.setEnabled(false);
		} catch (Exception e) {
			mostrarMensaje("Error al desplegar y reiniciar los servidores - " + e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

	private JTable crearTabla(){
		JTable tabla = new JTable() {
			@Serial
			private static final long serialVersionUID = 1L;
            public String getToolTipText(MouseEvent e) {
                String tip;
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                tip = getValueAt(rowIndex, colIndex)!=null?getValueAt(rowIndex, colIndex).toString():"";

                return tip;
            }
		};
		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "ESTADO", "ACTIVO" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
			   return false;
			}
		};
		tabla.setModel(tableModel);
		tabla.getColumnModel().getColumn(1).setPreferredWidth(1000);
		return tabla;
	}

	private void clearTable(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

	private int mostrarMensaje(String mensaje, int opcion) {
		if (opcion == JOptionPane.INFORMATION_MESSAGE) {
			JOptionPane.showMessageDialog(this, mensaje, "", JOptionPane.INFORMATION_MESSAGE);
			return 0;
		}else if (opcion == JOptionPane.OK_CANCEL_OPTION) {
			return JOptionPane.showConfirmDialog(this, mensaje, "", JOptionPane.YES_NO_OPTION);
		}else {
			JOptionPane.showMessageDialog(this, mensaje, "", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	
	private void identificarFicherosModificados(int opcion) {
		final JDialog waitForTrans = new JDialog();
		JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 5);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("Identificando ficheros modificados desde GIT...");
		final JOptionPane optionPane = new JOptionPane(progressBar, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        waitForTrans.setSize(200,200);
    	waitForTrans.setLocationRelativeTo(null);
    	waitForTrans.setTitle("Espere...");
    	waitForTrans.setModal(true);
    	waitForTrans.setContentPane(optionPane);
    	waitForTrans.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		SwingWorker worker = new SwingWorker() {
			public String doInBackground()  {
					try {
						identificarFicheros(opcion);
					} catch (Exception e) {
						System.out.println(e.getLocalizedMessage());
					}
				return null;
			}
			public void done() {
				waitForTrans.setVisible(false);
				waitForTrans.dispose();
			}
		};
		worker.execute();
		waitForTrans.pack();
		waitForTrans.setVisible(true);
	}

	private void ejecutarAccion(int action) {
		if (panelConsola == null) {
			panelConsola = new PanelConsola(appContext);
		}
		panelConsola.closeBtn.setEnabled(false);
		panelConsola.addText("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		String mensaje = switch (action) {
            case 1 -> "\n----------------------------------------------------------------------------------------   " + Constantes.INICIO_COPIA_FICHEROS + "   ----------------------------------------------------------------------------------------";
            case 2 -> "\n----------------------------------------------------------------------------------------     " + Constantes.INICIO_COMPILACION + "     ---------------------------------------------------------------------------------------";
            case 3 -> "\n---------------------------------------------------------------------------------------- " + Constantes.INICIO_DESPLIEGUE + " ---------------------------------------------------------------------------------------";
            default -> "";
        };
		panelConsola.addText(mensaje);
		panelConsola.addText("\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n");

		SwingWorker worker = new SwingWorker() {
			public String doInBackground()  {
				try {
					switch (action) {
						case 1 -> {
							copiarFicherosAMaquina();
							btnCopiarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/OK.png"))));
						}
						case 2 -> {
							compilar();
							btnCompilarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/OK.png"))));
						}
						case 3 -> {
							desplegarYReiniciar();
							btnDesplegarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/OK.png"))));
						}
					}
				} catch (Exception e) {
					mostrarMensaje("Error al ejecutar la acción - " + e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
					switch (action) {
						case 1 -> btnCopiarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/NOK.png"))));
						case 2 -> btnCompilarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/NOK.png"))));
						case 3 -> btnDesplegarResult.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/NOK.png"))));
					}
				}
				return null;
			}
			public void done() {
				panelConsola.addText("\n------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
				panelConsola.addText("\n--------------------------------------------------------------------------------------------------                   Proceso finalizado                      -------------------------------------------------------------------------------------------------");
				panelConsola.addText("\n------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n");
				panelConsola.closeBtn.setEnabled(true);
				btnAbrirConsola.setEnabled(true);
			}
		};
		worker.execute();
		panelConsola.setVisible(true);
	}

	private boolean pingURL(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			return (200 <= responseCode && responseCode <= 399) || responseCode == 401;
		} catch (IOException exception) {
			return false;
		}
	}
}