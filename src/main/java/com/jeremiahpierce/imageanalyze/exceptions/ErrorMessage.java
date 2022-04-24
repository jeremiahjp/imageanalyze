package com.jeremiahpierce.imageanalyze.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorMessage {
    private int statusCode;
    private String message;
    private String description;

    public ErrorMessage(int statusCode, String message, String description) {
        this.statusCode = statusCode;
        this.message = message;
        this.description = description;
    }
}
