package com.example.compiler_app.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CodeInfo {
    private String fileName;
    private String fileExtension;
    private String sourceCode;
    private String language;
    @Builder
    public CodeInfo(String fileName, String fileExtension, String sourceCode, String language) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.sourceCode = sourceCode;
        this.language = language;
    }

}
