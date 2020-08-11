package com.binarybeast.cipherstore.secret;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Secret {
    @NotNull
    private String mek;
    @NotNull
    private String dek;
}
