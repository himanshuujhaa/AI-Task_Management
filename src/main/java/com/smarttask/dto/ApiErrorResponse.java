package com.smarttask.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {

    private LocalDateTime timeStamp;
    private int status;
    private String message;
}
