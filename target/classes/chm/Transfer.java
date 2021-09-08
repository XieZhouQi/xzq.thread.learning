package chm;

import java.util.concurrent.ConcurrentHashMap;

public class Transfer {

    /**
     * Moves and/or copies the nodes in each bin to new table. See
     * above for explanation.
     */
    private final void transfer(ConcurrentHashMap.Node<K,V>[] tab, ConcurrentHashMap.Node<K,V>[] nextTab) {
        int n = tab.length, stride;
        // 计算每个线程处理的数据的区间大小，最小是16
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            stride = MIN_TRANSFER_STRIDE; // subdivide range

        // 表示扩容之后的数组，在原来的基础上扩大两倍
        if (nextTab == null) {            // initiating
            try {
                @SuppressWarnings("unchecked")
                ConcurrentHashMap.Node<K,V>[] nt = (ConcurrentHashMap.Node<K,V>[])new ConcurrentHashMap.Node<?,?>[n << 1];
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            nextTable = nextTab;
            transferIndex = n; // 转移的索引（transferIndex = old table[]的长度）
        }


        int nextn = nextTab.length;
        // 用来表示已经迁移完成的状态，也就是说，如果某个old数组的节点完成了迁移，则需要更改成fwd
        ConcurrentHashMap.ForwardingNode<K,V> fwd = new ConcurrentHashMap.ForwardingNode<K,V>(nextTab);

        boolean advance = true;
        boolean finishing = false; // to ensure sweep before committing nextTab
        for (int i = 0, bound = 0;;) {
            ConcurrentHashMap.Node<K,V> f; int fh;
            while (advance) {
                int nextIndex, nextBound;
                if (--i >= bound || finishing)
                    advance = false;
                else if ((nextIndex = transferIndex) <= 0) {
                    i = -1;
                    advance = false;
                }
                else if (U.compareAndSwapInt
                        (this, TRANSFERINDEX, nextIndex,
                                nextBound = (nextIndex > stride ?
                                        nextIndex - stride : 0))) {
                    bound = nextBound;
                    i = nextIndex - 1;
                    advance = false;
                }
                // 假设数组长度是32
                // 第一次[16（nextBound） 31（i）]
                // 第二次[0 15]
            }

            // 是否扩容结束（按照区间处理）
            if (i < 0 || i >= n || i + n >= nextn) {
                int sc;
                if (finishing) {
                    nextTable = null;
                    table = nextTab;
                    sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                        return;
                    finishing = advance = true;
                    i = n; // recheck before commit
                }
            }
            // 得到数组下标为31位置的值
            else if ((f = tabAt(tab, i)) == null) // 说明当前数组位置为空
                advance = casTabAt(tab, i, null, fwd);// 直接改成fwd -> 表示迁移完成
            else if ((fh = f.hash) == MOVED) // 判断是否已经被处理过了，如果是，则进入下一区间
                advance = true; // already processed
            else {
                // 加锁 ——》针对当前要去迁移的节点
                synchronized (f) { // 保证迁移过程中，其他线程调用put()方法时，必须要等待
                    // 下面的内容的猜想
                    // 下面一定是链表与红黑树的数据结构处理

                    if (tabAt(tab, i) == f) {
                        ConcurrentHashMap.Node<K,V> ln, hn;
                        // 表示不要迁移
                        if (fh >= 0) {
                            //
                            int runBit = fh & n; // fn是hash值
                            ConcurrentHashMap.Node<K,V> lastRun = f;
                            for (ConcurrentHashMap.Node<K,V> p = f.next; p != null; p = p.next) {
                                int b = p.hash & n;
                                if (b != runBit) {
                                    runBit = b;
                                    lastRun = p;
                                }
                            }
                            if (runBit == 0) {
                                ln = lastRun;
                                hn = null;
                            }
                            else {
                                hn = lastRun;
                                ln = null;
                            }
                            for (ConcurrentHashMap.Node<K,V> p = f; p != lastRun; p = p.next) {
                                int ph = p.hash; K pk = p.key; V pv = p.val;
                                if ((ph & n) == 0)
                                    ln = new ConcurrentHashMap.Node<K,V>(ph, pk, pv, ln);
                                else
                                    hn = new ConcurrentHashMap.Node<K,V>(ph, pk, pv, hn);
                            }
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                        // 表示要迁移
                        else if (f instanceof ConcurrentHashMap.TreeBin) {
                            ConcurrentHashMap.TreeBin<K,V> t = (ConcurrentHashMap.TreeBin<K,V>)f;
                            ConcurrentHashMap.TreeNode<K,V> lo = null, loTail = null;
                            ConcurrentHashMap.TreeNode<K,V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;
                            for (ConcurrentHashMap.Node<K,V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                ConcurrentHashMap.TreeNode<K,V> p = new ConcurrentHashMap.TreeNode<K,V>
                                        (h, e.key, e.val, null, null);
                                if ((h & n) == 0) {
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                }
                                else {
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                    (hc != 0) ? new ConcurrentHashMap.TreeBin<K,V>(lo) : t;
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                    (lc != 0) ? new ConcurrentHashMap.TreeBin<K,V>(hi) : t;
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                    }
                }
            }
        }
    }
}
