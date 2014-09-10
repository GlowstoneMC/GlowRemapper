package net.glowstone.remapper;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
        final File jarFile = project.file(ext.getTargetJar());
        final File tempFile = new File(jarFile.getPath().replace(".jar", "-remap.jar"));

        // build class and interface inheritance map
        InheritanceMap parents = new InheritanceMap();
        try (FileInputStream fileIn = new FileInputStream(jarFile);
             ZipInputStream zipIn = new ZipInputStream(fileIn)
        ) {
            ZipEntry entryIn;
            while ((entryIn = zipIn.getNextEntry()) != null) {
                String name = entryIn.getName();
                if (name.endsWith(".class") && !entryIn.isDirectory()) {
                    parents.readClass(zipIn);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // perform transformation
        try (FileInputStream fileIn = new FileInputStream(jarFile);
             ZipInputStream zipIn = new ZipInputStream(fileIn);
             FileOutputStream fileOut = new FileOutputStream(tempFile);
             ZipOutputStream zipOut = new ZipOutputStream(fileOut)
        ) {
            ZipEntry entryIn;
            while ((entryIn = zipIn.getNextEntry()) != null) {
                ZipEntry entryOut = new ZipEntry(entryIn);
                String name = entryIn.getName();

                if (name.endsWith(".class") && !entryIn.isDirectory()) {
                    // perform class transformation
                    byte[] data = transform(zipIn, parents, mappings);
                    entryOut.setSize(data.length);
                    entryOut.setCompressedSize(-1);
                    zipOut.putNextEntry(entryOut);
                    zipOut.write(data);
                } else {
                    // transfer directly
                    zipOut.putNextEntry(entryOut);
                    transfer(zipIn, zipOut);
                }
            }

            // add remapped sentinel
            if (ext.isSentinelEnabled()) {
                zipOut.putNextEntry(new ZipEntry("remapped"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // copy temporary jar over original
        try (FileInputStream fileIn = new FileInputStream(tempFile);
             FileOutputStream fileOut = new FileOutputStream(jarFile)
        ) {
            transfer(fileIn, fileOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // remove temporary jar
        if (!tempFile.delete()) {
            throw new RuntimeException("Failed to delete " + tempFile);
        }
    }

    /**
     * Run RemapClassVisitor over an input class.
     */
    private byte[] transform(InputStream in, InheritanceMap parents, List<Mapping> mappings) throws IOException {
        ClassReader reader = new ClassReader(in);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor visitor = new RemapClassVisitor(writer, parents, mappings);
        reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return writer.toByteArray();
    }

    /**
     * Copy between two streams.
     */
    private void transfer(InputStream in, OutputStream out) throws IOException {
        int n;
        byte[] buffer = new byte[4096];
        while ((n = in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }
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
