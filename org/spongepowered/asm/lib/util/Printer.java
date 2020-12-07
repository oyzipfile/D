// 
// Decompiled by Procyon v0.5.36
// 

package org.spongepowered.asm.lib.util;

import java.io.PrintWriter;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.Handle;
import org.spongepowered.asm.lib.Attribute;
import org.spongepowered.asm.lib.TypePath;
import java.util.ArrayList;
import java.util.List;

public abstract class Printer
{
    public static final String[] OPCODES;
    public static final String[] TYPES;
    public static final String[] HANDLE_TAG;
    protected final int api;
    protected final StringBuffer buf;
    public final List<Object> text;
    
    protected Printer(final int api) {
        this.api = api;
        this.buf = new StringBuffer();
        this.text = new ArrayList<Object>();
    }
    
    public abstract void visit(final int p0, final int p1, final String p2, final String p3, final String p4, final String[] p5);
    
    public abstract void visitSource(final String p0, final String p1);
    
    public abstract void visitOuterClass(final String p0, final String p1, final String p2);
    
    public abstract Printer visitClassAnnotation(final String p0, final boolean p1);
    
    public Printer visitClassTypeAnnotation(final int typeRef, final TypePath typePath, final String desc, final boolean visible) {
        throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitClassAttribute(final Attribute p0);
    
    public abstract void visitInnerClass(final String p0, final String p1, final String p2, final int p3);
    
    public abstract Printer visitField(final int p0, final String p1, final String p2, final String p3, final Object p4);
    
    public abstract Printer visitMethod(final int p0, final String p1, final String p2, final String p3, final String[] p4);
    
    public abstract void visitClassEnd();
    
    public abstract void visit(final String p0, final Object p1);
    
    public abstract void visitEnum(final String p0, final String p1, final String p2);
    
    public abstract Printer visitAnnotation(final String p0, final String p1);
    
    public abstract Printer visitArray(final String p0);
    
    public abstract void visitAnnotationEnd();
    
    public abstract Printer visitFieldAnnotation(final String p0, final boolean p1);
    
    public Printer visitFieldTypeAnnotation(final int typeRef, final TypePath typePath, final String desc, final boolean visible) {
        throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitFieldAttribute(final Attribute p0);
    
    public abstract void visitFieldEnd();
    
    public void visitParameter(final String name, final int access) {
        throw new RuntimeException("Must be overriden");
    }
    
    public abstract Printer visitAnnotationDefault();
    
    public abstract Printer visitMethodAnnotation(final String p0, final boolean p1);
    
    public Printer visitMethodTypeAnnotation(final int typeRef, final TypePath typePath, final String desc, final boolean visible) {
        throw new RuntimeException("Must be overriden");
    }
    
    public abstract Printer visitParameterAnnotation(final int p0, final String p1, final boolean p2);
    
    public abstract void visitMethodAttribute(final Attribute p0);
    
    public abstract void visitCode();
    
    public abstract void visitFrame(final int p0, final int p1, final Object[] p2, final int p3, final Object[] p4);
    
    public abstract void visitInsn(final int p0);
    
    public abstract void visitIntInsn(final int p0, final int p1);
    
    public abstract void visitVarInsn(final int p0, final int p1);
    
    public abstract void visitTypeInsn(final int p0, final String p1);
    
    public abstract void visitFieldInsn(final int p0, final String p1, final String p2, final String p3);
    
    @Deprecated
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        if (this.api >= 327680) {
            final boolean itf = opcode == 185;
            this.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        throw new RuntimeException("Must be overriden");
    }
    
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (this.api >= 327680) {
            throw new RuntimeException("Must be overriden");
        }
        if (itf != (opcode == 185)) {
            throw new IllegalArgumentException("INVOKESPECIAL/STATIC on interfaces require ASM 5");
        }
        this.visitMethodInsn(opcode, owner, name, desc);
    }
    
    public abstract void visitInvokeDynamicInsn(final String p0, final String p1, final Handle p2, final Object... p3);
    
    public abstract void visitJumpInsn(final int p0, final Label p1);
    
    public abstract void visitLabel(final Label p0);
    
    public abstract void visitLdcInsn(final Object p0);
    
    public abstract void visitIincInsn(final int p0, final int p1);
    
    public abstract void visitTableSwitchInsn(final int p0, final int p1, final Label p2, final Label... p3);
    
    public abstract void visitLookupSwitchInsn(final Label p0, final int[] p1, final Label[] p2);
    
    public abstract void visitMultiANewArrayInsn(final String p0, final int p1);
    
    public Printer visitInsnAnnotation(final int typeRef, final TypePath typePath, final String desc, final boolean visible) {
        throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitTryCatchBlock(final Label p0, final Label p1, final Label p2, final String p3);
    
    public Printer visitTryCatchAnnotation(final int typeRef, final TypePath typePath, final String desc, final boolean visible) {
        throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitLocalVariable(final String p0, final String p1, final String p2, final Label p3, final Label p4, final int p5);
    
    public Printer visitLocalVariableAnnotation(final int typeRef, final TypePath typePath, final Label[] start, final Label[] end, final int[] index, final String desc, final boolean visible) {
        throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitLineNumber(final int p0, final Label p1);
    
    public abstract void visitMaxs(final int p0, final int p1);
    
    public abstract void visitMethodEnd();
    
    public List<Object> getText() {
        return this.text;
    }
    
    public void print(final PrintWriter pw) {
        printList(pw, this.text);
    }
    
    public static void appendString(final StringBuffer buf, final String s) {
        buf.append('\"');
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c == '\n') {
                buf.append("\\n");
            }
            else if (c == '\r') {
                buf.append("\\r");
            }
            else if (c == '\\') {
                buf.append("\\\\");
            }
            else if (c == '\"') {
                buf.append("\\\"");
            }
            else if (c < ' ' || c > '\u007f') {
                buf.append("\\u");
                if (c < '\u0010') {
                    buf.append("000");
                }
                else if (c < '\u0100') {
                    buf.append("00");
                }
                else if (c < '\u1000') {
                    buf.append('0');
                }
                buf.append(Integer.toString(c, 16));
            }
            else {
                buf.append(c);
            }
        }
        buf.append('\"');
    }
    
    static void printList(final PrintWriter pw, final List<?> l) {
        for (int i = 0; i < l.size(); ++i) {
            final Object o = l.get(i);
            if (o instanceof List) {
                printList(pw, (List<?>)o);
            }
            else {
                pw.print(o.toString());
            }
        }
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: astore_0        /* s */
        //     3: sipush          200
        //     6: anewarray       Ljava/lang/String;
        //     9: putstatic       org/spongepowered/asm/lib/util/Printer.OPCODES:[Ljava/lang/String;
        //    12: iconst_0       
        //    13: istore_1        /* i */
        //    14: iconst_0       
        //    15: istore_2        /* j */
        //    16: aload_0         /* s */
        //    17: bipush          44
        //    19: iload_2         /* j */
        //    20: invokevirtual   java/lang/String.indexOf:(II)I
        //    23: istore_3       
        //    24: iload_3        
        //    25: ifle            60
        //    28: getstatic       org/spongepowered/asm/lib/util/Printer.OPCODES:[Ljava/lang/String;
        //    31: iload_1         /* i */
        //    32: iinc            i, 1
        //    35: iload_2         /* j */
        //    36: iconst_1       
        //    37: iadd           
        //    38: iload_3         /* l */
        //    39: if_icmpne       46
        //    42: aconst_null    
        //    43: goto            52
        //    46: aload_0         /* s */
        //    47: iload_2         /* j */
        //    48: iload_3         /* l */
        //    49: invokevirtual   java/lang/String.substring:(II)Ljava/lang/String;
        //    52: aastore        
        //    53: iload_3         /* l */
        //    54: iconst_1       
        //    55: iadd           
        //    56: istore_2        /* j */
        //    57: goto            16
        //    60: ldc             "T_BOOLEAN,T_CHAR,T_FLOAT,T_DOUBLE,T_BYTE,T_SHORT,T_INT,T_LONG,"
        //    62: astore_0        /* s */
        //    63: bipush          12
        //    65: anewarray       Ljava/lang/String;
        //    68: putstatic       org/spongepowered/asm/lib/util/Printer.TYPES:[Ljava/lang/String;
        //    71: iconst_0       
        //    72: istore_2        /* j */
        //    73: iconst_4       
        //    74: istore_1        /* i */
        //    75: aload_0         /* s */
        //    76: bipush          44
        //    78: iload_2         /* j */
        //    79: invokevirtual   java/lang/String.indexOf:(II)I
        //    82: istore_3        /* l */
        //    83: iload_3         /* l */
        //    84: ifle            108
        //    87: getstatic       org/spongepowered/asm/lib/util/Printer.TYPES:[Ljava/lang/String;
        //    90: iload_1         /* i */
        //    91: iinc            i, 1
        //    94: aload_0         /* s */
        //    95: iload_2         /* j */
        //    96: iload_3         /* l */
        //    97: invokevirtual   java/lang/String.substring:(II)Ljava/lang/String;
        //   100: aastore        
        //   101: iload_3         /* l */
        //   102: iconst_1       
        //   103: iadd           
        //   104: istore_2        /* j */
        //   105: goto            75
        //   108: ldc             "H_GETFIELD,H_GETSTATIC,H_PUTFIELD,H_PUTSTATIC,H_INVOKEVIRTUAL,H_INVOKESTATIC,H_INVOKESPECIAL,H_NEWINVOKESPECIAL,H_INVOKEINTERFACE,"
        //   110: astore_0        /* s */
        //   111: bipush          10
        //   113: anewarray       Ljava/lang/String;
        //   116: putstatic       org/spongepowered/asm/lib/util/Printer.HANDLE_TAG:[Ljava/lang/String;
        //   119: iconst_0       
        //   120: istore_2        /* j */
        //   121: iconst_1       
        //   122: istore_1        /* i */
        //   123: aload_0         /* s */
        //   124: bipush          44
        //   126: iload_2         /* j */
        //   127: invokevirtual   java/lang/String.indexOf:(II)I
        //   130: istore_3        /* l */
        //   131: iload_3         /* l */
        //   132: ifle            156
        //   135: getstatic       org/spongepowered/asm/lib/util/Printer.HANDLE_TAG:[Ljava/lang/String;
        //   138: iload_1         /* i */
        //   139: iinc            i, 1
        //   142: aload_0         /* s */
        //   143: iload_2         /* j */
        //   144: iload_3         /* l */
        //   145: invokevirtual   java/lang/String.substring:(II)Ljava/lang/String;
        //   148: aastore        
        //   149: iload_3         /* l */
        //   150: iconst_1       
        //   151: iadd           
        //   152: istore_2        /* j */
        //   153: goto            123
        //   156: return         
        //    StackMap: 00 08 00 10 00 03 07 00 6A 01 01 00 00 00 2E 00 04 07 00 6A 01 01 01 00 02 07 00 E6 01 00 34 00 04 07 00 6A 01 01 01 00 03 07 00 E6 01 07 00 6A 00 3C 00 04 07 00 6A 01 01 01 00 00 00 4B 00 04 07 00 6A 01 01 01 00 00 00 6C 00 04 07 00 6A 01 01 01 00 00 00 7B 00 04 07 00 6A 01 01 01 00 00 00 9C 00 04 07 00 6A 01 01 01 00 00
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2895)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
