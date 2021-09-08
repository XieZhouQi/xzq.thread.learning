package chapter4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞队列实现
 */
public class ConditionBlockQueueDemo {

    // 表示阻塞队列中的容器
    private List<String> list;

    // 元素个数（队列中已添加的元素个数）
    private volatile int size;

    // 数组的容量
    private volatile int count;

    private Lock lock = new ReentrantLock();

    // 让take方法阻塞
    private final Condition notEmpty = lock.newCondition();

    // 让add方法阻塞
    private final Condition notFull = lock.newCondition();

    public ConditionBlockQueueDemo(int count) {
        this.count = count;
        this.list = new ArrayList<>(count);
    }

    public void put(String item){
        lock.lock();
        try {
            if(size >= count){
                System.out.println("队列满了,需要等一会");
                notEmpty.await();
            }
            ++ size; // 增加元素个数
            list.add(item);
            notEmpty.signal();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public String take() throws InterruptedException {
        lock.lock();
        try {
            if(size == 0 ){
                System.out.println("阻塞队列空了，需要等一会");
                notEmpty.await(); // 阻塞线程
            }
            -- size; // 取出元素个数
            String item = list.remove(0);
            notFull.signal();
            return item;
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ConditionBlockQueueDemo cbqd = new ConditionBlockQueueDemo(10);

        // 生产折线程
        Thread producerT = new Thread(()->{
            for (int i = 0; i < 1000; i++) {
                String item = "item:"+i;
                cbqd.put(item);
                System.out.println("生产着线程生产一个元素："+ i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        producerT.start();
        Thread.sleep(1000);

        // 消费者线程
        Thread consumerT = new Thread(()->{
            Random random = new Random();
            for (;;){
                try {
                    String item = cbqd.take();
                    System.out.println("消费者线程消费一个元素："+ item);
                    Thread.sleep(random.nextInt(2000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        consumerT.start();


    }



}
