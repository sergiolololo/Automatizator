package com.telefonica.modulos.despliegues.pantalla;

import com.telefonica.interfaz.PantallaPrincipal;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class PanelConsola extends JDialog {

	@Serial
	private static final long serialVersionUID = 1L;
	
	public static JTextArea textTA;
	static JScrollPane sp;
	public static JButton closeBtn;
	private final JCheckBox lineWrapChk;

	public PanelConsola(ApplicationContext appContext) {
		super(appContext.getBean(PantallaPrincipal.class), true);

		setTitle("Consola de procesamiento");
		setSize(1550, 500);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(true);
		
		JPanel pane = new JPanel();
		BorderLayout bl = new BorderLayout();
		pane.setLayout(bl);
		setContentPane(pane);

		textTA = new JTextArea();
		textTA.setFont(new Font("Arial", Font.PLAIN, 12));
		textTA.setEditable(false);
		//textTA.append("Empieza el proceso de análisis de dependencias......\n\n");
		sp = new JScrollPane(textTA);
		getContentPane().add(sp, BorderLayout.CENTER);
		
		JPanel southPnl = new JPanel();
		southPnl.setLayout(new FlowLayout());

		// line wrap
		lineWrapChk = new JCheckBox("Ajuste de línea");
		lineWrapChk.setSelected(false);
		lineWrapChk.addActionListener(e -> textTA.setLineWrap(lineWrapChk.isSelected()));
		southPnl.add(lineWrapChk);

		// close button
		closeBtn = new JButton("Cerrar");
		closeBtn.addActionListener(e -> setVisible(false));
		southPnl.add(closeBtn);

		getContentPane().add(southPnl, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
	}
	
	public static void addText(String text) {
		textTA.append(text);
		sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());
		sp.getHorizontalScrollBar().setValue(sp.getHorizontalScrollBar().getMinimum());
	}
}