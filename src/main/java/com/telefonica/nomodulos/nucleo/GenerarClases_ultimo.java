package com.telefonica.nomodulos.nucleo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class GenerarClases_ultimo {
	
	// *************** La siguientes variables se tienen que adaptar a quien ejecute la clase ************* //
	
	private static String aplicacion = "infa";
	private static String servicio = "SPMessageMng";
	private static String servicioLocal = "SPMessageMngLocal";
	
	private static String rutaRaizServicio = "C:\\Users\\sherrerah\\Desktop\\MigracionOS4_2306_CODI\\INFA\\0_AT\\srv-nuc-jee\\srv-nuc-jee-SPMessageMng\\tags\\5.13.0-1-1";
	
	// *************************************************************************************************** //
	
	
	
	// ****** Las siguientes variables no deberian tocarse. Como mucho la ruta de las 3 plantillas en caso de no encontrarse en la raiz del servicio ***** //
	
	private static String servicioMinuscula = servicio.toLowerCase();
	private static String servicioMixto = servicio.substring(0, 1).toLowerCase() + servicio.substring(1);
	private static String servicioLocalMinuscula = servicioLocal.toLowerCase();
	private static String servicioLocalMixto = servicioLocal.substring(0, 1).toLowerCase() + servicioLocal.substring(1);
	
	private static String rutaCarpetaSrvMainJavaService = rutaRaizServicio + "\\src\\main\\java\\com\\telefonica\\" + aplicacion + "\\srv\\nuc\\" + servicioMinuscula + "\\service";
	private static String rutaCarpetaSrvMainJavaMsg = rutaRaizServicio + "\\src\\main\\java\\com\\telefonica\\" + aplicacion + "\\srv\\nuc\\" + servicioMinuscula + "\\msg";
	private static String rutaCarpetaSrvTestJava = rutaRaizServicio + "\\src\\test\\java\\com\\telefonica\\" + aplicacion + "\\srv\\nuc\\" + servicioMinuscula + "\\service";
	
	private static String rutaPlantillaCommand = rutaRaizServicio + "\\plantillaClaseCommand.java";
	private static String rutaPlantillaMapper = rutaRaizServicio + "\\plantillaClaseMapper.java";
	private static String rutaPlantillaCommandTest = rutaRaizServicio + "\\plantillaClaseTest.java";
	
	// *************************************************************************************************************************************************** //
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException {
		//generarClaseMapperAbstract("PARTE_CLASES_ABSTRASCTAS");
		fetchFiles(new File(rutaRaizServicio));
    }
	
	private static void fetchFiles(File dir) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException {

		if (dir.isDirectory()) {
			for (File file1 : dir.listFiles()) {
				fetchFiles(file1);
			}
		} else {
			if(dir.getAbsolutePath().contains(rutaCarpetaSrvMainJavaService)) {
	    		if(dir.getAbsolutePath().contains("Command.java")) {
	    			generarClases(dir, "Command", rutaPlantillaCommand);
	    		}else if(dir.getAbsolutePath().contains("Mapper.java")) {
	    			generarClases(dir, "Mapper", rutaPlantillaMapper);
	    		}
			}else if(dir.getAbsolutePath().contains(rutaCarpetaSrvTestJava)) {
				if(dir.getAbsolutePath().contains("CommandTest.java")) {
					generarClases(dir, "CommandTest", rutaPlantillaCommandTest);
				}
			}
		}
	}
	
	private static void generarClases(File dir, String tipoClase, String rutaPlantilla) throws IOException, ClassNotFoundException {
		String operacionMayuscula = dir.getAbsolutePath().substring(dir.getAbsolutePath().lastIndexOf("\\") + 1, dir.getAbsolutePath().lastIndexOf(tipoClase));
		String operacionMinuscula = operacionMayuscula.toLowerCase();
		String operacionMixta = operacionMayuscula.substring(0, 1).toLowerCase() + operacionMayuscula.substring(1);
		
		generarClase(dir, rutaPlantilla, operacionMayuscula, operacionMinuscula, operacionMixta, tipoClase);
	}
	
	private static void generarClase(File dir, String rutaPlantilla, String operacionMayuscula, String operacionMinuscula, String operacionMixta, String tipoClase) throws IOException, ClassNotFoundException {
		Path pathPlantilla = Paths.get(rutaPlantilla);
		String content = new String(Files.readAllBytes(pathPlantilla));
		content = content.replaceAll("APLICACION", aplicacion);
		content = content.replaceAll("SERVICIO_MINUSCULA", servicioMinuscula);
		content = content.replaceAll("SERVICIO_MIXTO", servicioMixto);
		content = content.replaceAll("OPERACION_MAYUSCULA", operacionMayuscula);
		content = content.replaceAll("OPERACION_MINUSCULA", operacionMinuscula);
		content = content.replaceAll("OPERACION_MIXTO", operacionMixta);
		content = content.replaceAll("SERVICIO_LOCAL_MAYUSCULA", servicioLocal);
		content = content.replaceAll("SERVICIO_LOCAL_MINUSCULA", servicioLocalMinuscula);
		content = content.replaceAll("SERVICIO_LOCAL_MIXTO", servicioLocalMixto);
		
		Path pathTest = Paths.get(dir.getAbsolutePath());
		Files.write(pathTest, content.getBytes());
		
		if(tipoClase.equals("Mapper")) {
			List<String> lines = generarClaseMapperAbstract(content, operacionMayuscula, operacionMinuscula);
			Files.write(pathTest, lines, StandardOpenOption.APPEND);
		}
	}
	
	private static List<String> generarClaseMapperAbstract(String content, String operacionMayuscula, String operacionMinuscula) throws ClassNotFoundException, IOException {
		
		File dir = new File(rutaCarpetaSrvMainJavaMsg + "\\" + operacionMinuscula);
		List<String> lines = new ArrayList<String>();
		List<String> lines2 = new ArrayList<String>();
		Map<Class<?>, List<Class<?>>> abstractClassMap = new HashMap<>();
		List<Class<?>> classList = new ArrayList<>();
		for (File file : dir.listFiles()) {
			if(!file.isDirectory()) {
				String packagePath = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf("com\\telefonica"), file.getAbsolutePath().lastIndexOf("."));
				packagePath = packagePath.replace("\\", ".");
				
				Class<?> classMsg = Class.forName(packagePath);
				if(classMsg.getSuperclass() != null && classMsg.getSuperclass() != Object.class){
					
					classList = abstractClassMap.get(classMsg.getSuperclass())!=null?abstractClassMap.get(classMsg.getSuperclass()):new ArrayList<>();
					classList.add(classMsg);
					abstractClassMap.put(classMsg.getSuperclass(), classList);
					
					lines.addAll(Arrays.asList(
							"",
							"	com.telefonica." + aplicacion + ".srv.nuc." + servicioLocalMinuscula + ".msg." + operacionMinuscula + "." + classMsg.getSimpleName() + " mapTo" + classMsg.getSimpleName() + "(",
							"			com.telefonica." + aplicacion + ".srv.nuc." + servicioMinuscula + ".msg." + operacionMinuscula + "." + classMsg.getSimpleName() + " " + classMsg.getSimpleName() + ");"
							));
					
					lines2.addAll(Arrays.asList(
							"",
							"		if (" + classMsg.getSuperclass().getSimpleName() + " instanceof " + classMsg.getName() + ")",
							"			return mapTo" + classMsg.getSimpleName() + "((" + classMsg.getName() + ") " + classMsg.getSuperclass().getSimpleName() + ");"
							));
				}
			}
		}
		
		for(Entry<Class<?>, List<Class<?>>> abstractClass: abstractClassMap.entrySet()) {
			lines.addAll(Arrays.asList(
					"",
					"	default com.telefonica." + aplicacion + ".srv.nuc." + servicioLocalMinuscula + ".msg." + operacionMinuscula + "." + abstractClass.getKey().getSimpleName() + " mapTo" + abstractClass.getKey().getSimpleName() + "(",
					"			com.telefonica." + aplicacion + ".srv.nuc." + servicioMinuscula + ".msg." + operacionMinuscula + "." + abstractClass.getKey().getSimpleName() + " " + abstractClass.getKey().getSimpleName() + ") {",
					"		if (" + abstractClass.getKey().getSimpleName() + " == null) {",
					"			return null;",
					"		}"
					));
			
			for(Class<?> classChild: abstractClass.getValue()) {
				/*lines.addAll(Arrays.asList(
						"",
						"		if (" + abstractClass.getKey().getSimpleName() + " instanceof " + classChild.getName() + ")",
						"			return mapTo" + classChild.getSimpleName() + "((" + classChild.getName() + ") " + abstractClass.getKey().getSimpleName() + ");"
						));*/
			}
			lines.addAll(Arrays.asList(
					"",
					"		throw new com.telefonica.coco.core.domain.exceptions.FunctionalException(\"INFA-XXXXX\", \"Nunca debería pasar por aquí, porque ya se valida esto antes\");",
					"	}"
					));
		}
		
		
		lines.add("}");
		return lines;
	}
}