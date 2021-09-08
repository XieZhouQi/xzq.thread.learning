package chapter5;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SaveProcessor extends Thread implements IRequestProcessor{
    protected IRequestProcessor nextProcessor;

    protected BlockingQueue<Request> requests = new LinkedBlockingQueue<>();

    public SaveProcessor(IRequestProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void processRequest(Request request) {
        requests.add(request);
        // do something
//        System.out.println("SaveProcessor");
//        nextProcessor.processRequest(request);
    }

    @Override
    public void run() {
        while (true){
            try {
                // 异步进行请求处理
                Request request = requests.take();
                System.out.println("SaveProcessor");
                if(null != nextProcessor){
                    nextProcessor.processRequest(request);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
