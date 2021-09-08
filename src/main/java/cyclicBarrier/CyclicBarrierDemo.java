package cyclicBarrier;

import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrierDemo
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        int num = 4;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(4,()->{
            System.out.println("所有线程都写入完成，继续处理其他任务");
        });
        for (int i = 0; i < 4; i++) {
            new Writer(cyclicBarrier).start();
        }

    }

    static class Writer extends Thread{

        private CyclicBarrier cyclicBarrier;

        public Writer(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + " 写入数据完毕，等待其他线程");
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
