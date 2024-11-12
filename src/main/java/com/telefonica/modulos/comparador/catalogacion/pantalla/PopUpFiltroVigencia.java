package com.telefonica.modulos.comparador.catalogacion.pantalla;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.IOException;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.github.lgooddatepicker.components.CalendarPanel;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.CalendarListener;
import com.github.lgooddatepicker.zinternaltools.CalendarSelectionEvent;
import com.github.lgooddatepicker.zinternaltools.YearMonthChangeEvent;

@SuppressWarnings("serial")
public class PopUpFiltroVigencia extends JPanel {

	/**
	 * Create the panel.
	 * @param contentPane 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	
	public static LocalDate fechaInicio;
	public static LocalDate fechaFin;
	
	public PopUpFiltroVigencia(JPanel contentPane) throws SAXException, IOException, ParserConfigurationException {
		
		DatePickerSettings settingFechaInicio = new DatePickerSettings();
		DatePickerSettings settingFechaFin = new DatePickerSettings();
	    CalendarPanel calendarioFechaInicio = new CalendarPanel(settingFechaInicio);
	    CalendarPanel calendarioFechaFin = new CalendarPanel(settingFechaFin);
	    
	    if(fechaInicio == null) {
	    	fechaInicio = LocalDate.now();
	    }
	    
	    calendarioFechaInicio.setSelectedDate(fechaInicio);
	    calendarioFechaInicio.setBorder(new LineBorder(Color.lightGray));
	    calendarioFechaInicio.addCalendarListener(new CalendarListener() {
			@Override
			public void yearMonthChanged(YearMonthChangeEvent arg0) {
			}
			@Override
			public void selectedDateChanged(CalendarSelectionEvent arg0) {
				fechaInicio = arg0.getNewDate();
			}
		});
	    
	    calendarioFechaFin.setSelectedDate(fechaFin);
	    calendarioFechaFin.setBorder(new LineBorder(Color.lightGray));
	    calendarioFechaFin.addCalendarListener(new CalendarListener() {
			@Override
			public void yearMonthChanged(YearMonthChangeEvent arg0) {
			}
			@Override
			public void selectedDateChanged(CalendarSelectionEvent arg0) {
				fechaFin= arg0.getNewDate();
			}
		});
	    
	    JPanel panel = new JPanel();
		panel.add(calendarioFechaInicio);
		panel.setBounds(0, 0, 572, 320);
		
		JLabel lblFechaInicio = new JLabel("Fecha inicio");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 0;
		calendarioFechaInicio.add(lblFechaInicio, gbc_lblNewLabel);
		panel.add(calendarioFechaFin);
		
		JLabel lblFechaFin = new JLabel("Fecha fin");
		GridBagConstraints gbc_lblNewLabel2 = new GridBagConstraints();
		gbc_lblNewLabel2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel2.gridx = 1;
		gbc_lblNewLabel2.gridy = 0;
		calendarioFechaFin.add(lblFechaFin, gbc_lblNewLabel2);
		
		add(panel);
	}
}