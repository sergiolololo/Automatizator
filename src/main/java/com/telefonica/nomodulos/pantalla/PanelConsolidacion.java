package com.telefonica.nomodulos.pantalla;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
public class PanelConsolidacion extends JPanel {
    private final JTextField txtPesp;
	private final JTextArea textAreaResultadoAnalisis;
	private final JTextField txtRutaInfa;
	private final JTextField txtRutaPrte;
	private final JTextField txtRutaTerc;
	private final Set<String> rutasCarpetasXSD = new HashSet<>();
	
	/**
	 * Create the panel.
	 */
	public PanelConsolidacion() {
		//setBounds(0, 0, 1144, 484);
		setBackground(SystemColor.inactiveCaption);
		setLayout(null);

        JPanel panelArriba = new JPanel();
		panelArriba.setBorder(new TitledBorder(new LineBorder(new Color(192, 192, 192), 2, true), "Creación directorio 2-DF, generar excel de carga UNVA y excel de asignación de DRS", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
		panelArriba.setBounds(0, 0, 1214, 658);
		panelArriba.setVisible(true);
		panelArriba.setLayout(null);
		add(panelArriba);

        JButton btnCrearEstructura = new JButton("EMPEZAR");
        btnCrearEstructura.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        		int opcion = mostrarMensaje();
		    	if(opcion == 0) {
		    		try {
		    			textAreaResultadoAnalisis.setText("");
		    			consolidar();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		    	}
        	}
        });
        btnCrearEstructura.setBounds(33, 103, 137, 33);
        panelArriba.add(btnCrearEstructura);

        JLabel lblRutaFicheroDependencias = new JLabel("PESP a consolidar");
        lblRutaFicheroDependencias.setBounds(33, 49, 146, 14);
        panelArriba.add(lblRutaFicheroDependencias);
        
        txtPesp = new JTextField();
        txtPesp.setColumns(10);
        txtPesp.setBounds(33, 73, 137, 20);
        panelArriba.add(txtPesp);
        
        textAreaResultadoAnalisis = new JTextArea();
        textAreaResultadoAnalisis.setLineWrap(true);
        textAreaResultadoAnalisis.setEditable(false);
        textAreaResultadoAnalisis.setBounds(23, 146, 1013, 405);
        
        
        JScrollPane sp = new JScrollPane(textAreaResultadoAnalisis);
        sp.setBounds(33, 146, 1130, 449);
        
        panelArriba.add(sp);
        
        JLabel lblRuta2DFInfa = new JLabel("* Ruta directorio 2-DF local INFA");
        lblRuta2DFInfa.setBounds(234, 49, 199, 14);
        panelArriba.add(lblRuta2DFInfa);
        
        txtRutaInfa = new JTextField();
        txtRutaInfa.setEnabled(false);
        txtRutaInfa.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		String directorio = seleccionarDirectorio();
        		txtRutaInfa.setText(directorio);
        		if(directorio.contains("INFA")) {
        			txtRutaPrte.setText(directorio.replace("INFA", "PRTE"));
        			txtRutaTerc.setText(directorio.replace("INFA", "TERC"));
        		}
        	}
        });
        txtRutaInfa.setColumns(10);
        txtRutaInfa.setBounds(234, 73, 137, 20);
        panelArriba.add(txtRutaInfa);
        
        JLabel lblRuta2DFPrte = new JLabel("* Ruta directorio 2-DF local PRTE");
        lblRuta2DFPrte.setBounds(450, 49, 199, 14);
        panelArriba.add(lblRuta2DFPrte);
        
        txtRutaPrte = new JTextField();
        txtRutaPrte.setEnabled(false);
        txtRutaPrte.setColumns(10);
        txtRutaPrte.setBounds(458, 73, 137, 20);
        panelArriba.add(txtRutaPrte);
        
        JLabel lblRuta2DFTerc = new JLabel("* Ruta directorio 2-DF local TERC");
        lblRuta2DFTerc.setBounds(659, 49, 219, 14);
        panelArriba.add(lblRuta2DFTerc);
        
        txtRutaTerc = new JTextField();
        txtRutaTerc.setEnabled(false);
        txtRutaTerc.setColumns(10);
        txtRutaTerc.setBounds(669, 73, 137, 20);
        panelArriba.add(txtRutaTerc);
	}
	
	
	private void consolidar() throws SAXException, IOException, ParserConfigurationException  {
		
		List<File> listaRutas = new ArrayList<>();
		listaRutas.add(new File(txtRutaInfa.getText()));
		listaRutas.add(new File(txtRutaPrte.getText()));
		listaRutas.add(new File(txtRutaTerc.getText()));
		
		for(File ruta: listaRutas){
			fetchFiles(ruta, txtPesp.getText());
			
			for(String stringRuta: rutasCarpetasXSD){
				File rutaBorrar = new File(stringRuta);
				deleteDir(rutaBorrar);	
			}
		}
	}
	
	
	public  void fetchFiles(File dir, String versionConsolidar) throws SAXException, IOException, ParserConfigurationException {

		try{
			if (dir.isDirectory()) {
				if(!dir.getAbsolutePath().contains("xsd") || dir.getAbsolutePath().contains(".xsd")){
					for (File file1 : dir.listFiles()) {
						fetchFiles(file1, versionConsolidar);
					}
				}else if(dir.getAbsolutePath().contains(versionConsolidar)){
					File directorioTrunk = new File(dir.getCanonicalPath() + "/../../../trunk/xsd");
					if(!directorioTrunk.exists()){
						directorioTrunk.mkdir();
					}
					for(File file: directorioTrunk.listFiles())
					    if (!file.isDirectory()) 
					        file.delete();
					for (File file1 : dir.listFiles()) {
						fetchFiles(file1, versionConsolidar);
					}
					
					File prueba = new File(dir.getCanonicalPath() + "/..");
					for(File carpeta: prueba.listFiles()){
						if(carpeta.isDirectory()){
							carpeta.delete();
						}
					}
				}
			} else {
				if(dir.getAbsolutePath().contains("branches")
					&& dir.getAbsolutePath().contains(versionConsolidar)
					&& (dir.getAbsolutePath().contains("doc") || dir.getAbsolutePath().contains("json") || dir.getAbsolutePath().contains("xmi") || dir.getAbsolutePath().contains("xsd"))){
		    		  
					//textAreaResultadoAnalisis.setText(textAreaResultadoAnalisis.getText()!=null&&!textAreaResultadoAnalisis.getText().equals("")?textAreaResultadoAnalisis.getText()+"\n----  " + dir.getAbsolutePath() + " ----":"---- " + dir.getAbsolutePath() + " ----");
					
					// estamos en la carpeta doc o xmi en el branch de la version a consolidar
					// tenemos que borrar de la carpeta trunk lo que haya dentro de doc y xmi,
					// y copiar lo que tenemos en el branch.
					// si hay más de un doc, se debe copiar solo el último
					if(dir.getAbsolutePath().contains("doc")){
						
						File directorioTrunk = new File(dir.getCanonicalPath() + "/../../../../trunk");
						if(!directorioTrunk.exists()){
							directorioTrunk.mkdir();
						}
						
						File directorioDocTrunk = new File(dir.getCanonicalPath() + "/../../../../trunk/doc");
						if(!directorioDocTrunk.exists()){
							directorioDocTrunk.mkdir();
						}
						
						// borramos el contenido de doc
						if(directorioDocTrunk.listFiles() != null){
							for(File file: directorioDocTrunk.listFiles())
							    if (!file.isDirectory() && !file.getAbsolutePath().contains("COMO")) 
							        file.delete();
						}
						
						rutasCarpetasXSD.add(dir.getCanonicalPath() + "/..");
						
						// copiamos de branch a trunk
						String nombreDTD = dir.getAbsolutePath().substring(dir.getAbsolutePath().lastIndexOf("\\") + 1);
						File directorioTrunk3 = new File(directorioDocTrunk.getCanonicalPath() + "\\" + nombreDTD);
						Files.copy(dir.toPath(), directorioTrunk3.toPath(), StandardCopyOption.REPLACE_EXISTING);
						
						// borramos la carpeta doc del branch
						deleteDir(dir);
						System.out.println("Consolidando " + nombreDTD);
						textAreaResultadoAnalisis.setText(textAreaResultadoAnalisis.getText()!=null&& !textAreaResultadoAnalisis.getText().isEmpty() ?textAreaResultadoAnalisis.getText()+"\nConsolidando " + nombreDTD:"Consolidando " + nombreDTD);
					}else if(dir.getAbsolutePath().contains("json")){
						
						File directorioTrunk = new File(dir.getCanonicalPath() + "/../../../../trunk");
						if(!directorioTrunk.exists()){
							directorioTrunk.mkdir();
						}
						
						File directorioDocTrunk = new File(dir.getCanonicalPath() + "/../../../../trunk/json");
						if(!directorioDocTrunk.exists()){
							directorioDocTrunk.mkdir();
						}
						
						// borramos el contenido de doc
						if(directorioDocTrunk.listFiles() != null){
							for(File file: directorioDocTrunk.listFiles())
							    if (!file.isDirectory()) 
							        file.delete();
						}
						
						//rutasCarpetasXSD.add(dir.getCanonicalPath() + "/..");
						
						if (!directorioDocTrunk.exists()){
							directorioDocTrunk.mkdir();
        			    }
						
						// copiamos de branch a trunk
						String nombreJson = dir.getAbsolutePath().substring(dir.getAbsolutePath().lastIndexOf("\\") + 1);
						File directorioTrunk3 = new File(directorioDocTrunk.getCanonicalPath() + "\\" + nombreJson);
						Files.copy(dir.toPath(), directorioTrunk3.toPath(), StandardCopyOption.REPLACE_EXISTING);
						
						// borramos la carpeta doc del branch
						deleteDir(dir);
						//System.out.println("Consolidando " + nombreJson);
						//textAreaResultadoAnalisis.setText(textAreaResultadoAnalisis.getText()!=null&&!textAreaResultadoAnalisis.getText().equals("")?textAreaResultadoAnalisis.getText()+"\nConsolidando " + nombreJson:"Consolidando " + nombreJson);
					}else if(dir.getAbsolutePath().contains("xmi") || dir.getAbsolutePath().contains("xsd")){
						
						File directorioTrunk1 = new File(dir.getCanonicalPath() + "/../../../../trunk");
						if(!directorioTrunk1.exists()){
							directorioTrunk1.mkdir();
						}
						
						File directorioTrunk;
						if(dir.getAbsolutePath().contains("xmi")){
							directorioTrunk = new File(dir.getCanonicalPath() + "/../../../../trunk/xmi");
						}else{
							directorioTrunk = new File(dir.getCanonicalPath() + "/../../../../trunk/xsd");
						}
						
						if(!directorioTrunk.exists()){
							directorioTrunk.mkdir();
						}
						
						//rutasCarpetasXSD.add(dir.getCanonicalPath() + "/..");
						
						String nombre1 = dir.getAbsolutePath().substring(dir.getAbsolutePath().lastIndexOf("\\") + 1);
						File directorioTrunk3 = new File(directorioTrunk.getCanonicalPath() + "\\" + nombre1);
						Files.copy(dir.toPath(), directorioTrunk3.toPath(), StandardCopyOption.REPLACE_EXISTING);
						
						// borramos la carpeta xmi de branch
						deleteDir(dir);
						//System.out.println("Consolidando " + nombre1);
						//textAreaResultadoAnalisis.setText(textAreaResultadoAnalisis.getText()!=null&&!textAreaResultadoAnalisis.getText().equals("")?textAreaResultadoAnalisis.getText()+"\nConsolidando " + nombre1:"Consolidando " + nombre1);
					}
				}
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	
	private void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    }
	    file.delete();
	}
	
	
	private String seleccionarDirectorio() {
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setAcceptAllFileFilterUsed(false);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    
	    String directorio = "";
	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	// Write the output to a file
	    	directorio = chooser.getSelectedFile().getAbsolutePath();
	    }
	    return directorio;
	}
	
	
	private int mostrarMensaje() {
		return JOptionPane.showConfirmDialog(this, "Se va a realizar la operación.\n¿Está seguro de que quiere continuar?", "", JOptionPane.YES_NO_OPTION);
	}
}