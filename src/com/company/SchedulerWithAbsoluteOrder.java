package com.company;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;

public class SchedulerWithAbsoluteOrder implements Scheduler {
    LinkedList<AbstractMap.SimpleEntry<Long, MethodRequest>> producerQueue = new LinkedList<>();
    LinkedList<AbstractMap.SimpleEntry<Long, MethodRequest>> consumerQueue = new LinkedList<>();
    Thread schedulerRuntimeThread;
    Long currentIndex = 0l;

    synchronized public void schedule(MethodRequest request) {
        if(request instanceof ConsumerMethodRequest){
            consumerQueue.add(new AbstractMap.SimpleEntry<>(currentIndex++, request));
        }
        if(request instanceof ProducerMethodRequest){
            producerQueue.add(new AbstractMap.SimpleEntry<>(currentIndex++, request));
        }
        if(schedulerRuntimeThread != null){
           synchronized (schedulerRuntimeThread){
               schedulerRuntimeThread.interrupt();
           }
        }
    }
    public void dispatch(){
        schedulerRuntimeThread = new Thread(() -> {
           while(true){
               AbstractMap.SimpleEntry<Long, MethodRequest> producerRequest = producerQueue.peekFirst();
               AbstractMap.SimpleEntry<Long, MethodRequest> consumerRequest = consumerQueue.peekFirst();
               if(producerRequest != null && consumerRequest == null && producerRequest.getValue().guard()) {
                   System.out.println("running producer");
                   producerQueue.getFirst();
                   producerRequest.getValue().call();
                   continue;
               }
               if(consumerRequest != null && producerRequest == null && consumerRequest.getValue().guard()) {
                   System.out.println("running consumer");
                   consumerQueue.getFirst();
                   consumerRequest.getValue().call();
                   continue;
               }
               if(consumerRequest != null && producerRequest != null) {
                   if (consumerRequest.getKey() > producerRequest.getKey() && producerRequest.getValue().guard()) {
                       System.out.println("running producer");
                       producerQueue.getFirst();
                       producerRequest.getValue().call();
                       continue;
                   }
                   if (consumerRequest.getValue().guard()) {
                       System.out.println("running consumer");
                       consumerQueue.getFirst();
                       consumerRequest.getValue().call();
                       continue;
                   }
               }
               System.out.println("no work, sleeping");
               synchronized (this) {
                   try {
                       wait();
                   } catch (InterruptedException e) {
                   }
               }
               continue;
           }
        });
        schedulerRuntimeThread.start();

    }
}
