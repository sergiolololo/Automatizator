package com.telefonica.modulos.comparador.catalogacion.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRendererTablaSeleccion extends DefaultTableCellRenderer  {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected,boolean hasFocus,int row,int column) {
		final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(!isSelected) {
        	c.setBackground(row % 2 == 0 ? new Color(213,226,231) : Color.WHITE);
        }else {
        	JComponent jc = (JComponent)c;
            jc.setBorder(new MatteBorder(1, 0, 1, 0, Color.BLACK) );
            
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.FAMILY, Font.DIALOG);
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            attributes.put(TextAttribute.SIZE, 14);
            jc.setFont(Font.getFont(attributes));
        }
        return c;
	}
}