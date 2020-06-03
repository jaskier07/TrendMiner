package pl.kania.trendminer.queryprocessor.cluster.model;

public enum ClusterSize {
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    ELEVEN;

    public static int getSize(ClusterSize size) {
        return size.ordinal() + 2;
    }

    public static ClusterSize next(ClusterSize size) {
        return values()[size.ordinal() + 1];
    }
}
