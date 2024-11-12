package com.telefonica.modulos.comparador.catalogacion.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import com.telefonica.modulos.comparador.catalogacion.pantalla.PanelTablas;
import com.telefonica.modulos.comparador.catalogacion.procesador.ProcesadorTabla;
import com.telefonica.modulos.comparador.catalogacion.utils.Constants;

public class CellRendererTablaComparacion extends DefaultTableCellRenderer  {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected,boolean hasFocus,int row,int column) {
		
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		String entorno = (String) table.getValueAt(row, 0);
		int idFilaReal = -1;
		if(!table.getModel().getValueAt(row, Constants.POSICION_NUMERO_FILA_REAL).equals("   ")) {
			idFilaReal = Integer.parseInt(table.getModel().getValueAt(row, Constants.POSICION_NUMERO_FILA_REAL).toString());
		}
		String nombreTabla = (String) table.getModel().getValueAt(row, Constants.POSICION_TABLA);
		//String valorCelda = "";
		
		// se comprueba si es una fila vac�a
		if(table.getModel().getValueAt(row, Constants.POSICION_TABLA).equals("   ")) {
			this.setBackground(new Color(198,198,198));
			this.setBorder(new MatteBorder(2, 0, 0, 0, Color.BLACK) );
			return this;
		}else {
			if(column == 0) {
				
				DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
			    TableColumn col = colModel.getColumn(column);
			    col.setMaxWidth(40);
				
				if(table.getModel().getValueAt(row, Constants.POSICION_ES_LIMITE).equals("1")) {
					this.setBorder(new MatteBorder(2, 2, 0, 2, Color.BLACK) );
				}else if((table.getRowCount()-1) == row) {
					this.setBorder(new MatteBorder(0, 2, 2, 2, Color.BLACK) );
				}else {
					this.setBorder(new MatteBorder(0, 2, 0, 2, Color.BLACK) );
				}
				this.setBackground(colorEntorno(table, row));
				
			}else if((column+1) == table.getColumnCount()) {
				if(table.getModel().getValueAt(row, Constants.POSICION_ES_LIMITE).equals("1")) {
					this.setBorder(new MatteBorder(2, 0, 0, 2, Color.BLACK) );
				}else if((table.getRowCount()-1) == row) {
					this.setBorder(new MatteBorder(0, 0, 2, 2, Color.BLACK) );
				}else {
					this.setBorder(new MatteBorder(0, 0, 0, 2, Color.BLACK) );
				}
			}else {
				if(table.getModel().getValueAt(row, Constants.POSICION_ES_LIMITE).equals("1")) {
					this.setBorder(new MatteBorder(2, 0, 0, 2, Color.BLACK) );
				}else if((table.getRowCount()-1) == row) {
					this.setBorder(new MatteBorder(0, 0, 2, 2, Color.BLACK) );
				}else {
					this.setBorder(new MatteBorder(0, 0, 0, 2, Color.BLACK) );
				}
			}
			
			if(column > 0) {
				int numeroEntornos = PanelTablas.listaEntornosSeleccionados.size();
				//String rowString = "" + row;
				boolean encontrado = false;
				if(idFilaReal != -1) {
					String valorCelda = (String) table.getValueAt(row, column);
					String valorOriginal = (String) ProcesadorTabla.mapaTablaFilasOriginal.get(nombreTabla).get(idFilaReal)[ProcesadorTabla.numeroColumnasExtra+column-1];
					if(!valorOriginal.equals(valorCelda)) {
						this.setBackground(new Color(242,247,98));
						Map<TextAttribute, Object> attributes = new HashMap<>();
			            attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
						if(PanelTablas.entornoReferencia.equals(entorno)) {
				            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
				            attributes.put(TextAttribute.SIZE, 13);
						}
						this.setFont(Font.getFont(attributes));
						encontrado = true;
					}
				}
				
				if(!encontrado) {
					if(numeroEntornos == 2) {
						String valorEntorno1 = "";
						String valorEntorno2 = "";
						
						if(table.getModel().getValueAt(row, Constants.POSICION_ES_LIMITE).equals("1")) {
							valorEntorno1 = (String) table.getValueAt(row, column);
							valorEntorno2 = (String) table.getValueAt(row+1, column);
						}else {
							valorEntorno1 = (String) table.getValueAt(row-1, column);
							valorEntorno2 = (String) table.getValueAt(row, column);
						}
						
						if((valorEntorno1 == null && valorEntorno2 != null) || 
								(valorEntorno1 != null && valorEntorno2 == null) ||
								(valorEntorno1 != null && valorEntorno2 != null && !valorEntorno1.equals(valorEntorno2))){
							
							if(PanelTablas.entornoReferencia.equals(entorno)) {
								this.setBackground(new Color(205,222,180));
								Map<TextAttribute, Object> attributes = new HashMap<>();
					            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
					            attributes.put(TextAttribute.SIZE, 13);
					            this.setFont(Font.getFont(attributes));
							}else {
								this.setBackground(new Color(255,165,163));	
							}
						}else {
							this.setBackground(Color.WHITE);
						}
					}else if(numeroEntornos > 2) {
						String valor1, valor2, valor3, valor4, valor5; valor1=valor2=valor3=valor4=valor5 = "";
						Color colorFila = colorEntorno(table, row);
						
						if(table.getModel().getValueAt(row, Constants.POSICION_ES_LIMITE).equals("1")) {
							if(PanelTablas.entornoReferencia.equals(entorno)) {
								valor1 = (String) table.getValueAt(row, column);
								valor2 = (String) table.getValueAt(row+1, column);
								valor3 = (String) table.getValueAt(row+2, column);
								valor4 = numeroEntornos>3?(String) table.getValueAt(row+3, column):null;
								valor5 = numeroEntornos>4?(String) table.getValueAt(row+4, column):null;
								
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, true);
								Map<TextAttribute, Object> attributes = new HashMap<>();
					            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
					            attributes.put(TextAttribute.SIZE, 13);
					            this.setFont(Font.getFont(attributes));
							}else {
								if(PanelTablas.entornoReferencia.equals(table.getValueAt(row+1, 0))) {
									valor1 = (String) table.getValueAt(row+1, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(PanelTablas.entornoReferencia.equals(table.getValueAt(row+2, 0))) {
									valor1 = (String) table.getValueAt(row+2, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(numeroEntornos>3 && PanelTablas.entornoReferencia.equals(table.getValueAt(row+3, 0))) {
									valor1 = (String) table.getValueAt(row+3, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								else if(numeroEntornos>4 && PanelTablas.entornoReferencia.equals(table.getValueAt(row+4, 0))) {
									valor1 = (String) table.getValueAt(row+4, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								colorFila = new Color(255,165,163);
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, false);
							}
						}else if(table.getModel().getValueAt(row-1, Constants.POSICION_ES_LIMITE).equals("1")) {
							if(PanelTablas.entornoReferencia.equals(entorno)) {
								valor1 = (String) table.getValueAt(row, column);
								valor2 = (String) table.getValueAt(row-1, column);
								valor3 = (String) table.getValueAt(row+1, column);
								valor4 = numeroEntornos>3?(String) table.getValueAt(row+2, column):null;
								valor5 = numeroEntornos>4?(String) table.getValueAt(row+3, column):null;
								
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, true);
								//compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, true);
								Map<TextAttribute, Object> attributes = new HashMap<>();
					            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
					            attributes.put(TextAttribute.SIZE, 13);
					            this.setFont(Font.getFont(attributes));
							}else {
								if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-1, 0))) {
									valor1 = (String) table.getValueAt(row-1, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(PanelTablas.entornoReferencia.equals(table.getValueAt(row+1, 0))) {
									valor1 = (String) table.getValueAt(row+1, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(numeroEntornos>3 && PanelTablas.entornoReferencia.equals(table.getValueAt(row+2, 0))) {
									valor1 = (String) table.getValueAt(row+2, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								else if(numeroEntornos>4 && PanelTablas.entornoReferencia.equals(table.getValueAt(row+3, 0))) {
									valor1 = (String) table.getValueAt(row+3, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								colorFila = new Color(255,165,163);
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, false);
							}
							
						}else if(table.getModel().getValueAt(row-2, Constants.POSICION_ES_LIMITE).equals("1")) {
							if(PanelTablas.entornoReferencia.equals(entorno)) {
								valor1 = (String) table.getValueAt(row, column);
								valor2 = (String) table.getValueAt(row-2, column);
								valor3 = (String) table.getValueAt(row-1, column);
								valor4 = numeroEntornos>3?(String) table.getValueAt(row+1, column):null;
								valor5 = numeroEntornos>4?(String) table.getValueAt(row+2, column):null;
								
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, true);
								//compararValores(valor1, valor2, valor3, valor4, colorFila, true);
								Map<TextAttribute, Object> attributes = new HashMap<>();
					            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
					            attributes.put(TextAttribute.SIZE, 13);
					            this.setFont(Font.getFont(attributes));
							}else {
								if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-2, 0))) {
									valor1 = (String) table.getValueAt(row-2, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-1, 0))) {
									valor1 = (String) table.getValueAt(row-1, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(numeroEntornos>3 && PanelTablas.entornoReferencia.equals(table.getValueAt(row+1, 0))) {
									valor1 = (String) table.getValueAt(row+1, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								else if(numeroEntornos>4 && PanelTablas.entornoReferencia.equals(table.getValueAt(row+2, 0))) {
									valor1 = (String) table.getValueAt(row+2, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								colorFila = new Color(255,165,163);
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, false);
							}
						}else if(numeroEntornos > 3 && table.getModel().getValueAt(row-3, Constants.POSICION_ES_LIMITE).equals("1")) {
							if(PanelTablas.entornoReferencia.equals(entorno)) {
								valor1 = (String) table.getValueAt(row, column);
								valor2 = (String) table.getValueAt(row-3, column);
								valor3 = (String) table.getValueAt(row-2, column);
								valor4 = numeroEntornos>3?(String) table.getValueAt(row-1, column):null;
								valor5 = numeroEntornos>4?(String) table.getValueAt(row+1, column):null;
								
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, true);
								//compararValores(valor1, valor2, valor3, valor4, colorFila, true);
								Map<TextAttribute, Object> attributes = new HashMap<>();
					            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
					            attributes.put(TextAttribute.SIZE, 13);
					            this.setFont(Font.getFont(attributes));
							}else {
								if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-3, 0))) {
									valor1 = (String) table.getValueAt(row-3, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-2, 0))) {
									valor1 = (String) table.getValueAt(row-2, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-1, 0))) {
									valor1 = (String) table.getValueAt(row-1, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								else if(PanelTablas.entornoReferencia.equals(table.getValueAt(row+1, 0))) {
									valor1 = (String) table.getValueAt(row+1, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								colorFila = new Color(255,165,163);
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, false);
							}
						}else if(numeroEntornos > 4 && table.getModel().getValueAt(row-4, Constants.POSICION_ES_LIMITE).equals("1")) {
							if(PanelTablas.entornoReferencia.equals(entorno)) {
								valor1 = (String) table.getValueAt(row, column);
								valor2 = (String) table.getValueAt(row-4, column);
								valor3 = (String) table.getValueAt(row-3, column);
								valor4 = numeroEntornos>3?(String) table.getValueAt(row-2, column):null;
								valor5 = numeroEntornos>4?(String) table.getValueAt(row-1, column):null;
								
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, true);
								//compararValores(valor1, valor2, valor3, valor4, colorFila, true);
								Map<TextAttribute, Object> attributes = new HashMap<>();
					            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
					            attributes.put(TextAttribute.SIZE, 13);
					            this.setFont(Font.getFont(attributes));
							}else {
								if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-4, 0))) {
									valor1 = (String) table.getValueAt(row-4, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-3, 0))) {
									valor1 = (String) table.getValueAt(row-3, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-2, 0))) {
									valor1 = (String) table.getValueAt(row-2, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								else if(PanelTablas.entornoReferencia.equals(table.getValueAt(row-1, 0))) {
									valor1 = (String) table.getValueAt(row-1, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								colorFila = new Color(255,165,163);
								compararValores(valor1, valor2, valor3, valor4, valor5, colorFila, false);
							}
						}
						
						
						
						
						
						
						
						
						
						
						/*
						
						else if(numeroEntornos==4 && table.getModel().getValueAt(row-3, 1).equals("1")) {
							if(PanelTablas_NO_TAB.entornoReferencia.equals(entorno)) {
								valor1 = (String) table.getValueAt(row, column);
								valor2 = (String) table.getValueAt(row-3, column);
								valor3 = (String) table.getValueAt(row-2, column);
								valor4 = (String) table.getValueAt(row-1, column);
								
								compararValores(valor1, valor2, valor3, valor4, colorFila, true);
								compararValores(valor1, valor2, valor3, valor4, colorFila, true);
								Map<TextAttribute, Object> attributes = new HashMap<>();
					            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
					            attributes.put(TextAttribute.SIZE, 13);
					            this.setFont(Font.getFont(attributes));
							}else {
								if(PanelTablas_NO_TAB.entornoReferencia.equals(table.getValueAt(row-2, 0))) {
									valor1 = (String) table.getValueAt(row-2, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(PanelTablas_NO_TAB.entornoReferencia.equals(table.getValueAt(row-1, 0))) {
									valor1 = (String) table.getValueAt(row-1, column);
									valor2 = (String) table.getValueAt(row, column);
								}else if(PanelTablas_NO_TAB.entornoReferencia.equals(table.getValueAt(row-3, 0))) {
									valor1 = (String) table.getValueAt(row-3, column);
									valor2 = (String) table.getValueAt(row, column);
								}
								colorFila = new Color(255,165,163);
								compararValores(valor1, valor2, valor3, valor4, colorFila, false);
							}
						}*/
					}	
				}
			}
			
	        return this;	
		}
	}
	
	private void compararValores(String valor1, String valor2, String valor3, String valor4, String valor5, Color colorFila, boolean compararTodosValores) {
		
		if((valor1 == null && valor2 != null) || 
				(valor1 != null && valor2 == null) ||
				(valor1 != null && valor2 != null && !valor1.equals(valor2))){
			
			this.setBackground(colorFila);
		}else if(compararTodosValores){
			if((valor1 == null && valor3 != null) || 
					(valor1 != null && valor3 == null) ||
					(valor1 != null && valor3 != null && !valor1.equals(valor3))) {
			
				this.setBackground(colorFila);
			}else if( valor4 != null && (
					(valor1 == null && valor4 != null) || 
					(valor1 != null && valor4 == null) ||
					(valor1 != null && valor4 != null && !valor1.equals(valor4)))) {
			
				this.setBackground(colorFila);
			}else if( valor5 != null && (
					(valor1 == null && valor5 != null) || 
					(valor1 != null && valor5 == null) ||
					(valor1 != null && valor5 != null && !valor1.equals(valor5)))) {
			
				this.setBackground(colorFila);
			}else {
				this.setBackground(Color.WHITE);
			}
		}else {
			this.setBackground(Color.WHITE);
		}
	}
	
	private Color colorEntorno(JTable table, int row) {
		Color color = null;
		switch(table.getValueAt(row, 0).toString()) {
			case "EDC":
				if(PanelTablas.entornoReferencia.contentEquals("EDC")){
					// color verde
					color = new Color(205,222,180);
				}else{
					// color naranja
					color = new Color(255,216,166);
				}
				break;
			case "EIN":
				if(PanelTablas.entornoReferencia.contentEquals("EDC")){
					// color naranja
					color = new Color(255,216,166);
				}else if(PanelTablas.entornoReferencia.contentEquals("EIN")){
					// color verde
					color = new Color(205,222,180);
				}else if(PanelTablas.entornoReferencia.contentEquals("ECE")){
					// color rosa
					color = new Color(250,180,232);
				}else if(PanelTablas.entornoReferencia.contentEquals("ECO")){
					// color azul
					color = new Color(160,229,229);
				}
				break;
			case "ECE":
				if(PanelTablas.entornoReferencia.contentEquals("ECE")){
					// color verde
					color = new Color(205,222,180);
				}else{
					// color rosa
					color = new Color(250,180,232);
				}
				break;
			case "ECO":
				if(PanelTablas.entornoReferencia.contentEquals("ECO")){
					// color verde
					color = new Color(205,222,180);
				}else{
					// color azul
					color = new Color(160,229,229);
				}
				break;
			case "EPR":
				if(PanelTablas.entornoReferencia.contentEquals("EPR")){
					color = new Color(205,222,180);
				}else{
					color = new Color(159,210,232);
				}
				break;
		}
		return color;
	}
}