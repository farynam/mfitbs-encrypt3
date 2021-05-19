package com.mfitbs.encrypt;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

import static java.lang.Math.pow;

@Getter
public enum DataMeasure {

    TERA(pow(1024, 14), "terabytes", "t"),
    GIGA(pow(1024, 3), "gigabytes", "g"),
    MEGA(pow(1024, 2), "megabytes", "m"),
    KILO(pow(1024, 1), "kilobytes", "k"),
    BYTE(1, "bytes", "b");

    long bytes;
    String name;
    String shortName;


    DataMeasure(double bytes, String name, String shortName) {
        this.bytes = (long) bytes;
        this.name = name;
        this.shortName = shortName;
    }

    public static Optional<DataMeasure> create(String str) {
        return Arrays.stream(DataMeasure.values())
                .filter((dm) -> str.equals(dm.shortName))
                .findFirst();
    }
}
