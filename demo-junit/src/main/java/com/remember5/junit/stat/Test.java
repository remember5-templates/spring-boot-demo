package com.remember5.junit.stat;

/**
 * @author wangjiahao
 * @date 2022/5/18 17:49
 */
public abstract class Test {


    static {
        System.err.println("test static");
    }

    abstract void say();

}
