package com.rsl.middleware.sdk.domain.service;

import com.rsl.middleware.sdk.infrastructure.chatglm.IChatGLM;
import com.rsl.middleware.sdk.infrastructure.git.GitCommand;
import com.rsl.middleware.sdk.infrastructure.weixin.WeiXin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @ description:
 * @ author: rsl
 * @ create: 2024-08-11 15:35
 **/
public abstract class AbstractChatGLMCodeReviewService implements IChatCLMCodeReviewService{
    private final Logger logger = LoggerFactory.getLogger(AbstractChatGLMCodeReviewService.class);

    protected final GitCommand gitCommand;
    protected final IChatGLM chatGLM;
    protected final WeiXin weiXin;

    public AbstractChatGLMCodeReviewService(GitCommand gitCommand, IChatGLM chatGLM, WeiXin weiXin) {
        this.gitCommand = gitCommand;
        this.chatGLM = chatGLM;
        this.weiXin = weiXin;
    }

    @Override
    public void exec() {
        try {
            // 1. 获取提交代码
            String diffCode = getDiffCode();
            // 2. 开始评审代码
            String recommend = codeReview(diffCode);
            // 3. 记录评审结果；返回日志地址
            String logUrl = recordCodeReview(recommend);
            // 4. 发送消息通知；日志地址、通知的内容
            pushMessage(logUrl);
        } catch (Exception e) {
            logger.error("chatglm-code-review error", e);
        }

    }

    protected abstract String getDiffCode() throws IOException, InterruptedException;

    protected abstract String codeReview(String diffCode) throws Exception;

    protected abstract String recordCodeReview(String recommend) throws Exception;

    protected abstract void pushMessage(String logUrl) throws Exception;

}
