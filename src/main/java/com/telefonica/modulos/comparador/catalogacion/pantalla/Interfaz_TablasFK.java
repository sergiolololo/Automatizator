package com.telefonica.modulos.comparador.catalogacion.pantalla;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class Interfaz_TablasFK extends JFrame {
	private PanelTablasFK panelTablasFK;
	private JPanel contentPane;
	public static Interfaz_TablasFK frame;
	public static JTable tablaOrigen;
	public static String nombreTablaOrigen;

	/**
	 * Launch the application.
	 */
	public static void createFrame(JTable tablaActivos, String nombreTabla) {
		try {
			tablaOrigen = tablaActivos;
			nombreTablaOrigen = nombreTabla;
			frame = new Interfaz_TablasFK();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws InterruptedException 
	 */
	public Interfaz_TablasFK() throws SAXException, IOException, ParserConfigurationException, InterruptedException {
		setAlwaysOnTop(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			}
		});
		setTitle("Comparador de catalogaci�n");
		setBounds(100, 100, 1156, 503);
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		contentPane = new JPanel();
		getContentPane().add(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		panelTablasFK = new PanelTablasFK();
		contentPane.add(panelTablasFK, "panelTablasFK");
	}
}
