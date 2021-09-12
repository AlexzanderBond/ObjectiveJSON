package edu.cbet.json.impl;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BufferSequence implements CharSequence {
    private final byte[] bytes;
    private final int len;
    private final int from;

    public BufferSequence(byte[] bytes) {
        this.bytes = bytes;
        this.len = bytes.length;
        this.from = 0;
    }

    public BufferSequence(byte[] bytes, int from, int len) {
        this.bytes = bytes;
        this.len = len;
        this.from = from;
    }


    @Override
    public int length() {
        return len;
    }

    @Override
    public char charAt(int index) {
        if(index < 0 || index >= len)
            throw new IndexOutOfBoundsException();

        return (char) bytes[from + index];
    }

    @Override
    public boolean isEmpty() {
        return len == 0;
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        if(start < 0 || start >= len)
            throw new IndexOutOfBoundsException();
        else if(end > len || end < start)
            throw new IndexOutOfBoundsException();

        return new BufferSequence(bytes, from + start, from + end);
    }

    @Override
    public String toString() {
        return new String(bytes, from, len, StandardCharsets.UTF_8);
    }
}
