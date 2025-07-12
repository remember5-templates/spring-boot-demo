package com.remember5.junit.stat;

/**
 * @author wangjiahao
 * @date 2022/5/18 17:51
 */
public class TestImpl extends Test {
    static {
        System.err.println("TestImpl static");
    }


    @Override
    void say() {
        System.err.println("TestImpl say");
    }
}
