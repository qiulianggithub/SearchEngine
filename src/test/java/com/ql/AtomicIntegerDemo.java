package com.ql;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 邱亮 on 2017/2/7.
 */
public class AtomicIntegerDemo {
    public static void main(String[] args) {
        AtomicInteger ai=new AtomicInteger(0);
        int i1=ai.get();
        v(i1);
        int i2=ai.getAndSet(5);
        v(i2);
        int i3=ai.get();
        v(i3);
        int i4=ai.getAndIncrement();
        v(i4);
        v(ai.get());
        int v5 = ai.getAndAdd(10);
        v(v5);
        v(ai.get());

    }
    static void v(int i)
    {
        System.out.println("i : "+i);
    }
}