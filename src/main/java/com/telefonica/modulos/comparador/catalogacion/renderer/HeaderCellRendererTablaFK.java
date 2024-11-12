package com.telefonica.modulos.comparador.catalogacion.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.telefonica.modulos.comparador.catalogacion.bean.CambioBean;
import com.telefonica.modulos.comparador.catalogacion.bean.FiltroBean;
import com.telefonica.modulos.comparador.catalogacion.procesador.ProcesadorTabla;

public class HeaderCellRendererTablaFK implements TableCellRenderer   {

    private JTableHeader header;

    public HeaderCellRendererTablaFK(JTableHeader header) {
        this.header = header;
    }
    
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected,boolean hasFocus,int row,int column) {
		Component c = header.getDefaultRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        CambioBean fotoActual = ProcesadorTabla.fotoActualFK;
        String nombreColumna = table.getColumnModel().getColumn(column).getHeaderValue().toString();
        
        Map<String, FiltroBean> filtros = fotoActual.getMapaColumnaFiltro();
        if(filtros != null && filtros.get(nombreColumna) != null) {
        	String filtroColuma = filtros.get(nombreColumna).getFiltroTabla();
        	if(filtroColuma != null && !filtroColuma.equals("")) {
        		c.setBackground(new Color(159,210,232));
        	}else {
            	c.setBackground(new Color(238,238,238));
            }
        }else {
        	c.setBackground(new Color(238,238,238));
        }
        return c;
	}
}