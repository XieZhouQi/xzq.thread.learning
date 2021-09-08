package chapter5;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ValidProcessor extends Thread implements IRequestProcessor{
    protected IRequestProcessor nextProcessor;

    protected BlockingQueue<Request> requests = new LinkedBlockingQueue<>();

    public ValidProcessor(IRequestProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void processRequest(Request request) {

        requests.add(request);
        // do something
//        System.out.println("ValidProcessor");
//        nextProcessor.processRequest(request);
    }

    @Override
    public void run() {
       while (true){
           try {
               // 异步进行请求处理
               Request request = requests.take();
               System.out.println("ValidProcessor");
               if(null != nextProcessor){
                   nextProcessor.processRequest(request);
               }
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
    }
}
