package com.telefonica.modulos.comparador.catalogacion.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRendererTablaResumen extends DefaultTableCellRenderer  {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected,boolean hasFocus,int row,int column) {
		
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// 159,210,232
		// color cabecera resumen excel
		if( table.getValueAt(row, 0).equals("1")) {
			this.setFont(this.getFont().deriveFont(Font.BOLD).deriveFont(14f));
			
			if(table.isRowSelected(row)) {
				this.setBackground(new Color(75,141,225));
			}else {
				this.setBackground(new Color(159,210,232));
			}
			
			if(column == 1) {
				this.setBorder(new MatteBorder(2, 0, 0, 0, Color.BLACK) );
			}else if(column == 2) {
				this.setBorder(new MatteBorder(2, 0, 0, 2, Color.BLACK) );
			}
		}else {
			this.setBackground(Color.WHITE);
		}
        return this;
	}
}