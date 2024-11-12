package com.telefonica.modulos.comparador.catalogacion.pantalla;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.telefonica.modulos.comparador.catalogacion.utils.Connection;

@SuppressWarnings("serial")
public class Interfaz extends JFrame {
	private PanelTablas panelTablas;
	private PanelComparacion panelComparacion;
	
	private PanelTablasFK panelTablasFK;
	
	public static JPanel contentPane;
	public static Interfaz frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Interfaz();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws InterruptedException 
	 */
	public Interfaz() throws SAXException, IOException, ParserConfigurationException, InterruptedException {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Connection.closeConnection();
			}
		});
		setTitle("Comparador de catalogaci�n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1156, 503);
		
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		contentPane = new JPanel();
		getContentPane().add(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		panelTablas = new PanelTablas();
		contentPane.add(panelTablas, "panelTablas");
		
		panelComparacion = new PanelComparacion();
		contentPane.add(panelComparacion, "panelComparacion");
		
		panelTablasFK = new PanelTablasFK();
		contentPane.add(panelTablasFK, "panelTablasFK");
	}
}
