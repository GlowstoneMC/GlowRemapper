package net.glowstone.remapper;

import org.gradle.api.internal.AbstractTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import java.io.File;

/**
 * The "remap" task for the remapper plugin.
 */
public class RemapperTask extends AbstractTask {

    private File mappingFile = null;
    private File inputJar = null;
    private File outputJar = null;

    public RemapperTask() {
        doLast(new RemapperAction());
    }

    @InputFile
    public File getMappingFile() {
        return mappingFile;
    }

    public void setMappingFile(File mappingFile) {
        this.mappingFile = mappingFile;
    }

    @InputFile
    public File getInputJar() {
        return inputJar;
    }

    public void setInputJar(File inputJar) {
        this.inputJar = inputJar;
        if (outputJar == null) {
            outputJar = new File(inputJar.getPath().replace(".jar", "-remapped.jar"));
        }
    }

    public void setInputTask(AbstractArchiveTask parent) {
        this.inputJar = parent.getArchivePath();
        dependsOn(parent);
        if (outputJar == null) {
            String classifier = parent.getClassifier();
            parent.setClassifier("remapped");
            outputJar = parent.getArchivePath();
            parent.setClassifier(classifier);
        }
    }

    @OutputFile
    public File getOutputJar() {
        return outputJar;
    }

    public void setOutputJar(File outputJar) {
        this.outputJar = outputJar;
    }

    public void setOutputFilename(String filename) {
        if (outputJar == null) {
            outputJar = new File(filename);
        } else {
            outputJar = new File(outputJar.getParent(), filename);
        }
    }

    public void validate() {
        if (mappingFile == null) {
            throw new IllegalArgumentException("Missing remapper argument \"mappingFile\".");
        }
        if (inputJar == null) {
            throw new IllegalArgumentException("Missing remapper argument \"inputJar\".");
        }
    }

}
