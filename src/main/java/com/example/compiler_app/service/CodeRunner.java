package com.example.compiler_app.service;


import com.example.compiler_app.domain.CodeInfo;
import com.example.compiler_app.domain.ImageInfo;
import com.example.compiler_app.domain.OutputInfo;
import com.example.compiler_app.util.AnsiEscapeCodeUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class CodeRunner {
    private static CodeRunner codeRunnerObject;
    private DockerClientConfig config;
    private DockerHttpClient httpClient;
    private DockerClient dockerClient;

    CodeRunner() {
        config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }
    public static CodeRunner getInstance() {
        if (codeRunnerObject == null) {
            codeRunnerObject = new CodeRunner();
        }
        return codeRunnerObject;
    }

    public OutputInfo run(CodeInfo codeInfo) {
        String language = codeInfo.getLanguage();
        String fileName = codeInfo.getFileName();
        String fileExtension = codeInfo.getFileExtension();
        String source = codeInfo.getSourceCode();
        if(language=="" || fileName == "" || fileExtension == "") {
            List<String> output = new ArrayList<>();
            output.add("Error: Language or File Name or File Extension is empty");
            return OutputInfo.builder().isSuccess(false).output(output).build();
        }
        String codeDir = createDir(genDir());
        boolean flag = writeFile(codeDir,fileName,fileExtension, source);

        if(!flag) {
            System.out.println("Error writing code");
            return null;
        }


        ImageInfo image = buildImage(language, codeDir, fileName);
        System.out.println(image.toString());
        if(image.isSuccess()) {
            List<String> output = runContainer(image.getId());
            return OutputInfo.builder().output(output).isSuccess(true).build();
        }
        else {
            return OutputInfo.builder().output(image.getErrors()).isSuccess(false).build();
        }
    }

    private String genDir(){
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String datePath = now.format(formatter);
        String directory = datePath + "/" + UUID.randomUUID().toString().replaceAll("-", "");

        return directory;
    }

    private String createDir(String directory) {
        File file = new File("./code/"+directory);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    private ImageInfo buildImage(String language, String codeDir, String fileName) {
        log.info("Build Image Start");
        List<String> errors = new ArrayList<>();
        String imageId = "";
        String errorSyn = "\u001B";
        final AtomicBoolean flag = new AtomicBoolean(true);

        BuildImageCmd buildImageCmd = dockerClient.buildImageCmd()
                .withBaseDirectory(new File("./"))
                .withDockerfile(new File("dockerfiles/" + language + ".Dockerfile"))
                .withBuildArg("FILE_DIRECTORY_ARG", codeDir)
                .withBuildArg("FILENAME_ARG", fileName);
        try {
            imageId = buildImageCmd.exec(new BuildImageResultCallback() {
                @Override
                public void onNext(BuildResponseItem item) {
                    if (item.getStream() != null) {
                        log.info(item.getStream());
                        if (item.getStream().contains(errorSyn)) {
                            errors.add(AnsiEscapeCodeUtil.removeAnsiEscapeCodes(item.getStream()));
                            flag.set(false);
                        }
                    } else if (item.getErrorDetail() != null) {
                        flag.set(false);
                    }
                    super.onNext(item);
                }
            }).awaitImageId();
        } catch (Exception e) {
            System.out.println("Build Image: " + e.toString());
            flag.set(false);
        }
        if(!flag.get()){
            try{
                dockerClient.removeImageCmd(imageId).exec();
            } catch (Exception e){
                log.error(e.toString());
            }
        }
        log.info(""+flag.get());
        System.out.println("Build Image End");
        return ImageInfo.builder().id(imageId)
                .isSuccess(flag.get())
                .errors(errors)
                .build();
    }

    private List<String> runContainer(String imageId){
        log.info("Run Container Start");
        List<String> logs = new ArrayList<>();
        CreateContainerResponse container  = dockerClient.createContainerCmd(imageId)
                .withStopTimeout(1)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withStopTimeout(1)
                .withHostConfig(HostConfig.newHostConfig()
//                                .withMemory(6L*1024L*1024L)
                ).exec();

        dockerClient.startContainerCmd(container.getId())
                .exec();

        LogContainerCmd logContainerCmd = dockerClient.logContainerCmd(container.getId());
        logContainerCmd.withStdOut(true).withStdErr(true)
                .withTail(1).withFollowStream(true);
        try {
            logContainerCmd.exec(new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame item) {
//                    System.out.println(new String(item.getPayload()));
                    logs.add(new String(item.getPayload()));
                }
            }).awaitCompletion(2000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.info("Interrupted Exception!{}", e.getMessage());
        }


        System.out.println(dockerClient.inspectContainerCmd(container.getId()).exec().getState().getRunning());


        //stop container
        try{
            if(Boolean.TRUE.equals(dockerClient.inspectContainerCmd(container.getId()).exec().getState().getRunning())){
                dockerClient.stopContainerCmd(container.getId()).withTimeout(1).exec();
            }
        }catch (Exception e){
            log.error("Stop Container: {}", e.toString());
        }
        // remove container
        try{
            dockerClient.removeContainerCmd(container.getId()).exec();
        }catch (Exception e){
            log.error("Remove Container: {}", e.toString());
        }
        // remove image
        try{
            dockerClient.removeImageCmd(imageId).exec();
        }catch (Exception e){
            log.error("Build Container: " + e.toString());
        }



        log.info("Output Length" + logs.size());


//        https://github.com/docker-java/docker-java/issues/738
        return logs;
    }

//    private List<String> runContainer(String imageId){
//        log.info("Run Container Start");
//        List<String> output = new ArrayList<>();
//        CreateContainerResponse container  = dockerClient.createContainerCmd(imageId)
//                .withStopTimeout(1)
//                .withAttachStdout(true)
//                .withStopTimeout(1)
//                .exec();
//
//        AttachContainerCmd attachCommand = dockerClient.attachContainerCmd(container.getId())
//                .withStdOut(true)
//                .withStdErr(true)
//                .withFollowStream(true);
//        attachCommand.exec(new AttachContainerResultCallback() {
//            @Override
//            public void onNext(Frame item) {
//                log.info(new String(item.getPayload()));
//                output.add(new String(item.getPayload()));
//                super.onNext(item);
//            }
//        });
//        dockerClient.startContainerCmd(container.getId())
//                .exec();
//
//
//
//        dockerClient.stopContainerCmd(container.getId()).exec();
//        dockerClient.removeContainerCmd(container.getId()).exec();
//        dockerClient.removeImageCmd(imageId).exec();
//        log.info("Run Container End");
//
//
////        https://github.com/docker-java/docker-java/issues/738
//        return output;
//    }

    private boolean writeFile(String directory, String fileName,String fileExtension, String sourceCode) {
        File file = new File(new File(directory), fileName + fileExtension);
        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(sourceCode);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
