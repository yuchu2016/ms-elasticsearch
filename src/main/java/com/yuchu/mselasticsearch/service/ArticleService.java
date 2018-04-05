package com.yuchu.mselasticsearch.service;

import com.yuchu.mselasticsearch.Repository.ArticleRepository;
import com.yuchu.mselasticsearch.modules.Article;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.MultiGetResultMapper;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.MoreLikeThisQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @Author: yuchu
 * @Description:
 * @Date: Create in 8:26  2018/4/1
 * @Modified By:
 */
@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public Page<Article> highLightSearch(String keyword, Integer pageNum) {
//        QueryBuilder queryBuilder = QueryBuilder;
        Pageable pager = new PageRequest(pageNum, 10);
        BoolQueryBuilder qb = QueryBuilders.boolQuery()
                .should(QueryBuilders.multiMatchQuery(keyword, "title", "content", "tags"));

        HighlightBuilder.Field contentField = new HighlightBuilder.Field("content")
                .preTags("<font color='#dd4b39'>")
                .postTags("</font>");
        HighlightBuilder.Field titleField = new HighlightBuilder.Field("title")
                .preTags("<font color='#dd4b39'>")
                .postTags("</font>");
        HighlightBuilder.Field tagsField = new HighlightBuilder.Field("tags")
                .preTags("<font color='#dd4b39'>")
                .postTags("</font>");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(matchQuery("title", keyword))
//                .withQuery(matchQuery("tags", keyword))
//                .withQuery(matchQuery("content", keyword))
                .withQuery(qb)
                .withHighlightFields(titleField)
//                .withHighlightFields(tagsField)
                .withHighlightFields(contentField)
                .withPageable(pager)
                .build();
//        Pageable pageable = new PageRequest(1,10);
        Page<Article> articlePage = elasticsearchTemplate.queryForPage(searchQuery, Article.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<Article> articleList = new ArrayList<>();
                // 总个数
                long total = response.getHits().getTotalHits();
                // 总页数
                int pages = (int) Math.ceil((double) total / pager.getPageSize());
                for (SearchHit searchHit : response.getHits()) {
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }
                    Article article = new Article();
                    article.setScore(searchHit.getScore());
                    Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                    HighlightField hlTitleField = highlightFields.get("title");
                    if (hlTitleField != null && hlTitleField.fragments() != null) {
                        article.setTitle(hlTitleField.fragments()[0].string());
                    } else {
                        article.setTitle((String) searchHit.getSource().get("title"));
                    }
//                    article.setTitle((String) searchHit.getSource().get("title"));
                    article.setUrl((String) searchHit.getSource().get("url"));
//                    article.setTitle(searchHit.getHighlightFields().get("title").fragments()[0].toString());
                    article.setAuthor((String) searchHit.getSource().get("author"));
                    article.setTags((List<String>) searchHit.getSource().get("tags"));
                    article.setWatch_nums((Integer) searchHit.getSource().get("watch_nums"));

                    HighlightField hlContentField = highlightFields.get("content");
                    if (hlContentField != null && hlContentField.fragments() != null) {
                        article.setContent(hlContentField.fragments()[0].string());
                    } else {
                        article.setContent(((String) searchHit.getSource().get("content")).substring(0,100));
                    }
//                    article.setHighlightedMessage(searchHit.getHighlightFields().get("content").fragments()[0].toString());
                    articleList.add(article);
                }
                if (articleList.size() > 0) {
//                    Pageable resultPage = new

                    return new AggregatedPageImpl<T>((List<T>) articleList, pageable, total);
//                    aggregatedPage.
                }
                return new AggregatedPageImpl<T>(null,pageable,total);
            }
        });
        return articlePage;
    }

    public Set<String> suggestSearch(String keyword) {
//        QueryBuilder queryBuilder = QueryBuilder;
        Set<String> articleList = new HashSet<>();
//        SuggestionBuilder<TermSuggestionBuilder> suggestionBuilder = new TermSuggestionBuilder("suggest").text(keyword).size(100);
        SuggestionBuilder<CompletionSuggestionBuilder> suggestionBuilder = new CompletionSuggestionBuilder("suggest").text(keyword).size(2);
        SuggestBuilder suggestBuilder = new SuggestBuilder().addSuggestion("suggest", suggestionBuilder);

        SearchResponse response = elasticsearchTemplate.suggest(suggestBuilder);
        if (response.getSuggest() != null && response.getSuggest().getSuggestion("suggest") != null) {
            List<? extends Suggest.Suggestion.Entry<Suggest.Suggestion.Entry.Option>> list = (List<? extends Suggest.Suggestion.Entry<Suggest.Suggestion.Entry.Option>>) response.getSuggest().getSuggestion("suggest").getEntries();
            for (Suggest.Suggestion.Entry<Suggest.Suggestion.Entry.Option> e : list) {
                for (Suggest.Suggestion.Entry.Option option : e) {
//                    Text text = option.getText();
//                    System.out.println(text.toString());
                    articleList.add(option.getText().toString());

                }
            }
        }
        return articleList;
    }
}
