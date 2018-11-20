package com.company;

import java.util.concurrent.Semaphore;

public class Philosopher extends Thread {
    public static Semaphore[] semaphores = new Semaphore[5];
    public static Semaphore butler = new Semaphore(4);
    int number;
    public Philosopher(int number){
        this.number = number;
        semaphores[number] = new Semaphore(1);
    }
    public void run(){
        while(true) {
            System.out.println("Thinking " + number);
            // sleep(); //thinking
            System.out.println("End thinking " + number);
            try {
                butler.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                semaphores[number].acquire();
                semaphores[(number + 1) % 5].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Eating " + number);
            // sleep(); //eating
            System.out.println("End eating " + number);
            semaphores[number].release();
            semaphores[(number + 1) % 5].release();
            butler.release();
        }
    }
    public void sleep(){
        try {
            Thread.sleep((long)(Math.random() * 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}