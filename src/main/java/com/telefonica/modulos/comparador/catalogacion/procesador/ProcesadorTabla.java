package com.telefonica.modulos.comparador.catalogacion.procesador;

import java.awt.AWTException;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Robot;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;

import com.telefonica.modulos.comparador.catalogacion.renderer.CellRendererTablaComparacion;
import com.telefonica.modulos.comparador.catalogacion.renderer.CellRendererTablaSeleccion;
import com.telefonica.modulos.comparador.catalogacion.renderer.HeaderCellRendererTablaComparacion;
import com.telefonica.modulos.comparador.catalogacion.renderer.HeaderCellRendererTablaFK;
import com.telefonica.modulos.comparador.catalogacion.utils.Connection;
import org.xml.sax.SAXException;

import com.telefonica.modulos.comparador.catalogacion.bean.CambioBean;
import com.telefonica.modulos.comparador.catalogacion.bean.FiltroBean;
import com.telefonica.modulos.comparador.catalogacion.pantalla.Interfaz;
import com.telefonica.modulos.comparador.catalogacion.pantalla.PanelComparacion;
import com.telefonica.modulos.comparador.catalogacion.pantalla.PanelTablas;
import com.telefonica.modulos.comparador.catalogacion.pantalla.PanelTablasFK;
import com.telefonica.modulos.comparador.catalogacion.pantalla.PopUpFiltroVigencia;
import com.telefonica.modulos.comparador.catalogacion.utils.Constants;

import javafx.util.Pair;

public class ProcesadorTabla {

	private static Map<String, Map<String, Map<String, Map<String, String>>>> mapaResultadosEntornos = new LinkedHashMap<String, Map<String,Map<String,Map<String,String>>>>();
	
	public static String usuarioEPR;
	public static String passwordEPR;
	
	private static Map<String, List<String>> mapaEntornoTablas = new LinkedHashMap<String, List<String>>();
	public static Map<String, List<String>> mapaAplicacionTablas = new LinkedHashMap<String, List<String>>();
	private static Map<String, List<String>> mapaTablaColumnas = new LinkedHashMap<String, List<String>>(); 
	public static Map<String, List<Object[]>> mapaTablaFilasOriginal = new LinkedHashMap<String, List<Object[]>>();
	public static Map<String, List<Object[]>> mapaTablaFilasModificado = new LinkedHashMap<String, List<Object[]>>();
	
	public static Set<String> listaColumnasIgnorar = new HashSet<String>();
	
	private static Map<String, Map<String, Integer>> mapaTablaColumnasDiscrepancias = new LinkedHashMap<String, Map<String,Integer>>();
	private static Map<String, Integer> mapaColumnaNumeroDiscrepancias;
	
	private static List<Object[]> listaFilasAnterior = new ArrayList<Object[]>();
	
	public static Map<String, List<CambioBean>> mapaTablaListaDeshacer = new LinkedHashMap<String, List<CambioBean>>();
	public static Map<String, List<CambioBean>> mapaTablaListaRehacer = new LinkedHashMap<String, List<CambioBean>>();
	
	private static String excepcion = null;
	private static boolean filaReferenciaCopiada = false;
	
	public static Map<String, List<String>> mapaTablaPKs = new HashMap<String, List<String>>();
	public static Map<String, List<Pair<String, String>>> mapaTablaFKs = new HashMap<String, List<Pair<String, String>>>();
	public static String nombreTablaFKEncontrada = null;
	
	public static Map<String, CambioBean> fotoActualTabla = new HashMap<String, CambioBean>();
	
	private static boolean cumpleDiferencia = false;
	private static boolean cumpleFiltroTotal = false;
	
	public static int numeroColumnasExtra = 5;
	
	
	private static Map<String, List<Object[]>> mapaTablaFilasFK = new LinkedHashMap<String, List<Object[]>>();
	public static CambioBean fotoActualFK = new CambioBean();
	
	public static void main() throws Exception {
		llamarDialogoEspera();
		for(Component component: PanelComparacion.cardLayout.getComponents()) {
			
			if(component instanceof JScrollPane) {
				String nombreTabla = component.getName();
				JScrollPane scrollPane = (JScrollPane) component;
				JViewport viewport = scrollPane.getViewport();
				JTable tablaActivos = (JTable)viewport.getView();
				List<Object[]> listaFilas = new ArrayList<Object[]>();
				List<Object[]> listaFilasModificadas = new ArrayList<Object[]>();
				for(int i=0; i<tablaActivos.getModel().getRowCount(); i++) {
					Object[] filaOriginal = new Object[tablaActivos.getModel().getColumnCount()];
					Object[] filaModificada = new Object[tablaActivos.getModel().getColumnCount()];
					for(int j=0; j<tablaActivos.getModel().getColumnCount(); j++) {
						filaOriginal[j] = tablaActivos.getModel().getValueAt(i, j);
						filaModificada[j] = tablaActivos.getModel().getValueAt(i, j);
					}
					listaFilas.add(filaOriginal);
					listaFilasModificadas.add(filaModificada);
				}
				mapaTablaFilasOriginal.put(nombreTabla, listaFilas);
				mapaTablaFilasModificado.put(nombreTabla, listaFilasModificadas);
			}
		}
		
		PanelComparacion.cardLayout.add(new JPanel(), "invisible");
		CardLayout c = (CardLayout)(PanelComparacion.cardLayout.getLayout());
		c.show(PanelComparacion.cardLayout, "invisible");
		PanelComparacion.cardLayout.repaint();
		if(excepcion != null) {
			//usuarioEPR = null;
			//passwordEPR = null;
			throw new Exception(excepcion);
		}
	}
	
	public static void fetchFiles() throws ClassNotFoundException, SQLException, IOException {
		
		List<String> listaFicherosTablas = new ArrayList<String>();
		listaFicherosTablas.add("Tablas_completas/tablas_INFTER1");
		listaFicherosTablas.add("Tablas_completas/tablas_PRVTER1");
		listaFicherosTablas.add("Tablas_completas/tablas_TERCBS1");
		
		listaColumnasIgnorar = new HashSet<String>();
		InputStream is = Interfaz.class.getClassLoader().getResourceAsStream("columnasBBBB_ignorar");
		if(is != null) {
			InputStreamReader streamReader = new InputStreamReader(is);
			BufferedReader in = new BufferedReader(streamReader);
			String line = in.readLine();
			while(line != null) {
				listaColumnasIgnorar.add(line);
				line = in.readLine();
			}
			
			is.close();
		}
		
		crearMapaResultados(listaFicherosTablas);
		
		for(String fichero: listaFicherosTablas) {
			String usuarioBBDD = fichero.substring(fichero.lastIndexOf("_") + 1);
			processCompare(fichero, usuarioBBDD);
		}
        createSheetResumen();
	}
	
	
	@SuppressWarnings("rawtypes")
	private static void llamarDialogoEspera() {
		final JDialog waitForTrans = new JDialog();
		JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 5);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("Procesando comparativa...");
		final JOptionPane optionPane = new JOptionPane(progressBar, JOptionPane.CLOSED_OPTION, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        waitForTrans.setSize(200,200);
    	waitForTrans.setLocationRelativeTo(null);
    	waitForTrans.setTitle("Espere...");
    	waitForTrans.setModal(true);
    	waitForTrans.setContentPane(optionPane);
    	waitForTrans.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		SwingWorker worker = new SwingWorker() {
			public String doInBackground()  {
					excepcion = null;
					try {
						fetchFiles();
					} catch (ClassNotFoundException e) {
						
						excepcion = e.getMessage();
					} catch (SQLException e) {
						
						excepcion = e.getMessage();
					} catch (IOException e) {
						
						excepcion = e.getMessage();
					}
				return null;
			}
			public void done() {
				waitForTrans.setVisible(false);
				waitForTrans.dispose();
			}
		};
		
		worker.execute();
		waitForTrans.pack();
		waitForTrans.setVisible(true);
	}
	
		
	private static void processCompare(String fichero, String usuarioBBDD) throws IOException  {
		InputStream hola = Interfaz.class.getClassLoader().getResourceAsStream(fichero);
		
		if(hola != null) {
			InputStreamReader streamReader = new InputStreamReader(hola);
			BufferedReader in = new BufferedReader(streamReader);
			String line = in.readLine();
			
            while(line != null) {
            	if(!line.startsWith("--")) {
            		String lineaPartida = line.substring(line.indexOf("FROM ")).substring(line.substring(line.indexOf("FROM ")).indexOf(" ") + 1);
	            	String nombreTabla = lineaPartida.substring(0, lineaPartida.indexOf(" ORDER"));
	            	
	            	if(PanelTablas.listaTablasSeleccionadas.contains(nombreTabla)) {
	    				mapaColumnaNumeroDiscrepancias = new LinkedHashMap<String, Integer>();
	            		
	                	
	    				final JTable tablaActivos = crearTabla(nombreTabla);
	                	Map<String, Map<String, String>> mapaResultSetComparativo = mapaResultadosEntornos.get(PanelTablas.entornoReferencia).get(nombreTabla);
	                	Set<String> idsFilas = new LinkedHashSet<>(mapaResultadosEntornos.get(PanelTablas.entornoReferencia).get(nombreTabla).keySet());
	                	
	                	List<Map<String, Map<String, String>>> listaMapaResultSet = new ArrayList<Map<String,Map<String,String>>>();
	                	for(String entono: PanelTablas.listaEntornosSeleccionados) {
	                		listaMapaResultSet.add(mapaResultadosEntornos.get(entono).get(nombreTabla));
	                	}
	                	
	                	int numeroColumnas = 0;
	                	// recorremos cada fila de la tabla
	        			for (String idFila : idsFilas) {
	        				numeroColumnas = crearFilas(idFila, listaMapaResultSet, nombreTabla, 
	        						true, numeroColumnas, mapaResultSetComparativo, tablaActivos, usuarioBBDD, true);
	        			}
	        			
	        			// creamos una fila vacia para separar las discrepancias con las filas que no existen en EIN
	        			if(numeroColumnas == 0) {
	        				for(Map<String, Map<String, String>> mapaResultSet: listaMapaResultSet) {
	        					if(mapaResultSet.size() > 0) {
	        						Entry<String, Map<String, String>> entry = mapaResultSet.entrySet().iterator().next();
		        					String key = entry.getKey();
		        					numeroColumnas = mapaResultSet.get(key).size()>numeroColumnas?mapaResultSet.get(key).size():numeroColumnas;
		        					break;
	        					}
	        				}
	        			}
	        			boolean hayFilasAntesDeNoExisten = false;
	        			if(idsFilas.size() > 0) {
	        				hayFilasAntesDeNoExisten = true;
	        				crearFilaVacia(numeroColumnas, tablaActivos, null, nombreTabla);
	        			}
	        			List<String> listaIdsNoEncontrados = new ArrayList<String>();
	        			int contadorFilasNoExisten = 0;
	        			
	        			Map<String, Map<String, String>> mapaResultSetReferencia = mapaResultadosEntornos.get(PanelTablas.entornoReferencia).get(nombreTabla);
	        			for(Entry<String, Map<String, Map<String, Map<String, String>>>> mapaMapaResultadosEntorno: mapaResultadosEntornos.entrySet()) {
	        				String entorno = mapaMapaResultadosEntorno.getKey();
	        				if(!entorno.equals(PanelTablas.entornoReferencia)) {
	        					Map<String, Map<String, String>> mapaResultSet = mapaMapaResultadosEntorno.getValue().get(nombreTabla);
	        					contadorFilasNoExisten = compararFilasNoExisten(mapaResultSetReferencia, mapaResultSet, listaIdsNoEncontrados, listaMapaResultSet, 
	        							nombreTabla, true, numeroColumnas, tablaActivos, contadorFilasNoExisten, usuarioBBDD, hayFilasAntesDeNoExisten);
	        				}
	        			}
	        			
	        			if(tablaActivos.getColumnModel().getColumnCount() > numeroColumnas+1) {
	        				for(int i=0; i<numeroColumnasExtra-1; i++) {
	        					tablaActivos.getColumnModel().removeColumn(tablaActivos.getColumnModel().getColumn(0));
	        				}
	        			}
	        			
	        			if(tablaActivos.getRowCount() > 0) {
	        				if(idsFilas.size() == 0) {
		        				crearFilaVacia(numeroColumnas, tablaActivos, 0, nombreTabla);
		        			}
	        				
	        				if(contadorFilasNoExisten == 0) {
		        				((DefaultTableModel) tablaActivos.getModel()).removeRow(tablaActivos.getRowCount()-1);
		        			}
		        			
		        			for(int i=0; i<tablaActivos.getColumnCount(); i++) {
		        				tablaActivos.getColumnModel().getColumn(i).setCellRenderer(new CellRendererTablaComparacion());
		        			}
		    				
		        			mapaTablaColumnasDiscrepancias.put(nombreTabla, mapaColumnaNumeroDiscrepancias);
		    				
		    				tablaActivos.getModel().addTableModelListener(new TableModelListener() {
								@Override
								public void tableChanged(TableModelEvent e) {
									tablaActivos.repaint();
								}
							});
		    				tablaActivos.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		    				tablaActivos.addPropertyChangeListener(new PropertyChangeListener() {
								@Override
								public void propertyChange(PropertyChangeEvent evt) {
	    		        			if ("tableCellEditor".equals(evt.getPropertyName())) {
										if (tablaActivos.isEditing()) {
											String nombreColumna = tablaActivos.getColumnModel().getColumn(tablaActivos.getSelectedColumn()).getHeaderValue().toString();
											listaFilasAnterior = new ArrayList<Object[]>();
											for(int i=0; i<tablaActivos.getModel().getRowCount(); i++) {
												Object[] fila = new Object[tablaActivos.getModel().getColumnCount()];
												for(int j=0; j<tablaActivos.getModel().getColumnCount(); j++) {
													fila[j] = tablaActivos.getModel().getValueAt(i, j);
												}
												listaFilasAnterior.add(fila);
											}
											
											int row = tablaActivos.getSelectedRow();
											int column = tablaActivos.getSelectedColumn();
											String idTablaString = tablaActivos.getValueAt(row, 1).toString();
											String entornoSeleccionado = tablaActivos.getValueAt(row, 0).toString();
											boolean registroNoExisteEnEntornoReferencia = false;
											for(int i=tablaActivos.getSelectedRow(); i>=0; i--) {
												if(tablaActivos.getValueAt(i, 0).equals("   ")) {
													registroNoExisteEnEntornoReferencia = true;
													break;
												}
			    	    		    		}
											

											if(!registroNoExisteEnEntornoReferencia) {
												if(!idTablaString.equals("------") || entornoSeleccionado.equals(PanelTablas.entornoReferencia)) {
													// se est� modificando una fila existente
													String usuarioBBDD = tablaActivos.getModel().getValueAt(row, Constants.POSICION_USUARIO).toString();
													nombreTablaFKEncontrada = null;
													if(mapaTablaFKs.get(nombreTabla) != null) {
														for(Pair<String, String> par: mapaTablaFKs.get(nombreTabla)) {
															if(par.getValue().equals(nombreColumna)) {
																nombreTablaFKEncontrada = par.getKey();
																break;
															}
														}
													}
													
													if(nombreTablaFKEncontrada != null) {
														try {
															mostrarVentanaTablaFK(tablaActivos, nombreTabla, usuarioBBDD);
														} catch (Exception e) {
															
															e.printStackTrace();
														}
													}
												}else {
													// hemos pinchado en una fila vac�a
													Integer opcion = JOptionPane.showConfirmDialog(tablaActivos, "�Desea copiar la fila de referencia en esta fila?", "", JOptionPane.YES_NO_OPTION);
													filaReferenciaCopiada = opcion==0?true:false;
													if(opcion == 0) {
														nombreTablaFKEncontrada = null;
														int numeroRecorrer = PanelTablas.listaEntornosSeleccionados.size()-1;
														boolean esLimiteRecorrerArriba = tablaActivos.getModel().getValueAt(row, Constants.POSICION_ES_LIMITE).equals("1");
														int numeroQuedaPendienteRecorrer = numeroRecorrer;
														
														for(int i=1; i<=numeroRecorrer&&!esLimiteRecorrerArriba;i++) {
															int fila = row-i;
															String entornoRecorriendo = tablaActivos.getValueAt(fila, 0).toString();
															if(entornoRecorriendo.equals(PanelTablas.entornoReferencia)) {
																// coincide el entorno, hay que copiar la fila
																int numeroColumnasBBDD = tablaActivos.getColumnCount();
																for(int j=1; j<numeroColumnasBBDD; j++) {
																	String value = tablaActivos.getValueAt(fila, j).toString();
																	if(j == column) {
																		JTextField editor = (JTextField) tablaActivos.getEditorComponent();
																		editor.setText(value);
																	}else {
																		tablaActivos.setValueAt(value, row, j);
																	}
																}
																numeroQuedaPendienteRecorrer = 0;
																esLimiteRecorrerArriba = true;
															}else {
																esLimiteRecorrerArriba = tablaActivos.getModel().getValueAt(fila, Constants.POSICION_ES_LIMITE).equals("1");
																numeroQuedaPendienteRecorrer--;	
															}
														}
														for(int i=1; i<=numeroQuedaPendienteRecorrer; i++) {
															int fila = row+i;
															String entornoRecorriendo = tablaActivos.getValueAt(fila, 0).toString();
															if(entornoRecorriendo.equals(PanelTablas.entornoReferencia)) {
																// coincide el entorno, hay que copiar la fila
																int numeroColumnasBBDD = tablaActivos.getColumnCount();
																for(int j=1; j<numeroColumnasBBDD; j++) {
																	String value = tablaActivos.getValueAt(fila, j).toString();
																	if(j == column) {
																		JTextField editor = (JTextField) tablaActivos.getEditorComponent();
																		editor.setText(value);
																	}else {
																		tablaActivos.setValueAt(value, row, j);
																	}
																}
																numeroQuedaPendienteRecorrer = 0;
															}
														}
													}else {
														String usuarioBBDD = tablaActivos.getModel().getValueAt(row, Constants.POSICION_USUARIO).toString();
														nombreTablaFKEncontrada = null;
														
														if(mapaTablaFKs.get(nombreTabla) != null) {
															for(Pair<String, String> par: mapaTablaFKs.get(nombreTabla)) {
																if(par.getValue().equals(nombreColumna)) {
																	nombreTablaFKEncontrada = par.getKey();
																	break;
																}
															}
														}
														if(nombreTablaFKEncontrada != null) {
															try {
																mostrarVentanaTablaFK(tablaActivos, nombreTabla, usuarioBBDD);
															} catch (Exception e) {
																
																e.printStackTrace();
															}
														}
													}
												}	
											}else {
												try {
													Robot robot = new Robot();
													robot.keyPress(KeyEvent.VK_ENTER);
												} catch (AWTException e) {
													
													e.printStackTrace();
												}
											}
										} else {
											boolean esSeccionIdNoExiste = false;
											for(int i=tablaActivos.getSelectedRow(); i>=0; i--) {
												if(tablaActivos.getValueAt(i, 0).equals("   ")) {
													esSeccionIdNoExiste = true;
													break;
												}
			    	    		    		}
											
											if(!esSeccionIdNoExiste && nombreTablaFKEncontrada == null){
												finishEditing(tablaActivos, nombreTabla);
											}
										}
									}
								}
							});
	        			}
	            	}	
            	}
            	line = in.readLine();
            }
			in.close();
		}
	}
	
	
	public static void actualizarListaaFilasModificadas(JTable tablaActivos, String nombreTabla) {
		List<Object[]> listaFilasTabla = new ArrayList<Object[]>();
		if(mapaTablaFilasModificado.get(nombreTabla) != null) {
			listaFilasTabla = mapaTablaFilasModificado.get(nombreTabla);
		}
		
		for(int i=0; i<tablaActivos.getModel().getRowCount(); i++) {
			Object[] fila = new Object[tablaActivos.getModel().getColumnCount()];
			for(int j=0; j<tablaActivos.getModel().getColumnCount(); j++) {
				fila[j] = tablaActivos.getModel().getValueAt(i, j);
			}
			if(!tablaActivos.getModel().getValueAt(i, Constants.POSICION_NUMERO_FILA_REAL).equals("   ")) {
				int numeroFilaReal = Integer.parseInt(tablaActivos.getModel().getValueAt(i, Constants.POSICION_NUMERO_FILA_REAL).toString());
				listaFilasTabla.set(numeroFilaReal, fila);	
			}
		}
		mapaTablaFilasModificado.put(nombreTabla, listaFilasTabla);
	}
	
	
	public static void finishEditing(JTable tablaActivos, String nombreTabla) {
		int row = tablaActivos.getSelectedRow();
		int column = tablaActivos.getSelectedColumn();
		
		String keyMap = "" + row;
		String valorNuevo = tablaActivos.getValueAt(row, column).toString();
		String idTabla = tablaActivos.getValueAt(row, 1).toString();
		
		String valorEnTabla = tablaActivos.getValueAt(row, column).toString();
		String valorEnLista = (String) listaFilasAnterior.get(row)[column+numeroColumnasExtra-1];
		if(!valorEnTabla.equals(valorEnLista)) {
			if(idTabla.equals("------")) {
				// ponemos el id de la tabla en la fila que estamos modificando para asignarle el ID que hasta ahora no tiene
				int numeroRecorrer = PanelTablas.listaEntornosSeleccionados.size()-1;
				boolean esLimiteRecorrerArriba = tablaActivos.getModel().getValueAt(row, Constants.POSICION_ES_LIMITE).equals("1");
				int numeroQuedaPendienteRecorrer = numeroRecorrer;
				String idTablaFilaSeleccionada = "";
				
				for(int i=1; i<=numeroRecorrer&&!esLimiteRecorrerArriba;i++) {
					int fila = row-i;
					String entornoRecorriendo = tablaActivos.getValueAt(fila, 0).toString();
					if(entornoRecorriendo.equals(PanelTablas.entornoReferencia)) {
						// coincide el entorno, hay que copiar la fila
						idTablaFilaSeleccionada = tablaActivos.getValueAt(fila, 1).toString();
						numeroQuedaPendienteRecorrer = 0;
						numeroRecorrer = 0;
					}else {
						esLimiteRecorrerArriba = tablaActivos.getModel().getValueAt(fila, Constants.POSICION_ES_LIMITE).equals("1");
						numeroQuedaPendienteRecorrer--;	
					}
				}
				for(int i=1; i<=numeroQuedaPendienteRecorrer; i++) {
					int fila = row+i;
					String entornoRecorriendo = tablaActivos.getValueAt(fila, 0).toString();
					if(entornoRecorriendo.equals(PanelTablas.entornoReferencia)) {
						// coincide el entorno, hay que copiar la fila
						idTablaFilaSeleccionada = tablaActivos.getValueAt(fila, 1).toString();
					}
				}
				tablaActivos.setValueAt(idTablaFilaSeleccionada, row, 1);
			}
			
			if(!filaReferenciaCopiada) {
				String entorno = tablaActivos.getValueAt(row, 0).toString();
				if(!entorno.equals(PanelTablas.entornoReferencia)) {
					mapaTablaListaRehacer.remove(nombreTabla);
					List<Object[]> listaFilas = new ArrayList<Object[]>();
					for(Object[] fila: listaFilasAnterior) {
						listaFilas.add(fila);
					}
					
					List<CambioBean> listaDeshacer = mapaTablaListaDeshacer.get(nombreTabla)!=null?mapaTablaListaDeshacer.get(nombreTabla):new LinkedList<CambioBean>();
					CambioBean cambioBean = new CambioBean();
					cambioBean.setListaFilas(listaFilas);
					cambioBean.setSoloDiferencias(PanelComparacion.btnMostrarSoloDiferencias.isSelected());
					
					Map<String, FiltroBean> filtrosTabla = new HashMap<String, FiltroBean>();
					if(fotoActualTabla.get(nombreTabla) != null && fotoActualTabla.get(nombreTabla).getMapaColumnaFiltro() != null) {
						filtrosTabla = fotoActualTabla.get(nombreTabla).getMapaColumnaFiltro();
					}
					cambioBean.setMapaColumnaFiltro(filtrosTabla);
					listaDeshacer.add(cambioBean);
					mapaTablaListaDeshacer.put(nombreTabla, listaDeshacer);
					
					PanelComparacion.btnDeshacer.setEnabled(true);
					PanelComparacion.btnRehacer.setEnabled(false);
				}else {
					
					Map<String,Map<String, String>> mapaFilaColumnasDeshacer = new LinkedHashMap<String, Map<String,String>>();
					Map<String, String> mapaColumnaValorDeshacer2 = new LinkedHashMap<String, String>();
					mapaFilaColumnasDeshacer.put(keyMap, mapaColumnaValorDeshacer2);
					
					// se ha modificado la fila de referencia, mostramos mensaje para llevar los cambios al resto de entornos
					String mensa = "�Desea llevar el cambio al resto de entornos? En ese";
					String mensa2 = "caso seleccione los entornos donde desea copiarla";
					Set<String> entornoDondeCopiar = new HashSet<String>();
					
					JPanel prueba = new JPanel();
					for(String entornoRecorrer: PanelTablas.listaEntornosSeleccionados) {
						if(!entornoRecorrer.equals(PanelTablas.entornoReferencia)) {
							JCheckBox check = new JCheckBox();
							check.setText(entornoRecorrer);
							check.addItemListener(new ItemListener() {
								public void itemStateChanged(ItemEvent e) {
									if(check.isSelected()) {
										entornoDondeCopiar.add(check.getText());	
									}else {
										entornoDondeCopiar.remove(check.getText());
									}
								}
							});
							
							prueba.add(check);
						}
					}
					Object[] params = {mensa, mensa2, prueba};
					
					Integer opcion = JOptionPane.showConfirmDialog(tablaActivos, params, "", JOptionPane.YES_NO_OPTION);
					if(opcion == 0) {
						int numeroRecorrer = PanelTablas.listaEntornosSeleccionados.size()-1;
						boolean esLimiteRecorrerArriba = tablaActivos.getModel().getValueAt(row, Constants.POSICION_ES_LIMITE).equals("1");
						int numeroQuedaPendienteRecorrer = numeroRecorrer;
						String idTablaACopiar = tablaActivos.getValueAt(row, 1).toString();
						
						for(int i=1; i<=numeroRecorrer&&!esLimiteRecorrerArriba;i++) {
							int fila = row-i;
							String entornoRecorriendo = tablaActivos.getValueAt(fila, 0).toString();
							if(entornoDondeCopiar.contains(entornoRecorriendo)) {
								tablaActivos.setValueAt(valorNuevo, fila, column);
								tablaActivos.setValueAt(idTablaACopiar, fila, 1);
							}
							esLimiteRecorrerArriba = tablaActivos.getModel().getValueAt(fila, Constants.POSICION_ES_LIMITE).equals("1");
							numeroQuedaPendienteRecorrer--;
						}
						for(int i=1; i<=numeroQuedaPendienteRecorrer; i++) {
							int fila = row+i;
							String entornoRecorriendo = tablaActivos.getValueAt(fila, 0).toString();
							if(entornoDondeCopiar.contains(entornoRecorriendo)) {
								tablaActivos.setValueAt(valorNuevo, fila, column);
								tablaActivos.setValueAt(idTablaACopiar, fila, 1);
							}
						}
					}
					List<Object[]> listaFilas = new ArrayList<Object[]>();
					for(Object[] fila: listaFilasAnterior) {
						listaFilas.add(fila);
					}
					
					List<CambioBean> listaDeshacer = mapaTablaListaDeshacer.get(nombreTabla)!=null?mapaTablaListaDeshacer.get(nombreTabla):new LinkedList<CambioBean>();
					CambioBean cambioBean = new CambioBean();
					cambioBean.setListaFilas(listaFilas);
					cambioBean.setSoloDiferencias(PanelComparacion.btnMostrarSoloDiferencias.isSelected());
					
					Map<String, FiltroBean> filtrosTabla = new HashMap<String, FiltroBean>();
					if(fotoActualTabla.get(nombreTabla) != null && fotoActualTabla.get(nombreTabla).getMapaColumnaFiltro() != null) {
						filtrosTabla = fotoActualTabla.get(nombreTabla).getMapaColumnaFiltro();
					}
					cambioBean.setMapaColumnaFiltro(filtrosTabla);
					
					listaDeshacer.add(cambioBean);
					mapaTablaListaDeshacer.put(nombreTabla, listaDeshacer);
					
					PanelComparacion.btnDeshacer.setEnabled(true);
					PanelComparacion.btnRehacer.setEnabled(false);
				}
			}
		}
		
		if(filaReferenciaCopiada) {
			mapaTablaListaRehacer.remove(nombreTabla);
			
			List<Object[]> listaFilas = new ArrayList<Object[]>();
			for(Object[] fila: listaFilasAnterior) {
				listaFilas.add(fila);
			}
			
			List<CambioBean> listaDeshacer = mapaTablaListaDeshacer.get(nombreTabla)!=null?mapaTablaListaDeshacer.get(nombreTabla):new LinkedList<CambioBean>();
			CambioBean cambioBean = new CambioBean();
			cambioBean.setListaFilas(listaFilas);
			cambioBean.setSoloDiferencias(PanelComparacion.btnMostrarSoloDiferencias.isSelected());
			
			Map<String, FiltroBean> filtrosTabla = new HashMap<String, FiltroBean>();
			if(fotoActualTabla.get(nombreTabla) != null && fotoActualTabla.get(nombreTabla).getMapaColumnaFiltro() != null) {
				filtrosTabla = fotoActualTabla.get(nombreTabla).getMapaColumnaFiltro();
			}
			cambioBean.setMapaColumnaFiltro(filtrosTabla);
			listaDeshacer.add(cambioBean);
			mapaTablaListaDeshacer.put(nombreTabla, listaDeshacer);
			
			PanelComparacion.btnDeshacer.setEnabled(true);
			PanelComparacion.btnRehacer.setEnabled(false);
		}
		filaReferenciaCopiada = false;
		
		if(PanelComparacion.btnDeshacer.isEnabled()) {
			PanelComparacion.btnGenerarScript.setEnabled(true);	
		}else {
			PanelComparacion.btnGenerarScript.setEnabled(false);
		}
		actualizarListaaFilasModificadas(tablaActivos, nombreTabla);
	}
	
	
	private static void mostrarVentanaTablaFK(JTable tablaActivos, String nombreTabla, String usuarioBBDD) {
		try {
    		CardLayout c = (CardLayout)(Interfaz.contentPane.getLayout());
			c.show(Interfaz.contentPane, "panelTablasFK");
    		crearTablaFK(nombreTabla, usuarioBBDD);
    		
    		CardLayout cc = (CardLayout)(PanelTablasFK.jpanelTablasFK.getLayout());
			cc.show(PanelTablasFK.jpanelTablasFK, nombreTablaFKEncontrada);
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	private static void crearFilaVacia(int numeroColumnas, JTable tablaActivos, Integer posicionFila, String nombreTabla) {
		
		Object[] filaVacia = new Object[numeroColumnas+numeroColumnasExtra];
		for(int i=0; i<filaVacia.length; i++) {
			filaVacia[i] = "   ";
		}
		if(posicionFila != null) {
			((DefaultTableModel) tablaActivos.getModel()).insertRow(posicionFila, filaVacia);
		}else {
			((DefaultTableModel) tablaActivos.getModel()).addRow(filaVacia);
		}
	}
	
	
	private static int compararFilasNoExisten(Map<String, Map<String, String>> mapaResultSetReferencia, Map<String, Map<String, String>> mapaResultSetAComparar, List<String> listaIdsNoEncontrados, 
			List<Map<String, Map<String, String>>> listaMapaResultSet, String nombreTabla, boolean valorarDiscrepancias, int numeroColumnas, JTable tablaActivos, int contadorFilasNoExisten, String usuarioBBDD, boolean hayFilasAntesDeNoExisten) throws IOException {

		Map<String, FiltroBean> filtrosTabla = new HashMap<String, FiltroBean>();
		if(fotoActualTabla.get(nombreTabla) != null && fotoActualTabla.get(nombreTabla).getMapaColumnaFiltro() != null) {
			filtrosTabla = fotoActualTabla.get(nombreTabla).getMapaColumnaFiltro();
		}
		
		if(mapaResultSetAComparar != null) {
			for (String idFila : mapaResultSetAComparar.keySet()) {
				
				boolean cumpleFiltro = false;
				if(filtrosTabla.entrySet().size() == 0) {
					cumpleFiltro = true;
				}else {
					for(Entry<String, FiltroBean> mapafiltros: filtrosTabla.entrySet()) {
						if(mapaResultSetAComparar.get(idFila).get(mapafiltros.getKey()) != null) {
							if(mapaResultSetAComparar.get(idFila).get(mapafiltros.getKey()).toUpperCase().contains(mapafiltros.getValue().getFiltroTabla().toUpperCase())) {
								cumpleFiltro = true;
							}else {
								cumpleFiltro = false;
								break;
							}
						}
					}
				}
				
				if(cumpleFiltro) {
					if(mapaResultSetReferencia.get(idFila) == null){
						boolean encontrado = false;
						for(int i=0; i<listaIdsNoEncontrados.size() && !encontrado; i++){
							if(listaIdsNoEncontrados.get(i).equals(idFila)){
								encontrado = true;
							}
						}
						if(!encontrado){
							contadorFilasNoExisten++;
							listaIdsNoEncontrados.add(idFila);
							crearFilas(idFila, listaMapaResultSet, nombreTabla, valorarDiscrepancias, numeroColumnas, mapaResultSetAComparar, tablaActivos, usuarioBBDD, hayFilasAntesDeNoExisten);
						}
					}
				}
			}
		}
		return contadorFilasNoExisten;
	}
	
	
	private static int crearFilas(String idFila, List<Map<String, Map<String, String>>> listaMapaResultSet, String nombreTabla, boolean valorarDiscrepancias, int numeroColumnas, 
			Map<String, Map<String, String>> mapaResultSetRecorrer, JTable tablaActivos, String usuarioBBDD, boolean hayFilasAntesDeNoExisten) throws IOException{
		
		numeroColumnas = mapaResultSetRecorrer.get(idFila).size()>numeroColumnas?mapaResultSetRecorrer.get(idFila).size():numeroColumnas;
		int numeroFila = tablaActivos.getRowCount();
		if(!hayFilasAntesDeNoExisten) {
			numeroFila++;
		}
		
		int contador = 0;
		List<Object[]> listaFilasTablas = new ArrayList<Object[]>();
		for(String entono: PanelTablas.listaEntornosSeleccionados) {
    		Object[] filaTabla = new Object[mapaResultSetRecorrer.get(idFila).size()+numeroColumnasExtra];
			filaTabla[Constants.POSICION_TABLA] = nombreTabla;
			filaTabla[Constants.POSICION_ES_LIMITE] = contador==0?"1":"0";
			filaTabla[Constants.POSICION_USUARIO] = usuarioBBDD;
			filaTabla[Constants.POSICION_NUMERO_FILA_REAL] = String.valueOf(numeroFila);
			filaTabla[Constants.POSICION_ENTORNO] = entono;
			listaFilasTablas.add(filaTabla);
			contador++;
			numeroFila++;
    	}
		
		int numeroColumna = numeroColumnasExtra;
		for (Entry<String, String> mapaColumnaValor : mapaResultSetRecorrer.get(idFila).entrySet()) {
			Map<String, String> mapaEntornoValorColumna = new LinkedHashMap<String, String>();
			int recorriendo = 0;
			String valorEntornoReferencia = null;
			for(Map<String, Map<String, String>> mapaResultSet: listaMapaResultSet) {
				if(mapaResultSet != null) {
					Map<String, String> mapaColumnaValorEntorno = mapaResultSet.get(idFila);
					String valorColumna = mapaColumnaValorEntorno!=null?
							mapaColumnaValorEntorno.get(mapaColumnaValor.getKey())!=null?mapaColumnaValorEntorno.get(mapaColumnaValor.getKey()):
							"------":"------";
					String entorno = (String) listaFilasTablas.get(recorriendo)[Constants.POSICION_ENTORNO];
					mapaEntornoValorColumna.put(entorno, valorColumna);
					if(entorno.equals(PanelTablas.entornoReferencia)) {
						valorEntornoReferencia = valorColumna;
					}
				}
				recorriendo++;
			}
			
			if(valorarDiscrepancias){
				
				boolean discrepancia = false;
				for(Entry<String, String> entornoValorColumna: mapaEntornoValorColumna.entrySet()) {
					discrepancia = compararEntornos(valorEntornoReferencia, entornoValorColumna.getValue(), discrepancia,
							mapaColumnaValor.getKey(), PanelTablas.entornoReferencia, entornoValorColumna.getKey());
				}
				discrepancia = false;
			}
			
			recorriendo = 0;
			for(Entry<String, String> hola: mapaEntornoValorColumna.entrySet()) {
				listaFilasTablas.get(recorriendo)[numeroColumna] = hola.getValue();
				recorriendo++;
			}
			
			numeroColumna++;
		}
		
		for(Object[] fila: listaFilasTablas) {
			((DefaultTableModel) tablaActivos.getModel()).addRow(fila);
		}
		
		return numeroColumnas;
	}

	private static boolean compararEntornos(String valorColumna1, String valorColumna2, boolean discrepancia,
			String key, String entorno1, String entorno2) throws IOException {

		if((valorColumna1 == null && valorColumna2 != null) || 
				(valorColumna1 != null && valorColumna2 == null) ||
				(valorColumna1 != null && valorColumna2 != null && !valorColumna1.equals(valorColumna2))){
			
			if(!discrepancia){
				int numeroDiscrepancias = 0;
				if(mapaColumnaNumeroDiscrepancias.get(key) != null){
					numeroDiscrepancias = mapaColumnaNumeroDiscrepancias.get(key) + 1;
				}else{
					numeroDiscrepancias = 1;
				}
				mapaColumnaNumeroDiscrepancias.put(key, numeroDiscrepancias);
				discrepancia = true;
			}
		}else if(!discrepancia){
			if(mapaColumnaNumeroDiscrepancias.get(key) == null){
				mapaColumnaNumeroDiscrepancias.put(key, 0);
			}
		}
		return discrepancia;
	}

	private static void createSheetResumen() {
        
		JTable tablaResumen = PanelComparacion.tablaResumen;
		
        for(String nombreTabla: mapaTablaColumnasDiscrepancias.keySet()){
        	if(PanelTablas.listaTablasSeleccionadas.contains(nombreTabla)) {
        		Object[] filaTabla = new Object[3];
    			filaTabla[0] = "1";
    			filaTabla[1] = nombreTabla;
    			filaTabla[2] = "";
    			((DefaultTableModel) tablaResumen.getModel()).addRow(filaTabla);
    			int numeroFilaDiscrepanciasTotales = tablaResumen.getRowCount()-1;
    			
            	Map<String, Integer> mapaColumnaNumeroDiscrepancias = mapaTablaColumnasDiscrepancias.get(nombreTabla);
            	int contadorTotalDiscrepancias = 0;
            	for(String nombreColumna: mapaColumnaNumeroDiscrepancias.keySet()){
    				filaTabla = new Object[3];
    				filaTabla[0] = "0";
    				filaTabla[1] = nombreColumna;
    				filaTabla[2] = mapaColumnaNumeroDiscrepancias.get(nombreColumna);
    				((DefaultTableModel) tablaResumen.getModel()).addRow(filaTabla);
    				
    				contadorTotalDiscrepancias += mapaColumnaNumeroDiscrepancias.get(nombreColumna);
            	}
    			((DefaultTableModel) tablaResumen.getModel()).setValueAt(contadorTotalDiscrepancias, numeroFilaDiscrepanciasTotales, 2);
        	}
        }
	}
	
	private static void crearMapaResultados(List<String> listaFicherosTablas) throws ClassNotFoundException, SQLException, IOException {
		
		for(String entorno: PanelTablas.listaEntornosSeleccionados) {
			
			if(entorno.equals("EPR") && (usuarioEPR == null || passwordEPR == null)) {
				Connection.closeConnection();
				String mensa = "Introduzca usuario y contrase�a de ARCO";
				
				JPanel prueba = new JPanel();
				JLabel lblUser = new JLabel("Usuario");
				JLabel lblPass = new JLabel("Contrase�a");
				JTextField txtUser = new JTextField();
				txtUser.setColumns(10);
				JPasswordField txtPass = new JPasswordField();
				txtPass.setColumns(10);
				
				prueba.add(lblUser);
				prueba.add(txtUser);
				prueba.add(lblPass);
				prueba.add(txtPass);
				
				Object[] params = {mensa, prueba};
				
				JOptionPane.showMessageDialog(null, params);
				usuarioEPR = txtUser.getText();
				passwordEPR = new String(txtPass.getPassword());
			}else if(entorno.equals("EPR")) {
				//com.telefonica.utils.Connection.closeConnection();
				//showEPRMessage();
			}
			List<String> listaTablasEntorno = new ArrayList<String>();
			if(mapaEntornoTablas.get(entorno) != null) {
				listaTablasEntorno = mapaEntornoTablas.get(entorno);
			}
			
			Map<String, Map<String, Map<String, String>>> mapaResultados = new LinkedHashMap<String, Map<String, Map<String, String>>>();
			if(mapaResultadosEntornos.get(entorno) != null) {
				mapaResultados = mapaResultadosEntornos.get(entorno);
			}
			for(String fichero: listaFicherosTablas) {
				InputStream hola = Interfaz.class.getClassLoader().getResourceAsStream(fichero);
				
				if(hola != null) {
					String usuarioBBDD = fichero.substring(fichero.lastIndexOf("_") + 1);
					InputStreamReader streamReader = new InputStreamReader(hola);
					BufferedReader in = new BufferedReader(streamReader);
					String line = in.readLine();
					
					List<String> listaTablas = new ArrayList<String>();
					if(mapaAplicacionTablas.get(usuarioBBDD) != null) {
						listaTablas = mapaAplicacionTablas.get(usuarioBBDD);
					}
					
		            while(line != null) {
		            	if(!line.startsWith("--")) {
		            		String lineaPartida = line.substring(line.indexOf("FROM ")).substring(line.substring(line.indexOf("FROM ")).indexOf(" ") + 1);
			            	String nombreTabla = lineaPartida.substring(0, lineaPartida.indexOf(" ORDER"));
			            	if(PanelTablas.listaTablasSeleccionadas.contains(nombreTabla)) {
			            		final JTable tablaActivos = crearTabla(nombreTabla);
			            		
			            		if(mapaResultadosEntornos.get(entorno) == null || mapaResultadosEntornos.get(entorno).get(nombreTabla) == null) {
			            			Map<String, Map<String, String>> mapaResultSetEPR = getResults(line, entorno, usuarioBBDD, tablaActivos, nombreTabla, usuarioEPR, passwordEPR);
					            	mapaResultados.put(nombreTabla, mapaResultSetEPR);
					            	if(!listaTablas.contains(nombreTabla)) {
					            		listaTablas.add(nombreTabla);	
					            	}
					            	listaTablasEntorno.add(nombreTabla);
			            		}else {
			            			DefaultTableModel defaultTableModel = (DefaultTableModel) tablaActivos.getModel();
			            			if(defaultTableModel.getColumnCount() == 0) {
			            				defaultTableModel.addColumn("");
			            				tablaActivos.getColumnModel().getColumn(0).setPreferredWidth(0);
			            				tablaActivos.getColumnModel().getColumn(0).setMaxWidth(0);
			            				
			            				defaultTableModel.addColumn("");
			            				tablaActivos.getColumnModel().getColumn(1).setPreferredWidth(0);
			            				tablaActivos.getColumnModel().getColumn(1).setMaxWidth(0);

			            				defaultTableModel.addColumn("");
			            				tablaActivos.getColumnModel().getColumn(2).setMaxWidth(0);
			            				tablaActivos.getColumnModel().getColumn(2).setPreferredWidth(0);
			            				
			            				defaultTableModel.addColumn("");
			            				tablaActivos.getColumnModel().getColumn(3).setMaxWidth(0);
			            				tablaActivos.getColumnModel().getColumn(3).setPreferredWidth(0);


			            				defaultTableModel.addColumn("");
			            				tablaActivos.getColumnModel().getColumn(4).setMaxWidth(40);
			            				tablaActivos.getColumnModel().getColumn(4).setPreferredWidth(40);
			            				
			            				for(String columna: mapaTablaColumnas.get(nombreTabla)) {
			            					defaultTableModel.addColumn(columna);
			            				}
			            			}
			            		}
			            	}
		            	}
		    			line = in.readLine();
		            }
		            mapaAplicacionTablas.put(usuarioBBDD, listaTablas);
		            in.close();
		            hola.close();
				}
			}
			mapaResultadosEntornos.put(entorno, mapaResultados);
			mapaEntornoTablas.put(entorno, listaTablasEntorno);
		}
	}
	
	
	public static Map<String, Map<String, String>> getResults(String consulta, String entorno, String usuario, JTable tablaActivos, String nombreTabla, String user, String pass) throws ClassNotFoundException, SQLException {
		
		Map<String, String> mapaColumnaValor = null;
		java.sql.Connection con = Connection.getConnection(entorno, usuario, user, pass);
		
		List<String> listaColumnasPK = new ArrayList<String>();
		if(mapaTablaPKs.get(nombreTabla) == null){
			DatabaseMetaData meta = con.getMetaData();
			ResultSet rs = meta.getPrimaryKeys(null, null, nombreTabla);
			while(rs.next()){
		        listaColumnasPK.add(rs.getString("COLUMN_NAME"));
			}
			rs.close();
			
			mapaTablaPKs.put(nombreTabla, listaColumnasPK);	
		}else{
			listaColumnasPK = mapaTablaPKs.get(nombreTabla);
		}
		
		List<Pair<String, String>> listaColumnasFK = new ArrayList<Pair<String, String>>();
		if(mapaTablaFKs.get(nombreTabla) == null){
			DatabaseMetaData meta = con.getMetaData();
			ResultSet rs = meta.getImportedKeys(null, null, nombreTabla);
			while(rs.next()){
				String nombreTablaFK  = rs.getString(3); // primary key table name being imported
    			String nombreColumnaTabla = rs.getString(8); // foreign key column name
    			Pair<String, String> par = new Pair<String, String>(nombreTablaFK, nombreColumnaTabla);
		        
    			listaColumnasFK.add(par);
			}
			rs.close();
			
			mapaTablaFKs.put(nombreTabla, listaColumnasFK);	
		}
		
		ResultSet resultSet = con.createStatement().executeQuery(consulta);
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int numeroColumnas = rsmd.getColumnCount();
		Map<String, Map<String, String>> mapaResultSet = new LinkedHashMap<String, Map<String,String>>();
		
		if(tablaActivos != null) {
			DefaultTableModel defaultTableModel = (DefaultTableModel) tablaActivos.getModel();
			if(defaultTableModel.getColumnCount() == 0) {
				
				List<String> listaColumnas = new ArrayList<String>();
				
				defaultTableModel.addColumn("");
				tablaActivos.getColumnModel().getColumn(0).setPreferredWidth(0);
				tablaActivos.getColumnModel().getColumn(0).setMaxWidth(0);
				
				defaultTableModel.addColumn("");
				tablaActivos.getColumnModel().getColumn(1).setPreferredWidth(0);
				tablaActivos.getColumnModel().getColumn(1).setMaxWidth(0);

				defaultTableModel.addColumn("");
				tablaActivos.getColumnModel().getColumn(2).setMaxWidth(0);
				tablaActivos.getColumnModel().getColumn(2).setPreferredWidth(0);
				
				defaultTableModel.addColumn("");
				tablaActivos.getColumnModel().getColumn(3).setMaxWidth(0);
				tablaActivos.getColumnModel().getColumn(3).setPreferredWidth(0);


				defaultTableModel.addColumn("");
				tablaActivos.getColumnModel().getColumn(4).setMaxWidth(40);
				tablaActivos.getColumnModel().getColumn(4).setPreferredWidth(40);
				
				for(int i=1; i<=numeroColumnas; i++){
					
					boolean ignorarColumna = false;
					for(String columnaIgnorar: listaColumnasIgnorar) {
						if(!columnaIgnorar.startsWith("--") && rsmd.getColumnName(i).contains(columnaIgnorar)) {
							ignorarColumna = true;
							break;
						}
					}
					if(!ignorarColumna) {
						defaultTableModel.addColumn(rsmd.getColumnName(i));
						listaColumnas.add(rsmd.getColumnName(i));
					}
				}
				mapaTablaColumnas.put(nombreTabla, listaColumnas);
			}
		}
		
		while(resultSet.next()){
			mapaColumnaValor = new LinkedHashMap<String, String>();
			String tablaPK = "";
			for(int i=1; i<=numeroColumnas; i++){
				
				boolean ignorarColumna = false;
				for(String columnaIgnorar: listaColumnasIgnorar) {
					if(!columnaIgnorar.startsWith("--") && rsmd.getColumnName(i).contains(columnaIgnorar)) {
						ignorarColumna = true;
						break;
					}
				}
				if(!ignorarColumna) {
					if(rsmd.getColumnType(i) == Types.TIMESTAMP) {
						Timestamp valorFecha = resultSet.getTimestamp(i);
						String fechaFormateada = null;
						if(valorFecha != null) {
							fechaFormateada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorFecha);
						}
						mapaColumnaValor.put(rsmd.getColumnName(i), fechaFormateada);
    				}else if(rsmd.getColumnType(i) == Types.DATE) {
    					Date valorFecha = resultSet.getDate(i);
    					String fechaFormateada = null;
						if(valorFecha != null) {
							String pattern = "dd/MM/yyyy";
	    					DateFormat df = new SimpleDateFormat(pattern); 
	    					fechaFormateada = df.format(valorFecha);	
						}
    					mapaColumnaValor.put(rsmd.getColumnName(i), fechaFormateada);
    				}else {
    					mapaColumnaValor.put(rsmd.getColumnName(i), resultSet.getString(i));
    				}
					
					if(listaColumnasPK.contains(rsmd.getColumnName(i))){
						tablaPK += resultSet.getString(i) + Constants.SEPARADOR;
					}
				}
			}
			tablaPK = tablaPK.substring(0, tablaPK.length()-1);
			mapaResultSet.put(tablaPK, mapaColumnaValor);
		}
		resultSet.close();
		
		return mapaResultSet;
	}
	
	@SuppressWarnings("serial")
	private static JTable crearTabla(String nombreTabla) {
		
		for(Component component: PanelComparacion.cardLayout.getComponents()) {
			if(nombreTabla.equals(component.getName())) {
				JScrollPane scrollPane = (JScrollPane) component;
				JViewport viewport = scrollPane.getViewport();
				final JTable tablaActivosExistente = (JTable)viewport.getView();
				return tablaActivosExistente;
			}
		}
		
		JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 1000, 345);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setName(nombreTabla);
        
        final JTable tablaActivos = new JTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			//Implement table cell tool tips.           
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                tip = getValueAt(rowIndex, colIndex)!=null?getValueAt(rowIndex, colIndex).toString():"";

                return tip;
            }
		};
        
		tablaActivos.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		tablaActivos.setName(nombreTabla);
        tablaActivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaActivos.setBounds(0, 0, 250, 1);
		tablaActivos.setModel(new DefaultTableModel() {
    	    @Override
    	    public boolean isCellEditable(int row, int column) {
    	    	if(column < numeroColumnasExtra){
    	    		return false;
    	    	}else {
    	    		String nombreColumna = tablaActivos.getModel().getColumnName(column);
    	    		if(mapaTablaPKs.get(nombreTabla).size() == 1 &&  mapaTablaPKs.get(nombreTabla).contains(nombreColumna)){
    	    			return false;
    	    		}else if (mapaTablaPKs.get(nombreTabla).size() > 1 && column == (numeroColumnasExtra)){
    	    			return false;
    	    		}else{
    	    			return true;
    	    		}
    	    	}
    	    }
    	});
			
    	tablaActivos.getTableHeader().addMouseListener(new MouseAdapter() {
        	public void mouseClicked(java.awt.event.MouseEvent e) {
        		
            	String nombreTabla = PanelComparacion.lblNombreTabla.getText();
				if(!nombreTabla.equals("")) {
					nombreTabla = nombreTabla.substring(nombreTabla.indexOf(" ") + 1);
				}
				
				int columnaFiltrando = tablaActivos.columnAtPoint(e.getPoint());
                String nombreColumna = tablaActivos.getColumnName(columnaFiltrando);
				
                CambioBean cambioBeanActual = new CambioBean();
        		if(fotoActualTabla.get(nombreTabla) != null) {
        			//cambioBeanActual = fotoActualTabla.get(nombreTabla);
        			cambioBeanActual.getMapaColumnaFiltro().putAll(fotoActualTabla.get(nombreTabla).getMapaColumnaFiltro());
        		}else {
        			cambioBeanActual.setSoloDiferencias(PanelComparacion.btnMostrarSoloDiferencias.isSelected());
        		}
                
        		FiltroBean filtroBean = new FiltroBean();
        		Map<String, FiltroBean> filtrosTabla = new HashMap<String, FiltroBean>();
                if(cambioBeanActual.getMapaColumnaFiltro() != null) {
                	filtrosTabla.putAll(cambioBeanActual.getMapaColumnaFiltro());
                	if(cambioBeanActual.getMapaColumnaFiltro().get(nombreColumna) != null) {
                		filtroBean.setFiltroTabla(cambioBeanActual.getMapaColumnaFiltro().get(nombreColumna).getFiltroTabla());
                		filtroBean.setNombreColumnaFiltrada(cambioBeanActual.getMapaColumnaFiltro().get(nombreColumna).getNombreColumnaFiltrada());
                		filtroBean.setNumeroColumnaFiltrada(cambioBeanActual.getMapaColumnaFiltro().get(nombreColumna).getNumeroColumnaFiltrada());
                	}
                }
        		String filtroTablaActual = filtroBean.getFiltroTabla()!=null?filtroBean.getFiltroTabla():"";
        		CambioBean cambioBeanModificado = new CambioBean();
        		cambioBeanModificado.setSoloDiferencias(PanelComparacion.btnMostrarSoloDiferencias.isSelected());
            	
        		
        		String filtrando = "";
        		if(nombreColumna.contains("TI_CREATION") || nombreColumna.contains("TI_UPDATE") || 
        				nombreColumna.contains("_START_") || nombreColumna.contains("_END_") || nombreColumna.contains("_DATE")) {
        			
        			// se abre un popUp con una caja de texto de fecha
    				PopUpFiltroVigencia prueba;
					try {
						prueba = new PopUpFiltroVigencia(Interfaz.contentPane);
						Object[] params = { prueba};
	    				Integer opcion = JOptionPane.showConfirmDialog(Interfaz.frame, params, "Filtrar columna " + nombreColumna, JOptionPane.OK_CANCEL_OPTION);
	    				if(opcion == 0) {
	    					if(PopUpFiltroVigencia.fechaInicio == null && PopUpFiltroVigencia.fechaFin == null) {
	    						filtrando = "";
	    					}else {
	    						filtrando = PopUpFiltroVigencia.fechaInicio + "&&" + PopUpFiltroVigencia.fechaFin;
	    					}
	    				}else {
	    					filtrando = null;
	    				}
					} catch (SAXException | IOException | ParserConfigurationException e1) {
						
						e1.printStackTrace();
					}
        		}else {
        			filtrando = JOptionPane.showInputDialog("Filtrar columna " + nombreColumna, filtroTablaActual);
        		}
            	if(filtrando != null && !filtrando.equals(filtroTablaActual)) {

            		if(filtrando.equals("") || filtrando.equals("null&&null")) {
            			filtroBean.setNumeroColumnaFiltrada(-1);
            		}else {
            			filtroBean.setNumeroColumnaFiltrada(columnaFiltrando);
            		}
            		filtroBean.setNombreColumnaFiltrada(nombreColumna);
            		filtroBean.setFiltroTabla(filtrando);
            		filtrosTabla.put(nombreColumna, filtroBean);
                	
                	try {
                		cambioBeanModificado.setMapaColumnaFiltro(filtrosTabla);
                		fotoActualTabla.put(nombreTabla, cambioBeanModificado);
						filtrarTabla(tablaActivos, nombreTabla, PanelComparacion.btnMostrarSoloDiferencias.isSelected(), cambioBeanActual, cambioBeanModificado);
						for(int i=0; i<tablaActivos.getTableHeader().getColumnModel().getColumnCount(); i++) {
							tablaActivos.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(new HeaderCellRendererTablaComparacion(tablaActivos.getTableHeader()));
	        			}
						tablaActivos.repaint();
						tablaActivos.getTableHeader().repaint();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
            	}else if(filtrando != null ) {
            		
            		filtroBean.setNombreColumnaFiltrada(nombreColumna);
            		filtroTablaActual = filtrando.equals("")||filtrando.equals("null&&null")?null:filtrando;
            		//if(filtrando.equals("")) {
            		if(filtroTablaActual == null) {
            			filtroBean.setNumeroColumnaFiltrada(-1);
            		}
            		
            		filtrosTabla.put(nombreColumna, filtroBean);
            		cambioBeanModificado.setMapaColumnaFiltro(filtrosTabla);
            		fotoActualTabla.put(nombreTabla, cambioBeanModificado);
            	}
        	} 
    	});
    	
        scrollPane.setViewportView(tablaActivos);
        scrollPane.setVisible(false);
        PanelComparacion.cardLayout.add(scrollPane, nombreTabla);
        
        return tablaActivos;
	}
	
	
	public static void filtrarTabla(JTable tablaActivos, String nombreTabla, Boolean mostrarDiferencias, CambioBean cambioBeanActual, CambioBean cambioBeanModificado) throws IOException {
		
		List<Object[]> listaFilasAnterior = new ArrayList<Object[]>();
		int numeroFilas = tablaActivos.getModel().getRowCount();
		for(int i=0; i<numeroFilas; i++) {
			Object[] fila = new Object[tablaActivos.getModel().getColumnCount()];
			for(int j=0; j<tablaActivos.getModel().getColumnCount(); j++) {
				fila[j] = tablaActivos.getModel().getValueAt(0, j);
			}
			listaFilasAnterior.add(fila);
			((DefaultTableModel) tablaActivos.getModel()).removeRow(0);
		}
		List<CambioBean> listaDeshacer = mapaTablaListaDeshacer.get(nombreTabla)!=null?mapaTablaListaDeshacer.get(nombreTabla):new LinkedList<CambioBean>();
		
		cambioBeanActual.setListaFilas(listaFilasAnterior);
		listaDeshacer.add(cambioBeanActual);
		mapaTablaListaDeshacer.put(nombreTabla, listaDeshacer);
		PanelComparacion.btnDeshacer.setEnabled(true);
		PanelComparacion.btnRehacer.setEnabled(false);
		
		Map<String, FiltroBean> mapaTablaListaFiltros = cambioBeanModificado.getMapaColumnaFiltro();
		List<Object[]> listaFilasModificadas = mapaTablaFilasModificado.get(nombreTabla)!=null?mapaTablaFilasModificado.get(nombreTabla):mapaTablaFilasOriginal.get(nombreTabla);
		//List<Object[]> listaFilasOriginal = mapaTablaFilasOriginal.get(nombreTabla);
		for(int ii=0; ii<listaFilasModificadas.size(); ii++){
			int numeroColumnas = listaFilasModificadas.get(ii).length;
			cumpleDiferencia = false;
			
			String idTabla = (String) listaFilasModificadas.get(ii)[numeroColumnasExtra];
    		if(!idTabla.equals("   ")) {
				int numeroRecorrer = PanelTablas.listaEntornosSeleccionados.size()-1;
				int numeroQuedaPendienteRecorrer = numeroRecorrer;
				boolean esLimiteRecorrerArriba = listaFilasModificadas.get(ii)[Constants.POSICION_ES_LIMITE].equals("1");
				cumpleFiltroTotal = false;
				
				for(int i=1; i<=numeroRecorrer&&!esLimiteRecorrerArriba;i++) {
					int numeroColumnasCumpleFiltro = 0;
					int fila = ii-i;
					comprobarCumpleFiltro(numeroColumnasExtra, numeroColumnas, tablaActivos, listaFilasModificadas, fila, mapaTablaListaFiltros, numeroColumnasCumpleFiltro, ii);
					
					numeroQuedaPendienteRecorrer--;
					esLimiteRecorrerArriba = listaFilasModificadas.get(fila)[Constants.POSICION_ES_LIMITE].equals("1");
				}
				for(int i=0; i<=numeroQuedaPendienteRecorrer; i++) {
					int numeroColumnasCumpleFiltro = 0;
					int fila = ii+i;
					comprobarCumpleFiltro(numeroColumnasExtra, numeroColumnas, tablaActivos, listaFilasModificadas, fila, mapaTablaListaFiltros, numeroColumnasCumpleFiltro, ii);
				}
				
				boolean soloDiferencias = false;
				if(fotoActualTabla.get(nombreTabla) != null && fotoActualTabla.get(nombreTabla).isSoloDiferencias()){
					soloDiferencias = true;
				}
				
				if(cumpleFiltroTotal && (!soloDiferencias || (soloDiferencias && cumpleDiferencia))) {
					int numeroEmpezarCargar = ii;
					
					esLimiteRecorrerArriba = listaFilasModificadas.get(numeroEmpezarCargar)[Constants.POSICION_ES_LIMITE].equals("1");
					while(!esLimiteRecorrerArriba) {
						numeroEmpezarCargar--;
						esLimiteRecorrerArriba = listaFilasModificadas.get(numeroEmpezarCargar)[Constants.POSICION_ES_LIMITE].equals("1");
					}
					
					for(int k=numeroEmpezarCargar; k<numeroEmpezarCargar+PanelTablas.listaEntornosSeleccionados.size(); k++) {
						((DefaultTableModel) tablaActivos.getModel()).addRow(listaFilasModificadas.get(k));
					}
				}
				ii+=numeroQuedaPendienteRecorrer;
    		}else{
    			((DefaultTableModel) tablaActivos.getModel()).addRow(listaFilasModificadas.get(ii));
    		}
		}
		tablaActivos.repaint();
		tablaActivos.getTableHeader().repaint();
	}
	
	
	
	
	public static void filtrarTablaFK(JTable tablaActivos, String nombreTabla, Boolean mostrarDiferencias, CambioBean cambioBeanActual) throws IOException {
		
		int numeroFilas = tablaActivos.getModel().getRowCount();
		for(int i=0; i<numeroFilas; i++) {
			((DefaultTableModel) tablaActivos.getModel()).removeRow(0);
		}
		
		Map<String, FiltroBean> mapaTablaListaFiltros = cambioBeanActual.getMapaColumnaFiltro();
		List<Object[]> listaFilasFK = mapaTablaFilasFK.get(nombreTabla);
		for(int ii=0; ii<listaFilasFK.size(); ii++){
			int numeroColumnas = listaFilasFK.get(ii).length;
			cumpleDiferencia = false;
			cumpleFiltroTotal = false;
			int numeroColumnasCumpleFiltro = 0;
			comprobarCumpleFiltro(0, numeroColumnas, tablaActivos, listaFilasFK, ii, mapaTablaListaFiltros, numeroColumnasCumpleFiltro, ii);
			
			if(cumpleFiltroTotal) {
				((DefaultTableModel) tablaActivos.getModel()).addRow(listaFilasFK.get(ii));
			}
		}
		tablaActivos.repaint();
		tablaActivos.getTableHeader().repaint();
	}
	
	
	
	private static boolean comprobarCumpleFiltro(int numeroCoumnaEmpezar, int numeroColumnas, JTable tablaActivos, List<Object[]> listaFilasModificadas, int fila, Map<String, FiltroBean> mapaTablaListaFiltros, int numeroColumnasCumpleFiltro, int ii) {
		for(int j=numeroCoumnaEmpezar; j<numeroColumnas; j++) {
			String nombreColumna = tablaActivos.getModel().getColumnName(j);
			String valorColumna = (String) listaFilasModificadas.get(fila)[j];
			String filtroTabla = null;
			if(mapaTablaListaFiltros.get(nombreColumna) != null) {
				filtroTabla = mapaTablaListaFiltros.get(nombreColumna).getFiltroTabla();
			}
			
			if("".equals(filtroTabla)) {
				numeroColumnasCumpleFiltro++;
			}else {
				if(nombreColumna.contains("TI_CREATION") || nombreColumna.contains("TI_UPDATE") || 
	     				nombreColumna.contains("_START_") || nombreColumna.contains("_END_") || nombreColumna.contains("_DATE")) {
					
					if(filtroTabla != null) {
						if(valorColumna != null && !valorColumna.equals("------")) {
							
							String filtroFechaInicio = filtroTabla.split("&&")[0];
							String filtroFechaFin = filtroTabla.split("&&")[1];
							
							try {
								Date fechaBBDD = new SimpleDateFormat("dd/MM/yyyy").parse(valorColumna);
								
								if(filtroFechaInicio != null && !filtroFechaInicio.equals("") && !filtroFechaInicio.equals("null")) {
									Date fechaInicioFiltro = new SimpleDateFormat("yyyy-MM-dd").parse(filtroFechaInicio);
									if(fechaBBDD.after(fechaInicioFiltro) || fechaBBDD.equals(fechaInicioFiltro)) {
										//cumpleFiltro = true;
										if(filtroFechaFin != null && !filtroFechaFin.equals("") && !filtroFechaFin.equals("null")) {
											Date fechaFinFiltro = new SimpleDateFormat("yyyy-MM-dd").parse(filtroFechaFin);
											if(fechaBBDD.before(fechaFinFiltro) || fechaBBDD.equals(fechaFinFiltro)) {
												numeroColumnasCumpleFiltro++;
											}
										}else {
											numeroColumnasCumpleFiltro++;
										}
									}
								}else {
									if(filtroFechaFin != null && !filtroFechaFin.equals("") && !filtroFechaFin.equals("null")) {
										Date fechaFinFiltro = new SimpleDateFormat("yyyy-MM-dd").parse(filtroFechaFin);
										if(fechaBBDD.before(fechaFinFiltro) || fechaBBDD.equals(fechaFinFiltro)) {
											numeroColumnasCumpleFiltro++;
										}
									}else {
										numeroColumnasCumpleFiltro++;
									}
								}
							} catch (ParseException e) {
								// intentamos formatear para el tipo TimeStamp
								SimpleDateFormat dateFormatBBDD = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								SimpleDateFormat dateFormatFiltro = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
								try {
									Date fechaBBDD = dateFormatBBDD.parse(valorColumna);
									Timestamp timestampBBDD = new java.sql.Timestamp(fechaBBDD.getTime());
									
									if(filtroFechaInicio != null && !filtroFechaInicio.equals("") && !filtroFechaInicio.equals("null")) {
										Date fechaInicioFiltro = dateFormatFiltro.parse(filtroFechaInicio);
										Timestamp timestampInicioFiltro = new java.sql.Timestamp(fechaInicioFiltro.getTime());
										
										if(timestampBBDD.after(timestampInicioFiltro) || timestampBBDD.equals(timestampInicioFiltro)) {
											//cumpleFiltro = true;
											if(filtroFechaFin != null && !filtroFechaFin.equals("") && !filtroFechaFin.equals("null")) {
												Date fechaFinFiltro = dateFormatFiltro.parse(filtroFechaFin);
												Timestamp timestampFinFiltro = new java.sql.Timestamp(fechaFinFiltro.getTime());
												
												if(timestampBBDD.before(timestampFinFiltro) || timestampBBDD.equals(timestampFinFiltro)) {
													numeroColumnasCumpleFiltro++;
												}else{
												}
											}else {
												numeroColumnasCumpleFiltro++;
											}
										}
									}else {
										if(filtroFechaFin != null && !filtroFechaFin.equals("") && !filtroFechaFin.equals("null")) {
											Date fechaFinFiltro = dateFormatFiltro.parse(filtroFechaFin);
											Timestamp timestampFinFiltro = new java.sql.Timestamp(fechaFinFiltro.getTime());
											
											if(timestampBBDD.before(timestampFinFiltro) || timestampBBDD.equals(timestampFinFiltro)) {
												numeroColumnasCumpleFiltro++;
											}else{
											}
										}else {
											numeroColumnasCumpleFiltro++;
										}
									}
								} catch (ParseException e1) {
								}
							}
						}
					}else {
						numeroColumnasCumpleFiltro++;
					}
				}else {
					if(filtroTabla != null && valorColumna != null) {
						String[] arrayValoresFiltro = filtroTabla.split(",");
						for(String valorFiltro: arrayValoresFiltro) {
							if(valorColumna.toUpperCase().contains(valorFiltro.toUpperCase())) {
								numeroColumnasCumpleFiltro++;
								break;
							}
						}
					}else if(filtroTabla == null) {
						numeroColumnasCumpleFiltro++;								
					}
				}
			}
			
			String valorReferencia = (String) listaFilasModificadas.get(ii)[j];
			if(valorReferencia != null && valorColumna != null && !valorReferencia.equals(valorColumna)) {
				cumpleDiferencia = true;
			}
		}
		if(numeroColumnasCumpleFiltro == (numeroColumnas-numeroCoumnaEmpezar)) {
			cumpleFiltroTotal = true;
		}
		return cumpleDiferencia;
	}
	
	
	public static CambioBean llevarFotoActualADeshacer(JTable tablaActivos) {
		List<Object[]> listaFilasAnterior = new ArrayList<Object[]>();
		int numeroFilas = tablaActivos.getModel().getRowCount();
		for(int i=0; i<numeroFilas; i++) {
			Object[] fila = new Object[tablaActivos.getModel().getColumnCount()];
			for(int j=0; j<tablaActivos.getModel().getColumnCount(); j++) {
				fila[j] = tablaActivos.getModel().getValueAt(i, j);
			}
			listaFilasAnterior.add(fila);
		}
		List<CambioBean> listaDeshacer = ProcesadorTabla.mapaTablaListaDeshacer.get(tablaActivos.getName())!=null?ProcesadorTabla.mapaTablaListaDeshacer.get(tablaActivos.getName()):new LinkedList<CambioBean>();
		
		CambioBean cambioBeanActual = new CambioBean();
		if(ProcesadorTabla.fotoActualTabla.get(tablaActivos.getName()) != null) {
			cambioBeanActual.getMapaColumnaFiltro().putAll(ProcesadorTabla.fotoActualTabla.get(tablaActivos.getName()).getMapaColumnaFiltro());
		}
		cambioBeanActual.setSoloDiferencias(PanelComparacion.btnMostrarSoloDiferencias.isSelected());
		cambioBeanActual.setListaFilas(listaFilasAnterior);
		listaDeshacer.add(cambioBeanActual);
		ProcesadorTabla.mapaTablaListaDeshacer.put(tablaActivos.getName(), listaDeshacer);
		PanelComparacion.btnDeshacer.setEnabled(true);
		PanelComparacion.btnRehacer.setEnabled(false);
		
		return cambioBeanActual;
	}
	
	
	@SuppressWarnings("serial")
	private static JTable crearTablaFK(String nombreTabla, String usuarioBBDD) throws SQLException, IOException, ClassNotFoundException {
		
		PanelTablasFK.nombreTablaOrigen = nombreTabla;
		for(Component component: PanelTablasFK.jpanelTablasFK.getComponents()) {
			if(nombreTablaFKEncontrada.equals(component.getName())) {
				JScrollPane scrollPane = (JScrollPane) component;
				JViewport viewport = scrollPane.getViewport();
				final JTable tablaActivosExistente = (JTable)viewport.getView();
				return tablaActivosExistente;
			}
		}
		
		JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setName(nombreTablaFKEncontrada);
        
        final JTable tablaActivos = new JTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			//Implement table cell tool tips.           
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                tip = getValueAt(rowIndex, colIndex)!=null?getValueAt(rowIndex, colIndex).toString():"";

                return tip;
            }
		};
        
        tablaActivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	tablaActivos.setModel(new DefaultTableModel() {
    	    @Override
    	    public boolean isCellEditable(int row, int column) {
    	    	return false;
    	    }
    	});
    	
    	tablaActivos.getTableHeader().addMouseListener(new MouseAdapter() {
        	public void mouseClicked(java.awt.event.MouseEvent e) {
        		
            	String nombreTabla = PanelTablasFK.nombreTablaOrigen;
				int columnaFiltrando = tablaActivos.columnAtPoint(e.getPoint());
                String nombreColumna = tablaActivos.getColumnName(columnaFiltrando);
                
        		FiltroBean filtroBean = new FiltroBean();
        		Map<String, FiltroBean> filtrosTabla = new HashMap<String, FiltroBean>();
                if(fotoActualFK.getMapaColumnaFiltro() != null) {
                	filtrosTabla.putAll(fotoActualFK.getMapaColumnaFiltro());
                	if(fotoActualFK.getMapaColumnaFiltro().get(nombreColumna) != null) {
                		filtroBean.setFiltroTabla(fotoActualFK.getMapaColumnaFiltro().get(nombreColumna).getFiltroTabla());
                		filtroBean.setNombreColumnaFiltrada(fotoActualFK.getMapaColumnaFiltro().get(nombreColumna).getNombreColumnaFiltrada());
                		filtroBean.setNumeroColumnaFiltrada(fotoActualFK.getMapaColumnaFiltro().get(nombreColumna).getNumeroColumnaFiltrada());
                	}
                }
        		String filtroTablaActual = filtroBean.getFiltroTabla()!=null?filtroBean.getFiltroTabla():"";
        		
        		String filtrando = "";
        		if(nombreColumna.contains("TI_CREATION") || nombreColumna.contains("TI_UPDATE") || 
        				nombreColumna.contains("_START_") || nombreColumna.contains("_END_") || nombreColumna.contains("_DATE")) {
        			
        			// se abre un popUp con una caja de texto de fecha
    				PopUpFiltroVigencia prueba;
					try {
						prueba = new PopUpFiltroVigencia(Interfaz.contentPane);
						Object[] params = { prueba};
	    				Integer opcion = JOptionPane.showConfirmDialog(Interfaz.frame, params, "Filtrar columna " + nombreColumna, JOptionPane.OK_CANCEL_OPTION);
	    				if(opcion == 0) {
	    					if(PopUpFiltroVigencia.fechaInicio == null && PopUpFiltroVigencia.fechaFin == null) {
	    						filtrando = "";
	    					}else {
	    						filtrando = PopUpFiltroVigencia.fechaInicio + "&&" + PopUpFiltroVigencia.fechaFin;
	    					}
	    				}else {
	    					filtrando = null;
	    				}
					} catch (SAXException | IOException | ParserConfigurationException e1) {
						
						e1.printStackTrace();
					}
        		}else {
        			filtrando = JOptionPane.showInputDialog("Filtrar columna " + nombreColumna, filtroTablaActual);
        		}
            	if(filtrando != null && !filtrando.equals(filtroTablaActual)) {

            		if(filtrando.equals("") || filtrando.equals("null&&null")) {
            			filtroBean.setNumeroColumnaFiltrada(-1);
            		}else {
            			filtroBean.setNumeroColumnaFiltrada(columnaFiltrando);
            		}
            		filtroBean.setNombreColumnaFiltrada(nombreColumna);
            		filtroBean.setFiltroTabla(filtrando);
            		filtrosTabla.put(nombreColumna, filtroBean);
                	
                	try {
                		fotoActualFK.setMapaColumnaFiltro(filtrosTabla);
                		filtrarTablaFK(tablaActivos, nombreTabla, PanelComparacion.btnMostrarSoloDiferencias.isSelected(), fotoActualFK);
						for(int i=0; i<tablaActivos.getTableHeader().getColumnModel().getColumnCount(); i++) {
							tablaActivos.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(new HeaderCellRendererTablaFK(tablaActivos.getTableHeader()));
	        			}
						tablaActivos.repaint();
						tablaActivos.getTableHeader().repaint();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
            	}else if(filtrando != null ) {
            		
            		filtroBean.setNombreColumnaFiltrada(nombreColumna);
            		filtroTablaActual = filtrando.equals("")||filtrando.equals("null&&null")?null:filtrando;
            		if(filtroTablaActual == null) {
            			filtroBean.setNumeroColumnaFiltrada(-1);
            		}
            		
            		filtrosTabla.put(nombreColumna, filtroBean);
            		fotoActualFK.setMapaColumnaFiltro(filtrosTabla);
            	}
        	} 
    	});
    	
    	tablaActivos.getTableHeader().setReorderingAllowed(false);
    	tablaActivos.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    	
    	List<String> listaFicherosTablas = new ArrayList<String>();
		listaFicherosTablas.add("Tablas_completas/tablas_INFTER1");
		listaFicherosTablas.add("Tablas_completas/tablas_PRVTER1");
		listaFicherosTablas.add("Tablas_completas/tablas_TERCBS1");
		
		String consultaTablaFK = null;
		for(String fichero: listaFicherosTablas) {
			InputStream is = Interfaz.class.getClassLoader().getResourceAsStream(fichero);
			
			if(is != null) {
				InputStreamReader streamReader = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(streamReader);
				String line = in.readLine();
				
	            while(line != null) {
	            	if(line.contains(nombreTablaFKEncontrada)) {
	            		consultaTablaFK = line;
	            		break;
	            	}
	            	line = in.readLine();
	            }
	            in.close();
	            streamReader.close();
	            is.close();
			}
		}
    	
    	java.sql.Connection con = Connection.getConnection(PanelTablas.entornoReferencia, usuarioBBDD, usuarioEPR, passwordEPR);
		ResultSet resultSet = con.createStatement().executeQuery(consultaTablaFK);
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int numeroColumnas = rsmd.getColumnCount();
		
		DefaultTableModel defaultTableModel = (DefaultTableModel) tablaActivos.getModel();
		for(int i=1; i<=numeroColumnas; i++){
			defaultTableModel.addColumn(rsmd.getColumnName(i));
		}
		
		List<Object[]> listaFilas = new ArrayList<Object[]>();
		while(resultSet.next()){
			Object[] filaTabla = new Object[numeroColumnas];
			for(int i=1; i<=numeroColumnas; i++) {
				if(rsmd.getColumnType(i) == Types.TIMESTAMP) {
					Timestamp valorFecha = resultSet.getTimestamp(i);
					String fechaFormateada = null;
					if(valorFecha != null) {
						fechaFormateada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(valorFecha);
					}
					filaTabla[i-1] = fechaFormateada;
				}else if(rsmd.getColumnType(i) == Types.DATE) {
					Date valorFecha = resultSet.getDate(i);
					String fechaFormateada = null;
					if(valorFecha != null) {
						String pattern = "dd/MM/yyyy";
    					DateFormat df = new SimpleDateFormat(pattern); 
    					fechaFormateada = df.format(valorFecha);	
					}
					filaTabla[i-1] = fechaFormateada;
				}else {
					filaTabla[i-1] = resultSet.getString(i);
				}
			}
			((DefaultTableModel) tablaActivos.getModel()).addRow(filaTabla);
			listaFilas.add(filaTabla);
		}
		mapaTablaFilasFK.put(nombreTablaFKEncontrada, listaFilas);
		
		for(int i=0; i<tablaActivos.getColumnCount(); i++) {
			tablaActivos.getColumnModel().getColumn(i).setCellRenderer(new CellRendererTablaSeleccion());
		}
		resultSet.close();
		Connection.closeConnection();
    	
    	
        scrollPane.setViewportView(tablaActivos);
        scrollPane.setVisible(true);
        PanelTablasFK.jpanelTablasFK.add(scrollPane, nombreTablaFKEncontrada);
        
        return tablaActivos;
	}
	
	
	@SuppressWarnings("unused")
	private static void showEPRMessage(){
    	JOptionPane.showMessageDialog(null, "Con�ctate a ARCO, y cuando est�s listo, acepta.");
	}
	
	@SuppressWarnings("unused")
	private static void showNoEPRMessage(){
    	JOptionPane.showMessageDialog(null, "Con�ctate a ARU, y cuando est�s listo, acepta.");
	}
}