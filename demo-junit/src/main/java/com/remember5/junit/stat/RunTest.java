package com.remember5.junit.stat;

/**
 * @author wangjiahao
 * @date 2022/5/18 17:52
 */
public class RunTest {

    static {
        TestImpl test;
    }

    public static int increment(int a){
        return a + 1;
    }


    public static void main(String[] args) {
        TestImpl test = new TestImpl();
//        test.say();
//        System.err.println("hi");
    }
}
