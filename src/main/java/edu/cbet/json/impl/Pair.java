package edu.cbet.json.impl;

public class Pair<L, R> {
    private L left;
    private R right;

    public Pair() {
        this(null, null);
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public R getRight() {
        return right;
    }

    public L getLeft() {
        return left;
    }

    public Pair<L, R> copy() {
        return new Pair<>(left, right);
    }
}
