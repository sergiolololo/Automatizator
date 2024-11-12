package com.telefonica.modulos.comparador.poms.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int col) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if (!Objects.equals(table.getValueAt(row, 1), table.getValueAt(row, 2))) {
            this.setFont(this.getFont().deriveFont(Font.BOLD).deriveFont(14f));
        }
        if(col == 1){
            if(table.getModel().getValueAt(table.convertRowIndexToModel(row), 3).equals(Boolean.FALSE)) {
                this.setForeground(Color.RED);
            }else {
                this.setForeground(Color.BLACK);
            }
        }else if(col == 2){
            if(table.getModel().getValueAt(table.convertRowIndexToModel(row), 4).equals(Boolean.FALSE)) {
                this.setForeground(Color.RED);
            }else {
                this.setForeground(Color.BLACK);
            }
        }
        return this;
    }
}