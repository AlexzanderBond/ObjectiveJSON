package edu.cbet.json;

public interface Filter {
    Filter ANY = name -> true;

    boolean allowProperty(String name);
}
