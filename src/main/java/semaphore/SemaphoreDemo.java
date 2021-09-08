package semaphore;

import java.util.concurrent.*;

/**
 * 信号量demo
 */
public class SemaphoreDemo{

    public static void main(String[] args) {
        // 限制资源访问的并发数
        Semaphore semaphore = new Semaphore(20);
        for (int i = 0; i < 20; i++) {
            new Car(i, semaphore).start();
        }
    }


    public static class Car extends Thread{
        private int num;

        private Semaphore semaphore;

        public Car(int num, Semaphore semaphore) {
            this.num = num;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {

            try {
                semaphore.acquire(); // 可获得一个令牌
                System.out.println("车辆："+ num + " 抢到一个车位");
                TimeUnit.SECONDS.sleep(2);
                System.out.println("车辆："+ num + " 走了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                semaphore.release(); // 释放一个令牌
            }
        }
    }
}
