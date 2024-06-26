package Backend.socket.api.chat.service.dto.response;

import Backend.socket.domain.chat.domain.ChatContent;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatMessageElementResponseDto {
    private String userName;
    private String content;
    private String time;
    private String sessionId;
    private String profile;
    private String image;
    private Boolean isOwner;


//    public static List<ChatMessageElementResponseDto> listOf(List<ChatContent> chatContentList,String sessionId,String profile) {
//        return chatContentList.stream()
//                .map(chatContent -> ChatMessageElementResponseDto.of(chatContent, sessionId, profile))
//                .collect(Collectors.toList());
//    }


    public static ChatMessageElementResponseDto of(ChatContent chatContent, String sessionId,String profile, String image,Boolean isOwner) {
        return ChatMessageElementResponseDto.builder()
                .userName(chatContent.getUserName())
                .content(chatContent.getContent())
                .time(chatContent.getTime().toString())
                .sessionId(sessionId)
                .profile(profile)
                .image(image)
                .isOwner(isOwner)
                .build();
    }

}

