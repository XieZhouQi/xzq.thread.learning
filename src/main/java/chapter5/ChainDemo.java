package chapter5;

/**
 * 责任链测试
 */
public class ChainDemo {

    public static void main(String[] args) {
        FinalProcessor finalProcessor = new FinalProcessor();
        SaveProcessor saveProcessor = new SaveProcessor(finalProcessor);
        saveProcessor.start();
        PrintProcessor printProcessor = new PrintProcessor(saveProcessor);
        printProcessor.start();
        ValidProcessor validProcessor  = new ValidProcessor(printProcessor);
        validProcessor.start();
        validProcessor.processRequest(new Request(""));
    }
}
