package chapter4;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ConditionNotify implements Runnable{

    private Lock lock;

    private Condition condition;

    public ConditionNotify(Lock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
        System.out.println("ConditionNotify begin ***********");
        lock.lock(); // 等价于synchronized(lock)
        try {
            condition.signal(); // 唤醒处于等待下的线程
            System.out.println("ConditionNotify end ***********");
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
    }
}
