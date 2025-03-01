package org.example;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    public void sum() {
        int a = 2;
        int b = 1;

        assertEquals(a + b, new Main().sum(1, 2));

        System.out.println("Прачечная [f[f]");
    }
}
