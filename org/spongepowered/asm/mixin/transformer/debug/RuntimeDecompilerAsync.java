// 
// Decompiled by Procyon v0.5.36
// 

package org.spongepowered.asm.mixin.transformer.debug;

import java.util.concurrent.LinkedBlockingQueue;
import java.io.File;
import java.util.concurrent.BlockingQueue;

public class RuntimeDecompilerAsync extends RuntimeDecompiler implements Runnable, Thread.UncaughtExceptionHandler
{
    private final BlockingQueue<File> queue;
    private final Thread thread;
    private boolean run;
    
    public RuntimeDecompilerAsync(final File outputPath) {
        super(outputPath);
        this.queue = new LinkedBlockingQueue<File>();
        this.run = true;
        (this.thread = new Thread(this, "Decompiler thread")).setDaemon(true);
        this.thread.setPriority(1);
        this.thread.setUncaughtExceptionHandler(this);
        this.thread.start();
    }
    
    @Override
    public void decompile(final File file) {
        if (this.run) {
            this.queue.offer(file);
        }
        else {
            super.decompile(file);
        }
    }
    
    @Override
    public void run() {
        while (this.run) {
            try {
                final File file = this.queue.take();
                super.decompile(file);
            }
            catch (InterruptedException ex2) {
                this.run = false;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        this.logger.error("Async decompiler encountered an error and will terminate. Further decompile requests will be handled synchronously. {} {}", new Object[] { ex.getClass().getName(), ex.getMessage() });
        this.flush();
    }
    
    private void flush() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: iconst_0       
        //     2: putfield        org/spongepowered/asm/mixin/transformer/debug/RuntimeDecompilerAsync.run:Z
        //     5: aload_0         /* this */
        //     6: getfield        org/spongepowered/asm/mixin/transformer/debug/RuntimeDecompilerAsync.queue:Ljava/util/concurrent/BlockingQueue;
        //     9: invokeinterface java/util/concurrent/BlockingQueue.poll:()Ljava/lang/Object;
        //    14: checkcast       Ljava/io/File;
        //    17: astore_1       
        //    18: aload_1        
        //    19: ifnull          30
        //    22: aload_0         /* this */
        //    23: aload_1         /* file */
        //    24: invokevirtual   org/spongepowered/asm/mixin/transformer/debug/RuntimeDecompilerAsync.decompile:(Ljava/io/File;)V
        //    27: goto            5
        //    30: return         
        //    StackMapTable: 00 02 05 FC 00 18 07 00 4E
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
