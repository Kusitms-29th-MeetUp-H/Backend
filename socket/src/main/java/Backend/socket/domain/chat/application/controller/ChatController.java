package Backend.socket.domain.chat.application.controller;


import Backend.socket.domain.chat.application.controller.dto.request.ChatListRequestDto;
import Backend.socket.domain.chat.application.controller.dto.request.ChatMessageListRequestDto;
import Backend.socket.domain.chat.application.controller.dto.request.ChatMessageRequestDto;
import Backend.socket.domain.chat.application.controller.dto.response.ChatListResponseDto;
import Backend.socket.domain.chat.application.controller.dto.response.ChatMessageListResponseDto;
import Backend.socket.domain.chat.application.controller.dto.response.ChatMessageResponseDto;
import Backend.socket.domain.chat.application.service.ChatService;
import Backend.socket.global.common.MessageSuccessCode;
import Backend.socket.global.common.MessageSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate template;
    private final RedisTemplate redisTemplate;
    public ChatController(ChatService chatService, SimpMessagingTemplate template, @Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        this.chatService = chatService;
        this.template = template;
        this.redisTemplate = redisTemplate;
    }
    @MessageMapping("/chat")
    public void sendChatMessage(@Header("sessionId") final String sessionId,
                                @RequestBody final ChatMessageRequestDto chatMessageRequestDto) {
        final ChatMessageResponseDto responseDto = chatService.createSendMessageContent(sessionId, chatMessageRequestDto);
        redisTemplate.convertAndSend("meetingRoom", responseDto);
    }

    @MessageMapping("/chat/detail")
    public void sendChatDetailMessage(@Header("sessionId") final String sessionId,
                                      @RequestBody final ChatMessageListRequestDto chatMessageListRequestDto) {
        final ChatMessageListResponseDto responseDto = chatService.sendChatDetailMessage(sessionId, chatMessageListRequestDto);
        template.convertAndSend("/sub/chat/" + sessionId, MessageSuccessResponse.of(MessageSuccessCode.MESSAGE, responseDto));
    }

    @MessageMapping("/chat/all")
    public void sendUserChatListMessage(@Header("sessionId") final String sessionId,
                                        @RequestBody final ChatListRequestDto chatListRequestDto) {
        final ChatListResponseDto responseDto = chatService.sendUserChatListMessage(sessionId, chatListRequestDto);
        template.convertAndSend("/sub/chat/" + sessionId, MessageSuccessResponse.of(MessageSuccessCode.CHATLIST, responseDto));
    }

}