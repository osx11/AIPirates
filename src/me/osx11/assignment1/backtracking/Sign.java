package me.osx11.assignment1.backtracking;

public enum Sign {
    PLUS,
    MINUS,
    NOTHING;

    static int calculate(int n, int add, Sign sign) {
        switch (sign) {
            case PLUS:
                return n + add;
            case MINUS:
                return n - add;
            default: return n;
        }
    }
}
