
package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        ProducerConsumerRandomSizeWithFlags pc = new ProducerConsumerRandomSizeWithFlags();
        int processCount = 100;
        int runCount = 10000;
        int[] produceTimes = new int[processCount];
        int[] consumeTimes = new int[processCount];
        final long startTime = System.nanoTime();
        for(int i=0;i<processCount;i++){
            produceTimes[i] = 0;
            consumeTimes[i] = 0;
        }
        Random r = new Random();
        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();
        for(int i=0;i<processCount;i++) {
            final int x = i;
            producers.add(new Thread(() -> {
                while (true) {
                    pc.produce(r.nextInt() % 100 + 1, 1000 + r.nextInt() % 100);
                    produceTimes[x] += 1;
                    if(produceTimes[x] == runCount){
                        System.out.println((System.nanoTime() - startTime)/1000 + ": Finished producing on process " + x + " with produceCounts  "+java.util.Arrays.toString(produceTimes)+" and consumeCounts "+java.util.Arrays.toString(consumeTimes));
                    }
                }
            }));
            consumers.add(new Thread(() -> {
                while (true) {
                    pc.consume(r.nextInt() % 100 + 1);
                    consumeTimes[x] += 1;
                    if(consumeTimes[x] == runCount){
                        System.out.println((System.nanoTime() - startTime)/1000 + ": Finished consuming on process " + x +" with produceCounts  "+java.util.Arrays.toString(produceTimes)+" and consumeCounts "+java.util.Arrays.toString(consumeTimes));
                    }
                }
            }));
        }
        for (int i = producers.size()-1; i >= 0; i--) {
            producers.get(i).start();
            consumers.get(i).start();
        }
        try {
            for (int i = 0; i < producers.size(); i++) {
                producers.get(i).join();
                consumers.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}