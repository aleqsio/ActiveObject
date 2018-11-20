package com.company;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerRandomSizeWithFlags {
    static int size = 200;
    private static CBuffer buffer = new CBuffer(size);

    private ReentrantLock lock = new ReentrantLock();
    private Condition firstProducer = lock.newCondition();
    private Condition firstConsumer = lock.newCondition();
    private Condition restOfProducers = lock.newCondition();
    private Condition restOfConsumers = lock.newCondition();

    private boolean hasWaitingFirstProducer = false;
    private boolean hasWaitingFirstConsumer = false;
    private boolean signalRestOfProducers = false;
    private boolean signalRestOfConsumers = false;

    public boolean canEnterProducer(int count){
        hasWaitingFirstProducer = size - buffer.getLength() < count;
        return !hasWaitingFirstProducer;
    }

    public void produce(int count, int value) {
            lock.lock();
            try {
                while (hasWaitingFirstProducer || signalRestOfProducers) {
                    restOfProducers.await();
                    signalRestOfProducers = false;
                }
                while (!canEnterProducer(count)) {
                    firstProducer.await();
                }
                for (int i = 0; i < count; i++) {
                    buffer.put(value);
                }
                signalRestOfProducers = lock.hasWaiters(restOfProducers);
                restOfProducers.signal();
                firstConsumer.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

    }

    public boolean canEnterConsumer(int count){
        hasWaitingFirstConsumer = buffer.getLength() < count;
        return !hasWaitingFirstConsumer;
    }

    public void consume(int count) {
            lock.lock();
            try {
                while (hasWaitingFirstConsumer || signalRestOfConsumers) {
                    restOfConsumers.await();
                    signalRestOfConsumers = false;

                }
                while (!canEnterConsumer(count)) {
                    firstConsumer.await();
                }
                for (int i = 0; i < count; i++) {
                    buffer.get();
                }
                signalRestOfConsumers = lock.hasWaiters(restOfConsumers);
                restOfConsumers.signal();
                firstProducer.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
    }
}
