package com.telefonica.modulos.busqueda.pantalla;

import com.telefonica.modulos.busqueda.service.BusquedaActivos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"rawtypes" })
@Component
public class PanelBusqueda extends JPanel {
	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private BusquedaActivos fileUtil;
	@Value("${ruta.1CO}")
	private String ruta1CO;
    public static JTextField txtPesp;
	public static JTable analisisCODI;
	public static JTable impactosManual;
	public static JTextField txtRuta1COInfa;
	public static JTextField txtRuta1COPrte;
	public static JTextField txtRuta1COTerc;
	public static JLabel lblAnalisisCodi;
	private final JButton btnEliminar;
	private JTextField txtBuscar;
	private final JCheckBox chkBuscarJavas, chkBuscarSoa, chkBuscarPantalla, chkBuscarOsb;
	
	public PanelBusqueda() {

        JButton btnEmpezar = new JButton("EMPEZAR");
		btnEmpezar.setBounds(15, 170, 125, 33);
        btnEmpezar.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				if(comprobarSiProcesar()) {
					llamarDialogoEspera();
				}
        	}
        });

        JLabel lblVersionComercial = new JLabel("* Versión comercial");
        lblVersionComercial.setBounds(288, 16, 137, 13);
        
        txtPesp = new JTextField();
        txtPesp.setName("txtPesp");
        txtPesp.setBounds(288, 36, 147, 20);
        txtPesp.setColumns(10);
        lblAnalisisCodi = new JLabel("Búsqueda CODI");
        lblAnalisisCodi.setBounds(15, 257, 133, 13);
        JLabel lblRutaDirectorio = new JLabel("* Ruta directorio 1-CO local INFA");
        lblRutaDirectorio.setBounds(15, 16, 235, 14);
        
        txtRuta1COInfa = new JTextField();
        txtRuta1COInfa.setName("txtRuta1COInfa");
        txtRuta1COInfa.setBounds(15, 36, 235, 20);
        txtRuta1COInfa.setEnabled(false);
        txtRuta1COInfa.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		String directorio = fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, ruta1CO.replace("?", "INFA"));
        		txtRuta1COInfa.setText(directorio);
        		if(directorio.contains("INFA")) {
        			directorio = directorio.replace("INFA", "?");
        			txtRuta1COPrte.setText(directorio.replace("?", "PRTE"));
        			txtRuta1COTerc.setText(directorio.replace("?", "TERC"));
        		}
        	}
        });
        
        txtRuta1COInfa.setColumns(10);
        
        JLabel lblRutaDirectorio_1 = new JLabel("* Ruta directorio 1-CO local PRTE");
        lblRutaDirectorio_1.setBounds(15, 60, 235, 14);
        
        txtRuta1COPrte = new JTextField();
        txtRuta1COPrte.setName("txtRuta1COPrte");
        txtRuta1COPrte.setBounds(15, 80, 235, 20);
        txtRuta1COPrte.setEnabled(false);
        txtRuta1COPrte.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		txtRuta1COPrte.setText(fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, ruta1CO.replace("?", "PRTE")));
        	}
        });
        
        txtRuta1COPrte.setColumns(10);
        
        JLabel lblRutaDirectorio_2 = new JLabel("* Ruta directorio 1-CO local TERC");
        lblRutaDirectorio_2.setBounds(15, 104, 235, 14);
        
        txtRuta1COTerc = new JTextField();
        txtRuta1COTerc.setName("txtRuta1COTerc");
        txtRuta1COTerc.setBounds(15, 124, 235, 20);
        txtRuta1COTerc.setEnabled(false);
        txtRuta1COTerc.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		txtRuta1COTerc.setText(fileUtil.seleccionarDirectorio(JFileChooser.DIRECTORIES_ONLY, ruta1CO.replace("?", "TERC")));
        	}
        });
        
        txtRuta1COTerc.setColumns(10);
        
        JCheckBox chkCompararAnalisis = new JCheckBox("Comparar excel y codi");
        chkCompararAnalisis.setEnabled(false);
        
        JPanel panel_1 = new JPanel();
        panel_1.setBounds(15, 280, 848, 310);
        setLayout(null);
        add(lblRutaDirectorio);
        add(lblVersionComercial);
        add(btnEmpezar);
        add(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));
        
        analisisCODI = crearTablas(true);
		impactosManual = crearTablas(false);
        JScrollPane scrollPane_1 = new JScrollPane(analisisCODI);
        panel_1.add(scrollPane_1);
        add(txtRuta1COInfa);
        add(txtRuta1COPrte);
        add(txtRuta1COTerc);
        add(lblRutaDirectorio_1);
        add(lblRutaDirectorio_2);
        add(txtPesp);
        add(lblAnalisisCodi);
        
        JScrollPane jscroll = new JScrollPane(impactosManual);
        jscroll.setBounds(20, 31, 334, 113);
        
        JLabel lblImpactosManual = new JLabel("Línea buscar");
        lblImpactosManual.setBounds(20, 10, 90, 13);
        
        JButton btnAniadir = new JButton("AÑADIR");
        btnAniadir.addActionListener(e -> {
			if(!txtBuscar.getText().trim().isEmpty()){
				boolean encontrado = false;
				for(int i=0; i<impactosManual.getModel().getRowCount() && !encontrado; i++) {
					if(txtBuscar.getText().equals(impactosManual.getModel().getValueAt(i, 0).toString())){
						encontrado = true;
					}
				}
				if(encontrado) {
					fileUtil.mostrarMensajeInformativo("El registro ya existe");
					return;
				}else{
					((DefaultTableModel)impactosManual.getModel()).addRow(new Object[] { txtBuscar.getText() });
					txtBuscar.setText("");
				}
			}
        });
        btnAniadir.setBounds(74, 186, 107, 33);
        
        btnEliminar = new JButton("ELIMINAR");
        btnEliminar.addActionListener(e -> {
            int opcion = mostrarMensaje("¿Desea eliminar el registro?");
            if(opcion == 0) {
                ((DefaultTableModel)impactosManual.getModel()).removeRow(impactosManual.getSelectedRow());
            }
        });
        btnEliminar.setEnabled(false);
        btnEliminar.setBounds(191, 186, 107, 33);
        
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, Color.LIGHT_GRAY));
        panel.setBounds(487, 16, 376, 244);
        panel.add(btnAniadir);
        panel.add(btnEliminar);
        panel.add(jscroll);
        panel.add(lblImpactosManual);
        add(panel);
        panel.setLayout(null);
        
        txtBuscar = new JTextField();
        txtBuscar.setBounds(62, 154, 249, 19);
        panel.add(txtBuscar);
        txtBuscar.setColumns(10);
        
        JLabel lblNewLabel = new JLabel("Buscar");
        lblNewLabel.setBounds(20, 157, 53, 13);
        panel.add(lblNewLabel);
        
        chkBuscarJavas = new JCheckBox("Buscar en javas");
        chkBuscarJavas.setBounds(288, 72, 147, 21);
        add(chkBuscarJavas);
        
        chkBuscarSoa = new JCheckBox("Buscar en SOAs");
        chkBuscarSoa.setBounds(288, 94, 147, 21);
        add(chkBuscarSoa);
        
        chkBuscarPantalla = new JCheckBox("Buscar en pantallas");
        chkBuscarPantalla.setBounds(288, 116, 147, 21);
        add(chkBuscarPantalla);
        
        chkBuscarOsb = new JCheckBox("Buscar en OSBs");
        chkBuscarOsb.setBounds(288, 139, 147, 21);
        add(chkBuscarOsb);
	}

	private boolean comprobarSiProcesar() {
		boolean procesar = false;
		if(txtRuta1COInfa.getText().isEmpty() || txtRuta1COPrte.getText().isEmpty() ||
				txtRuta1COTerc.getText().isEmpty() || txtPesp.getText().isEmpty() ||
				(!chkBuscarJavas.isSelected() && !chkBuscarSoa.isSelected() && !chkBuscarPantalla.isSelected() && !chkBuscarOsb.isSelected())) {
			fileUtil.mostrarMensajeInformativo("Hay campos obligatorios sin rellenar");
		}else {
			if(impactosManual.getModel().getRowCount() == 0) {
				fileUtil.mostrarMensajeInformativo("Añada búsquedas antes de continuar");
			}else {
				String mensaje = "Se va a realizar la operación.\n¿Está seguro de que quiere continuar?";
				int opcion = mostrarMensaje(mensaje);
				if(opcion == 0) {
					procesar = true;
				}
			}
		}
		return procesar;
	}
	
	private JTable crearTablas(boolean renderer){
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
		if(renderer) {
			tabla.setModel(new DefaultTableModel(new Object[] { "ACTIVO", "APLICACIÓN", "RUTA" }, 0));
		}else {
			DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "BÚSQUEDA" }, 0) {
			    @Override
			    public boolean isCellEditable(int row, int column) {
			       return false;
			    }
			};
			
			tabla.setModel(tableModel);
			tabla.getSelectionModel().addListSelectionListener(arg0 -> {
                if (!arg0.getValueIsAdjusting()) {
                    btnEliminar.setEnabled(tabla.getSelectedRow() >= 0);
                }
            });
		}
		tabla.getColumnModel().getColumn(0).setPreferredWidth(300);
		return tabla;
	}
	
	private List<String> ejecutarBusqueda() {
		List<String> impactosManuales = new ArrayList<>();
		for(int i=0; i<impactosManual.getModel().getRowCount(); i++) {
			impactosManuales.add(impactosManual.getModel().getValueAt(i, 0).toString());
		}
		List<File> listaRutasbuscar = new ArrayList<>();
		listaRutasbuscar.add(new File(txtRuta1COInfa.getText()));
		listaRutasbuscar.add(new File(txtRuta1COPrte.getText()));
		listaRutasbuscar.add(new File(txtRuta1COTerc.getText()));

		fileUtil.inicializarListaRutas(chkBuscarJavas.isSelected(), chkBuscarSoa.isSelected(), chkBuscarPantalla.isSelected(), chkBuscarOsb.isSelected());
		return fileUtil.ejecutarBusqueda(impactosManuales, listaRutasbuscar, txtPesp.getText(), txtRuta1COInfa.getText(), txtRuta1COPrte.getText(), txtRuta1COTerc.getText());
	}
	
	private void clearTable(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }
	
	private int mostrarMensaje(String mensaje) {
		return JOptionPane.showConfirmDialog(this, mensaje, "", JOptionPane.YES_NO_OPTION);
	}
	
	private void llamarDialogoEspera() {
		
		final JDialog waitForTrans = new JDialog();
		JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 5);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("Procesando análisis...");
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
						Date startDate = new Date();
						List<String> listaActivosEncontrados = ejecutarBusqueda();
		    			cargarTablaCodi(listaActivosEncontrados);
		    			
		    			Date endDate = new Date();
		    			int numSeconds = (int)((endDate.getTime() - startDate.getTime()) / 1000);
		    			fileUtil.mostrarMensajeInformativo("Tiempo en procesar el análisis: " + numSeconds + " segundos");
					} catch (Exception e) {
						System.out.println(e.getLocalizedMessage());
					}
				return null;
			}
			public void done() {
//				waitForTrans.setVisible(false);
//				waitForTrans.dispose();
//				panelConsola.setVisible(false);
//				panelConsola.dispose();
			}
		};
		
		worker.execute();
//		waitForTrans.pack();
//		waitForTrans.setVisible(true);
	}
	
	private void cargarTablaCodi(List<String> listaActivosEncontrados) {
		DefaultTableModel modelCodi = (DefaultTableModel) analisisCODI.getModel();
		clearTable(modelCodi);
		for(String activo: listaActivosEncontrados){
			modelCodi.addRow(new Object[] { activo.split("\\|")[1], activo.split("\\|")[0], activo.split("\\|")[2] });
		}
	}
}