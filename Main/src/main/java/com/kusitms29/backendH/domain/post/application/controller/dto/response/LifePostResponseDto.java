package com.kusitms29.backendH.domain.post.application.controller.dto.response;

import com.kusitms29.backendH.domain.post.domain.PostType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class LifePostResponseDto {
    private long postId;
    private PostType postType;
    private String writer;
    private LocalDateTime createdDate;
    private String title;
    private String content;
    private int likeCnt;
    private boolean isLikedByUser;
    private int commentCnt;

    public static LifePostResponseDto of(Long postId, PostType postType, String writer,
                                         LocalDateTime createdDate, String title, String content,
                                         int likeCnt, boolean isLikedByUser, int commentCnt) {
        return LifePostResponseDto.builder()
                .postId(postId)
                .postType(postType)
                .writer(writer)
                .createdDate(createdDate)
                .title(title)
                .content(content)
                .likeCnt(likeCnt)
                .isLikedByUser(isLikedByUser)
                .commentCnt(commentCnt)
                .build();
    }
}
