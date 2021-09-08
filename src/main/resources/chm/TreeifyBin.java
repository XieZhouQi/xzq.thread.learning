package chm;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 会根据阈值判断是转化为红黑树还是扩容
 */
public class TreeifyBin {

    /**
     * 会根据阈值判断是转化为红黑树还是扩容
     * Replaces all linked nodes in bin at given index unless table is
     * too small, in which case resizes instead.
     */
    private final void treeifyBin(ConcurrentHashMap.Node<K,V>[] tab, int index) {
        ConcurrentHashMap.Node<K,V> b; int n, sc;
        if (tab != null) {
            // 如果table长度小于64
            if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
                // 扩容
                tryPresize(n << 1);
            else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
                synchronized (b) {
                    // 红黑树转化
                    if (tabAt(tab, index) == b) {
                        ConcurrentHashMap.TreeNode<K,V> hd = null, tl = null;
                        for (ConcurrentHashMap.Node<K,V> e = b; e != null; e = e.next) {
                            ConcurrentHashMap.TreeNode<K,V> p =
                                    new ConcurrentHashMap.TreeNode<K,V>(e.hash, e.key, e.val,
                                            null, null);
                            if ((p.prev = tl) == null)
                                hd = p;
                            else
                                tl.next = p;
                            tl = p;
                        }
                        setTabAt(tab, index, new ConcurrentHashMap.TreeBin<K,V>(hd));
                    }
                }
            }
        }
    }
}
