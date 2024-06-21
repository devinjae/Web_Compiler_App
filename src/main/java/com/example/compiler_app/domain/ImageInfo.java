package com.example.compiler_app.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class ImageInfo {
    private String id;
    private boolean isSuccess;
    private List<String> errors;

    @Builder
    public ImageInfo(String id, boolean isSuccess, List<String> errors) {
        this.id = id;
        this.isSuccess = isSuccess;
        this.errors = errors;
    }
}
