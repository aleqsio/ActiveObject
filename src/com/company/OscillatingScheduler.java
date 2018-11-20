package com.company;

public class OscillatingScheduler implements Scheduler {
    SimpleActivationQueue producerQueue;
    SimpleActivationQueue consumerQueue;
    Thread schedulerRuntimeThread;
    public OscillatingScheduler(SimpleActivationQueue consumerQueue, SimpleActivationQueue producerQueue) {
        this.consumerQueue = consumerQueue;
        this.producerQueue = producerQueue;
    }

    public void schedule(MethodRequest request) {
        if(request instanceof ConsumerMethodRequest){
            consumerQueue.enqueue(request);
        }
        if(request instanceof ProducerMethodRequest){
            producerQueue.enqueue(request);
        }
        if(schedulerRuntimeThread != null){
           synchronized (schedulerRuntimeThread){
               schedulerRuntimeThread.interrupt();
           }
        }
    }
    public void dispatch(){
        schedulerRuntimeThread = new Thread(() -> {
            SimpleActivationQueue lastQueue = producerQueue;
            SimpleActivationQueue newQueue;
            long t= System.currentTimeMillis();
            long end = t+10000;
            while(System.currentTimeMillis() < end) {
                newQueue = (lastQueue == producerQueue ? consumerQueue : producerQueue);
                MethodRequest preferableRequest = newQueue.preview();
                MethodRequest otherRequest = lastQueue.preview();
                if (preferableRequest != null && preferableRequest.guard()) {
                    newQueue.dequeue().call();
                    lastQueue = newQueue;
                } else if (otherRequest != null && otherRequest.guard()) {
                    lastQueue.dequeue().call();
                } else {
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }

        });


        schedulerRuntimeThread.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
