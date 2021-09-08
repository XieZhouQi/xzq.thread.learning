package chapter4;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ConditionWait implements Runnable{

    private Lock lock;

    private Condition condition;

    public ConditionWait(Lock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
        System.out.println("ConditionWait begin ***********");
        lock.lock();
        try {
            condition.await(); // 让当前线程阻塞
            System.out.println("ConditionWait end ***********");
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
    }
}
