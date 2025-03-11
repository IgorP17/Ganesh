import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Test
    public void sum() {
        int a = 2;
        int b = 1;

        assertEquals(a + b, new Main().sum(a, b));

    }
}
