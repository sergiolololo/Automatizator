package com.telefonica.modulos.comparador.catalogacion.pantalla;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.telefonica.modulos.comparador.catalogacion.bean.CambioBean;
import com.telefonica.modulos.comparador.catalogacion.procesador.ProcesadorTabla;
import com.telefonica.modulos.comparador.catalogacion.renderer.CellRendererTablaSeleccion;
import com.telefonica.modulos.comparador.catalogacion.utils.Constants;

@SuppressWarnings("serial")
@Component
public class PanelTablas extends JPanel {
	private JPanel contentPane;
	
	private JTable tablaActivos;
	private JTable tablaRutas;
	private JButton btnCargarTablas;
	private JButton btnLlevarTodas;
	private JCheckBox chkINFA;
	private JCheckBox chkPRTE;
	private JCheckBox chkTERC;
	private JCheckBox chkEDC;
	private JCheckBox chkEIN;
	private JCheckBox chkECE;
	private JCheckBox chkECO;
	private JCheckBox chkEPR;
	private List<JCheckBox> listaChkEntornos = new ArrayList<JCheckBox>();
	private JTextField txtFiltro;
	private JLabel lblNewLabel;
	private JButton btnComparar;
	private JComboBox<String> comboPESP;
	private TableRowSorter<TableModel> rowSorter;
	
	public static List<String> listaTablasSeleccionadas = new ArrayList<String>();
	public static Set<String> listaEntornosSeleccionados = new HashSet<String>();
	public static String entornoReferencia;
	
	/**
	 * Create the panel.
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	
	public void setContentPane(JPanel contentPane) {
		this.contentPane = contentPane;
	}
	
	public PanelTablas() throws SAXException, IOException, ParserConfigurationException {
		setBounds(0, 0, 1144, 484);
		tablaActivos = new JTable() {
			//Implement table cell tool tips.           
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	JOptionPane.showMessageDialog(null, e1.getMessage());
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }	
		};
		
		tablaRutas = new JTable() {
			//Implement table cell tool tips.           
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	JOptionPane.showMessageDialog(null, e1.getMessage());
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }	
		};
		
		setBorder(new TitledBorder(new LineBorder(new Color(192, 192, 192), 2, true), "Pantalla de elecci�n de tablas", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
		btnCargarTablas = new JButton("CARGAR TABLAS");
		btnCargarTablas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer opcion = mostrarMensaje("Se resetear�n ambas tablas.\n�Est� seguro de que quiere continuar?");
		    	if(opcion == 0) {
					txtFiltro.setText("");
					btnComparar.setEnabled(false);
					resetearTablas();
					rowSorter = new TableRowSorter<>(tablaActivos.getModel());
					try {
						fetchFiles();
					}catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage());
						e1.printStackTrace();
					}
					if(tablaActivos.getRowCount() > 0) {
						txtFiltro.setEnabled(true);
					}else {
						txtFiltro.setEnabled(false);
					}
					btnLlevarTodas.setEnabled(true);
		    	}
			}
		});
		
		btnLlevarTodas = new JButton("CARGAR TODAS");
		btnLlevarTodas.setEnabled(false);
		btnLlevarTodas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer opcion = mostrarMensaje("Se llevar�n todas las tablas. �Desea continuar?");
		    	if(opcion == 0) {
        			for(int i=0; i<tablaActivos.getRowCount(); i++){
        				String idFila = tablaActivos.getValueAt(i, 0).toString();
            			String nombreTabla = tablaActivos.getValueAt(i, 1).toString();
            			String usuarioBBDD = tablaActivos.getValueAt(i, 2).toString();
	        			Object[] fila = new Object[3];
	        			fila[0] = idFila;
	        			fila[1] = nombreTabla;
	        			fila[2] = usuarioBBDD;
	        			((DefaultTableModel) tablaRutas.getModel()).addRow(fila);
		        		// despu�s de quitar el filtro, buscamos la tabla para borrarla
		        		listaTablasSeleccionadas.add(nombreTabla);
        			}
        			
        			int numeroFilas = ((DefaultTableModel) tablaActivos.getModel()).getRowCount();
	        		for(int i=numeroFilas-1; i>=0; i--) {
		    			((DefaultTableModel) tablaActivos.getModel()).removeRow(i);
		    		}
        			
        			btnComparar.setEnabled(true);
        			txtFiltro.setText("");
	        		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(((DefaultTableModel) tablaActivos.getModel()));
	        		tablaActivos.setRowSorter(sorter);
		    	}
			}
		});
		setLayout(null);
		JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		tablaActivos.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"", "TABLA", "PLANO"
        	}
        ) {
			@Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		});
		
        tablaActivos.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaActivos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaActivos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaActivos.getColumnModel().getColumn(1).setPreferredWidth(300);
        tablaActivos.getColumnModel().getColumn(2).setPreferredWidth(60);
        tablaActivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaActivos.setBounds(0, 0, 300, 1);
        JTableHeader cabecera = tablaActivos.getTableHeader();
        cabecera.setReorderingAllowed(false);
        scrollPane.setViewportView(tablaActivos);
        
        tablaActivos.addMouseListener(new java.awt.event.MouseAdapter() {
        	public void mouseClicked(java.awt.event.MouseEvent e) {
        		if(e.getClickCount()==2){
        			
        			String idFila = tablaActivos.getValueAt(tablaActivos.getSelectedRow(), 0).toString();
        			String nombreTabla = tablaActivos.getValueAt(tablaActivos.getSelectedRow(), 1).toString();
        			String usuarioBBDD = tablaActivos.getValueAt(tablaActivos.getSelectedRow(), 2).toString();
        			
        			int numeroFilas = ((DefaultTableModel) tablaRutas.getModel()).getRowCount();
        			boolean encontrado = false;
	        		for(int i=0; i<numeroFilas;i++) {
	        			String idFilaTabla = tablaRutas.getValueAt(i, 0).toString();
	        			if(idFilaTabla.equals(idFila)) {
	        				encontrado = true;
	        			}
	        		}
	        		if(!encontrado) {
	        			Object[] fila = new Object[3];
	        			fila[0] = idFila;
	        			fila[1] = nombreTabla;
	        			fila[2] = usuarioBBDD;
	        			((DefaultTableModel) tablaRutas.getModel()).addRow(fila);
	        			
	        			txtFiltro.setText("");
		        		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(((DefaultTableModel) tablaActivos.getModel()));
		        		tablaActivos.setRowSorter(sorter);
		        		
		        		// despu�s de quitar el filtro, buscamos la tabla para borrarla
		        		numeroFilas = ((DefaultTableModel) tablaActivos.getModel()).getRowCount();
	        			encontrado = false;
		        		for(int i=0; i<numeroFilas;i++) {
		        			String idFilaTabla = tablaActivos.getValueAt(i, 0).toString();
		        			if(idFilaTabla.equals(idFila)) {
		        				((DefaultTableModel) tablaActivos.getModel()).removeRow(i);
		        				break;
		        			}
		        		}
	        			
	        			listaTablasSeleccionadas.add(nombreTabla);
	        			btnComparar.setEnabled(true);
	        		}
        		}
        	}
        });
        
        
        tablaActivos.setDefaultRenderer(Object.class, new CellRendererTablaSeleccion());
        tablaRutas.setDefaultRenderer(Object.class, new CellRendererTablaSeleccion());
        
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        tablaRutas.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"", "TABLA", "PLANO"
        	}
        ) {
			@Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		});
        tablaRutas.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaRutas.getColumnModel().getColumn(0).setMinWidth(0);
        tablaRutas.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaRutas.getColumnModel().getColumn(1).setPreferredWidth(306);
        tablaRutas.getColumnModel().getColumn(2).setPreferredWidth(85);
        tablaRutas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRutas.setBounds(0, 0, 300, 1);
        JTableHeader cabecera2 = tablaRutas.getTableHeader();
        cabecera2.setReorderingAllowed(false);
        scrollPane_1.setViewportView(tablaRutas);
        
        tablaRutas.addMouseListener(new java.awt.event.MouseAdapter() {
        	public void mouseClicked(java.awt.event.MouseEvent e) {
        		if(e.getClickCount()==2){
        			String idFila = tablaRutas.getValueAt(tablaRutas.getSelectedRow(), 0).toString();
        			String nombreTabla = tablaRutas.getValueAt(tablaRutas.getSelectedRow(), 1).toString();
        			String usuarioBBDD = tablaRutas.getValueAt(tablaRutas.getSelectedRow(), 2).toString();
        			
        			int numeroFilas = ((DefaultTableModel) tablaActivos.getModel()).getRowCount();
        			boolean encontrado = false;
	        		for(int i=0; i<numeroFilas;i++) {
	        			String idFilaTabla2 = tablaActivos.getValueAt(i, 0).toString();
	        			if(idFilaTabla2.equals(idFila)) {
	        				encontrado = true;
	        			}
	        		}
	        		if(!encontrado) {
	        			Object[] fila = new Object[3];
	        			fila[0] = idFila;
	        			fila[1] = nombreTabla;
	        			fila[2] = usuarioBBDD;
	        			((DefaultTableModel) tablaActivos.getModel()).addRow(fila);
	        			((DefaultTableModel) tablaRutas.getModel()).removeRow(tablaRutas.getSelectedRow());
	        			listaTablasSeleccionadas.remove(nombreTabla);
	        			
	        			if(tablaRutas.getModel().getRowCount() == 0) {
	        				btnComparar.setEnabled(false);
	        			}
	        		}
        		}
        	}
        });
        
        chkINFA = new JCheckBox("INFTER1");
        chkPRTE = new JCheckBox("PRVTER1");
        chkTERC = new JCheckBox("TERCBS1");
        
        txtFiltro = new JTextField();
        txtFiltro.setEnabled(false);
        txtFiltro.getDocument().addDocumentListener(new DocumentListener()
		{
	        @Override
	        public void insertUpdate(DocumentEvent e) {
	            String str = txtFiltro.getText();
	            if (str.trim().length() == 0) {
	            	rowSorter.setRowFilter(null);
	            } else {
	            	rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
	            }
	            tablaActivos.setRowSorter(rowSorter);
	        }
	        @Override
	        public void removeUpdate(DocumentEvent e) {
	            String str = txtFiltro.getText();
	            if (str.trim().length() == 0) {
	            	rowSorter.setRowFilter(null);
	            } else {
	            	rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
	            }
	            tablaActivos.setRowSorter(rowSorter);
	        }
	        @Override
	        public void changedUpdate(DocumentEvent e) {}
	    });
        txtFiltro.setColumns(10);
        
        lblNewLabel = new JLabel("Buscar");
        
        btnComparar = new JButton("COMPARAR");
        btnComparar.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		int seleccionados = listaEntornosSeleccionados.size();
        		if(seleccionados < 2) {
        			JOptionPane.showMessageDialog(null, "Debe seleccionar al menos dos entornos para poder comparar");
        		}else {
        			Integer opcion = mostrarMensaje("Se va a realizar la comparación.\n¿Está seguro de que quiere continuar?");
    		    	if(opcion == 0) {
    		    		resetearPanelComparacion();
    		    		entornoReferencia = (String) comboPESP.getSelectedItem();
    		    		switch(entornoReferencia) {
	    		    		case "EDC":
	    		    			PanelComparacion.lblMensajeFilasNoExisten.setText(Constants.MENSAJE_FILAS_NO_EXISTEN_EDC);
	    		    			break;
	    		    		case "EIN":
	    		    			PanelComparacion.lblMensajeFilasNoExisten.setText(Constants.MENSAJE_FILAS_NO_EXISTEN_EIN);
	    		    			break;
	    		    		case "ECE":
	    		    			PanelComparacion.lblMensajeFilasNoExisten.setText(Constants.MENSAJE_FILAS_NO_EXISTEN_ECE);
	    		    			break;
	    		    		case "ECO":
	    		    			PanelComparacion.lblMensajeFilasNoExisten.setText(Constants.MENSAJE_FILAS_NO_EXISTEN_ECO);
	    		    			break;
	    		    		case "EPR":
	    		    			PanelComparacion.lblMensajeFilasNoExisten.setText(Constants.MENSAJE_FILAS_NO_EXISTEN_EPR);
	    		    			break;
    		    		}
    		    		
    		    		try {
							ProcesadorTabla.main();
							CardLayout c = (CardLayout)(contentPane.getLayout());
	    					c.show(contentPane, "panelComparacion");
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage());
						}
    		    	}	
        		}
        	}

			private void resetearPanelComparacion() {
				PanelComparacion.resetearListas();
				PanelComparacion.btnMostrarSoloDiferencias.setEnabled(true);
				PanelComparacion.btnMostrarSoloDiferencias.setSelected(false);
	    		ProcesadorTabla.mapaTablaFilasModificado = new LinkedHashMap<String, List<Object[]>>();
	    		PanelComparacion.lblNombreTabla.setText("");
	    		ProcesadorTabla.fotoActualTabla = new HashMap<String, CambioBean>();
	    		PanelTablasFK.nombreTablaOrigen = null;
	    		
				for(java.awt.Component component: PanelComparacion.cardLayout.getComponents()) {
					if(component instanceof JScrollPane) {
						JScrollPane scrollPane = (JScrollPane) component;
						JViewport viewport = scrollPane.getViewport();
						JTable tablaActivos = (JTable)viewport.getView();
						int numeroFilas = ((DefaultTableModel) tablaActivos.getModel()).getRowCount();
			    		for(int i=numeroFilas-1; i>=0; i--) {
			    			((DefaultTableModel) tablaActivos.getModel()).removeRow(i);
			    		}
					}
				}
				PanelComparacion.cardLayout.removeAll();
	    		
	    		int numeroFilas = ((DefaultTableModel) PanelComparacion.tablaResumen.getModel()).getRowCount();
	    		for(int i=numeroFilas-1; i>=0; i--) {
	    			((DefaultTableModel) PanelComparacion.tablaResumen.getModel()).removeRow(i);
	    		}
			}
        });
        btnComparar.setEnabled(false);
        
        comboPESP = new JComboBox<String>();
        
        chkEDC = new JCheckBox("EDC");
        listaChkEntornos.add(chkEDC);
        
        chkEIN = new JCheckBox("EIN");
        listaChkEntornos.add(chkEIN);
        
        chkECE = new JCheckBox("ECE");
        listaChkEntornos.add(chkECE);
        
        chkECO = new JCheckBox("ECO");
        listaChkEntornos.add(chkECO);
        
        chkEPR = new JCheckBox("EPR");
        listaChkEntornos.add(chkEPR);
        
        JLabel lblNewLabel_1 = new JLabel("Comparar:");
        
        JLabel lblNewLabel_2 = new JLabel("Entorno de referencia:");
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
        			.addGap(39)
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addGroup(groupLayout.createSequentialGroup()
        					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
        					.addContainerGap())
        				.addGroup(groupLayout.createSequentialGroup()
        					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        						.addComponent(txtFiltro, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
        						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE))
        					.addPreferredGap(ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
        					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        						.addComponent(chkINFA, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
        						.addComponent(chkPRTE, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
        						.addComponent(chkTERC, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btnCargarTablas, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btnLlevarTodas, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
        						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
        						.addGroup(groupLayout.createSequentialGroup()
        							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        								.addComponent(chkEDC, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
        								.addComponent(chkEIN, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
        								)
        							.addGap(6)
        							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        								.addComponent(chkECO, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
        								.addComponent(chkECE, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
        								)
        							.addGap(6)
        							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        								.addComponent(chkEPR, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
        								)
        							)
        						.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
        						.addComponent(comboPESP, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btnComparar, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE))
        					.addGap(40)
        					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
        					.addGap(67))))
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addGap(29)
        			.addComponent(lblNewLabel)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addGroup(groupLayout.createSequentialGroup()
        					.addComponent(txtFiltro, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addGap(11)
        					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
        				.addGroup(groupLayout.createSequentialGroup()
        					.addGap(31)
        					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        						.addComponent(scrollPane_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
        						.addGroup(groupLayout.createSequentialGroup()
        							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        								.addComponent(chkINFA)
        								.addGroup(groupLayout.createSequentialGroup()
        									.addGap(20)
        									.addComponent(chkPRTE))
        								.addGroup(groupLayout.createSequentialGroup()
        									.addGap(40)
        									.addComponent(chkTERC)))
        							.addGap(7)
        							.addComponent(btnCargarTablas)
        							.addComponent(btnLlevarTodas)
        							.addGap(45)
        							.addComponent(lblNewLabel_1)
        							.addGap(1)
        							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        								.addComponent(chkEDC)
        								.addGroup(groupLayout.createSequentialGroup()
        									.addGap(20)
        									.addComponent(chkEIN))
        								.addGroup(groupLayout.createSequentialGroup()
        									.addGap(20)
        									.addComponent(chkECO))
        								.addComponent(chkECE)
        								.addComponent(chkEPR)
        									)
        							.addGap(11)
        							.addComponent(lblNewLabel_2)
        							.addGap(5)
        							.addComponent(comboPESP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
        							.addComponent(btnComparar)))))
        			.addGap(57))
        );
        setLayout(groupLayout);
        
        for(JCheckBox chkEntorno: listaChkEntornos) {
        	chkEntorno.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				comboPESP.removeAllItems();
    				for(JCheckBox chkEntorno: listaChkEntornos) {
    					if(chkEntorno.isSelected()) {
    						comboPESP.addItem(chkEntorno.getText());
    						listaEntornosSeleccionados.add(chkEntorno.getText());
    					}else {
    						listaEntornosSeleccionados.remove(chkEntorno.getText());
    					}
    				}
    			}
    		});
		}
	}
	
	private int mostrarMensaje(String mensaje) {
		return JOptionPane.showConfirmDialog(this, mensaje, "", JOptionPane.YES_NO_OPTION);
	}
	
	private void fetchFiles() throws SAXException, IOException, ParserConfigurationException {

		List<String> listaFicherosTablas = new ArrayList<String>();
		listaFicherosTablas.add("Tablas_completas/tablas_INFTER1");
		listaFicherosTablas.add("Tablas_completas/tablas_PRVTER1");
		listaFicherosTablas.add("Tablas_completas/tablas_TERCBS1");
		
		for(String fichero: listaFicherosTablas) {
			if((chkINFA.isSelected() && fichero.contains(chkINFA.getText())) ||
					(chkPRTE.isSelected() && fichero.contains(chkPRTE.getText())) || 
					(chkTERC.isSelected() && fichero.contains(chkTERC.getText()))) {
				
				String usuarioBBDD = fichero.substring(fichero.lastIndexOf("_") + 1);
				InputStream hola = getClass().getClassLoader().getResourceAsStream(fichero);
				
				if(hola != null) {
					try (InputStreamReader streamReader =
		                    new InputStreamReader(hola);
		             BufferedReader reader = new BufferedReader(streamReader)) {

			            String line = reader.readLine();;
			            while (line != null) {
			            	if(!line.startsWith("--")) {
			            		String lineaPartida = line.substring(line.indexOf("FROM ")).substring(line.substring(line.indexOf("FROM ")).indexOf(" ") + 1);
				            	String nombreTabla = lineaPartida.substring(0, lineaPartida.indexOf(" ORDER"));
				            	
				            	Object[] fila = new Object[3];
				    			fila[0] = nombreTabla;
				    			fila[1] = nombreTabla;
				    			fila[2] = usuarioBBDD;
				    			((DefaultTableModel) tablaActivos.getModel()).addRow(fila);
			            	}
			    			line = reader.readLine();
			            }
			            reader.close();
			            streamReader.close();
			        } catch (IOException a) {
			        	JOptionPane.showMessageDialog(null, a.getMessage());
			            a.printStackTrace();
			        }
					hola.close();
				}
			}
		}
	}
	
	private void resetearTablas() {
		
		listaTablasSeleccionadas = new ArrayList<String>();
		
		int numeroFilas = ((DefaultTableModel) tablaActivos.getModel()).getRowCount();
		tablaActivos.setRowSorter(null);
		for(int i=numeroFilas-1; i>=0; i--) {
			((DefaultTableModel) tablaActivos.getModel()).removeRow(i);
		}
		
		int numeroFilas2 = ((DefaultTableModel) tablaRutas.getModel()).getRowCount();
		for(int i=numeroFilas2-1; i>=0; i--) {
			((DefaultTableModel) tablaRutas.getModel()).removeRow(i);
		}
	}
}