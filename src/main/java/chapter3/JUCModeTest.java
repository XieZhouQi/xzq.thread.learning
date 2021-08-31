package chapter3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JUCModeTest {
    ConcurrentHashMap map = new ConcurrentHashMap();

    Lock lock = new ReentrantLock();
}
