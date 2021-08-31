package chapter3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockExample {

    static Lock lock = new ReentrantLock();
    private static int count = 0;

    public static void incr(){
        lock.lock();  //抢占锁 这里如果没有抢占到锁，会阻塞

        // 表示可中断抢占
        // lock.lockInterruptibly();

        // 治理如果没有抢占到锁，不会阻塞
//        if(lock.tryLock()){   // false 获取锁失败；true 获取锁成功
//            // 已经有人抢占到锁，就不去执行相关代码（使用场景）
//        }

        try {
            Thread.sleep(1);
            count ++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            new Thread(()->{
                LockExample.incr();
            }).start();
        }
        Thread.sleep(3000);
        System.out.println("result: "+ count);
       }
}
