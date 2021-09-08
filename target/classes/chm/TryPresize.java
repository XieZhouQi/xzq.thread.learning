package chm;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩容
 */
public class TryPresize {

    /**
     * Tries to presize table to accommodate the given number of elements.
     *
     * @param size number of elements (doesn't need to be perfectly accurate)
     */
    private final void tryPresize(int size) {
        // 用来判断扩容的目标大小
        int c = (size >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY :
                tableSizeFor(size + (size >>> 1) + 1);
        int sc;
        while ((sc = sizeCtl) >= 0) { // 说明要做数组的初始化（因为putAll方法中也调用此方法）
            ConcurrentHashMap.Node<K,V>[] tab = table; int n;
            // 初始化
            if (tab == null || (n = tab.length) == 0) {
                n = (sc > c) ? sc : c; // 初始容量和扩容的目标容量，谁最大取谁
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                    try {
                        if (table == tab) {
                            @SuppressWarnings("unchecked")
                            ConcurrentHashMap.Node<K,V>[] nt = (ConcurrentHashMap.Node<K,V>[])new ConcurrentHashMap.Node<?,?>[n];
                            table = nt;
                            sc = n - (n >>> 2);
                        }
                    } finally {
                        sizeCtl = sc;
                    }
                }
            }

            // 已经是最大容量了，不能扩容，直接返回
            else if (c <= sc || n >= MAXIMUM_CAPACITY)
                break;
            //
            else if (tab == table) {
                int rs = resizeStamp(n); // 扩容戳，保证当前扩容范围的唯一性
                // 第一次扩容的时候，不会走这段逻辑
                if (sc < 0) {
                    ConcurrentHashMap.Node<K,V>[] nt;
                    // 表示扩容结束
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                            sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                            transferIndex <= 0)
                        break;
                    // 表示没有结束，每增加一个扩容线程，则在低位 + 1
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        transfer(tab, nt);
                }
                // 第一次扩容走这段逻辑
                else if (U.compareAndSwapInt(this, SIZECTL, sc,
                        (rs << RESIZE_STAMP_SHIFT) + 2))
                    transfer(tab, null);
            }
        }
    }
}
