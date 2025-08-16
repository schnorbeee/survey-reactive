package com.dynata.survayhw.handlers.responses;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ExceptionResponse {

    private HttpStatus statusCode;

    private String message;

    private String detail;
}
