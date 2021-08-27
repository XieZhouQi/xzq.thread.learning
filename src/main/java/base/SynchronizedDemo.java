package base;

public class SynchronizedDemo {
    int i = 0;
    // 修饰实例方法
    public synchronized void m1(){

    }

    public void m2(){
        // 修饰代码块
        synchronized (this){

        }
    }


    public static void main(String[] args) {
        // synchronized 加锁范围
        // 加锁一定会带来性能开销
        SynchronizedDemo s1 = new SynchronizedDemo();
        SynchronizedDemo s2 = new SynchronizedDemo();
        new Thread(()->{ s1.m1();}).start();

        new Thread(()->{
            s2.m2();
        }).start();
    }

    // 修饰静态方法
    public synchronized static void m3(){

    }

}
