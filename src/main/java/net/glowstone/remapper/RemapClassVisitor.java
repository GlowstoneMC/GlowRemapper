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

    private Mapping find(String className, String name, String desc) {
        for (Mapping mapping : mappings) {
            if (name.equals(mapping.getMethodName()) &&
                    desc.equals(mapping.getMethodSignature()) &&
                    parents.isParent(className, mapping.getClassName())) {
                mapping.addUsage();
                return mapping;
            }
        }
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // try to find a matching mapping
        Mapping mapping = find(currentClass, name, desc);
        if (mapping != null) {
            name = mapping.getNewName();
            access |= Opcodes.ACC_SYNTHETIC;
        }
        return new RemapperMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
    }

    private class RemapperMethodVisitor extends MethodVisitor {
        public RemapperMethodVisitor(MethodVisitor methodVisitor) {
            super(Opcodes.ASM4, methodVisitor);
            // ASM4 used intentionally - overrides older API method
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            Mapping mapping = find(owner, name, desc);
            if (mapping != null) {
                name = mapping.getNewName();
            }
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }
}
