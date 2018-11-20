package com.company;

import java.util.*;

public class Main {
    public static long synchronizedTaskLoad;
    public static long unsynchronizedTaskLoad;
    public static int successCount;
    public static int time = 5000;
    public static void main(String[] args) {

        for(synchronizedTaskLoad = 1; synchronizedTaskLoad <= 1000000000L; synchronizedTaskLoad*=10){
            for(unsynchronizedTaskLoad = 1; unsynchronizedTaskLoad <= 1000000000L; unsynchronizedTaskLoad *= 10){
                successCount =0;

                //runImplementationOscillating(processCount);
                runImpMonitor(3);
                System.out.println(3 + " "+synchronizedTaskLoad + " " + unsynchronizedTaskLoad+ " "+ successCount);
            }
        }
        System.out.println("end of monitor");

        for(synchronizedTaskLoad = 1; synchronizedTaskLoad <= 1000000000L; synchronizedTaskLoad*=10){
            for(unsynchronizedTaskLoad = 1; unsynchronizedTaskLoad <= 1000000000L; unsynchronizedTaskLoad *= 10){
                successCount =0;

                //runImplementationOscillating(processCount);
                runImplementationOscillating(3);
                System.out.println(3 + " "+synchronizedTaskLoad + " " + unsynchronizedTaskLoad+ " "+ successCount);
            }
        }
        System.out.println("end of ao");
        // runImplementationAbsOrder();
    }


    public static synchronized void onSuccess(){
        successCount+=1;
    }


    public static synchronized void onSuccess(int count){
        successCount+=1;
    }

    public static synchronized void onSuccess(boolean count){
        successCount+=1;
    }

    public static void runImpMonitor(int processCount){

        ProducerConsumerRandomSize pc = new ProducerConsumerRandomSize();
        long t= System.currentTimeMillis();
        long end = t+ time;

        Random r = new Random();
        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();
        for(int i=0;i<processCount;i++) {
            final int x = i;
            producers.add(new Thread(() -> {
                while (true) {
                    runTask(unsynchronizedTaskLoad);
                    pc.produce(r.nextInt() % 100 + 1, 1000 + r.nextInt() % 100);
                    onSuccess();
                    if(System.currentTimeMillis() > end) break;
                }
            }));
            consumers.add(new Thread(() -> {
                while (true) {
                    runTask(unsynchronizedTaskLoad);
                    pc.consume(r.nextInt() % 100 + 1);
                    onSuccess();
                    if(System.currentTimeMillis() > end) break;
                }
            }));
        }
        for (int i = 0; i <producers.size(); i++) {
            producers.get(i).start();
            consumers.get(i).start();
        }
        try {
            Thread.sleep(time + time/10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void runImplementationOscillating(int processCount) {
        SimpleActivationQueue producerQueue = new SimpleActivationQueue();
        SimpleActivationQueue consumerQueue = new SimpleActivationQueue();
        Scheduler scheduler = new OscillatingScheduler(producerQueue, consumerQueue);
        Buffer buffer = new Buffer(200);
        ConsumerServant consumerServant = new ConsumerServant(buffer);
        ConsumerProxy consumerProxy = new ConsumerProxy(scheduler, consumerServant);

        ProducerServant producerServant = new ProducerServant(buffer);
        ProducerProxy producerProxy = new ProducerProxy(scheduler, producerServant);
        spawnProcessesOnProxies(consumerProxy, producerProxy, processCount);

        scheduler.dispatch();

    }

    public static void runTask(long size){
        boolean test = false;
        for(long i=0;i<size;i++){
           test = !test;
        }
    }

    public static void spawnProcessesOnProxies(ConsumerProxy consumerProxy, ProducerProxy producerProxy, int processCount){
        Random random = new Random();
        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();
        long t= System.currentTimeMillis();
        long end = t+ time;
        for(int i=0;i<processCount;i++) {
            producers.add(new Thread(() -> {
                while (true) {
                    Future<Boolean> produceResult = producerProxy.produce(random.nextInt(100));
                    runTask(Main.unsynchronizedTaskLoad);
                    produceResult.then(Main::onSuccess);
                    if(System.currentTimeMillis() > end) break;
                }
            }));
            consumers.add(new Thread(() -> {
                while (true) {
                    Future<Integer> consumeResult = consumerProxy.consume(random.nextInt(100));
                    runTask(Main.unsynchronizedTaskLoad);
                    consumeResult.then(Main::onSuccess);
                    if(System.currentTimeMillis() > end) break;
                }
            }));
        }
        for (int i = 0; i < processCount; i++) {
            producers.get(i).start();
            consumers.get(i).start();
        }
    }
}
