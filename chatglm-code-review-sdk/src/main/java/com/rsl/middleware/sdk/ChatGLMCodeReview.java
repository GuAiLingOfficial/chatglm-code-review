package com.rsl.middleware.sdk;

import com.rsl.middleware.sdk.domain.service.impl.ChatGLMCodeReviewService;
import com.rsl.middleware.sdk.infrastructure.chatglm.IChatGLM;
import com.rsl.middleware.sdk.infrastructure.chatglm.impl.ChatGLM;
import com.rsl.middleware.sdk.infrastructure.git.GitCommand;
import com.rsl.middleware.sdk.infrastructure.weixin.WeiXin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @ description:sdk入口
 * META-INF 用来告知调用该SDK的程序SDK的入口是什么
 * @ author: rsl
 * @ create: 2024-08-06 16:51
 **/
public class ChatGLMCodeReview {
    private static final Logger logger = LoggerFactory.getLogger(ChatGLMCodeReview.class);


    // 微信配置
    private String weixin_appid = "wx39c8e979403732a2";
    private String weixin_secret = "9134bf7d1a9da45abad9a6e5411614e8";
    private String weixin_touser = "oLZcc6nU1py0vfnpJae8gjg0zZ7s";
    private String weixin_template_id = "4oQk1VWTjAsHvUJ0TIo4kTVgNSh4UdtbtEI3arGS0jQ";

    // ChatGLM 配置
    private String chatglm_apiHost = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private String chatglm_apiKeySecret = "";

    // Github 配置
    private String github_review_log_uri;
    private String github_token;

    // 工程配置 - 自动获取
    private String github_project;
    private String github_branch;
    private String github_author;

    public static void main(String[] args) throws Exception {
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        /**
         * 项目：{{repo_name.DATA}} 分支：{{branch_name.DATA}} 作者：{{commit_author.DATA}} 说明：{{commit_message.DATA}}
         */
        WeiXin weiXin = new WeiXin(
                getEnv("WEIXIN_APPID"),
                getEnv("WEIXIN_SECRET"),
                getEnv("WEIXIN_TOUSER"),
                getEnv("WEIXIN_TEMPLATE_ID")
        );


        IChatGLM chatGLM = new ChatGLM(getEnv("CHATGLM_APIHOST"), getEnv("CHATGLM_APIKEYSECRET"));

        ChatGLMCodeReviewService chatGLMCodeReviewService = new ChatGLMCodeReviewService(gitCommand, chatGLM, weiXin);
        chatGLMCodeReviewService.exec();

        logger.info("chatglm-code-review done!");
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }


}

