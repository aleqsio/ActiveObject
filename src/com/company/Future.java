package com.company;

import java.util.function.Consumer;

public class Future<T> {
    public T getResult() {
        return result;
    }

    private T result = null;
    private boolean isBlocked = false;
    public Consumer<T> callback = null;

    public void then(Consumer<T> callback){
        this.callback = callback;
        if(result != null){
            callback.accept(result);
        }
    }

    synchronized public T block(){
        isBlocked = true;
        while(this.result == null){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.result;
    }

    synchronized public void resolve(T result){
        this.result = result;
        if(isBlocked){
            notify();
        }
        if(callback == null) return;
        callback.accept(result);
    }
}
