package com.telefonica.modulos.comparador.catalogacion.pantalla;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.telefonica.modulos.comparador.catalogacion.procesador.ProcesadorTabla;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
public class PanelTablasFK extends JPanel {
	private JPanel contentPane;
	
	public static JLabel lblNombreTabla;
	private JButton btnNewButton_2;
	public static JPanel jpanelTablasFK;
	//public static JTable tablaOrigen;
	public static String nombreTablaOrigen;
	
	/**
	 * Create the panel.
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public void setContentPane(JPanel contentPane) {
		this.contentPane = contentPane;
	}
	
	public PanelTablasFK() throws SAXException, IOException, ParserConfigurationException {
		setBounds(0, 0, 1144, 484);
		
		jpanelTablasFK = new JPanel();
		lblNombreTabla = new JLabel("Seleccione el identificador que desea");
		lblNombreTabla.setHorizontalAlignment(SwingConstants.CENTER);
		lblNombreTabla.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		JButton btnNewButton_1 = new JButton("VOLVER");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer opcion = mostrarMensajeSiNo("�Est� seguro de que desea volver?");
		    	if(opcion == 0) {
		    		
					if(!nombreTablaOrigen.equals("")) {
						JTable tablaActivos = null;
						for(Component component: PanelComparacion.cardLayout.getComponents()) {
							if(nombreTablaOrigen.equals(component.getName())) {
								JScrollPane scrollPane = (JScrollPane) component;
								JViewport viewport = scrollPane.getViewport();
								tablaActivos = (JTable)viewport.getView();
								break;
							}
						}
						if(tablaActivos != null) {
							ProcesadorTabla.finishEditing(tablaActivos, nombreTablaOrigen);
						}
					}
		    		
		    		CardLayout c = (CardLayout)(contentPane.getLayout());
					c.show(contentPane, "panelComparacion");
					
					CardLayout cc = (CardLayout)(PanelComparacion.cardLayout.getLayout());
    				cc.show(PanelComparacion.cardLayout, nombreTablaOrigen);
    				
    				ProcesadorTabla.nombreTablaFKEncontrada = null;
		    	}
			}
		});
		
		btnNewButton_2 = new JButton("SELECCIONAR");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JTable tablaActivosFK = null;
				for(Component component: jpanelTablasFK.getComponents()) {
					if(ProcesadorTabla.nombreTablaFKEncontrada.equals(component.getName())) {
						JScrollPane scrollPane = (JScrollPane) component;
						JViewport viewport = scrollPane.getViewport();
						tablaActivosFK = (JTable)viewport.getView();
					}
				}
	    		
				JTable tablaActivos = null;
				
				int row = tablaActivosFK.getSelectedRow();
				if(row != -1) {
					String valorIdSeleccionado = tablaActivosFK.getValueAt(row, 0).toString();
					if(!nombreTablaOrigen.equals("")) {
						for(Component component: PanelComparacion.cardLayout.getComponents()) {
							if(nombreTablaOrigen.equals(component.getName())) {
								JScrollPane scrollPane = (JScrollPane) component;
								JViewport viewport = scrollPane.getViewport();
								tablaActivos = (JTable)viewport.getView();
								break;
							}
						}
						if(tablaActivos != null) {
							tablaActivos.setValueAt(valorIdSeleccionado, tablaActivos.getSelectedRow(), tablaActivos.getSelectedColumn());
						}
					}
					
					CardLayout c = (CardLayout)(contentPane.getLayout());
					c.show(contentPane, "panelComparacion");
					
					CardLayout cc = (CardLayout)(PanelComparacion.cardLayout.getLayout());
    				cc.show(PanelComparacion.cardLayout, nombreTablaOrigen);
		    		
    				if(tablaActivos != null) {
    					ProcesadorTabla.finishEditing(tablaActivos, nombreTablaOrigen);
    				}
				}else {
					JOptionPane.showMessageDialog(null, "Debe seleccionar una fila antes de continuar");
				}
			}
		});
		btnNewButton_2.setEnabled(true);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(22)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
							.addGap(208)
							.addComponent(lblNombreTabla, GroupLayout.PREFERRED_SIZE, 424, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
							.addComponent(btnNewButton_2, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
						.addComponent(jpanelTablasFK, GroupLayout.DEFAULT_SIZE, 1101, Short.MAX_VALUE))
					.addGap(21))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(31)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnNewButton_1)
							.addComponent(lblNombreTabla, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnNewButton_2))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(jpanelTablasFK, GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
					.addGap(28))
		);
		jpanelTablasFK.setLayout(new CardLayout(0, 0));
		setLayout(groupLayout);
	}
	
	
	public int mostrarMensajeSiNo(String mensaje) {
		return JOptionPane.showConfirmDialog(this, mensaje, "", JOptionPane.YES_NO_OPTION);
	}
}