package net.glowstone.remapper;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;

/**
 * ClassVisitor that performs remapping.
 */
public class RemapClassVisitor extends ClassVisitor {

    public RemapClassVisitor(ClassVisitor other) {
        super(Opcodes.ASM5, other);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println(" visiting class: " + name + " " + superName + " " + Arrays.toString(interfaces));
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println("  visiting method: " + access + " " + name + " " + desc + " " + signature + " " + Arrays.toString(exceptions));
        name += "_0";
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
