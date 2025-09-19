package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SearchUtilTest {

    private static final String[] array1 = new String[]{"apple", "banana", "cherry"};

    @Test
    void testContainsFound(){
        assertTrue(
             SearchUtil.getBuilder()
                     .withArray(array1)
                     .build()
                     .contains("banana"),
                "banana isn't in the house?"
        );
    }

    @Test
    void testContainsNotFound(){
        assertFalse(
                SearchUtil.getBuilder()
                        .withArray(array1)
                        .build()
                        .contains("buya"),
                "buya is in the house?"
        );
    }

}
