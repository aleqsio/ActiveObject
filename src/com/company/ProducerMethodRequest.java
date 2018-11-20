package com.company;

public class ProducerMethodRequest implements MethodRequest {
    private int count;
    private Future<Boolean> future;
    private ProducerServant servant;

    public ProducerMethodRequest(int count, Future<Boolean> future, ProducerServant servant) {
        this.count = count;
        this.future = future;
        this.servant = servant;
    }

    public boolean guard(){
        return servant.canProduce(count);
    }

    public void call(){
        servant.produce(count);
        future.resolve(true);
    }
}
