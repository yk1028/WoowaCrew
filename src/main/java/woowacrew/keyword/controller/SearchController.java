package woowacrew.keyword.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import woowacrew.keyword.domain.KeywordDto;
import woowacrew.keyword.domain.KeywordResponse;
import woowacrew.keyword.service.KeywordService;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";
    private static final String REDIRECT = "redirect:";
    private static final String UTF_8 = "UTF-8";

    private KeywordService keywordService;

    @Autowired
    public SearchController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @GetMapping("/search/rank")
    public ModelAndView searchRank() {
        ModelAndView modelAndView = new ModelAndView("keywordRank");

        List<KeywordResponse> keywordRank = keywordService.keywordRank();
        modelAndView.addObject("keywordRank", keywordRank);

        return modelAndView;
    }

    @PostMapping("/search")
    public String search(KeywordDto keywordDto) throws UnsupportedEncodingException {
        KeywordResponse keywordResponse = keywordService.save(keywordDto);
        logger.debug("Google search : {}, Keyword Id : {}", keywordResponse.getContent(), keywordResponse.getId());

        return REDIRECT + GOOGLE_SEARCH_URL + URLEncoder.encode(keywordResponse.getContent(), UTF_8);
    }

    @PostMapping("/search/{id}")
    public void increaseViews(@PathVariable Long id, HttpServletResponse response) throws UnsupportedEncodingException {
        KeywordResponse keywordResponse = keywordService.increaseViews(id);
        String url = GOOGLE_SEARCH_URL + URLEncoder.encode(keywordResponse.getContent(), UTF_8);

        response.setHeader("Location", url);
        logger.debug("Success keyword views to increase : {}", keywordResponse.getContent());
    }
}
