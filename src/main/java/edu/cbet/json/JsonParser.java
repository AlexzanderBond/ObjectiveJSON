package edu.cbet.json;

import edu.cbet.json.impl.BufferSequence;
import edu.cbet.json.impl.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Stack;

public class JsonParser {
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 4; //4096 bytes
    public static final byte[] TRUE_BYTES = {'t', 'r', 'u', 'e'};
    public static final byte[] FALSE_BYTES = {'f', 'a', 'l', 's', 'e'};
    public static final byte[] NULL_BYTES = {'n', 'u', 'l', 'l'};
    private byte[] textBuffer;
    private int strLen;

    public JsonParser() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public JsonParser(int bufferSize) {
        this.textBuffer = new byte[bufferSize];
    }

    public JsonValue parseJson(String json) {
        try {
            return parse(new ByteContainer(json));
        } catch (IOException ignored) { //This shouldn't every happen every, like literally ever, the moon will escape Earth's gravity and the sun will implode before a String throws an IOException
            return JsonNull.NULL;
        }
    }

    public JsonValue parseJson(InputStream inputStream) throws IOException {
        return parse(new ByteContainer(inputStream));
    }

    public JsonValue parseJson(FileInputStream fileInputStream) throws IOException {
        return parse(new ByteContainer(fileInputStream));
    }

    public JsonValue parseJson(File file) throws IOException {
        try(FileInputStream fis = new FileInputStream(file)) {
            return parse(new ByteContainer(fis));
        }
    }

    public JsonValue parseJson(URL url) throws IOException {
        try (InputStream is = url.openStream()) {
            return parse(new ByteContainer(is));
        }
    }

    private JsonValue parse(ByteContainer container) throws IOException {
        byte[] bytes = container.getBytes();
        boolean isInString = false;
        strLen = 0;

        Stack<Pair<String, JsonValue>> valueStack = new Stack<>();

        Pair<String, JsonValue> current = new Pair<>();
        JsonValue value = null;
        String valueName = null;

        while(true) {
            int read = container.readBytes();

            if(read == -1)
                break;

            for (int x = 0; x < read; x++) {
                byte b = bytes[x];
                if (isInString) {
                    if (b == '\"') {
                        String str;
                        if (strLen == 0)
                            str = "";
                        else
                            str = new String(textBuffer, 0, strLen);
                        if (current.getRight().isObject()) {

                            if (valueName == null) {
                                valueName = str;
                            } else {
                                value = JsonString.valueOf(str);
                            }
                        } else if (current.getRight().isArray()) {
                            value = JsonString.valueOf(str);
                        }
                        strLen = 0;
                        isInString = false;
                    } else if(b == '\\') {
                        if(x+1 != read) {
                            byte n = bytes[++x];

                            if(n == 'n') {
                                _textChar((byte) '\n');
                            } else if(n == 't') {
                                _textChar((byte) '\t');
                            } else if(n == 'r') {
                                _textChar((byte) '\r');
                            } else if(n == 'b') {
                                _textChar((byte) '\b');
                            } else if(n == '0') {
                                _textChar((byte) '\0');
                            } else if(n == '\\') {
                                _textChar((byte) '\\');
                            } else if(n == '\"') {
                                _textChar((byte) '\"');
                            } else if(n == '\'') {
                                _textChar((byte) '\'');
                            } else if(n == 'u') {
                                try {
                                    byte[] uni = new byte[4];

                                    for(int i = 0; i < 4; i++) {
                                        if(i == read) {
                                            read = container.readBytes();

                                            if(read == -1)
                                                throw new IllegalArgumentException("Unicode character literal started but never finished at index " + x + ", json preview " + getSurroundingSection(bytes, x));

                                            x = 0;
                                        }
                                        uni[i] = bytes[++x];
                                    }

                                    char v = (char) Integer.parseInt(new BufferSequence(uni, 0, 4), 0, 4, 16);

                                    if (bytes[x + 1] == '\\' && bytes[x + 2] == 'u') {
                                        x+=2; //Move to the 'u' so that the next 4 bytes can be read

                                        for(int i = 0; i < 4; i++) {
                                            if(i == read) {
                                                read = container.readBytes();
                                                if(read == -1)
                                                    throw new IllegalArgumentException("Unicode character literal started but never finished at index " + x + ", json preview " + getSurroundingSection(bytes, x));
                                                x = 0;
                                            }
                                            uni[i] = bytes[++x];
                                        }

                                        char v1 = (char) Integer.parseInt(new BufferSequence(uni, 0, 4), 0, 4, 16);

                                        ByteBuffer bytes1 = StandardCharsets.UTF_8.encode(CharBuffer.allocate(2).append(v).append(v1).flip());

                                        for (int i = 0; i < bytes1.limit(); i++) {
                                            _textChar(bytes1.get());
                                        }
                                    } else {
                                        ByteBuffer bytes1 = StandardCharsets.UTF_8.encode(CharBuffer.allocate(1).append(v).flip());

                                        for (int i = 0; i < bytes1.limit(); i++) {
                                            _textChar(bytes1.get());
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    throw new IllegalArgumentException("Invalid unicode escape sequence at index " + x + " (" + e.getMessage() + ")" + ", json preview " + getSurroundingSection(bytes, x));
                                }
                            } else {
                                throw new IllegalArgumentException("Invalid character was escaped at index " + (x+1) + ", json preview " + getSurroundingSection(bytes, x));
                            }
                        }
                    } else {
                        _textChar(b);
                    }
                } else if (b != 0 && !Character.isWhitespace(b)) {
                    if (b == '\"') {
                        if (strLen != 0)
                            throw new IllegalArgumentException("Non whitespace character(s) before String at index " + x + ", json preview " + getSurroundingSection(bytes, x));
                        if (current.getRight() == null)
                            throw new IllegalArgumentException("Start of json should indicate an array or object, json preview '" + new String(bytes, 0, Math.min(10, bytes.length)) + '\'');
                        isInString = true;
                    } else if (b == '{') {
                        JsonObject obj = new JsonObject();
                        if (current.getRight() != null) {
                            if (current.getRight().isObject()) {
                                if (valueName == null)
                                    throw new IllegalArgumentException("Expected field name for object value at index " + x + ", json preview '" + getSurroundingSection(bytes, x) + '\'');
                            }
                            valueStack.push(current.copy());
                            current.setRight(obj);
                            current.setLeft(valueName);
                            valueName = null;
                        } else {
                            current.setRight(obj);
                            current.setLeft(valueName);
                            valueName = null;
                            valueStack.push(current.copy());
                        }
                    } else if (b == '[') {
                        JsonArray arr = new JsonArray();
                        if (current.getRight() != null) {
                            if (current.getRight().isObject()) {
                                if (valueName == null)
                                    throw new IllegalArgumentException("Expected field name for array value at index " + x + ", json preview '" + getSurroundingSection(bytes, x) + '\'');
                            }
                            valueStack.push(current.copy());
                            current.setRight(arr);
                            current.setLeft(valueName);
                            valueName = null;
                        } else {
                            current.setRight(arr);
                            current.setLeft(valueName);
                            valueName = null;

                            valueStack.push(current.copy());
                        }
                    } else if (b == ':') {
                        if (valueName == null) {
                            throw new IllegalArgumentException("Missing field name at index " + x + ", preview of the section '" + getSurroundingSection(bytes, x) + '\'');
                        }
                    } else if (b == '}') {
                        if (!(strLen == 0 && value == null)) {
                            if (value == null) {
                                value = parseValue(strLen, bytes, x);
                                strLen = 0;
                            } else if (valueName == null) {
                                throw new IllegalArgumentException("Missing field name at index " + x + ", preview of the section '" + getSurroundingSection(bytes, x) + '\'');
                            } else if (strLen != 0) {
                                throw new IllegalArgumentException("Multiple values found at index " + x + ", json preview '" + getSurroundingSection(bytes, x) + '\'');
                            }

                            if (current.getRight() == null)
                                throw new IllegalArgumentException("Object closed but was never opened");
                            else if (current.getRight().isArray())
                                throw new IllegalArgumentException("Expected object after closing curly bracket but got an array instead, json preview '" + getSurroundingSection(bytes, x) + '\'');

                            current.getRight().getAsObject().put(valueName, value);
                        }

                        valueName = current.getLeft();
                        value = current.getRight();

                        current = valueStack.pop();
                    } else if (b == ']') {
                        if (!(strLen == 0 && value == null)) {
                            if (value == null) {
                                value = parseValue(strLen, bytes, x);
                                strLen = 0;
                            } else if (strLen != 0) {
                                throw new IllegalArgumentException("Multiple values found at index " + x + ", json preview '" + getSurroundingSection(bytes, x) + '\'');
                            }

                            if (current.getRight() == null)
                                throw new IllegalArgumentException("Array closed but was never opened");
                            else if (current.getRight().isObject())
                                throw new IllegalArgumentException("Expected array after closing square bracket but got an array instead, json preview '" + getSurroundingSection(bytes, x) + '\'');

                            current.getRight().getAsArray().add(value);
                        }

                        valueName = current.getLeft();
                        value = current.getRight();

                        current = valueStack.pop();
                    } else if (b == ',') {
                        if (value == null) {
                            value = parseValue(strLen, bytes, x);
                            strLen = 0;
                        } else if (strLen != 0) {
                            throw new IllegalArgumentException("Multiple values found at index " + x + ", json preview '" + getSurroundingSection(bytes, x) + '\'');
                        }

                        if (current.getRight() == null)
                            throw new IllegalArgumentException("Expected the start of either an object or array found a comma instead '" + getSurroundingSection(bytes, 0) + '\'');
                        else if (current.getRight().isArray())
                            current.getRight().getAsArray().add(value);
                        else if (current.getRight().isObject()) {
                            if (valueName != null) {
                                current.getRight().getAsObject().put(valueName, value);
                                valueName = null;
                            } else {
                                throw new IllegalArgumentException("No field name for value in object at index " + x + ", json preview '" + getSurroundingSection(bytes, x) + '\'');
                            }
                        }

                        value = null;
                    } else {
                        _textChar(b);
                    }
                } else if (strLen != 0) {
                    if (value == null) {
                        value = parseValue(strLen, bytes, x);
                        strLen = 0;
                    } else {
                        throw new IllegalArgumentException("Multiple values found at index " + x + ", json preview '" + getSurroundingSection(bytes, x) + '\'');
                    }
                }
            }
        }

        if(valueStack.size() != 0) {
            throw new IllegalArgumentException("Unclosed objects and/or arrays in json");
        }

        if(current.getRight() == null) {
            return JsonNull.NULL;
        } else {
            return current.getRight();
        }
    }

    private void _textChar(byte b) {
        if(strLen == textBuffer.length) {
            this.textBuffer = Arrays.copyOf(this.textBuffer, this.textBuffer.length << 1);
        }

        this.textBuffer[strLen++] = b;
    }

    private JsonValue parseValue(int strLen, byte[] buffer, int index) {
        if(strLen == 0)
            throw new IllegalArgumentException("Json value can not have length of 0 at index " + index + ", json preview '" + getSurroundingSection(buffer, index) + '\'');

        if(strLen == 4 && Arrays.equals(textBuffer, 0, 4, TRUE_BYTES, 0, 4)) {
            return JsonBoolean.TRUE;
        } else if(strLen == 5 && Arrays.equals(textBuffer, 0, 5, FALSE_BYTES, 0, 5)) {
            return JsonBoolean.FALSE;
        } else if(strLen == 4 && Arrays.equals(textBuffer, 0, 4, NULL_BYTES, 0, 4)) {
            return JsonNull.NULL;
        }

        boolean number = true;
        boolean integer = true;
        boolean negative = false;

        for(int x = 0; x < strLen; x++) {
            byte b = textBuffer[x];
            if(b == '.') {
                integer = false;
            } else if(b == '-' && !negative) {
                negative = true;
            } else if(!Character.isDigit(b)) {
                number = false;
                integer = false;
                break;
            }
        }

        BufferSequence bufferSequence = new BufferSequence(textBuffer, 0, strLen);

        if(integer)
            return JsonNumber.valueOf(Long.parseLong(bufferSequence, 0, strLen, 10));
        else if(number)
            return JsonNumber.valueOf(Double.parseDouble(bufferSequence.toString()));
        else
            throw new IllegalArgumentException("Invalid json value at index " + index + ", json preview '" + bufferSequence + '\'');
    }

    private static String getSurroundingSection(byte[] buffer, int x) {
        int len = x + 10;
        int from = Math.max(x - 10, 0);

        int total = from + len;

        if(total > buffer.length) {
            len = buffer.length - from;

            if(len < 0) {
                from = 0;
                len = 0;
            }
        }

        return new String(buffer, from, len).replaceAll("[\n\t\r\0]", "");
    }

    private static class ByteContainer {
        private static final int BUFFER_SIZE = 8192;
        private final InputStream is;
        private final byte[] bytes;
        private boolean eof;

        private ByteContainer(InputStream is) {
            this.is = is;
            this.bytes = new byte[BUFFER_SIZE];
            this.eof = false;
        }

        private ByteContainer(@NotNull FileInputStream fileInputStream) throws IOException {
            long fileSize = fileInputStream.getChannel().size();

            this.is = fileInputStream;

            if(fileSize < BUFFER_SIZE) {
                this.bytes = new byte[(int)fileSize];
            } else {
                this.bytes = new byte[BUFFER_SIZE];
            }
            this.eof = false;
        }

        private ByteContainer(@NotNull String str) {
            this.bytes = str.getBytes(StandardCharsets.UTF_8);
            this.is = null;
            this.eof = false;
        }

        private int readBytes() throws IOException {
            if(!eof) {
                if (this.is != null) {
                    int read = this.is.read(bytes);

                    if (read == -1) {
                        this.eof = true;
                    }

                    return read;
                } else { //Return the strings UTF-8 length for the initial read
                    eof = true;
                    return this.bytes.length;
                }
            }

            return -1;
        }

        private byte[] getBytes() {
            return this.bytes;
        }
    }
}
