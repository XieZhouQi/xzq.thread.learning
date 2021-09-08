package chm;

import java.util.concurrent.ConcurrentHashMap;

public class InitTable {
    /**
     * Initializes table, using the size recorded in sizeCtl.
     */
    private final ConcurrentHashMap.Node<K,V>[] initTable() {
        ConcurrentHashMap.Node<K,V>[] tab; int sc;
        // 只要table没有初始化，就不断循环直到初始化完成
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0) //sc是size control 从内存中获取的大小
                Thread.yield(); // lost initialization race; just spin
            // 通过cas自旋，通过cas占用一个锁的标记
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                // 进来了，说明当前线程抢占了锁（占位）
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        // 初始化大小16
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        ConcurrentHashMap.Node<K,V>[] nt = (ConcurrentHashMap.Node<K,V>[])new ConcurrentHashMap.Node<?,?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2); //保留扩容的阈值
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }

}
