package net.glowstone.remapper;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * The action to execute for the remap task.
 */
public class RemapperAction implements Action<Task> {

    @Override
    public void execute(Task task) {
        final Project project = task.getProject();
        final RemapperExtension ext = (RemapperExtension) project.getExtensions().getByName("remapper");
        ext.validate();

        final List<Mapping> mappings = readMappings(project.file(ext.getMappingFile()));
        System.out.println("Mappings: " + mappings);
        System.out.println("Target jar: " + ext.getTargetJar());
    }

    /**
     * Read the list of mappings from a file.
     */
    private List<Mapping> readMappings(File file) {
        List<Mapping> result = new LinkedList<>();

        try (FileInputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader buf = new BufferedReader(reader)
        ) {
            while (true) {
                String line = buf.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] split = line.split("[ \t]+");
                if (split.length != 4) {
                    throw new IllegalArgumentException("Error parsing mapping: \"" + split.length + "\"");
                }

                result.add(new Mapping(split[0], split[1], split[2], split[3]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
