package com.binarybeast.cipherstore.types;

import lombok.Getter;

@Getter
public enum Versions {
    V1("v1"), V2("v2");

    String simpleValue;

    Versions(String simpleValue) {
        this.simpleValue = simpleValue;
    }
}
