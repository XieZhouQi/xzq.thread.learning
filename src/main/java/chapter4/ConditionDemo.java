package chapter4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * condition demo
 */
public class ConditionDemo {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        ConditionWait wait = new ConditionWait(lock, condition);
        ConditionNotify notify = new ConditionNotify(lock, condition);

        new Thread(wait).start();
        new Thread(notify).start();
    }


}
