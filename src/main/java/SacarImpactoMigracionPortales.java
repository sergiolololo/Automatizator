import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SacarImpactoMigracionPortales {

    public static void main(String[] args) {
        File file = new File("O:\\git_repositories\\PROVISIONAL");



        for (File f : file.listFiles()) {
            if (f.isDirectory() && f.getName().contains("-REPO-DES")) {
                // lambda para imprimir número de archivos .jsp
                System.out.println(f.getName());
                for (File dir : f.listFiles()) {
                    if (dir.isDirectory() && dir.getName().contains("servidor")) {
                        System.out.println("   " + dir.getName());

                        List<String> listaJsp = new ArrayList<>();
                        List<String> listaJs = new ArrayList<>();
                        List<String> listaCss = new ArrayList<>();
                        List<String> listaHtml = new ArrayList<>();
                        List<String> listaHtm = new ArrayList<>();
                        List<String> listaJava = new ArrayList<>();

                        fetchFiles(listaJsp, listaJs, listaCss, listaHtml, listaHtm, listaJava, dir);
                        System.out.println("      JSP: " + listaJsp.size());
                        System.out.println("      JS: " + listaJs.size());
                        System.out.println("      CSS: " + listaCss.size());
                        System.out.println("      HTML: " + listaHtml.size());
                        System.out.println("      HTM: " + listaHtm.size());
                        System.out.println("      JAVA: " + listaJava.size());
                    }
                }



                /*Arrays.stream(f.listFiles()).filter(dir -> dir.getName().contains("servidor")).forEach(dir -> {
                    try (Stream<Path> paths = Files.walk(Paths.get(dir.getAbsolutePath()))) {

                        long jspFiles = paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".jsp")).count();
                        long jsFiles = paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".js")).count();
                        long cssFiles = paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".css")).count();
                        long htmlFiles = paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".html")).count();

                        System.out.println(dir.getName() + ": " + jspFiles);
                        System.out.println(dir.getName() + ": " + jsFiles);
                        System.out.println(dir.getName() + ": " + cssFiles);
                        System.out.println(dir.getName() + ": " + htmlFiles);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });*/
            }
        }
    }

    private static void fetchFiles(List<String> listaJsp, List<String> listaJs, List<String> listaCss, List<String> listaHtml, List<String> listaHtm, List<String> listaJava, File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    fetchFiles(listaJsp, listaJs, listaCss, listaHtml, listaHtm, listaJava, file);
                } else {
                    rellenarListas(listaJsp, listaJs, listaCss, listaHtml, listaHtm, listaJava, file);
                }
            }
        }else {
            rellenarListas(listaJsp, listaJs, listaCss, listaHtml, listaHtm, listaJava, dir);
        }
    }

    private static void rellenarListas(List<String> listaJsp, List<String> listaJs, List<String> listaCss, List<String> listaHtml, List<String> listaHtm, List<String> listaJava, File file) {
        if (file.getName().endsWith(".jsp")) {
            listaJsp.add(file.getName());
        } else if (file.getName().endsWith(".js")) {
            listaJs.add(file.getName());
        } else if (file.getName().endsWith(".css")) {
            listaCss.add(file.getName());
        } else if (file.getName().endsWith(".html")) {
            listaHtml.add(file.getName());
        } else if (file.getName().endsWith(".htm")) {
            listaHtm.add(file.getName());
        } else if (file.getName().endsWith(".java")) {
            try (Stream<String> lines = Files.lines(Paths.get(file.getAbsolutePath()), StandardCharsets.ISO_8859_1)) {
                if(lines.anyMatch(line -> line.contains("<script"))) {
                    listaJava.add(file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
