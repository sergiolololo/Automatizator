import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EstructuraProyectos {

    public static void main(String[] args) {
        File file = new File("O:\\git_repositories\\V0-REPO-DES");
        try (Stream<Path> paths = Files.walk(Paths.get(file.getAbsolutePath()))) {
            paths.filter(Files::isDirectory)
                    .filter(path -> !path.toString().contains(".git") && !path.toString().contains("bin") && !path.toString().contains("out"))
                    .forEach(path -> {
                        int level = path.getNameCount() - Paths.get(file.getAbsolutePath()).getNameCount();
                        System.out.println("   ".repeat(level) + path.getFileName());
                    });
        } catch (IOException e) {
            e.printStackTrace();
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
