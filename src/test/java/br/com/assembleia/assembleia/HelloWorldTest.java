package br.com.assembleia.assembleia;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {
        String hello = "Hello World";
        assertEquals("Hello World", hello);
        assertTrue(hello.contains("Hello"));
        assertTrue(hello.contains("World"));
    }

    @Test
    public void testSimpleAddition() {
        int result = 2 + 2;
        assertEquals(4, result);
    }

    @Test
    public void testStringLength() {
        String test = "Test";
        assertEquals(4, test.length());
    }
}
