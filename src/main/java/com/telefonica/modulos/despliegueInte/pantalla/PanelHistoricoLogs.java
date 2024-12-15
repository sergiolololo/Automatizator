package com.telefonica.modulos.despliegueInte.pantalla;

import com.telefonica.interfaz.PantallaPrincipal;
import com.telefonica.modulos.despliegueInte.utils.Constantes;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

public class PanelHistoricoLogs extends JDialog {

	@Serial
	private static final long serialVersionUID = 1L;

	private final JTable tablaLogs;

	public PanelHistoricoLogs(ApplicationContext appContext, String rutaCarpetaLogs) {
		super(appContext.getBean(PantallaPrincipal.class), true);

		setTitle("Histórico de logs");
		setSize(771, 411);
		setResizable(true);
		
		JPanel pane = new JPanel();
		setContentPane(pane);
		pane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Listado de logs (doble click para visualizar)");
		lblNewLabel.setBounds(25, 23, 478, 13);
		pane.add(lblNewLabel);
		
		JPanel panelTabla = new JPanel();
		panelTabla.setBounds(25, 59, 709, 297);
		pane.add(panelTabla);
		panelTabla.setLayout(new GridLayout(0, 1, 0, 0));
		
		tablaLogs = new JTable() {
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
		DefaultTableModel tableModel = new DefaultTableModel(new Object[] { "RUTA_ARCHIVO", "ANAGRAMA", "FECHA", "ACCIONES", "RESULTADO" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
			   return false;
			}
		};
		tablaLogs.setModel(tableModel);
		tablaLogs.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				JTable table =(JTable) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
					PanelConsola panelConsola = new PanelConsola(appContext);
					String rutaArchivo = tablaLogs.getModel().getValueAt(tablaLogs.getSelectedRow(), 0).toString();
					try (Stream<String> stream = Files.lines(Paths.get(rutaArchivo), StandardCharsets.ISO_8859_1)) {
						stream.forEach(line -> panelConsola.addText(line + "\n"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					panelConsola.closeBtn.setEnabled(true);
					panelConsola.setVisible(true);
				}
			}
		});
		tablaLogs.getColumnModel().getColumn(0).setPreferredWidth(0);
		tablaLogs.getColumnModel().getColumn(0).setMinWidth(0);
		tablaLogs.getColumnModel().getColumn(0).setMaxWidth(0);

		tablaLogs.getColumnModel().getColumn(1).setPreferredWidth(125);
		tablaLogs.getColumnModel().getColumn(1).setMaxWidth(125);
		tablaLogs.getColumnModel().getColumn(2).setPreferredWidth(150);
		tablaLogs.getColumnModel().getColumn(2).setMaxWidth(150);
		tablaLogs.getColumnModel().getColumn(3).setPreferredWidth(300);
		tablaLogs.getColumnModel().getColumn(3).setMaxWidth(300);

		JScrollPane scrollPane = new JScrollPane(tablaLogs);
		panelTabla.add(scrollPane);

		rellenarTabla(rutaCarpetaLogs);

		setLocationRelativeTo(null);
	}

	private void rellenarTabla(String rutaCarpetaLogs) {
		File carpeta = new File(rutaCarpetaLogs);
		File[] archivos = carpeta.listFiles();
		if (archivos != null) {
			DefaultTableModel tableModel = (DefaultTableModel) tablaLogs.getModel();
			for (File archivo : archivos) {
				if (archivo.isFile()) {
					String nombreArchivo = archivo.getName().replace("log_", "").replace(".txt", "");
					String anagrama = nombreArchivo.substring(0, nombreArchivo.lastIndexOf("_"));
					String fechaMilisegundos = nombreArchivo.substring(nombreArchivo.lastIndexOf("_") + 1);
					String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(Long.parseLong(fechaMilisegundos)));

					boolean error = false;
					Set<String> acciones = new LinkedHashSet<>();

					try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
						String line = br.readLine();
						while (line != null) {
							if (line.contains("ERROR")) {
								error = true;
							}
							if (line.contains(Constantes.INICIO_COPIA_FICHEROS)) {
								acciones.add("Copia de ficheros");
							}
							if (line.contains(Constantes.INICIO_COMPILACION)) {
								acciones.add("Compilación");
							}
							if (line.contains(Constantes.INICIO_DESPLIEGUE)) {
								acciones.add("Despliegue y reinicio");
							}
							line = br.readLine();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					tableModel.addRow(new Object[] { archivo.getAbsolutePath(), anagrama, fecha, String.join(", ", new ArrayList<>(acciones)), error ? "ERROR" : "OK" });
				}
			}
		}
	}
}