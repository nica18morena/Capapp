package com.example.jessi.pictovent;

/**
 * Created by jessi on 1/27/2018.
 */

public class WordObject {
    public enum Type {
        MONTH, DAY, DURATION, TIME_HR, TIME_MIN
    }
    private final char C_NULL   = '\u0000';
    private Type type;
    private int size;
    private char l0;
    private char l1;
    private char l2;
    private char l3;
    private char l4;
    private char l5;
    private char l6;
    private char l7;
    private char l8;
    private char l9;

    public WordObject(Type _type, String _word){

        this.type = _type;
        this.size = _word.length();
        this.l0 = (size > 0)? _word.charAt(0): C_NULL;
        this.l1 = (size > 1)? _word.charAt(1): C_NULL;
        this.l2 = (size > 2)? _word.charAt(2): C_NULL;
        this.l3 = (size > 3)? _word.charAt(3): C_NULL;
        this.l4 = (size > 4)? _word.charAt(4): C_NULL;
        this.l5 = (size > 5)? _word.charAt(5): C_NULL;
        this.l6 = (size > 6)? _word.charAt(6): C_NULL;
        this.l7 = (size > 7)? _word.charAt(7): C_NULL;
        this.l8 = (size > 8)? _word.charAt(8): C_NULL;
        this.l9 = (size > 9)? _word.charAt(9): C_NULL;
    }

    public char getL0() {
        return l0;
    }

    public char getL1() {
        return l1;
    }

    public char getL2() {
        return l2;
    }

    public char getL3() {
        return l3;
    }

    public char getL4() {
        return l4;
    }

    public char getL5() {
        return l5;
    }

    public char getL6() {
        return l6;
    }

    public char getL7() {
        return l7;
    }

    public char getL8() {
        return l8;
    }

    public char getL9() {
        return l9;
    }

    public Type getType() {
        return type;
    }

    public int getSize() {
        return size;
    }
}
