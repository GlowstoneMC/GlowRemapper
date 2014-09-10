package net.glowstone.remapper;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

/**
 * ClassVisitor that performs remapping.
 */
public class RemapClassVisitor extends ClassVisitor {

    private final InheritanceMap parents;
    private final List<Mapping> mappings;

    private String currentClass;

    public RemapClassVisitor(ClassVisitor other, InheritanceMap parents, List<Mapping> mappings) {
        super(Opcodes.ASM5, other);
        this.parents = parents;
        this.mappings = mappings;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        currentClass = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // try to find a matching mapping
        for (Mapping mapping : mappings) {
            if (name.equals(mapping.getMethodName()) &&
                    desc.equals(mapping.getMethodSignature()) &&
                    parents.isParent(currentClass, mapping.getClassName())) {
                name = mapping.getNewName();
                access |= Opcodes.ACC_SYNTHETIC;
                break;
            }
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
