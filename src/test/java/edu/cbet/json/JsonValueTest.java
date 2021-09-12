package edu.cbet.json;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SimplifiableAssertion")
public class JsonValueTest {

    @Test
    public void JsonBooleanTest() {
        assertTrue(JsonBoolean.FALSE.isBoolean());
        assertTrue(JsonBoolean.TRUE.isBoolean());
        assertTrue(JsonBoolean.FALSE.equals(false));
        assertTrue(JsonBoolean.TRUE.equals(true));
        assertFalse(JsonBoolean.FALSE.equals(true));
        assertFalse(JsonBoolean.TRUE.equals(false));
        assertTrue(JsonBoolean.FALSE.equals(JsonBoolean.FALSE));
        assertTrue(JsonBoolean.TRUE.equals(JsonBoolean.TRUE));
        assertEquals("Boolean", JsonBoolean.TRUE.getType());
        assertEquals(JsonBoolean.valueOf(true), JsonBoolean.TRUE);
        assertEquals(JsonBoolean.valueOf(false), JsonBoolean.FALSE);
        assertEquals(JsonBoolean.TRUE.toString(), "true");
        assertEquals(JsonBoolean.FALSE.toString(), "false");
    }
}
