package com.example.compiler_app.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OutputInfo {
    boolean isSuccess;
    List<String> output;
    @Builder
    public OutputInfo(boolean isSuccess, List<String> output) {
        this.isSuccess = isSuccess;
        this.output = output;
    }
}
