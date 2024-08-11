package com.rsl.middleware.sdk.domain.service.impl;

import com.rsl.middleware.sdk.domain.model.Model;
import com.rsl.middleware.sdk.domain.service.AbstractChatGLMCodeReviewService;
import com.rsl.middleware.sdk.infrastructure.chatglm.IChatGLM;
import com.rsl.middleware.sdk.infrastructure.chatglm.dto.ChatCompletionRequestDTO;
import com.rsl.middleware.sdk.infrastructure.chatglm.dto.ChatCompletionSyncResponseDTO;
import com.rsl.middleware.sdk.infrastructure.git.GitCommand;
import com.rsl.middleware.sdk.infrastructure.weixin.WeiXin;
import com.rsl.middleware.sdk.infrastructure.weixin.dto.TemplateMessageDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @ description:
 * @ author: rsl
 * @ create: 2024-08-11 15:38
 **/
public class ChatGLMCodeReviewService extends AbstractChatGLMCodeReviewService {
    public ChatGLMCodeReviewService(GitCommand gitCommand, IChatGLM chatGLM, WeiXin weiXin) {
        super(gitCommand, chatGLM, weiXin);
    }

    @Override
    protected String getDiffCode() throws IOException, InterruptedException {
        return gitCommand.diff();
    }

    @Override
    protected String codeReview(String diffCode) throws Exception {
        ChatCompletionRequestDTO chatCompletionRequest = new ChatCompletionRequestDTO();
        chatCompletionRequest.setModel(Model.GLM_4_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequestDTO.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(new ChatCompletionRequestDTO.Prompt("user",
                        "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，按照以下格式对代码做出评审:" +
                                "1. 代码风格和一致性\n" +
                                "观察：\n" +
                                "[描述代码风格的观察结果，例如代码是否遵循既定的格式规则。]\n" +
                                "建议：\n" +
                                "[提供任何关于代码风格改进的建议，没有可以不写。]\n" +
                                "2. 修改内容概述\n" +
                                "变更描述：\n" +
                                "文件/类名： [具体说明涉及的文件或类名。]\n" +
                                "修改前： [描述代码在修改前的状态。]\n" +
                                "修改后： [描述代码在修改后的状态。]\n" +
                                "影响：\n" +
                                "[讨论此变更对代码库或程序的影响。]\n" +
                                "3. 代码逻辑\n" +
                                "解决的问题：\n" +
                                "问题描述： [明确问题是什么，为什么需要进行修改。]\n" +
                                "解决方案： [解释修改是如何解决问题的。]\n" +
                                "建议：\n" +
                                "[提供任何关于逻辑改进或进一步优化的建议。]\n" +
                                "4. 测试用例审查\n" +
                                "目的：\n" +
                                "[描述测试用例的目标]\n" +
                                "测试用例有效性：\n" +
                                "修改前： [描述修改前测试用例的表现。]\n" +
                                "修改后： [描述修改后测试用例的表现。]\n" +
                                "建议：\n" +
                                "[建议扩展或改进测试用例的方式]\n" +
                                "5. 评审总结\n" +
                                "积极方面：\n" +
                                "[列出此次变更的积极影响或正确处理的部分。]\n" +
                                "改进建议：\n" +
                                "[提出任何进一步的改进建议，例如增强测试覆盖率或改善代码结构等。]" +
                                "需要评审的代码如下:"));
                add(new ChatCompletionRequestDTO.Prompt("user", diffCode));
            }
        });

        ChatCompletionSyncResponseDTO completions = chatGLM.completions(chatCompletionRequest);
        ChatCompletionSyncResponseDTO.Message message = completions.getChoices().get(0).getMessage();
        return message.getContent();
    }

    @Override
    protected String recordCodeReview(String recommend) throws Exception {
        return gitCommand.commitAndPush(recommend);
    }

    @Override
    protected void pushMessage(String logUrl) throws Exception {
        Map<String, Map<String, String>> data = new HashMap<>();
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.REPO_NAME, gitCommand.getProject());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.BRANCH_NAME, gitCommand.getBranch());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_AUTHOR, gitCommand.getAuthor());
        TemplateMessageDTO.put(data, TemplateMessageDTO.TemplateKey.COMMIT_MESSAGE, gitCommand.getMessage());
        weiXin.sendTemplateMessage(logUrl, data);
    }

}
