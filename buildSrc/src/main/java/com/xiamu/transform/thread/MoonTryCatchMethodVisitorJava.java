package com.xiamu.transform.thread;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

//https://cloud.tencent.com/developer/article/1615800
public class MoonTryCatchMethodVisitorJava extends AdviceAdapter {

    private Label from = new Label();
    private Label to = new Label();
    private Label target = new Label();

    public MoonTryCatchMethodVisitorJava(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String className) {
        super(api, methodVisitor, access, name, descriptor);
    }


    // 方法进入时修改字节码
    //方法进入时设置一些基本内容，比如当前纳秒用于后续监控方法的执行耗时。还有就是一些 Try 块的开始。
    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        //设置Label，Label的作用相当于表示方法在字节码中的位置
        visitLabel(from);
        visitTryCatchBlock(from,
                to,
                target,
                "java/lang/Exception");
    }

    // 访问局部变量和操作数栈
    //设置这个本地方法最大操作数栈和最大本地变量表
    //visitMaxs方法的调用必须在所有的MethodVisitor指令结束后调用
    //这个是在方法结束前，用于添加 Catch 块。到这也就可以将整个方法进行包裹起来了
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        //标志：try块结束
        mv.visitLabel(to);

        //标志：catch块开始位置
        mv.visitLabel(target);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});

        // 异常信息保存到局部变量
        int local = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(ASTORE, local);

        // 抛出异常
        mv.visitVarInsn(ALOAD, local);
        mv.visitInsn(ATHROW);
        super.visitMaxs(maxStack, maxLocals);
    }

    // 方法退出时修改字节码
    //最后是这个方法退出时，用于 RETURN 之前，可以注入结尾的字节码加强，比如调用外部方法输出监控信息。
    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);

    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }
}
