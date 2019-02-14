package com.serical.funai;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ARETURN;

public class Agent implements ClassFileTransformer {

    public static void premain(String args, Instrumentation instrumentation) {
        instrumentation.addTransformer(new Agent());
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (className.equals("java/util/Scanner")) {
                ClassReader classReader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                NextLineClassVisitor classVisitor = new NextLineClassVisitor(classWriter);
                classReader.accept(classVisitor, Opcodes.ASM5);
                return classWriter.toByteArray();
            }
        } catch (Exception e) {
            // 为了效果暂时注释掉
            // e.printStackTrace();
        }
        return classfileBuffer;
    }
}

class NextLineClassVisitor extends ClassVisitor {

    NextLineClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public void visitEnd() {
        // 注入runnable线程延迟5秒结束
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC, "lambda$nextLine$0", "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/InterruptedException");
        mv.visitLabel(l0);
        mv.visitLineNumber(10, l0);
        mv.visitLdcInsn(5000L);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLineNumber(11, l3);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("\u8bf4\u4e86\u4f60\u662f\u4e2a\u50bb\u903c\u5427\uff0c\u8fd8\u4e0d\u4fe1\uff1f");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(12, l4);
        mv.visitIntInsn(Opcodes.SIPUSH, 666);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "exit", "(I)V", false);
        mv.visitLabel(l1);
        mv.visitLineNumber(15, l1);
        Label l5 = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, l5);
        mv.visitLabel(l2);
        mv.visitLineNumber(13, l2);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/InterruptedException"});
        mv.visitVarInsn(Opcodes.ASTORE, 0);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(14, l6);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/InterruptedException", "printStackTrace", "()V", false);
        mv.visitLabel(l5);
        mv.visitLineNumber(16, l5);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitLocalVariable("e", "Ljava/lang/InterruptedException;", null, l6, l5, 0);
        mv.visitMaxs(2, 1);

        super.visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("nextLine".equals(name)) {
            MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
            return new NextLineMethodVisitor(methodVisitor);
        }
        return cv.visitMethod(access, name, desc, signature, exceptions);
    }
}

class NextLineMethodVisitor extends MethodVisitor {

    NextLineMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == ARETURN) {
            // java/util/Scanner的nextLine方法注入判断是否包含"傻逼"逻辑
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(7, l0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("\u50bb\u903c");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(8, l2);
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/Thread");
            mv.visitInsn(Opcodes.DUP);
            mv.visitInvokeDynamicInsn("run", "()Ljava/lang/Runnable;",
                    new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
                    Type.getType("()V"),
                    new Handle(Opcodes.H_INVOKESTATIC, "java/util/Scanner", "lambda$nextLine$0", "()V", false),
                    Type.getType("()V"));
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Thread", "<init>", "(Ljava/lang/Runnable;)V", false);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLineNumber(16, l3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "start", "()V", false);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLineNumber(17, l4);
            mv.visitLdcInsn("\u4f60\u624d\u662f\u50bb\u903c");
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitLabel(l1);
            mv.visitLineNumber(19, l1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitInsn(Opcodes.ARETURN);
        }

        super.visitInsn(opcode);
    }
}
