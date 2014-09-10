package net.glowstone.remapper;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A map of inheritance relationships used to determine parenthood.
 */
public class InheritanceMap {

    private final Map<String, List<String>> entries = new HashMap<>();

    /**
     * Add a class on an InputStream to the map.
     * @param in The InputStream to read.
     * @throws IOException if an I/O error occurs.
     */
    public void readClass(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        ClassVisitor visitor = new MappingClassVisitor();
        reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }

    /**
     * Check if one class is the same class, direct parent, or indirect
     * parent of another.
     * @param name The child class to check.
     * @param parent The parent class to check.
     * @return Whether the child and parent are related.
     */
    public boolean isParent(String name, String parent) {
        if (name.equals(parent)) {
            return true;
        }
        if (entries.containsKey(name)) {
            for (String directParent : entries.get(name)) {
                if (isParent(directParent, parent)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setParent(String name, String parent) {
        List<String> parents = entries.get(name);
        if (parents == null) {
            parents = new LinkedList<>();
            entries.put(name, parents);
        }
        parents.add(parent);
    }

    private class MappingClassVisitor extends ClassVisitor {
        public MappingClassVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            setParent(name, superName);
            for (String intface : interfaces) {
                setParent(name, intface);
            }
            super.visit(version, access, name, signature, superName, interfaces);
        }
    }
}
