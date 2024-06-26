package com.kusitms29.backendH.domain.chat.entity;

import com.kusitms29.backendH.domain.sync.entity.Sync;
import com.kusitms29.backendH.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Document(collection = "room")
public class Room {
    @Id
    private String roomId;
    private String roomName;
    private String roomSession;
    private String syncName;
    private String image;
    private String ownerSession;

    @Builder.Default
    private List<ChatUser> chatUserList = new ArrayList<>();
    @Builder.Default
    private List<ChatContent> chatContentList = new ArrayList<>();

    public static Room createRoom(List<ChatUser> users, List<ChatContent> contents, String roomName, Sync sync, User user) {
        Room room = Room.builder().
                syncName(sync.getSyncName()).
                image(sync.getImage()).
                roomName(roomName).
                ownerSession(user.getSessionId()).
                build();
        for(ChatUser chatUser : users){
            room.addChatRoom(chatUser);
        }
        for(ChatContent chatContent : contents){
            room.addChatContent(chatContent);
        }
        return room;
    }
    public static Room chat(String roomName,ChatContent chatContent) {
        Room room = Room.builder()
                .roomName(roomName)
                .build();
        room.addChatContent(chatContent);
        return room;
    }
    public void addChatContent(ChatContent content) {
        this.chatContentList.add(content);
    }

    public void addChatRoom(ChatUser chatUser) {
        this.chatUserList.add(chatUser);
    }
    public Room(String roomId, String roomName, String roomSession, String syncName, String image, String ownerSession, List<ChatUser> chatUserList, List<ChatContent> chatContentList) {
        this.roomId = roomId;
        this.image = image;
        this.roomName = roomName;
        this.roomSession = roomSession;
        this.syncName = syncName;
        this.ownerSession = ownerSession;
        this.chatUserList = chatUserList != null ? chatUserList : new ArrayList<>();
        this.chatContentList = chatContentList != null ? chatContentList : new ArrayList<>();
    }
}
