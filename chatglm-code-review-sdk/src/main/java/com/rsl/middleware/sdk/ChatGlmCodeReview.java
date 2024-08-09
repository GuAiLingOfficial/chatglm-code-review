package com.rsl.middleware.sdk;

import com.alibaba.fastjson2.JSON;
import com.rsl.middleware.sdk.domain.model.ChatCompletionRequest;
import com.rsl.middleware.sdk.domain.model.ChatCompletionSyncResponse;
import com.rsl.middleware.sdk.domain.model.Model;
import com.rsl.middleware.sdk.types.utils.BearerTokenUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @ description:sdk入口
 * META-INF 用来告知调用该SDK的程序SDK的入口是什么
 * @ author: rsl
 * @ create: 2024-08-06 16:51
 **/
public class ChatGlmCodeReview {
    public static void main(String[] args) throws Exception {
        System.out.println("测试执行");

        // 1. 代码检出
        // 创建一个 ProcessBuilder 对象来运行 git diff HEAD~1 HEAD 命令，
        // 该命令比较当前提交 (HEAD) 和上一个提交 (HEAD~1) 之间的差异。
        // processBuilder.directory(new File(".")) 设置当前工作目录为代码所在目录。
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));

        // 启动进程
        Process process = processBuilder.start();


        // 通过 BufferedReader 读取 git diff 命令的输出，将每一行读取的内容追加到 StringBuilder 对象 diffCode 中。
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        StringBuilder diffCode = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            diffCode.append(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Exited with code:" + exitCode);

        System.out.println("评审代码：" + diffCode);

        // 2. chatglm 代码评审
        String log = codeReview(diffCode.toString());
        System.out.println("code review：" + log);

    }

    private static String codeReview(String diffCode) throws Exception {

        String apiKeySecret = "831bbe9ca955813008a0dc96f11d2c8d.LuGmnfI4Ef1Uc4rL";
        String token = BearerTokenUtils.getToken(apiKeySecret);

        URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);

        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel(Model.GLM_4_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(new ChatCompletionRequest.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:"));
                add(new ChatCompletionRequest.Prompt("user", diffCode));
            }
        });

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(chatCompletionRequest).getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }

        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        System.out.println("评审结果：" + content.toString());

        ChatCompletionSyncResponse response = JSON.parseObject(content.toString(), ChatCompletionSyncResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }



}

