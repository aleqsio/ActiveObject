package com.company;

import static com.company.Main.runTask;

public class ProducerServant {
    public ProducerServant(Buffer buffer) {
        this.buffer = buffer;
    }

    public Buffer buffer;

    void produce(int count){
        runTask(Main.synchronizedTaskLoad);
        for(int i=0;i<count;i++){
            buffer.put(i);
        }
    }

    boolean canProduce(int count){
        return buffer.getSpace() > count;
    }
}
