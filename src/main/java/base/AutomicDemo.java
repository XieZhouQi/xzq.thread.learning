package base;

public class AutomicDemo {
    int i = 0;
    // 排他锁、互斥锁
    public synchronized void incr(){
        i += 1;
    }
    public static void main(String[] args) throws InterruptedException {
        AutomicDemo automicDemo = new AutomicDemo();
        Thread[] thread = new Thread[2];
        for(int i = 0; i<2; i ++){
            thread[i] = new Thread(()->{
                for(int j = 0; j<10000; j ++){
                    automicDemo.incr();
                }
            });
            thread[i].start();
        }
        thread[0].join();
        thread[1].join();
        System.out.println("result ： " +automicDemo.i);
    }
}
