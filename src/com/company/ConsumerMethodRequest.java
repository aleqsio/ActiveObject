package com.company;

public class ConsumerMethodRequest implements MethodRequest {
    private int count;
    private Future<Integer> future;
    private ConsumerServant servant;

    public ConsumerMethodRequest(int count, Future<Integer> future, ConsumerServant servant) {
        this.count = count;
        this.future = future;
        this.servant = servant;
    }

    public boolean guard(){
        return servant.canConsume(count);
    }

    public void call(){
        future.resolve(servant.consume(count));
    }
}
