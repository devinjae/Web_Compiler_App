package com.example.compiler_app.controller;

import com.example.compiler_app.domain.CodeInfo;
import com.example.compiler_app.domain.OutputInfo;
import com.example.compiler_app.service.CodeRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppController {

    @CrossOrigin(origins="*")
    @PostMapping
    public OutputInfo java(@RequestBody CodeInfo codeInfo) {
        CodeRunner codeRunner = CodeRunner.getInstance();
        return codeRunner.run(codeInfo);
    }
}
