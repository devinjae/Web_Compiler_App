package com.example.compiler_app.util;

import com.example.compiler_app.domain.CodeInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileUtil {
    public static String createFile(CodeInfo codeInfo) throws IOException {
        // Generate random directory
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String datePath = now.format(formatter);
        String randDir = datePath + "/" + UUID.randomUUID().toString().replaceAll("-", "");

        // Create random directory
        File dir = new File("./code/"+randDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Write a file inside the random directory
        File sourceFile = new File(new File(dir.getPath()), codeInfo.getFileName() + codeInfo.getFileExtension());
        try (FileWriter fw = new FileWriter(sourceFile); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(codeInfo.getSourceCode());
            bw.newLine();
        }

        return dir.getPath();
    }
}
