package com.yuchu.mselasticsearch.Repository;

import com.yuchu.mselasticsearch.modules.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author: yuchu
 * @Description:
 * @Date: Create in 16:23  2018/3/30
 * @Modified By:
 */
public interface ArticleRepository extends ElasticsearchRepository<Article,String> {

    List<Article> findByTitleLike(String keywords);


    List<Article> findByTitleLikeOrTagsLikeOrContentLike(String keywords);
}
