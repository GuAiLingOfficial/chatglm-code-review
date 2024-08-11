package com.rsl.middleware.sdk.infrastructure.chatglm;

import com.rsl.middleware.sdk.infrastructure.chatglm.dto.ChatCompletionRequestDTO;
import com.rsl.middleware.sdk.infrastructure.chatglm.dto.ChatCompletionSyncResponseDTO;

/**
 * @ description:
 * @ author: rsl
 * @ create: 2024-08-11 15:08
 **/
public interface IChatGLM {
    ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO requestDTO) throws Exception;
}
