package chapter2;

public class VisibilityDemo {
    public static boolean flag = false;
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            int i = 0;
            while (!flag){
                i ++;
            }
        });
        t1.start();
        System.out.println("start t1");
        Thread.sleep(100);
        flag = true;
    }
}
