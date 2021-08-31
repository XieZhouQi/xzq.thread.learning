package chapter2;

import sun.misc.Contended;

/**
 * 缓存行测试
 */
public class CacheLineExample implements Runnable{

    public final static long ITERATIONS = 500L * 1000L * 100L;

    private int arrayIndex = 0;

    private static ValueNoPadding[] longs;

    public CacheLineExample(int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i < 10; i++){
            System.gc();
            final long start = System.currentTimeMillis();
            runTest(i);
            System.out.println(i + " thread  duration: " + (System.currentTimeMillis() - start));
        }
    }


    public static void runTest(int num) throws InterruptedException {
        Thread[] threads = new Thread[num];
        longs = new ValueNoPadding[num];
        for(int i = 0; i < longs.length; i++){
           longs[i] = new ValueNoPadding();
        }

        for(int i = 0; i < threads.length; i++){
            threads[i] = new Thread(new CacheLineExample(i));
        }

        for (Thread t: threads){
            t.start();
        }

        for (Thread t: threads){
            t.join();
        }
    }

    public final static class ValuePadding{
        protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;
        protected long p9, p10, p11, p12, p13, p14;
        protected long p15;

    }

    @Contended
    public final static class ValueNoPadding{
        //protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;
        //protected long p9, p10, p11, p12, p13, p14, p15;
    }

    @Override
    public void run() {
        long i = ITERATIONS + 1;
        while(0 != --i){
            longs[arrayIndex].value = 0L;
        }
    }
}
