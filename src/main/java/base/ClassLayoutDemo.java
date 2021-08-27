package base;

import org.openjdk.jol.info.ClassLayout;

public class ClassLayoutDemo {
    Object o = new Object();
    public static void main(String[] args) {
        // 构建对象实例
        ClassLayoutDemo classLayoutDemo = new ClassLayoutDemo();
        // 打印对象在内存中的布局
        System.out.println(ClassLayout.parseInstance(classLayoutDemo).toPrintable());
    }
}
