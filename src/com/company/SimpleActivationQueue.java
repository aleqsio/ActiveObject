package com.company;

import java.util.LinkedList;

public class SimpleActivationQueue {
    LinkedList<MethodRequest> queue = new LinkedList<>();
    synchronized public void enqueue(MethodRequest request){
        queue.add(request);

    }
    synchronized public MethodRequest dequeue(){
        if(queue.peekFirst() == null) return null;
        return queue.pop();
    }
    synchronized public MethodRequest preview(){
        return queue.peekFirst();
    }
    synchronized public int size(){
        return queue.size();
    }
}
