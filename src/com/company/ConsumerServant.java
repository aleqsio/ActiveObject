package com.company;


import static com.company.Main.runTask;

public class ConsumerServant {
    public ConsumerServant(Buffer buffer) {
        this.buffer = buffer;
    }

    public Buffer buffer;

    int consume(int count){
        runTask(Main.synchronizedTaskLoad);
        int sum = 0;
        for(int i=0;i<count;i++){
            sum += buffer.get();
        }
        return sum;
    }

    boolean canConsume(int count){
        return buffer.getLength() > count;
    }
}
