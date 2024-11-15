package com.telefonica;

import java.io.IOException;
import java.text.ParseException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import com.telefonica.interfaz.PantallaPrincipal;

@SpringBootApplication
public class Automatizator {

	public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException, ParseException {
		UIManager.setLookAndFeel(new AluminiumLookAndFeel());
		
		ApplicationContext context = new SpringApplicationBuilder(Automatizator.class).headless(false).run(args);
	    PantallaPrincipal appFrame = context.getBean(PantallaPrincipal.class);
	    String[] names = context.getBeanDefinitionNames();
	    for(String name : names){
            System.out.println("-----------------");
            System.out.println(name);
        }

	    appFrame.init();
	    appFrame.setLocationRelativeTo(null);
	    appFrame.setVisible(true);
	}
}