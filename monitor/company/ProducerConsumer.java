package com.company;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer {
    private static Buffer buffer = new Buffer(100);
    private Lock lock = new ReentrantLock();
    private Condition canBeProduced = lock.newCondition();
    private Condition canBeConsumed = lock.newCondition();

    public void produce(){

        int val = 1;
            lock.lock();
            try {
                while (!buffer.hasSpace())
                    canBeProduced.await();
                //producing
                buffer.put(val);
                val += 1;
                canBeConsumed.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
    }

    public void consume(){
            lock.lock();
            try {
                while (buffer.isEmpty())
                    canBeConsumed.await();
                //consuming

                System.out.println(buffer.get());
                canBeProduced.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
}



