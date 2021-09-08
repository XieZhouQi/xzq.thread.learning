package chm;

import java.util.concurrent.ConcurrentHashMap;

public class Put {

    public V put(K key, V value) {
        return putVal(key, value, false);
    }



    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;

        // 自旋（;;） cas操作
        for (ConcurrentHashMap.Node<K,V>[] tab = table;;) {
            ConcurrentHashMap.Node<K,V> f; int n, i, fh;
            // 如果tab为空说明还没有初始化
            if (tab == null || (n = tab.length) == 0)
                // 初始化， 当初始化完成后进入下一次循环
                tab = initTable();

            // i = (n - 1) & hash -> 0-15 -> 计算数组下标位置
            // f代表节点
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                // 如果档期那的node的位置为空，直接存储到该位置
                // 通过cas来保证原子性
                if (casTabAt(tab, i, null,
                        new ConcurrentHashMap.Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;

                // 锁住当前node节点，便面线程安全问题
                synchronized (f) {
                    if (tabAt(tab, i) == f) { // 重新判断（避免f有更新）

                        // 针对链表来处理
                        if (fh >= 0) {
                            binCount = 1; // 统计了链表的长度
                            for (ConcurrentHashMap.Node<K,V> e = f;; ++binCount) {
                                K ek;
                                 // 是否存在相同的key，如果存在，则覆盖
                                if (e.hash == hash &&
                                        ((ek = e.key) == key ||
                                                (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                // 如果不存在则把当前的key/value添加到链表中（就是同样的key执行覆盖操作）
                                ConcurrentHashMap.Node<K,V> pred = e;
                                // 说明到了最后一个节点，直接添加到尾部
                                if ((e = e.next) == null) {
                                    pred.next = new ConcurrentHashMap.Node<K,V>(hash, key,
                                            value, null);
                                    break;
                                }
                            }
                        }

                        // 针对红黑树的处理
                        else if (f instanceof ConcurrentHashMap.TreeBin) {
                            ConcurrentHashMap.Node<K,V> p;
                            binCount = 2;
                            if ((p = ((ConcurrentHashMap.TreeBin<K,V>)f).putTreeVal(hash, key,
                                    value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }

                if (binCount != 0) {
                    // 如果链表长度大于等于8，则会调用treeifyBin方法
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }
}
