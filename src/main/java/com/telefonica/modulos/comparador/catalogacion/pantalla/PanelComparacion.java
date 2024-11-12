package com.telefonica.modulos.comparador.catalogacion.pantalla;

import com.telefonica.modulos.comparador.catalogacion.bean.CambioBean;
import com.telefonica.modulos.comparador.catalogacion.procesador.ProcesadorTabla;
import com.telefonica.modulos.comparador.catalogacion.renderer.CellRendererTablaResumen;
import com.telefonica.modulos.comparador.catalogacion.utils.Constants;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

@org.springframework.stereotype.Component
public class PanelComparacion extends JPanel {
	
	private JPanel contentPane;
	public static JTable tablaResumen;
	public static JLabel lblNombreTabla;
	public static JLabel lblMensajeFilasNoExisten;
    public static JButton btnRehacer;
	public static JButton btnDeshacer;
	public static JPanel cardLayout;
	public static JCheckBox btnMostrarSoloDiferencias;
	public static JButton btnGenerarScript;
    public static JButton btnNewButton_1;
	public static JSplitPane splitPane;
	private static String excepcion = null;

	/**
	 * Create the panel.
     */
	
	public void setContentPane(JPanel contentPane) {
		this.contentPane = contentPane;
	}
	
	public PanelComparacion() throws IOException {
		setBounds(0, 0, 1152, 484);
		
		splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(0.39);

        JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		
		JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
        tablaResumen = new JTable() {
			//Implement table cell tool tips.           
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }	
		};
		
		tablaResumen.setDefaultRenderer(Object.class, new CellRendererTablaResumen());
		tablaResumen.setModel(new DefaultTableModel(
	        	new Object[][] {
	        	},
	        	new String[] {
	        		"", "TABLA/COLUMNA", "DISCREPANCIAS"
	        	}
	        ) {
				@Override
			    public boolean isCellEditable(int row, int column) {
			       //all cells false
			       return false;
			    }
			});
			
        tablaResumen.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaResumen.getColumnModel().getColumn(0).setMinWidth(0);
        tablaResumen.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaResumen.getColumnModel().getColumn(1).setPreferredWidth(290);
        tablaResumen.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaResumen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResumen.setBounds(0, 0, 300, 1);
        JTableHeader cabecera = tablaResumen.getTableHeader();
        cabecera.setReorderingAllowed(false);
        scrollPane.setViewportView(tablaResumen);
        
        tablaResumen.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(java.awt.event.MouseEvent e) {
    			if(tablaResumen.getValueAt(tablaResumen.getSelectedRow(), 0).equals("1")) {
    				
    				//int option = mostrarMensajeSiNo("Se perder�n los cambios realizados.\n�Est� seguro de que quiere continuar?");
    				//if(option == 0) {
    					String nombreTabla = tablaResumen.getValueAt(tablaResumen.getSelectedRow(), 1).toString();
    					btnMostrarSoloDiferencias.setSelected(ProcesadorTabla.fotoActualTabla.get(nombreTabla) != null && ProcesadorTabla.fotoActualTabla.get(nombreTabla).isSoloDiferencias());
                    	btnDeshacer.setEnabled(ProcesadorTabla.mapaTablaListaDeshacer.get(nombreTabla) != null
                            && !ProcesadorTabla.mapaTablaListaDeshacer.get(nombreTabla).isEmpty());
                    	btnRehacer.setEnabled(ProcesadorTabla.mapaTablaListaRehacer.get(nombreTabla) != null
                            && !ProcesadorTabla.mapaTablaListaRehacer.get(nombreTabla).isEmpty());
        				lblNombreTabla.setText("Tabla: " + nombreTabla);
        				CardLayout c = (CardLayout)(cardLayout.getLayout());
        				c.show(cardLayout, nombreTabla);
    				/*}else{
    					String nombreTablaLabel = lblNombreTabla.getText();
    					nombreTablaLabel = nombreTablaLabel.substring(nombreTablaLabel.indexOf(" ") + 1);
    					for(int i=0; i<tablaResumen.getRowCount(); i++){
    						String nombreTablaFila = tablaResumen.getValueAt(i, 1).toString();
    						if(nombreTablaFila.equals(nombreTablaLabel)){
    							tablaResumen.setRowSelectionInterval(i, i);
    							break;
    						}
    					}
    				}*/
    			}
        	}
        });
        
        tablaResumen.addMouseMotionListener(new MouseInputAdapter() {
        	public void mouseMoved(MouseEvent e)
            {
               int row = tablaResumen.rowAtPoint(e.getPoint());
               if(tablaResumen.getValueAt(row, 0).equals("1")) {
            	   tablaResumen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
               }else {
            	   tablaResumen.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
               }
            }
		});
        panel.setLayout(new CardLayout(0, 0));
        panel.add(scrollPane, "name_949161250139900");
		
		cardLayout = new JPanel();
		cardLayout.setLayout(new CardLayout(0, 0));
		splitPane.setRightComponent(cardLayout);

        JButton btnNivelarEntornos = new JButton("NIVELAR ENTORNOS");
		btnNivelarEntornos.addActionListener(e -> {

            int option2 = mostrarMensajeSiNo("Se va a nivelar la tabla, �est� seguro de que desea continuar?");
            if(option2 == 0) {
                int option = mostrarMensajeSiNo("�Desea nivelar tambi�n el resto de tablas?");
                boolean nivelarTodas = option == 0;
                nivelarEntornos(nivelarTodas);
                //llamarDialogoEspera(nivelarTodas);

                if(excepcion != null) {
                    mostrarMensajeInformativo(excepcion);
                    excepcion = null;
                }
            }
        });
		
		lblNombreTabla = new JLabel("");
		lblNombreTabla.setHorizontalAlignment(SwingConstants.CENTER);
		lblNombreTabla.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		btnNewButton_1 = new JButton("VOLVER");
		btnNewButton_1.addActionListener(e -> {
            int opcion = mostrarMensajeSiNo("¿Está seguro de que desea volver?");
            if(opcion == 0) {
                CardLayout c = (CardLayout)(contentPane.getLayout());
                c.show(contentPane, "panelTablas");
            }
        });
		
		lblMensajeFilasNoExisten = new JLabel("");
		
		BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("images/Undo-icon.png"));
		ImageIcon imagenDeshacer = new ImageIcon(image);
		imagenDeshacer.setImage(imagenDeshacer.getImage().getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ));
		
		btnDeshacer = new JButton("");
		btnDeshacer.addActionListener(e -> {
            String nombreTabla = lblNombreTabla.getText();
            if(!nombreTabla.isEmpty()) {
                nombreTabla = nombreTabla.substring(nombreTabla.indexOf(" ") + 1);
                JTable tablaActivos = null;
                for(Component component: cardLayout.getComponents()) {
                    if(nombreTabla.equals(component.getName())) {
                        JScrollPane scrollPane1 = (JScrollPane) component;
                        JViewport viewport = scrollPane1.getViewport();
                        tablaActivos = (JTable)viewport.getView();
                        break;
                    }
                }

                List<Object[]> listaFilasRehacer = new ArrayList<>();
                int numeroFilas = tablaActivos.getModel().getRowCount();
                for(int i=0; i<numeroFilas; i++) {
                    Object[] fila = new Object[tablaActivos.getModel().getColumnCount()];
                    for(int j=0; j<tablaActivos.getModel().getColumnCount(); j++) {
                        fila[j] = tablaActivos.getModel().getValueAt(0, j);
                    }
                    ((DefaultTableModel) tablaActivos.getModel()).removeRow(0);
                    listaFilasRehacer.add(fila);
                }

                List<CambioBean> listaRehacer = ProcesadorTabla.mapaTablaListaRehacer.get(nombreTabla)!=null?ProcesadorTabla.mapaTablaListaRehacer.get(nombreTabla): new LinkedList<>();
                CambioBean cambioBean = ProcesadorTabla.fotoActualTabla.get(nombreTabla)!=null?ProcesadorTabla.fotoActualTabla.get(nombreTabla):new CambioBean();
                cambioBean.setListaFilas(listaFilasRehacer);
                listaRehacer.add(cambioBean);
                ProcesadorTabla.mapaTablaListaRehacer.put(nombreTabla, listaRehacer);

                CambioBean cambio = ProcesadorTabla.mapaTablaListaDeshacer.get(nombreTabla).get(ProcesadorTabla.mapaTablaListaDeshacer.get(nombreTabla).size()-1);
                btnMostrarSoloDiferencias.setSelected(cambio.isSoloDiferencias());
                ProcesadorTabla.fotoActualTabla.put(nombreTabla, cambio);

                List<Object[]> listaFilas = cambio.getListaFilas();
                for (Object[] listaFila : listaFilas) {
                    ((DefaultTableModel) tablaActivos.getModel()).addRow(listaFila);
                }

                ProcesadorTabla.mapaTablaListaDeshacer.get(nombreTabla).remove(ProcesadorTabla.mapaTablaListaDeshacer.get(nombreTabla).size()-1);
                if(ProcesadorTabla.mapaTablaListaDeshacer.get(nombreTabla).isEmpty()) {
                    ProcesadorTabla.mapaTablaListaDeshacer.remove(nombreTabla);
                    btnDeshacer.setEnabled(false);
                    btnGenerarScript.setEnabled(false);
                }
                btnRehacer.setEnabled(true);
                ProcesadorTabla.actualizarListaaFilasModificadas(tablaActivos, nombreTabla);
                tablaActivos.repaint();
                tablaActivos.getTableHeader().repaint();
            }
        });
		btnDeshacer.setEnabled(false);
		btnDeshacer.setSize(30, 30);
		// Set image to size of JButton...
		int offset = btnDeshacer.getInsets().left;
		btnDeshacer.setIcon(resizeIcon(imagenDeshacer, btnDeshacer.getWidth() - offset, btnDeshacer.getHeight() - offset));
		
		BufferedImage image2 = ImageIO.read(getClass().getClassLoader().getResource("images/Redo-icon.png"));
		ImageIcon imagenRehacer = new ImageIcon(image2);
		imagenRehacer.setImage(imagenRehacer.getImage().getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ));
		
		btnRehacer = new JButton("");
		btnRehacer.addActionListener(e -> {
            String nombreTabla = lblNombreTabla.getText();
            if(!nombreTabla.isEmpty()) {
                nombreTabla = nombreTabla.substring(nombreTabla.indexOf(" ") + 1);
                JTable tablaActivos = null;
                for(Component component: cardLayout.getComponents()) {
                    if(nombreTabla.equals(component.getName())) {
                        JScrollPane scrollPane12 = (JScrollPane) component;
                        JViewport viewport = scrollPane12.getViewport();
                        tablaActivos = (JTable)viewport.getView();
                        break;
                    }
                }

                List<Object[]> listaFilasDeshacer = new ArrayList<>();
                int numeroFilas = tablaActivos.getModel().getRowCount();
                for(int i=0; i<numeroFilas; i++) {
                    Object[] fila = new Object[tablaActivos.getModel().getColumnCount()];
                    for(int j=0; j<tablaActivos.getModel().getColumnCount(); j++) {
                        fila[j] = tablaActivos.getModel().getValueAt(0, j);
                    }
                    ((DefaultTableModel) tablaActivos.getModel()).removeRow(0);
                    listaFilasDeshacer.add(fila);
                }

                List<CambioBean> listaDeshacer = ProcesadorTabla.mapaTablaListaDeshacer.get(nombreTabla)!=null?ProcesadorTabla.mapaTablaListaDeshacer.get(nombreTabla): new LinkedList<>();
                CambioBean cambioBean = ProcesadorTabla.fotoActualTabla.get(nombreTabla)!=null?ProcesadorTabla.fotoActualTabla.get(nombreTabla):new CambioBean();
                cambioBean.setListaFilas(listaFilasDeshacer);
                listaDeshacer.add(cambioBean);
                ProcesadorTabla.mapaTablaListaDeshacer.put(nombreTabla, listaDeshacer);

                CambioBean cambio = ProcesadorTabla.mapaTablaListaRehacer.get(nombreTabla).get(ProcesadorTabla.mapaTablaListaRehacer.get(nombreTabla).size()-1);
                btnMostrarSoloDiferencias.setSelected(cambio.isSoloDiferencias());
                ProcesadorTabla.fotoActualTabla.put(nombreTabla, cambio);

                List<Object[]> listaFilas = cambio.getListaFilas();
                for (Object[] listaFila : listaFilas) {
                    ((DefaultTableModel) tablaActivos.getModel()).addRow(listaFila);
                }

                ProcesadorTabla.mapaTablaListaRehacer.get(nombreTabla).remove(ProcesadorTabla.mapaTablaListaRehacer.get(nombreTabla).size()-1);
                if(ProcesadorTabla.mapaTablaListaRehacer.get(nombreTabla).isEmpty()) {
                    ProcesadorTabla.mapaTablaListaRehacer.remove(nombreTabla);
                    btnRehacer.setEnabled(false);
                }

                btnDeshacer.setEnabled(true);
                btnGenerarScript.setEnabled(true);
                ProcesadorTabla.actualizarListaaFilasModificadas(tablaActivos, nombreTabla);
                tablaActivos.repaint();
                tablaActivos.getTableHeader().repaint();
            }
        });
		btnRehacer.setEnabled(false);
		btnRehacer.setSize(30, 30);
		// Set image to size of JButton...
		int offset2 = btnRehacer.getInsets().left;
		btnRehacer.setIcon(resizeIcon(imagenRehacer, btnRehacer.getWidth() - offset2, btnRehacer.getHeight() - offset2));
		
		
		btnMostrarSoloDiferencias = new JCheckBox("Diferencias");
		btnMostrarSoloDiferencias.addActionListener(e -> {

            String nombreTabla = lblNombreTabla.getText();
            if(!nombreTabla.isEmpty()) {
                nombreTabla = nombreTabla.substring(nombreTabla.indexOf(" ") + 1);
                JTable tablaActivos = null;
                for(Component component: cardLayout.getComponents()) {
                    if(nombreTabla.equals(component.getName())) {
                        JScrollPane scrollPane13 = (JScrollPane) component;
                        JViewport viewport = scrollPane13.getViewport();
                        tablaActivos = (JTable)viewport.getView();
                        break;
                    }
                }

                CambioBean cambioBeanActual = ProcesadorTabla.fotoActualTabla.get(nombreTabla)!=null?ProcesadorTabla.fotoActualTabla.get(nombreTabla):new CambioBean();

                CambioBean cambioBeanModificado = new CambioBean();
                cambioBeanModificado.setSoloDiferencias(btnMostrarSoloDiferencias.isSelected());
                cambioBeanModificado.getMapaColumnaFiltro().putAll(cambioBeanActual.getMapaColumnaFiltro());
                ProcesadorTabla.fotoActualTabla.put(nombreTabla, cambioBeanModificado);

                try {
                    ProcesadorTabla.filtrarTabla(tablaActivos, nombreTabla, !btnMostrarSoloDiferencias.isSelected(), cambioBeanActual, cambioBeanModificado);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, e1.getMessage());
                }
            }
        });
		
		btnGenerarScript = new JButton("GENERAR SCRIPT");
		btnGenerarScript.setEnabled(false);
		btnGenerarScript.addActionListener(e -> {

            int opcion = mostrarMensajeSiNo("Se va a generar el script con las modificaciones realizadas.\n�Est� seguro de que desea continuar?");
            if(opcion == 0) {
                llamarDialogoEspera();
            }
            if(excepcion != null) {
                mostrarMensajeInformativo(excepcion);
                excepcion = null;
            }
        });
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(22)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 1060, Short.MAX_VALUE)
							.addGap(41))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
							.addGap(57)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblMensajeFilasNoExisten, GroupLayout.PREFERRED_SIZE, 733, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 218, Short.MAX_VALUE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblNombreTabla, GroupLayout.PREFERRED_SIZE, 424, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnGenerarScript, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnNivelarEntornos, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
									.addGap(6)
									.addComponent(btnMostrarSoloDiferencias, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnDeshacer, GroupLayout.PREFERRED_SIZE, 39, Short.MAX_VALUE)
									.addGap(6)
									.addComponent(btnRehacer, GroupLayout.PREFERRED_SIZE, 39, Short.MAX_VALUE)
									.addGap(12)))
							.addGap(21))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(11)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnNewButton_1)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblMensajeFilasNoExisten, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
							.addGap(9)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblNombreTabla, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
									.addComponent(btnGenerarScript)
									.addComponent(btnNivelarEntornos))))
						.addComponent(btnMostrarSoloDiferencias, 30, 30, 30)
						.addComponent(btnDeshacer, 30, 30, 30)
						.addComponent(btnRehacer, 30, 30, 30))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
					.addGap(28))
		);
		setLayout(groupLayout);
		
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				if(PanelTablasFK.nombreTablaOrigen != null) {
					CardLayout cc = (CardLayout)(cardLayout.getLayout());
    				cc.show(cardLayout, PanelTablasFK.nombreTablaOrigen);
				}else {
					cardLayout.getComponent(0).setVisible(false);
				}
			}
		});
	}
	
	
	@SuppressWarnings("rawtypes")
	private static void llamarDialogoEspera() {
		final JDialog waitForTrans = new JDialog();
		JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 5);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("Generando scripts...");
		final JOptionPane optionPane = new JOptionPane(progressBar, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        waitForTrans.setSize(200,200);
    	waitForTrans.setLocationRelativeTo(null);
    	waitForTrans.setTitle("Espere...");
    	waitForTrans.setModal(true);
    	waitForTrans.setContentPane(optionPane);
    	waitForTrans.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		SwingWorker worker = new SwingWorker() {
			public String doInBackground()  {
					excepcion = null;
					try {
						generarScripts();
					} catch (Exception e) {
						excepcion = e.getMessage();
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
	
	
	private static void generarScripts() {
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setAcceptAllFileFilterUsed(false);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Documento de texto (*.sql)", "sql");
	    chooser.setFileFilter(filter);
	    chooser.setSelectedFile(new File("scriptBBDD"));
	    
	    if (chooser.showSaveDialog(btnGenerarScript) == JFileChooser.APPROVE_OPTION) {
	    	Map<String, BufferedWriter> mapaWriter = new HashMap<>();
	    	Map<String, BufferedWriter> mapaWriterRollback = new HashMap<>();
	    	Map<String, File> mapaFichero = new HashMap<>();
	    	Map<String, List<String>> mapaConsultasrollback = new HashMap<>();
	    	try {
	    		String userEPR = null;
	    		String passEPR = null;
	    		if(PanelTablas.entornoReferencia.equals("EPR")) {
	    			userEPR = ProcesadorTabla.usuarioEPR;
	    			passEPR = ProcesadorTabla.passwordEPR;
	    		}

                List<String> listaTablasAProcesar = new ArrayList<>(PanelTablas.listaTablasSeleccionadas);
				
				Map<String, List<String>> mapaTablaIds = new LinkedHashMap<>();
				while(!listaTablasAProcesar.isEmpty()){
					for(int iii=0; iii<listaTablasAProcesar.size(); iii++){
						
						boolean continuar = true;
		    			String nombreTabla = listaTablasAProcesar.get(iii);
		    			for(Pair<String, String> par: ProcesadorTabla.mapaTablaFKs.get(nombreTabla)){
		    				String nombreTablaFK = par.getKey();
		    				if(!nombreTabla.equals(nombreTablaFK) && listaTablasAProcesar.contains(nombreTablaFK) && !nombreTablaFK.equals("SPORR_BI_ITEM_SPEC_REL")){
		    					continuar = false;
		    				}
		    			}
		    			
		    			if(listaTablasAProcesar.contains(nombreTabla) && continuar){
		    				JTable tablaActivos = null;
			    			for(Component component: cardLayout.getComponents()) {
			    				if(nombreTabla.equals(component.getName())) {
			    					JScrollPane scrollPane = (JScrollPane) component;
			    					JViewport viewport = scrollPane.getViewport();
			    					tablaActivos = (JTable)viewport.getView();
			    					break;
			    				}
			    			}
			    			
			    			List<Object[]> listaFilas = ProcesadorTabla.mapaTablaFilasModificado.get(nombreTabla);
			    			if(listaFilas != null && !listaFilas.isEmpty()){
			    				String usuarioBBDD = null;
								for(Entry<String, List<String>> mapaUsuarioTablas: ProcesadorTabla.mapaAplicacionTablas.entrySet()) {
									if(mapaUsuarioTablas.getValue().contains(nombreTabla)) {
										usuarioBBDD = mapaUsuarioTablas.getKey();
										break;
									}
								}
								
						    	String nombreColumnaId = tablaActivos.getColumnModel().getColumn(1).getHeaderValue().toString();
						    	String idTabla = null;
						    	String entornoFilaModificar;
                                for (Object[] listaFila : listaFilas) {
                                    if (!listaFila[ProcesadorTabla.numeroColumnasExtra].toString().equals("------") && !listaFila[ProcesadorTabla.numeroColumnasExtra].toString().equals("   ")
                                            && listaFila[Constants.POSICION_ENTORNO].toString().equals(PanelTablas.entornoReferencia)) {
                                        idTabla = listaFila[ProcesadorTabla.numeroColumnasExtra].toString();
                                        break;
                                    }
                                }
						    	
						    	StringBuilder consultaFila = new StringBuilder("SELECT * FROM " + nombreTabla + " WHERE " + nombreColumnaId + " = " + idTabla);
						    	Connection con = com.telefonica.modulos.comparador.catalogacion.utils.Connection.getConnection(PanelTablas.entornoReferencia, usuarioBBDD, userEPR, passEPR);
						    	
					    		ResultSet resultSetUpdate = con.createStatement().executeQuery(consultaFila.toString());
								ResultSetMetaData rsmdUpdate = resultSetUpdate.getMetaData();
						    	
								ResultSet resultSetInsert = null;
								ResultSetMetaData rsmdInsert;
								
						    	for(int i=0; i<listaFilas.size(); i++) {
						    		boolean aniadir = false;
						    		entornoFilaModificar = listaFilas.get(i)[Constants.POSICION_ENTORNO].toString();
						    		String keyWriter = usuarioBBDD + Constants.SEPARADOR + entornoFilaModificar;
						    		String keyWriterRollback = usuarioBBDD + Constants.SEPARADOR + entornoFilaModificar + "_rollback";
						    		idTabla = listaFilas.get(i)[ProcesadorTabla.numeroColumnasExtra].toString();
						    		if(!idTabla.equals("   ")) {
						    			String idTablaOriginal = (String) ProcesadorTabla.mapaTablaFilasOriginal.get(nombreTabla).get(i)[ProcesadorTabla.numeroColumnasExtra];
					    				
					    				if(!idTablaOriginal.equals("------")) {
							    			StringBuilder statementConValores = new StringBuilder("UPDATE " + nombreTabla + " SET ");
							    			StringBuilder statementConValoresRollback = new StringBuilder("UPDATE " + nombreTabla + " SET ");
							    			StringBuilder statementWhere = new StringBuilder(" WHERE ");
				    						for(int j=0; j<tablaActivos.getModel().getColumnCount(); j++){
				    							String nombreColumna = tablaActivos.getModel().getColumnName(j);
				    							if(ProcesadorTabla.mapaTablaPKs.get(nombreTabla).contains(nombreColumna)){
				    								statementWhere.append(nombreColumna).append(" = ").append(ProcesadorTabla.mapaTablaFilasModificado.get(nombreTabla).get(i)[j]).append(" AND ");
				    							}
				    						}
				    						statementWhere = new StringBuilder(statementWhere.substring(0, statementWhere.length() - 5));
											try {
									    		for(int j=ProcesadorTabla.numeroColumnasExtra; j<tablaActivos.getModel().getColumnCount(); j++) {
									    			String valorColumna = (String) listaFilas.get(i)[j];
									    			String valorOriginal = (String) ProcesadorTabla.mapaTablaFilasOriginal.get(nombreTabla).get(i)[j];
									    			if(!valorColumna.equals(valorOriginal)) {
									    				aniadir = true;
									    				String nombreColumna = tablaActivos.getModel().getColumnName(j);
										    			if(valorColumna == null || valorColumna.equals("------")) {
									    					statementConValores.append(nombreColumna).append(" = null,");
									    				}else {
									    					for(int k=1; k<=rsmdUpdate.getColumnCount(); k++) {
									    						String nombreColumnaBBDD = rsmdUpdate.getColumnName(k);
									    						if(nombreColumna.equals(nombreColumnaBBDD)) {
									    							if(rsmdUpdate.getColumnType(k) == Types.TIMESTAMP) {
												    					//statementConValores += nombreColumna + " = TO_TIMESTAMP('" + valorColumna + "', 'YYYY-MM-DD HH24:MI:SS.FF'),";
									    								statementConValores.append(nombreColumna).append(" = TO_TIMESTAMP('").append(valorColumna).append("', 'DD/MM/YYYY HH24:MI:SS'),");
												    				}else if(rsmdUpdate.getColumnType(k) == Types.DATE) {
												    					//statementConValores += nombreColumna + " = TO_DATE('" + valorColumna + "', 'YYYY-MM-DD'),";
												    					statementConValores.append(nombreColumna).append(" = TO_DATE('").append(valorColumna).append("', 'DD/MM/YYYY'),");
												    				}else if(rsmdUpdate.getColumnType(k) != Types.BIGINT && rsmdUpdate.getColumnType(k) != Types.DECIMAL && rsmdUpdate.getColumnType(k) != Types.DOUBLE
												    						 && rsmdUpdate.getColumnType(k) != Types.FLOAT && rsmdUpdate.getColumnType(k) != Types.INTEGER
												    						 && rsmdUpdate.getColumnType(k) != Types.NUMERIC) {
												    					
												    					valorColumna = valorColumna.replace("'", "''");
												    					statementConValores.append(nombreColumna).append(" = '").append(valorColumna).append("',");
												    				}
												    				else {
												    					statementConValores.append(nombreColumna).append(" = ").append(valorColumna).append(",");
												    				}
									    							break;
									    						}
									    					}
									    				}
										    			
										    			if(valorOriginal == null || valorOriginal.equals("------")) {
									    					statementConValoresRollback.append(nombreColumna).append(" = null,");
									    				}else {
									    					for(int k=1; k<=rsmdUpdate.getColumnCount(); k++) {
									    						String nombreColumnaBBDD = rsmdUpdate.getColumnName(k);
									    						if(nombreColumna.equals(nombreColumnaBBDD)) {
									    							if(rsmdUpdate.getColumnType(k) == Types.TIMESTAMP) {
									    								//statementConValoresRollback += nombreColumna + " = TO_TIMESTAMP('" + valorOriginal + "', 'YYYY-MM-DD HH24:MI:SS.FF'),";
									    								statementConValoresRollback.append(nombreColumna).append(" = TO_TIMESTAMP('").append(valorOriginal).append("', 'DD/MM/YYYY HH24:MI:SS'),");
												    				}else if(rsmdUpdate.getColumnType(k) == Types.DATE) {
												    					//statementConValoresRollback += nombreColumna + " = TO_DATE('" + valorOriginal + "', 'YYYY-MM-DD'),";
												    					statementConValoresRollback.append(nombreColumna).append(" = TO_DATE('").append(valorOriginal).append("', 'DD/MM/YYYY'),");
												    				}else if(rsmdUpdate.getColumnType(k) != Types.BIGINT && rsmdUpdate.getColumnType(k) != Types.DECIMAL && rsmdUpdate.getColumnType(k) != Types.DOUBLE
												    						 && rsmdUpdate.getColumnType(k) != Types.FLOAT && rsmdUpdate.getColumnType(k) != Types.INTEGER
												    						 && rsmdUpdate.getColumnType(k) != Types.NUMERIC) {
												    					
												    					valorOriginal = valorOriginal.replace("'", "''");
												    					statementConValoresRollback.append(nombreColumna).append(" = '").append(valorOriginal).append("',");
												    				}
												    				else {
												    					statementConValoresRollback.append(nombreColumna).append(" = ").append(valorOriginal).append(",");
												    				}
									    							break;
									    						}
									    					}
									    				}
									    			}
									    		}
											}catch(Exception ex){
												if(resultSetUpdate != null){
													resultSetUpdate.close();
												}
												mostrarMensajeInformativo(ex.getMessage());
											}
											
											if(aniadir) {
									    		if(mapaFichero.get(keyWriter) == null){
									    			File dir1 = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + usuarioBBDD);
									    			if(!dir1.exists()){
									    				dir1.mkdir();
									    			}
									    			File dir2 = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + usuarioBBDD + "\\" + entornoFilaModificar);
									    			if(!dir2.exists()) {
									    				dir2.mkdir();
										    		}
									    			File dir3 = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + usuarioBBDD + "\\" + entornoFilaModificar + "\\" + chooser.getSelectedFile().getName() + "_" + keyWriter + ".sql");
									    			File dir4 = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + usuarioBBDD + "\\" + entornoFilaModificar + "\\" + chooser.getSelectedFile().getName() + "_" + keyWriter + "_rollback.sql");
									    			mapaFichero.put(keyWriter, dir3);
									    			mapaFichero.put(keyWriterRollback, dir4);
									    		}
												
												statementConValores = new StringBuilder(statementConValores.substring(0, statementConValores.length() - 1));
							    				String consulta = statementConValores.toString() + statementWhere + ";";
							    				
							    				statementConValoresRollback = new StringBuilder(statementConValoresRollback.substring(0, statementConValoresRollback.length() - 1));
							    				String consultaRollback = statementConValoresRollback.toString() + statementWhere + ";";
							    				
							    				
							    				
							    				BufferedWriter writer;
							    				if(mapaWriter.get(keyWriter) != null){
							    					writer = mapaWriter.get(keyWriter);
							    				}else{
							    					writer = new BufferedWriter(new FileWriter(mapaFichero.get(keyWriter).getAbsolutePath()));
							    					mapaWriter.put(keyWriter, writer);
							    				}
							    				writer.write("\n" + consulta);
							    				
							    				BufferedWriter writerRollback;
							    				if(mapaWriterRollback.get(keyWriterRollback) != null){
							    					writerRollback = mapaWriterRollback.get(keyWriterRollback);
							    				}else{
							    					writerRollback = new BufferedWriter(new FileWriter(mapaFichero.get(keyWriterRollback).getAbsolutePath()));
							    				}
												mapaWriterRollback.put(keyWriterRollback, writerRollback);
							    				
							    				List<String> listaConsultasRollback;
							    				if(mapaConsultasrollback.get(keyWriterRollback) != null){
							    					listaConsultasRollback = mapaConsultasrollback.get(keyWriterRollback);
							    				}else {
							    					listaConsultasRollback = new ArrayList<>();
							    				}
							    				listaConsultasRollback.add(0, "\n" + consultaRollback);
							    				mapaConsultasrollback.put(keyWriterRollback, listaConsultasRollback);
											}
					    				}else {
					    					consultaFila = new StringBuilder("SELECT * FROM " + nombreTabla + " WHERE ");
					    					StringBuilder statementDelete = new StringBuilder("DELETE " + nombreTabla + " WHERE ");
				    						for(int j=0; j<tablaActivos.getModel().getColumnCount(); j++){
				    							String nombreColumna = tablaActivos.getModel().getColumnName(j);
				    							if(ProcesadorTabla.mapaTablaPKs.get(nombreTabla).contains(nombreColumna)){
				    								consultaFila.append(nombreColumna).append(" = ").append(ProcesadorTabla.mapaTablaFilasModificado.get(nombreTabla).get(i)[j]).append(" AND ");
				    								statementDelete.append(nombreColumna).append(" = ").append(ProcesadorTabla.mapaTablaFilasModificado.get(nombreTabla).get(i)[j]).append(" AND ");
				    							}
				    						}
				    						consultaFila = new StringBuilder(consultaFila.substring(0, consultaFila.length() - 5));
				    						statementDelete = new StringBuilder(statementDelete.substring(0, statementDelete.length() - 5) + ";");
					    					
					    					idTabla = (String) ProcesadorTabla.mapaTablaFilasModificado.get(nombreTabla).get(i)[ProcesadorTabla.numeroColumnasExtra];
					    					if(!idTabla.equals("------")){
												try {
													if(mapaTablaIds.get(nombreTabla) == null || !mapaTablaIds.get(nombreTabla).contains(consultaFila.toString())) {
														if(resultSetInsert != null){
															resultSetInsert.close();	
														}
														
														resultSetInsert = com.telefonica.modulos.comparador.catalogacion.utils.Connection.getConnection(PanelTablas.entornoReferencia, usuarioBBDD, userEPR, passEPR).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(consultaFila.toString());
														rsmdInsert = resultSetInsert.getMetaData();
														
														List<String> listaConsultas = mapaTablaIds.get(nombreTabla)!=null?mapaTablaIds.get(nombreTabla): new ArrayList<>();
														listaConsultas.add(consultaFila.toString());
														mapaTablaIds.put(nombreTabla, listaConsultas);
													}else {
														resultSetInsert.beforeFirst();
														rsmdInsert = resultSetInsert.getMetaData();
													}
										    		int numeroColumnas = rsmdInsert.getColumnCount();
										    		StringBuilder statementInsert = new StringBuilder("INSERT INTO " + nombreTabla + " (");
										    		String statementInsertValues = ") VALUES (";
										    		StringBuilder statementConValores = new StringBuilder(") VALUES (");
										    		
										    		while(resultSetInsert.next()){
											    		statementInsertValues = statementInsertValues.substring(0, statementInsertValues.length()-1) + ")";
											    		for(int j=1; j<=numeroColumnas; j++){
											    			statementInsert.append(rsmdInsert.getColumnName(j)).append(",");
										    				Object valor = resultSetInsert.getObject(j);
										    				boolean encontrado = false;
										    				for(int k=0; k<tablaActivos.getModel().getColumnCount(); k++) {
										    					String nombreColumna = tablaActivos.getModel().getColumnName(k);
										    					if(nombreColumna.equals(rsmdInsert.getColumnName(j))) {
										    						if(!listaFilas.get(i)[k].equals("------")) {
										    							valor = listaFilas.get(i)[k];
										    							encontrado = true;
										    						}
										    						break;
										    					}
										    				}
										    				
										    				// para sacar por consola la consulta
										    				if(valor != null) {
										    					if(rsmdInsert.getColumnType(j) == Types.TIMESTAMP) {
										    						if(!encontrado){
										    							// el formateo es el que viene de BBDD
										    							Timestamp valorFecha = resultSetInsert.getTimestamp(j);
                                                                        valor = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorFecha);
										    						}
										    						statementConValores.append("TO_TIMESTAMP('").append(valor).append("', 'DD/MM/YYYY HH24:MI:SS'),");
											    				}else if(rsmdInsert.getColumnType(j) == Types.DATE) {
											    					if(!encontrado){
										    							// el formateo es el que viene de BBDD
										    							Date valorFecha = resultSetInsert.getDate(j);
										    							String pattern = "dd/MM/yyyy";
										    	    					DateFormat df = new SimpleDateFormat(pattern);
                                                                        valor = df.format(valorFecha);
										    						}
											    					statementConValores.append("TO_DATE('").append(valor).append("', 'DD/MM/YYYY'),");
											    				}else if(rsmdInsert.getColumnType(j) != Types.BIGINT && rsmdInsert.getColumnType(j) != Types.DECIMAL && rsmdInsert.getColumnType(j) != Types.DOUBLE
											    						 && rsmdInsert.getColumnType(j) != Types.FLOAT && rsmdInsert.getColumnType(j) != Types.INTEGER
											    						 && rsmdInsert.getColumnType(j) != Types.NUMERIC) {
											    					
											    					valor = valor.toString().replace("'", "''");
											    					statementConValores.append("'").append(valor).append("',");
											    				}
											    				else {
											    					statementConValores.append(valor).append(",");
											    				}
										    				}else {
										    					statementConValores.append(valor).append(",");
										    				}
										    			}
										    			statementInsert = new StringBuilder(statementInsert.substring(0, statementInsert.length() - 1));
											    		statementConValores.insert(0, statementInsert);
											    		statementConValores = new StringBuilder(statementConValores.substring(0, statementConValores.length() - 1) + ");");
								    					
											    		if(mapaFichero.get(keyWriter) == null){
											    			File dir1 = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + usuarioBBDD);
											    			if(!dir1.exists()){
											    				dir1.mkdir();
											    			}
											    			File dir2 = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + usuarioBBDD + "\\" + entornoFilaModificar);
											    			if(!dir2.exists()) {
											    				dir2.mkdir();
												    		}
											    			File dir3 = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + usuarioBBDD + "\\" + entornoFilaModificar + "\\" + chooser.getSelectedFile().getName() + "_" + keyWriter + ".sql");
											    			File dir4 = new File(chooser.getCurrentDirectory().getAbsolutePath() + "\\" + usuarioBBDD + "\\" + entornoFilaModificar + "\\" + chooser.getSelectedFile().getName() + "_" + keyWriter + "_rollback.sql");
											    			mapaFichero.put(keyWriter, dir3);
											    			mapaFichero.put(keyWriterRollback, dir4);
											    		}
											    		
											    		BufferedWriter writer;
									    				if(mapaWriter.get(keyWriter) != null){
									    					writer = mapaWriter.get(keyWriter);
									    				}else{
									    					writer = new BufferedWriter(new FileWriter(mapaFichero.get(keyWriter).getAbsolutePath()));
									    					mapaWriter.put(keyWriter, writer);
									    				}
									    				writer.write("\n" + statementConValores);
									    				
									    				BufferedWriter writerRollback;
									    				if(mapaWriterRollback.get(keyWriterRollback) != null){
									    					writerRollback = mapaWriterRollback.get(keyWriterRollback);
									    				}else{
									    					writerRollback = new BufferedWriter(new FileWriter(mapaFichero.get(keyWriterRollback).getAbsolutePath()));
									    				}
														mapaWriterRollback.put(keyWriterRollback, writerRollback);
									    				
									    				List<String> listaConsultasRollback;
									    				if(mapaConsultasrollback.get(keyWriterRollback) != null){
									    					listaConsultasRollback = mapaConsultasrollback.get(keyWriterRollback);
									    				}else {
									    					listaConsultasRollback = new ArrayList<>();
									    				}
									    				listaConsultasRollback.add(0, "\n" + statementDelete);
									    				mapaConsultasrollback.put(keyWriterRollback, listaConsultasRollback);
										    		}
												} catch (Exception e1) {
													mostrarMensajeInformativo(e1.getMessage());
												}
					    					}
					    				}
						    		}
						    	}
						    	if(resultSetUpdate != null){
						    		resultSetUpdate.close();	
								}
						    	tablaActivos.repaint();
			    			}
			    			listaTablasAProcesar.remove(nombreTabla);
			    			iii--;
		    			}
					}	
				}
		    	com.telefonica.modulos.comparador.catalogacion.utils.Connection.closeConnection();
		    	for(Entry<String, BufferedWriter> mapa: mapaWriter.entrySet()){
		    		mapa.getValue().close();
		    	}
		    	
		    	for(Entry<String, BufferedWriter> mapa: mapaWriterRollback.entrySet()){
		    		BufferedWriter writer = mapa.getValue();
		    		List<String> listaConsultas = mapaConsultasrollback.get(mapa.getKey());
		    		for(String consulta: listaConsultas) {
		    			writer.write(consulta);
		    		}
		    		writer.close();
		    	}
	    		mostrarMensajeInformativo("Se han generado los scripts correctamente");
	    		resetearListas();
	    		btnGenerarScript.setEnabled(false);
				
				
	    	} catch (Exception e2) {
	    		mostrarMensajeInformativo(e2.getMessage());
			}
	    }
	}
	
	
	private static void nivelarEntornos(boolean nivelarTodas){String nombreTablaSeleccionada = null;
		if(!nivelarTodas) {
			nombreTablaSeleccionada = lblNombreTabla.getText();
			if(!nombreTablaSeleccionada.isEmpty()) {
				nombreTablaSeleccionada = nombreTablaSeleccionada.substring(nombreTablaSeleccionada.indexOf(" ") + 1);
			}
		}
		
		for(Entry<String, List<Object[]>> mapa: ProcesadorTabla.mapaTablaFilasOriginal.entrySet()){
			String nombreTabla = mapa.getKey();
			if(nivelarTodas || (nombreTabla.equals(nombreTablaSeleccionada))) {
				JTable tablaActivos = null;
				for(Component component: cardLayout.getComponents()) {
					if(nombreTabla.equals(component.getName())) {
						JScrollPane scrollPane = (JScrollPane) component;
						JViewport viewport = scrollPane.getViewport();
						tablaActivos = (JTable)viewport.getView();
						break;
					}
				}
				
				if(tablaActivos != null) {
					
					ProcesadorTabla.llevarFotoActualADeshacer(tablaActivos);
					
					for(int ii=0; ii<tablaActivos.getRowCount(); ii++){
						String idTabla = tablaActivos.getValueAt(ii, 1).toString();
			    		if(!idTabla.equals("   ")) {
			    			String entornoSeleccionado = tablaActivos.getValueAt(ii, 0).toString();
			    			if(entornoSeleccionado.equals(PanelTablas.entornoReferencia)){
			    				int numeroRecorrer = PanelTablas.listaEntornosSeleccionados.size()-1;
			    				int numeroQuedaPendienteRecorrer = numeroRecorrer;
			    				boolean esLimiteRecorrerArriba = tablaActivos.getModel().getValueAt(ii, Constants.POSICION_ES_LIMITE).equals("1");
			    				
			    				for(int i=1; i<=numeroRecorrer&&!esLimiteRecorrerArriba;i++) {
			    					int fila = ii-i;
		    						// coincide el entorno, hay que copiar la fila
		    						int numeroColumnasBBDD = tablaActivos.getColumnCount();
		    						for(int j=1; j<numeroColumnasBBDD; j++) {
		    							String value = tablaActivos.getValueAt(ii, j).toString();
		    							tablaActivos.setValueAt(value, fila, j);
		    						}
		    						numeroQuedaPendienteRecorrer--;
									esLimiteRecorrerArriba = tablaActivos.getModel().getValueAt(fila, Constants.POSICION_ES_LIMITE).equals("1");
			    				}
			    				for(int i=1; i<=numeroQuedaPendienteRecorrer; i++) {
			    					int fila = ii+i;
		    						int numeroColumnasBBDD = tablaActivos.getColumnCount();
		    						for(int j=1; j<numeroColumnasBBDD; j++) {
		    							String value = tablaActivos.getValueAt(ii, j).toString();
		    							tablaActivos.setValueAt(value, fila, j);
		    						}
			    				}
			    				ii+=numeroQuedaPendienteRecorrer;
			    			}
			    		}else{
			    			break;
			    		}
					}
					ProcesadorTabla.actualizarListaaFilasModificadas(tablaActivos, nombreTabla);
				}
			}
		}
		
		btnGenerarScript.setEnabled(true);
	}
	
	
	public static void resetearListas() {
		ProcesadorTabla.mapaTablaListaDeshacer = new LinkedHashMap<>();
		ProcesadorTabla.mapaTablaListaRehacer = new LinkedHashMap<>();
		
		btnDeshacer.setEnabled(false);
		btnRehacer.setEnabled(false);
		btnGenerarScript.setEnabled(false);
	}
	
	
	private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
	    Image img = icon.getImage();  
	    Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);  
	    return new ImageIcon(resizedImage);
	}
	
	
	public int mostrarMensajeSiNo(String mensaje) {
		return JOptionPane.showConfirmDialog(this, mensaje, "", JOptionPane.YES_NO_OPTION);
	}
	
	
	public static void mostrarMensajeInformativo(String mensaje) {
		JOptionPane.showMessageDialog(null, mensaje);
	}
}