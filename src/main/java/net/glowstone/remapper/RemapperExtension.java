package net.glowstone.remapper;

/**
 * The extension for providing arguments to the remapper.
 */
public class RemapperExtension {

    private String mappingFile;
    private String targetJar;

    public String getMappingFile() {
        return mappingFile;
    }

    public void setMappingFile(String mappingFile) {
        this.mappingFile = mappingFile;
    }

    public String getTargetJar() {
        return targetJar;
    }

    public void setTargetJar(String targetJar) {
        this.targetJar = targetJar;
    }

    public void validate() {
        if (mappingFile == null) {
            throw new IllegalArgumentException("Missing remapper argument \"mappingFile\".");
        }
        if (targetJar == null) {
            throw new IllegalArgumentException("Missing remapper argument \"targetJar\".");
        }
    }
}
