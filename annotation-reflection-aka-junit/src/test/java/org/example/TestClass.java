package org.example;

import org.junit.jupiter.api.Assertions;

public class TestClass {

    private int int1 = 0;
    private String s1 = "s1";

    public TestClass() {
    }

    public TestClass(int int1, String s1) {
        this.int1 = int1;
        this.s1 = s1;
    }

    @MyTestAnnotation
    void test1() {
        Assertions.assertEquals("OK", "OK");
    }

    @MyTestAnnotation
    private void test2() {
        Assertions.assertEquals("OK", "NOT_OK");
    }

    protected void test3() {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

    public int getInt1() {
        return int1;
    }

    public String getS1() {
        return s1;
    }
}
