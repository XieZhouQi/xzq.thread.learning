package forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * fork join
 */
public class ForkJoinDemo {

    //  针对一个数字，做计算
    private static final Integer MAX = 2000;

    static class CalcForkJoinTask extends RecursiveTask<Integer>{
        private Integer startValue; // 子任务开始计算的值

        private Integer endValue; //子任务结束计算的值

        public CalcForkJoinTask(Integer startValue, Integer endValue) {
            this.startValue = startValue;
            this.endValue = endValue;
        }

        @Override
        protected Integer compute() {
            // 如果当前的数据区间已经小于MAX了，那么接下来的计算不需要做拆分
            if(endValue - startValue<MAX){
                System.out.println("开始计算：" + startValue +" ; endValue: "+ endValue);
                Integer totalValue = 0;
                for (int i = this.startValue; i <= this.endValue; i++) {
                    totalValue += i;
                }
                return totalValue;
            }
            CalcForkJoinTask subTask = new CalcForkJoinTask(startValue, (startValue +endValue) /2);
            subTask.fork();

            CalcForkJoinTask calcForkJoinTask = new CalcForkJoinTask((startValue +endValue) /2+1, endValue);
            calcForkJoinTask.fork();
            return subTask.join() + calcForkJoinTask.join();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CalcForkJoinTask calcForkJoinTask = new CalcForkJoinTask(1, 10000);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> forkJoinTask =forkJoinPool.submit(calcForkJoinTask);
        Integer result = forkJoinTask.get();
        System.out.println("result: "+ result);

    }
}
