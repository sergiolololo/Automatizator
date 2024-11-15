package com.telefonica.interfaz;

import com.telefonica.modulos.despliegues.pantalla.PanelDespliegues;
import com.telefonica.modulos.despliegues.utils.BackgroundPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

@Service
public class PantallaPrincipal extends JFrame {

	@Autowired
	private PanelDespliegues panelDespliegues;
	
	private JPanel contentPane;

    public PantallaPrincipal() {
	}
	
	public void init() throws IOException, ParseException  {
		
		addWindowListener(new WindowAdapter() {
        });
		setTitle("Automatizator");
		
		BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("images/tools_terminal_rabbit_12989.png")));
		setIconImage(image);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setShape(new RoundRectangle2D.Double(0, 0, 1221, 716, 30, 30));
		setResizable(false);
		setSize(1221, 716);
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setVisible(true);
		setJMenuBar(menuBar);

        JMenu menu = new JMenu("Menú");
		menuBar.add(menu);

		JMenuItem menuDespliegues = new JMenuItem("Despliegues");
		menuDespliegues.addActionListener(evt -> {
			setShape(new RoundRectangle2D.Double(0, 0, 1120, 750, 30, 30));
			setSize(1120, 750);
			setResizable(false);
			setLocationRelativeTo(null);
			CardLayout c = (CardLayout)(contentPane.getLayout());
			c.show(contentPane, "panelDespliegues");
		});
		menu.add(menuDespliegues);
		
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		contentPane = new JPanel();
		getContentPane().add(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));

		BackgroundPanel panelVacio = new BackgroundPanel("images/automation2.png");
		contentPane.add(panelVacio, "panelVacio");
		contentPane.add(panelDespliegues, "panelDespliegues");
		
		changeFont(this, new Font("Arial", Font.PLAIN, 12));
		changeFontMenu(menu, new Font("Arial", Font.BOLD, 16));
		
		UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 12));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 12));
        UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 12));
	}
	
	private static void changeFont(Component component, Font font) {
	    component.setFont(font);
	    if (component instanceof Container) {
	        for (Component child : ((Container)component).getComponents()) {
	            changeFont(child, font);
	        }
	    }
	}
	
	private static void changeFontMenu(Component component, Font font) {
		component.setFont(font);
	    if(component instanceof JMenu) {
	    	for (int i=0; i<((JMenu)component).getItemCount(); i++) {
	    		((JMenu)component).getItem(i).setFont(new Font("Arial", Font.BOLD, 12));
	        }
	    }
	}
}