package chm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class FullAddCount {

    // See LongAdder version for explanation
    private final void fullAddCount(long x, boolean wasUncontended) {
        int h;
        if ((h = ThreadLocalRandom.getProbe()) == 0) {
            ThreadLocalRandom.localInit();      // force initialization
            h = ThreadLocalRandom.getProbe();
            wasUncontended = true;
        }
        boolean collide = false;                // True if last slot nonempty
        for (;;) {
            ConcurrentHashMap.CounterCell[] as; ConcurrentHashMap.CounterCell a; int n; long v;
            if ((as = counterCells) != null && (n = as.length) > 0) {
                if ((a = as[(n - 1) & h]) == null) {
                    if (cellsBusy == 0) {            // Try to attach new Cell
                        ConcurrentHashMap.CounterCell r = new ConcurrentHashMap.CounterCell(x); // Optimistic create
                        if (cellsBusy == 0 &&
                                U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                            boolean created = false;
                            try {               // Recheck under lock
                                // 针对已经初始化的数组的某个位置，去添加一个counterCell
                                ConcurrentHashMap.CounterCell[] rs; int m, j;
                                if ((rs = counterCells) != null &&
                                        (m = rs.length) > 0 &&
                                        rs[j = (m - 1) & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                cellsBusy = 0;
                            }
                            if (created)
                                break;
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;
                }
                else if (!wasUncontended)       // CAS already known to fail
                    wasUncontended = true;      // Continue after rehash
                else if (U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))
                    break;
                else if (counterCells != as || n >= NCPU)
                    collide = false;            // At max size or stale
                else if (!collide)
                    collide = true;
                // 扩容部分
                else if (cellsBusy == 0 &&
                        U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) { // 获得锁
                    try {
                        if (counterCells == as) {// Expand table unless stale
                            ConcurrentHashMap.CounterCell[] rs = new ConcurrentHashMap.CounterCell[n << 1]; // 口容一倍
                            for (int i = 0; i < n; ++i) // 便利数组，添加到新的数组中
                                rs[i] = as[i];
                            counterCells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                h = ThreadLocalRandom.advanceProbe(h);
            }

            // 如果counterCell为空，保证再初始化过程的线程安全性
            else if (cellsBusy == 0 && counterCells == as &&
                    U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) { // 一旦cas成功，说明当前线程抢占到锁
                boolean init = false;
                try {                           // Initialize table
                    if (counterCells == as) {
                        // 初始化长度为2的数组
                        ConcurrentHashMap.CounterCell[] rs = new ConcurrentHashMap.CounterCell[2];
                        rs[h & 1] = new ConcurrentHashMap.CounterCell(x); // 把x保存到某个位置
                        counterCells = rs; // 赋值给成员变量counterCells
                        init = true;
                    }
                } finally {
                    cellsBusy = 0; // 释放锁
                }
                if (init)
                    break;
            }
            // 最终的情况，直接修改baseCount
            else if (U.compareAndSwapLong(this, BASECOUNT, v = baseCount, v + x))
                break;                          // Fall back on using base
        }
    }
}
