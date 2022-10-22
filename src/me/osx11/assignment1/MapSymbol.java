package me.osx11.assignment1;

public enum MapSymbol {
    FREE('·'),
    JACK('J'),
    DAVY('D'),
    KRAKEN('K'),
    ROCK('R'),
    CHEST('C'),
    TORTUGA('T'),
    DANGER_ZONE('#'),
    PATH('@');

    public final char symbol;

    MapSymbol(char symbol) {
        this.symbol = symbol;
    }
}
