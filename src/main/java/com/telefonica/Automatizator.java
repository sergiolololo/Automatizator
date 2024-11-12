package com.telefonica;

import java.io.IOException;
import java.text.ParseException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.xml.sax.SAXException;

import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import com.telefonica.interfaz.PantallaPrincipal;

@SpringBootApplication
@PropertySources({
    @PropertySource("classpath:url.properties"),
    @PropertySource("classpath:guiaImpactos"),
    @PropertySource("classpath:conexiones.properties")
})
public class Automatizator {

	/**
	 * Launch the application.
     */
	public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException, SAXException, ParserConfigurationException, ParseException {
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