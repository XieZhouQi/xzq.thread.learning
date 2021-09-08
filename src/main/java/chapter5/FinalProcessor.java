package chapter5;

public class FinalProcessor implements IRequestProcessor{


    @Override
    public void processRequest(Request request) {

        // do something
        System.out.println("SaveProcessor");

    }
}
