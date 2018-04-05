package com.yuchu.mselasticsearch.Controller;

import com.yuchu.mselasticsearch.Repository.ArticleRepository;
import com.yuchu.mselasticsearch.modules.Article;
import com.yuchu.mselasticsearch.service.ArticleService;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yuchu
 * @Description:
 * @Date: Create in 16:24  2018/3/30
 * @Modified By:
 */
@RestController
@RequestMapping("/search")
public class ArticleController {
    @Autowired
    private ArticleRepository repository;
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private ArticleService articleService;
    @GetMapping("/articles")
    public List<String> getItem(@RequestParam("keyword") String keyword) {
        List<Article> articleList = repository.findByTitleLike(keyword);
        List<String> suggestions = new ArrayList<>();
        articleService.suggestSearch(keyword).forEach(suggest->{
            suggestions.add(suggest);
        });
        articleList.stream().forEach(article -> {
            suggestions.add(article.getTitle());
        });
//        return titles;
        return suggestions;
    }
    @GetMapping("/content")
    public Page<Article> getArticles(@RequestParam("keyword") String keyword,@RequestParam(name = "pageNum",defaultValue = "1")Integer pageNum){
        Page<Article> articlePage = articleService.highLightSearch(keyword,pageNum);
//        System.out.println(articlePage.getTotalPages());
//        System.out.println(articlePage.getTotalElements());
        return articlePage;
    }
    @GetMapping("/suggest")
    public List<String> moreLike(@RequestParam("keyword") String keyword){
//        List<String> articlePage = articleService.suggestSearch(keyword);
//        return articlePage;
        return null;
    }
//    @GetMapping("/q")
//    public List<Article> searchView(String keywords){
//
//    }

}
