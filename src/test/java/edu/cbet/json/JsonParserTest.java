package edu.cbet.json;

import org.junit.jupiter.api.Test;
import test.util.ExceptionTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonParserTest {

    @Test
    void testStringParseJson() {
        JsonParser parser = new JsonParser();

        ExceptionTest.<String, JsonValue>expectException(IllegalArgumentException.class, parser::parseJson, "[1 1]", "Multiple values found at index 4, json preview '[1 1]'");
        assertEquals(JsonNull.NULL, parser.parseJson(""));
        assertEquals(JsonObject.of(), parser.parseJson("{}"));
        assertEquals(JsonObject.of("field", "name"), parser.parseJson("{\"field\":\"name\"}"));
        assertEquals(JsonObject.of("field", "name"), parser.parseJson("  { \"field\"  :   \"name\"  }  "));
        assertEquals(JsonObject.of("field", "name", "innerObj", JsonObject.of("innerField", 342)), parser.parseJson("  { \"field\"  :   \"name\" , \"innerObj\": {\"innerField\" : 342} } "));
        assertEquals(JsonArray.of(), parser.parseJson("[]"));
        assertEquals(JsonArray.of(1), parser.parseJson("[1]"));
        assertEquals(JsonArray.of(1), parser.parseJson(" [1   ]   "));
        assertEquals(JsonArray.of(1, 2, 3), parser.parseJson("[1,2,3]"));
        assertEquals(JsonArray.of(1, 2, 3), parser.parseJson(" [ 1 ,  2 , 3  ] "));
        assertEquals(JsonArray.of(Long.MAX_VALUE), parser.parseJson("[%d]".formatted(Long.MAX_VALUE)));
        assertEquals(JsonArray.of(1.4d), parser.parseJson("[1.4]"));
        assertEquals(JsonArray.of(1, 2, 1.4d), parser.parseJson("[ 1 ,   2 ,   1.4 ]  "));
        assertEquals(JsonArray.of(1.4d, 2, 1), parser.parseJson("\0[\r1.4  \t       ,     2, \n 1]"));
    }

    @Test
    void testInputStreamParseJson() throws IOException {
        JsonParser parser = new JsonParser();

        assertEquals(JsonObject.of(" ", JsonArray.of(1, 2, null, "4", 5), "inner", JsonObject.of("", 3, "1", null), "1", JsonObject.of("3", 2)),
                parser.parseJson(JsonParserTest.class.getResourceAsStream("/TestFile.json")));
    }

    /*
     * The file input stream is handled slightly different by the JsonParser, it gets the length of the file to create a smaller buffer if necessary to save resources.
     */
    @Test
    void testFileInputStreamParseJson() throws Exception {
        File file = File.createTempFile("objective-json-test-", Long.toString(System.currentTimeMillis()));
        file.deleteOnExit();

        FileOutputStream fos = new FileOutputStream(file);

        fos.write("""
                {
                " ": [1, 2, null, "4", 5],
                "inner": {
                "": 3,
                "1": null
                },
                "1": {
                    "3": 2
                }
                }
                """.getBytes(StandardCharsets.UTF_8));

        fos.close();

        JsonParser parser = new JsonParser();

        assertEquals(JsonObject.of(" ", JsonArray.of(1, 2, null, "4", 5), "inner", JsonObject.of("", 3, "1", null), "1", JsonObject.of("3", 2)), parser.parseJson(file));
    }
}