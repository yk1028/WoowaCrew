package woowacrew.keyword.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import woowacrew.keyword.domain.Keyword;
import woowacrew.keyword.domain.KeywordDto;
import woowacrew.keyword.domain.KeywordRepository;
import woowacrew.keyword.domain.KeywordResponse;
import woowacrew.keyword.exception.NotFoundKeyword;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KeywordServiceTest {

    @Mock
    private KeywordRepository mockKeywordRepository;

    @InjectMocks
    private KeywordService mockKeywordService;

    @Autowired
    private KeywordService keywordService;

    @Test
    void 정상적으로_검색어_저장() {
        Keyword mockKeyword = mock(Keyword.class);
        KeywordDto keywordDto = new KeywordDto("test");

        when(mockKeywordRepository.findByContent(anyString())).thenReturn(null);
        when(mockKeywordRepository.save(any())).thenReturn(mockKeyword);

        assertDoesNotThrow(() -> mockKeywordService.save(keywordDto));
        verify(mockKeywordRepository, times(1)).save(any());
        verify(mockKeyword, times(1)).increaseViews();
    }

    @Test
    @DisplayName("존재하는 검색어를 저장하면 조회수를 올린다.")
    void 중복된_검색어를_저장() {
        Keyword mockKeyword = mock(Keyword.class);
        when(mockKeywordRepository.findByContent(anyString())).thenReturn(mockKeyword);

        assertDoesNotThrow(() -> mockKeywordService.save(new KeywordDto("test")));
        verify(mockKeywordRepository, times(0)).save(any());
        verify(mockKeyword, times(1)).increaseViews();
    }

    @Test
    @DisplayName("조회수가 높은 순으로 검색어를 10개 정렬한다.")
    void 인기순으로_검색어를_10개_찾기() {
        List<KeywordResponse> keywords = keywordService.keywordRank();

        assertTrue(keywords.size() <= 10);

        assertThat(keywords.get(0).getContent()).isEqualTo("최다 조회수 A");
        assertThat(keywords.get(1).getContent()).isEqualTo("최다 조회수 B");
        assertThat(keywords.get(2).getContent()).isEqualTo("최다 조회수 C");
    }

    @Test
    void 검색어_조회수_증가() {
        Keyword keyword = new Keyword("검색어 조회수 증가");

        when(mockKeywordRepository.findById(anyLong())).thenReturn(Optional.of(keyword));

        assertThat(mockKeywordService.increaseViews(1L).getContent()).isEqualTo(keyword.getContent());
        assertTrue(keyword.getViews() != 0);
    }

    @Test
    @DisplayName("존재하지 않는 검색어를 증가시키는 경우 예외가 발생한다.")
    void 없는_검색어를_증가시키는_경우() {
        when(mockKeywordRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundKeyword.class, () -> mockKeywordService.increaseViews(1L));
    }
}