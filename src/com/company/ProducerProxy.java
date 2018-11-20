package com.company;

public class ProducerProxy {
    public ProducerProxy(Scheduler scheduler, ProducerServant servant) {
        this.scheduler = scheduler;
        this.servant = servant;
    }

    Scheduler scheduler;
    ProducerServant servant;

    Future<Boolean> produce(int count){
        Future<Boolean> future = new Future<>();
        MethodRequest request = new ProducerMethodRequest(count, future, servant);
        scheduler.schedule(request);
        return future;
    }
}
