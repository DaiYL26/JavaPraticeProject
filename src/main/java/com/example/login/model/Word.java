package com.example.login.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Word {
    private Integer id;
    private String word;
    private String json;
}
