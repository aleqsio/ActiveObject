package com.company;

public interface Scheduler {
    void schedule(MethodRequest request);
    void dispatch();
}
