package com.telefonica.modulos.comparador.poms.pantalla;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;

import com.telefonica.modulos.comparador.poms.bean.DependencyBean;
import com.telefonica.modulos.comparador.poms.bean.PomBean;
import com.telefonica.modulos.comparador.poms.renderer.StatusColumnCellRenderer;
import com.telefonica.modulos.comparador.poms.service.ComparatorService;
import com.telefonica.modulos.comparador.poms.service.JsonService;
import com.telefonica.modulos.comparador.poms.service.PomService;
import com.telefonica.modulos.comparador.poms.utils.Constants;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

@Component
public class PanelComparadorPoms extends JPanel {
    @Serial
	private static final long serialVersionUID = 1L;
    private final PomService pomService = new PomService();
    private final JsonService jsonService = new JsonService();
    private ComparatorService comparatorService;

    private final JButton btnMergearIzquierda;
    private JTable tabla1 = null;
    private JTable tabla2 = null;
    private final JTextField txtRutaPom1CO;
    private final JTextField txtRutaPom0AT;
    private final JComboBox<String> comboBox;
    private final JButton btnComprobarEnClariveIzquierda;
    private final JButton btnComprobarEnClariveDerecha;
    private boolean actionListenerActive = false;
    
    private final Map<String, String> mapaPoms1CO = new HashMap<>();
    private final Map<String, String> mapaPoms0AT = new HashMap<>();

    private Map<String, PomBean> mapaDependencias;

    public PanelComparadorPoms() {

		setBackground(SystemColor.inactiveCaption);
		setLayout(null);

        JPanel panelArriba = new JPanel();
		panelArriba.setBorder(new TitledBorder(new LineBorder(new Color(192, 192, 192), 2, true), "Comparador de poms", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
		panelArriba.setBounds(0, 0, 1214, 663);
		panelArriba.setVisible(true);
		panelArriba.setLayout(null);
		add(panelArriba);

        tabla1 = crearTabla(true);
        tabla1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if(e.getClickCount()==2){
                    String version1CO = tabla1.getValueAt(tabla1.getSelectedRow(), 1).toString();
                    String version0AT = tabla1.getValueAt(tabla1.getSelectedRow(), 2).toString();
                    if(version1CO.equals(version0AT)){
                        JOptionPane.showMessageDialog(getParent(), "La versión de 1-CO y 0-AT es la misma");
                    }else{
                        String dependencia = comparatorService.obtenerNombreDependenciaSinTipo(tabla1.getValueAt(tabla1.getSelectedRow(), 0).toString());
                        llevarDependenciaATabla2(dependencia);
                    }
                }
            }
        });

        tabla2 = crearTabla(false);
        tabla2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if(e.getClickCount()==2){
                    List<Object[]> filas = comparatorService.crearListaFilas(mapaDependencias);
                    String dependencia = comparatorService.obtenerNombreDependenciaSinTipo(tabla2.getValueAt(tabla2.getSelectedRow(), 0).toString());
                    aniadirFilasTabla(filas, dependencia);
                    ((DefaultTableModel) tabla2.getModel()).removeRow(tabla2.convertRowIndexToModel(tabla2.getSelectedRow()));
                }
            }
        });
        
        JScrollPane logScrollPane1 = new JScrollPane(tabla1);
        JScrollPane logScrollPane2 = new JScrollPane(tabla2);
        
        JLabel lblRutaActivosco = new JLabel("Ruta activos 1-CO");
        lblRutaActivosco.setBounds(117, 38, 121, 13);
        panelArriba.add(lblRutaActivosco);
        
        txtRutaPom1CO = new JTextField(50);
        txtRutaPom1CO.setBounds(225, 35, 731, 19);
        panelArriba.add(txtRutaPom1CO);
        txtRutaPom1CO.setDragEnabled(true);

        JButton btnRuta1CO = new JButton(createImageIcon("images/open-file-icon.png"));
        btnRuta1CO.setBounds(996, 35, 33, 19);
        panelArriba.add(btnRuta1CO);
        btnRuta1CO.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        			JFileChooser chooser = new JFileChooser();
        		    chooser.setCurrentDirectory(new File("."));
        		    chooser.setAcceptAllFileFilterUsed(false);
        		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        		    
        		    String directorio = "";
        		    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        		    	// Write the output to a file
        		    	directorio = chooser.getSelectedFile().getAbsolutePath();
        		    }
        		    txtRutaPom1CO.setText(directorio);
        		    txtRutaPom0AT.setText(directorio.replace("1-CO", "0-AT"));
        	}
        });

        JButton btnRuta0AT = new JButton(createImageIcon("images/open-file-icon.png"));
        btnRuta0AT.setBounds(996, 69, 33, 19);
        panelArriba.add(btnRuta0AT);
        btnRuta0AT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                String directorio = "";
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    // Write the output to a file
                    directorio = chooser.getSelectedFile().getAbsolutePath();
                }
                txtRutaPom0AT.setText(directorio);
            }
        });
        
        JLabel lblRutaActivosat = new JLabel("Ruta activos 0-AT");
        lblRutaActivosat.setBounds(117, 72, 121, 13);
        panelArriba.add(lblRutaActivosat);
        
        txtRutaPom0AT = new JTextField(50);
        txtRutaPom0AT.setBounds(225, 69, 731, 19);
        panelArriba.add(txtRutaPom0AT);
        txtRutaPom0AT.setDragEnabled(true);

        JLabel lblPomAComparar = new JLabel("Pom a comparar");
        lblPomAComparar.setBounds(117, 108, 97, 13);
        panelArriba.add(lblPomAComparar);
        
        comboBox = new JComboBox<>();
        comboBox.setBounds(225, 104, 731, 21);
        panelArriba.add(comboBox);
        comboBox.addItemListener(new ItemListener() {
        	@Override
			public void itemStateChanged(ItemEvent event) {
        		if (event.getStateChange() == ItemEvent.SELECTED && actionListenerActive) {
        			Object item = event.getItem();
        			try {
                        clearTable((DefaultTableModel) tabla2.getModel());
						comparePoms(item.toString());
						btnMergearIzquierda.setEnabled(true);
				        btnComprobarEnClariveIzquierda.setEnabled(true);
				        btnComprobarEnClariveDerecha.setEnabled(true);
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
        		}
        	}
        });

        JButton compareButton_1 = new JButton("Comparar poms", createImageIcon("images/compareFiles.png"));
        compareButton_1.setBounds(475, 144, 218, 33);
        panelArriba.add(compareButton_1);
        compareButton_1.addActionListener(e -> dialogoEspera(true, null));
        
        btnMergearIzquierda = new JButton("Mergear en 1-CO", null);
        btnMergearIzquierda.setEnabled(false);
        btnMergearIzquierda.setBounds(793, 583, 218, 33);
        panelArriba.add(btnMergearIzquierda);
        btnMergearIzquierda.addActionListener(e -> {
            String nameSelected = (String) comboBox.getSelectedItem();
            String rutaFile = mapaPoms1CO.get(comboBox.getSelectedItem().toString());
            int opcion = mostrarMensaje("Se va a actualizar el pom de 1-CO con las dependencias seleccionadas.\n¿Está seguro de que quiere continuar?");
            if (opcion == 0) {
                try{
                    comparatorService.merge(rutaFile, tabla2, comboBox.getSelectedItem().toString());
                    clearTable((DefaultTableModel) tabla2.getModel());
                    if(nameSelected.contains(".cnt-") || nameSelected.contains(".cgt-")){
                        //mergearJson(rutaFile);
                    }else{
                        ///mergearPom(rutaFile);
                        /*comparatorService.merge(rutaFile, tabla2, comboBox.getSelectedItem().toString());
                        clearTable((DefaultTableModel) tabla2.getModel());*/
                    }
                    comparePoms((String) comboBox.getSelectedItem());
                } catch (IOException el) {
                    System.out.println(el.getMessage());
                }
                btnMergearIzquierda.setEnabled(false);
            }

        });
        
        btnComprobarEnClariveIzquierda = new JButton("Comprobar en clarive 1-CO", null);
        btnComprobarEnClariveIzquierda.addActionListener(e -> dialogoEspera(false, true));
        btnComprobarEnClariveIzquierda.setEnabled(false);
        btnComprobarEnClariveIzquierda.setBounds(24, 583, 218, 33);
        panelArriba.add(btnComprobarEnClariveIzquierda);
        
        btnComprobarEnClariveDerecha = new JButton("Comprobar en clarive 0-AT", null);
        btnComprobarEnClariveDerecha.addActionListener(e -> dialogoEspera(false, false));
        btnComprobarEnClariveDerecha.setEnabled(false);
        btnComprobarEnClariveDerecha.setBounds(249, 583, 218, 33);
        panelArriba.add(btnComprobarEnClariveDerecha);
        
        JButton btnGuardarIzquierda = new JButton("Mergear todos los poms en 1-CO", null);
        btnGuardarIzquierda.addActionListener(e -> {
            try {
                for(int i=0; i<comboBox.getItemCount(); i++) {
                    comboBox.setSelectedItem(comboBox.getItemAt(i));
                    mergearPom(mapaPoms1CO.get(comboBox.getItemAt(i)));
                }
                comparePoms(comboBox.getItemAt(comboBox.getItemCount()-1));
            } catch (IOException e1) {
                System.out.println(e1.getMessage());
            }
        });
        btnGuardarIzquierda.setBounds(1021, 583, 183, 33);
        btnGuardarIzquierda.setVisible(false);
        panelArriba.add(btnGuardarIzquierda);
        
        JLabel lblTablaComparativa = new JLabel("Tabla comparativa");
        lblTablaComparativa.setBounds(24, 164, 121, 13);
        panelArriba.add(lblTablaComparativa);
        
        JLabel lblTablaSeleccinPara = new JLabel("Tabla selección para mergear");
        lblTablaSeleccinPara.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTablaSeleccinPara.setBounds(996, 164, 194, 13);
        panelArriba.add(lblTablaSeleccinPara);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.7);
        splitPane.setBounds(24, 187, 1166, 371);
        splitPane.setLeftComponent(logScrollPane1);
        splitPane.setRightComponent(logScrollPane2);
        panelArriba.add(splitPane);
    }
    
    private JTable crearTabla(boolean esIzquierda) {
        JTable table;
        if(esIzquierda){
            table = new JTable(new DefaultTableModel(new Object[] { "DEPENDENCIA", "POM 1-CO", "POM 0-AT", "", "" }, 0){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            table.getColumnModel().getColumn(0).setPreferredWidth(210);
            table.getColumnModel().getColumn(0).setCellRenderer(new StatusColumnCellRenderer());
            table.getColumnModel().getColumn(1).setCellRenderer(new StatusColumnCellRenderer());
            table.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer());
            table.getColumnModel().getColumn(3).setPreferredWidth(0);
            table.getColumnModel().getColumn(3).setMaxWidth(0);
            table.getColumnModel().getColumn(3).setCellRenderer(new StatusColumnCellRenderer());
            table.getColumnModel().getColumn(4).setPreferredWidth(0);
            table.getColumnModel().getColumn(4).setMaxWidth(0);
            table.getColumnModel().getColumn(4).setCellRenderer(new StatusColumnCellRenderer());

            TableColumnModel tcm = table.getColumnModel();
            tcm.removeColumn( tcm.getColumn(4) );
            tcm.removeColumn( tcm.getColumn(3) );
        }else{
            table = new JTable(new DefaultTableModel(new Object[] { "DEPENDENCIA", "VERSIÓN NUEVA" }, 0){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            table.getColumnModel().getColumn(0).setPreferredWidth(220);
        }

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        sorter.setComparator(0, (Comparator<String>) String::compareTo);
        sorter.setSortsOnUpdates(true);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        table.setRowSorter(sorter);

        return table;
    }

    private void llevarDependenciaATabla2(String dependencia){
        PomBean pomBean = mapaDependencias.get(dependencia);
        DependencyBean dependenciaMerge;
        if(pomBean.getDependencia0AT() == null){
            JOptionPane.showMessageDialog(getParent(), "No se puede mergear una dependencia con versión null");
        }else{
            if(pomBean.getDependencia1CO() == null || pomBean.getDependencia1CO().getArtifactId().equals(pomBean.getDependencia0AT().getArtifactId())){
                Object[] fila = new Object[]{
                        pomBean.getDependencia0AT().getArtifactId(), pomBean.getDependencia0AT().getVersion()
                };
                ((DefaultTableModel) tabla2.getModel()).addRow(fila);
                ((DefaultTableModel) tabla1.getModel()).removeRow(tabla1.convertRowIndexToModel(tabla1.getSelectedRow()));

                dependenciaMerge =
                        new DependencyBean(pomBean.getDependencia0AT().getGroupId(), pomBean.getDependencia0AT().getArtifactId(), pomBean.getDependencia0AT().getVersion());
            }else{
                dependenciaMerge =
                        new DependencyBean(pomBean.getDependencia0AT().getGroupId(), pomBean.getDependencia0AT().getArtifactId(), pomBean.getDependencia0AT().getVersion());

                String mensaje = "Se ha detectado que la dependencia está como cliews en 1-CO. \n¿Desea sobreescribirla por cliers?" +
                        " Esto producirá errores de compilación.";

                if(pomBean.getDependencia1CO().getArtifactId().contains("cliers-")){
                    if(pomBean.getDependencia1CO().getVersion().equals(pomBean.getDependencia0AT().getVersion())){
                        JOptionPane.showMessageDialog(getParent(), "Se ha detectado que la dependencia está como cliers en 1-CO \ny tiene la misma versión que el cliews de 0-AT.");
                        return;
                    }else{
                        mensaje = "Se ha detectado que la dependencia está como cliers en 1-CO.\n" +
                                "¿Desea actualizar el número de versión por el del cliews de 0-AT?";
                        dependenciaMerge = new DependencyBean(pomBean.getDependencia1CO().getGroupId(), pomBean.getDependencia1CO().getArtifactId(), pomBean.getDependencia0AT().getVersion());
                    }
                }
                int opcion = mostrarMensaje(mensaje);
                if(opcion == 0){
                    Object[] fila = new Object[]{
                            dependenciaMerge.getArtifactId(), dependenciaMerge.getVersion()
                    };
                    ((DefaultTableModel) tabla2.getModel()).addRow(fila);

                    for(int i=0; i<tabla1.getRowCount(); i++){
                        String nombreDependencia = comparatorService.obtenerNombreDependenciaSinTipo(tabla1.getModel().getValueAt(i, 0).toString());
                        if(nombreDependencia.equals(pomBean.getNombreDependencia())){
                            ((DefaultTableModel) tabla1.getModel()).removeRow(i);
                            i--;
                        }
                    }
                }
            }
            pomBean.setDependenciaMerge(dependenciaMerge);
            mapaDependencias.put(dependencia, pomBean);
        }
    }
    
    private void mergearPom(String rutaPom) throws IOException {
        try (FileReader pom1Reader = new FileReader(rutaPom)) {
            BufferedReader br = new BufferedReader(pom1Reader);
            List<String> pomModificado = new ArrayList<>();
            try {
                String lineaAnterior = "";
                String line = br.readLine();
                while (line != null) {
                    if(line.contains("</dependencies>")){
                        // se añaden las nuevas dependencias al final
                        aniadirNuevasDependenciasAlPom(pomModificado);
                    }
                    if(line.contains("<artifactId>")){
                        String dependencia = line.replace("<artifactId>", "").replace("</artifactId>", "").trim();
                        dependencia = comparatorService.obtenerNombreDependenciaSinTipo(dependencia);
                        String finalDependencia = dependencia;
                        Entry<String, PomBean> entry = mapaDependencias.entrySet().stream().filter(e -> e.getValue().getDependenciaMerge() != null
                                && e.getValue().getDependencia1CO() != null
                                && comparatorService.obtenerNombreDependenciaSinTipo(e.getValue().getDependenciaMerge().getArtifactId()).equals(finalDependencia)
                                ).findFirst().orElse(null);
                        if(entry != null){
                            PomBean pomBean = entry.getValue();
                            lineaAnterior = lineaAnterior.replace(pomBean.getDependencia1CO().getGroupId(), pomBean.getDependenciaMerge().getGroupId());
                            pomModificado.remove(pomModificado.size()-1);
                            pomModificado.add(lineaAnterior);

                            line = line.replace(pomBean.getDependencia1CO().getArtifactId(), pomBean.getDependenciaMerge().getArtifactId());
                            pomModificado.add(line);

                            line = br.readLine();
                            line = line.replace(pomBean.getDependencia1CO().getVersion(), pomBean.getDependenciaMerge().getVersion());
                        }else {
                            if(dependencia.equals(comparatorService.obtenerNombreDependenciaSinTipo(comboBox.getSelectedItem().toString()))
                                    && mapaDependencias.get("version") != null
                                    && mapaDependencias.get("version").getDependenciaMerge() != null){
                                pomModificado.add(line);
                                System.out.println(line);
                                line = br.readLine();
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
        clearTable((DefaultTableModel) tabla2.getModel());
	}

    private void aniadirNuevasDependenciasAlPom(List<String> pomModificado) throws IOException {
        for(int i=0; i<tabla2.getRowCount(); i++){
            String dependencia = comparatorService.obtenerNombreDependenciaSinTipo(tabla2.getValueAt(i, 0).toString());
            Entry<String, PomBean> entry = mapaDependencias.entrySet().stream().filter(e -> e.getValue().getDependenciaMerge() != null
                    && e.getValue().getDependencia1CO() == null
                    && comparatorService.obtenerNombreDependenciaSinTipo(e.getValue().getDependenciaMerge().getArtifactId()).equals(dependencia)).findFirst().orElse(null);
            if(entry != null){
                PomBean pomBean = entry.getValue();
                String nuevaLinea = com.telefonica.modulos.comparador.poms.utils.Constants.PLANTILLA_DEPENDENCIA_POM.replace("${GROUP_ID}", pomBean.getDependenciaMerge().getGroupId())
                        .replace("${ARTIFACT_ID}", pomBean.getDependenciaMerge().getArtifactId())
                        .replace("${VERSION}", pomBean.getDependenciaMerge().getVersion());
                pomModificado.add(nuevaLinea);
                break;
            }
        }
    }

    private void mergearJson(String rutaJson) throws IOException {
		// valor=0 -> mergear izquierda
		// valor=1 -> mergear derecha
		// FileReader json1Reader = null;

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readTree(new FileReader(rutaJson));
		ObjectNode objectNodeDepen = (ObjectNode) node.get(Constants.NAME_JSON_DEPENDENCIES);


		// json1Reader = new FileReader(rutaJson);

			// manda 0-AT
			DefaultTableModel modelTabla = (DefaultTableModel) tabla1.getModel();

			for (int j = 0; j < modelTabla.getRowCount(); j++) {
				String dependencia = (String) modelTabla.getValueAt(j, 0);
				String version1CO = (String) modelTabla.getValueAt(j, 1);
				String version0AT = (String) modelTabla.getValueAt(j, 2);

				TextNode versionAtJson = new TextNode(version0AT);

				if (Constants.NAME_JSON_VERSION.equals(dependencia)) {
					((ObjectNode) node).replace(Constants.NAME_JSON_VERSION, versionAtJson);
				} else if (version0AT.equals("null")) {
					objectNodeDepen.remove((Constants.TELEFONICA_PREFIX + dependencia));
				} else if (!version1CO.equals(version0AT)) {
					objectNodeDepen.replace((Constants.TELEFONICA_PREFIX + dependencia), versionAtJson);
				}

			}
			((ObjectNode) node).replace(Constants.NAME_JSON_DEPENDENCIES, objectNodeDepen);
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objectMapper.writeValue(new File(rutaJson), node);
	}

    private void comparePoms(String ruta) throws IOException {
        try (FileReader pom1Reader = new FileReader(mapaPoms1CO.get(ruta)); FileReader pom2Reader = new FileReader(mapaPoms0AT.get(ruta))) {
            Map<String, String> dependencias1CO;
            Map<String, String> dependencias0AT;
            if (ruta.contains(".cnt-") || ruta.contains(".cgt-")) {
                comparatorService = jsonService;
            } else {
                comparatorService = pomService;
            }
            dependencias1CO = comparatorService.getSortedDependencies(pom1Reader);
            dependencias0AT = comparatorService.getSortedDependencies(pom2Reader);
            mapaDependencias = comparatorService.compare(dependencias1CO, dependencias0AT);
            List<Object[]> filas = comparatorService.crearListaFilas(mapaDependencias);
            aniadirFilasTabla(filas, null);
        } catch (Exception e1) {
            createErrorDialog(e1, "Error al comparar poms");
        }
	}

    private void dialogoEspera(boolean compararPoms, Boolean comparando1CO){
        final JDialog waitForTrans = new JDialog();
        JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 5);
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString(compararPoms ? "Comparando poms..." : "Comprobando dependencias...");
        final JOptionPane optionPane = new JOptionPane(progressBar, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        waitForTrans.setSize(200,200);
        waitForTrans.setLocationRelativeTo(null);
        waitForTrans.setTitle("Espere...");
        waitForTrans.setModal(true);
        waitForTrans.setContentPane(optionPane);
        waitForTrans.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        SwingWorker worker = new SwingWorker() {
            @Override
            public String doInBackground()  {
                if(compararPoms){
                    try {
                        if(txtRutaPom1CO.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(getParent(), "Por favor, seleccione la ruta de 1-CO");
                        }else if(txtRutaPom0AT.getText().isEmpty()) {
                            JOptionPane.showMessageDialog(getParent(), "Por favor, seleccione la ruta de 0-AT");
                        }else {
                            comboBox.removeAllItems();
                            File file1CO = new File(txtRutaPom1CO.getText());
                            File file0At = new File(txtRutaPom0AT.getText());
                            fetchFiles(file1CO, mapaPoms1CO, true);
                            fetchFiles(file0At, mapaPoms0AT, false);
                            comboBox.setSelectedIndex(-1);
                            actionListenerActive = true;
                        }
                        clearTable((DefaultTableModel) tabla1.getModel());
                        clearTable((DefaultTableModel) tabla2.getModel());
                    } catch (SAXException | IOException | ParserConfigurationException | XmlPullParserException e1) {
                        System.out.println(e1.getMessage());
                    }
                }else{
                    checkDependencies((String) comboBox.getSelectedItem(), comparando1CO ? mapaPoms1CO : mapaPoms0AT, comparando1CO);
                }
                return null;
            }
            @Override
            public void done() {
                waitForTrans.setVisible(false);
                waitForTrans.dispose();
            }
        };
        worker.execute();
        waitForTrans.pack();
        waitForTrans.setVisible(true);
    }

	private void checkDependencies(String selectedItem, Map<String, String> mapaPoms, boolean comparando1CO) {
        if (selectedItem == null || selectedItem.isEmpty()) {
            JOptionPane.showMessageDialog(getParent(), "Por favor, selecciona un servicio en el desplegable");
            return;
        }
        try {
            getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<String> noExistingDependencies = new ArrayList<>();

            noExistingDependencies = comparatorService.isDependencyInClarive(tabla1, comparando1CO);

            Map<String, String> sortedDependenciesPom1 = comparatorService.getSortedDependencies(new FileReader(mapaPoms.get(selectedItem)));
            /*for (Entry<String, String> entrySet : sortedDependenciesPom1.entrySet()) {
                if(!entrySet.getKey().contains("coco.")){
                    String[] split = entrySet.getKey().split("_");
                    String name = split[0];

                    String version;
                    if (!name.equals("tap-parent") && !name.equals("version")) {
                        version = entrySet.getValue();

                        if(!comparatorService.isDependencyInClarive(entrySet.getKey(), version)) {
                            noExistingDependencies.add(entrySet.getKey() + " - " + version);
                            if(comparando1CO){
                                boolean bajarDependencia = true;
                                int opcion = 1;
                            /*int opcion = JOptionPane.showConfirmDialog(this,
                                    "No existe dependencia en " + selectedItem + " -> " + name + "_" + version + "\n¿Desea bajar la dependencia a la última existente en clarive?",
                                    "", JOptionPane.YES_NO_OPTION);

                            while(bajarDependencia && opcion == 0) {
                                if(!comparatorService.isDependencyInClarive(entrySet.getKey(), version)) {
                                    noExistingDependencies.add("No existe dependencia en " + selectedItem + " -> " + name + "_" + version);
                                    System.out.println("No existe dependencia en " + selectedItem + " -> " + name + "_" + version);
                                    String major = (version.split("-")[0]).split("\\.")[0];
                                    String minor = (version.split("-")[0]).split("\\.")[1];
                                    int minorInt = Integer.parseInt(minor);
                                    String revision = (version.split("-")[0]).split("\\.")[2];
                                    version = major + "." +  (minorInt-1) + "." + revision + "-" + version.split("-")[1];
                                }else {
                                    bajarDependencia = false;
                                }
                            }
                            if(opcion == 0 && !entrySet.getValue().equals(version)) {
                                DefaultTableModel model = (DefaultTableModel) tabla1.getModel();
                                for (int i=0; i<model.getRowCount(); i++) {
                                    String dependencia = model.getValueAt(i, 0).toString();
                                    if(dependencia.equals(name)) {
                                        model.setValueAt(true, i, 4);
                                        model.setValueAt(version, i, 2);
                                        break;
                                    }
                                }

                                mergearPom(mapaPoms.get(selectedItem));

                                model = (DefaultTableModel) tabla1.getModel();
                                for (int i=0; i<model.getRowCount(); i++) {
                                    String dependencia = model.getValueAt(i, 0).toString();
                                    if(dependencia.equals(name)) {
                                        model.setValueAt(true, i, 4);
                                        model.setValueAt(entrySet.getValue(), i, 2);
                                        break;
                                    }
                                }
                            }else*/ /*if(opcion == 1){
                                    DefaultTableModel model = (DefaultTableModel) tabla1.getModel();
                                    for (int i=0; i<model.getRowCount(); i++) {
                                        String dependencia = model.getValueAt(i, 0).toString();
                                        if(dependencia.equals(name)) {
                                            model.setValueAt(false, i, 3);
                                            break;
                                        }
                                    }
                                }
                            }else{
                                DefaultTableModel model = (DefaultTableModel) tabla1.getModel();
                                for (int i=0; i<model.getRowCount(); i++) {
                                    String dependencia = model.getValueAt(i, 0).toString();
                                    if(dependencia.equals(name)) {
                                        model.setValueAt(false, i, 4);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }*/
            if(noExistingDependencies.isEmpty()){
                JOptionPane.showMessageDialog(getParent(), "Todas las dependencias están en clarive");
            }else{
                String message = String.join("\n", noExistingDependencies);
                JOptionPane.showMessageDialog(getParent(), "Las siguientes dependencias no están en clarive:\n" + message);
            }
            getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception e1) {
            createErrorDialog(e1, "Error al comprobar en clarive");
        }
    }

    private void createErrorDialog(Exception e1, String text) {
        String descErro = e1.getStackTrace()[0].getMethodName();
        JOptionPane.showMessageDialog(getParent(), text + "\n(" + descErro + ")", "Error", ERROR_MESSAGE);
    }

    private void aniadirFilasTabla(List<Object[]> filas, String dependencia) {
        DefaultTableModel model = (DefaultTableModel) tabla1.getModel();
        if(dependencia == null){
            clearTable(model);
        }
        for(Object[] fila : filas){
            if(dependencia != null){
                String dependencia1CO = comparatorService.obtenerNombreDependenciaSinTipo(fila[0].toString());
                if(dependencia1CO.equals(dependencia)){
                    model.addRow(fila);
                }
            }else{
                model.addRow(fila);
            }
        }
    }

    private void clearTable(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    private static ImageIcon createImageIcon(String path) {
		try {
			BufferedImage image = ImageIO.read(PanelComparadorPoms.class.getClassLoader().getResource(path));
			return new ImageIcon(image);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
    }
    
    private int mostrarMensaje(String mensaje) {
		return JOptionPane.showConfirmDialog(this, mensaje, "", JOptionPane.YES_NO_OPTION);
	}
    
    private void fetchFiles(File dir, Map<String, String> mapaPoms1CO2, boolean aniadirCombo) throws SAXException, IOException, ParserConfigurationException, XmlPullParserException {

		if (dir.isDirectory() && !dir.getAbsolutePath().contains("classes") && !dir.getAbsolutePath().contains("target")
				&& !dir.getAbsolutePath().contains("\\bin") && condicionesCNTs(dir.getAbsolutePath())) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1, mapaPoms1CO2, aniadirCombo);
			}
		} else if(dir.getName().endsWith("pom.xml")) {
			FileReader pom1Reader = new FileReader(dir.getAbsolutePath());
			Model model = new MavenXpp3Reader().read(pom1Reader);
			String nombreActivo = model.getArtifactId();
			//comboBox.addItem(dir.getAbsolutePath().replace("C:\\Users\\sherrerah\\Desktop\\PRUEBA_DEPENDENCIAS\\1-CO\\", ""));
			mapaPoms1CO2.put(nombreActivo, dir.getAbsolutePath());
			if(aniadirCombo) {
				comboBox.addItem(nombreActivo);	
			}
			pom1Reader.close();
		} else if (dir.getName().endsWith("package.json")) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode node = objectMapper.readTree(new FileReader(dir.getAbsolutePath()));
			String nombreActivo = node.get("name").asText().replace(Constants.TELEFONICA_PREFIX, "");
			mapaPoms1CO2.put(nombreActivo, dir.getAbsolutePath());// Cambiar nombre del
																								// mapa???
			if (aniadirCombo) {
				comboBox.addItem(nombreActivo);
			}
		}
	}

	private boolean condicionesCNTs(String pathDir) {
		boolean condiciones = true;
		if (pathDir.contains(".tmp") || pathDir.contains("node_modules")) {
			return false;
		}
		return condiciones;
	}
}
