package woowacrew.comment.utils;

import woowacrew.comment.domain.Comment;
import woowacrew.comment.dto.CommentResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class CommentConverter {
    private CommentConverter() {
    }

    public static CommentResponseDto toDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getAuthor().getNickname(),
                comment.getContent(),
                comment.getCreatedDate()
        );
    }

    public static List<CommentResponseDto> toDtos(List<Comment> comments) {
        return comments.stream()
                .map(CommentConverter::toDto)
                .collect(Collectors.toList());
    }
}
