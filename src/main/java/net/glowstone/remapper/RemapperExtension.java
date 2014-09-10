package net.glowstone.remapper;

/**
 * The extension for providing arguments to the remapper.
 */
public class RemapperExtension {

    private String mappingFile = null;
    private String inputJar = null;
    private String outputJar = null;
    private boolean sentinelEnabled = false;

    public String getMappingFile() {
        return mappingFile;
    }

    public void setMappingFile(String mappingFile) {
        this.mappingFile = mappingFile;
    }

    public String getInputJar() {
        return inputJar;
    }

    public void setInputJar(String inputJar) {
        this.inputJar = inputJar;
    }

    public String getOutputJar() {
        return outputJar;
    }

    public void setOutputJar(String outputJar) {
        this.outputJar = outputJar;
    }

    public boolean isSentinelEnabled() {
        return sentinelEnabled;
    }

    public void setSentinelEnabled(boolean sentinelEnabled) {
        this.sentinelEnabled = sentinelEnabled;
    }

    public void validate() {
        if (mappingFile == null) {
            throw new IllegalArgumentException("Missing remapper argument \"mappingFile\".");
        }
        if (inputJar == null) {
            throw new IllegalArgumentException("Missing remapper argument \"inputJar\".");
        }
        if (outputJar == null) {
            outputJar = inputJar.replace(".jar", "-remap.jar");
        }
    }
}
