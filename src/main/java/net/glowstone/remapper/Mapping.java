package net.glowstone.remapper;

/**
 * Structure representing a single remap operation.
 */
public final class Mapping {

    private final String className;
    private final String methodName;
    private final String methodSignature;
    private final String newName;

    private int usages;

    public Mapping(String className, String methodName, String methodSignature, String newName) {
        this.className = className;
        this.methodName = methodName;
        this.methodSignature = methodSignature;
        this.newName = newName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String getNewName() {
        return newName;
    }

    public int getUsages() {
        return usages;
    }

    public void addUsage() {
        usages++;
    }

    @Override
    public String toString() {
        return "{" + className + " " + methodName + " " + methodSignature + " " + newName + "}";
    }

}
