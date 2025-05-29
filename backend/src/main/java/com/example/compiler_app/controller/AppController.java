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
    private final CodeRunner codeRunner;

    @CrossOrigin(origins="*")
    @PostMapping
    public OutputInfo java(@RequestBody CodeInfo codeInfo) {
        try{
            OutputInfo outputInfo = codeRunner.run(codeInfo);
            System.out.println(outputInfo);
            return outputInfo;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
