package edu.cbet.json;

import org.junit.jupiter.api.Test;

import java.util.List;

public class JSONSerializerTest {
    @Test
    void toJson() {
        ObjectSerializer serializer = new ObjectSerializer();

        serializer.addFilter(TestClass1.class, name -> {
            if (name.equals("exclude")) {
                return false;
            }

            return true;
        });

        System.out.println(serializer.serializeValue(new TestClass1()));
    }

    class TestClass1 {
        private byte[] bytes = {1,4,1,5};
        private String str = "should be in quotes";
        private char c = '2';
        private long longValue = 43534583;
        private List<Object> objects = List.of("a", new TestClass2(), 'b');
        private String exclude = "Should not be in json";
        private Double nan = Double.POSITIVE_INFINITY;
    }

    class TestClass2 {
        byte a = 5;
        byte b = 2;
    }
}
