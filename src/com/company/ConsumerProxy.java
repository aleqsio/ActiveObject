package com.company;

public class ConsumerProxy {
    public ConsumerProxy(Scheduler scheduler, ConsumerServant servant) {
        this.scheduler = scheduler;
        this.servant = servant;
    }

    Scheduler scheduler;
    ConsumerServant servant;

    Future<Integer> consume(int count){
        Future<Integer> future = new Future<>();
        MethodRequest request = new ConsumerMethodRequest(count, future, servant);
        scheduler.schedule(request);
        return future;
    }
}
