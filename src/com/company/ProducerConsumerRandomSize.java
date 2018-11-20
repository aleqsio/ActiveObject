package com.company;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.company.Main.runTask;
import static com.company.Main.synchronizedTaskLoad;

public class ProducerConsumerRandomSize {
    static int size = 200;
    private static CBuffer buffer = new CBuffer(size);

    private ReentrantLock lock = new ReentrantLock();
    private Condition firstProducer = lock.newCondition();
    private Condition firstConsumer = lock.newCondition();
    private Condition restOfProducers = lock.newCondition();
    private Condition restOfConsumers = lock.newCondition();

    public void produce(int count, int value) {
            lock.lock();
            try {
                while (lock.hasWaiters(firstProducer)) {
                    restOfProducers.await();
                }
                while (size - buffer.getLength() < count) {
                    firstProducer.await();
                }
                runTask(synchronizedTaskLoad);
                for (int i = 0; i < count; i++) {
                    buffer.put(value);
                }
                restOfProducers.signal();
                firstConsumer.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

    }

    public void consume(int count) {
            lock.lock();
            try {
                while (lock.hasWaiters(firstConsumer)) {
                    restOfConsumers.await();

                }
                while (buffer.getLength() < count) {
                    firstConsumer.await();
                }
                runTask(synchronizedTaskLoad);
                for (int i = 0; i < count; i++) {
                    buffer.get();
                }
                restOfConsumers.signal();
                firstProducer.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

    }

}