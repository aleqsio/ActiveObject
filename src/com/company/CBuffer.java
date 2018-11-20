package com.company;

public class CBuffer {
    int length = 0;
    int start = 0;
    int size = 0;
    int[] elements;
    CBuffer(int size){
        this.size = size;
        elements = new int[size];
    }
    int get(){
        if(length<=0) throw new RuntimeException("tried to get from empty buffer");
        int result = elements[start % size];
        start = (start + 1) % size;
        length--;
        return result;
    }
    boolean put(int element){
        if(length >= size) throw new RuntimeException("tried to put in full buffer");
        elements[(start + length) % size] = element;
        length++;
        return true;
    }
    boolean hasSpace(){
        return this.length < this.size;
    }
    int getLength(){
        return this.length;
    }
    boolean isEmpty(){
        return this.length == 0;
    }

}