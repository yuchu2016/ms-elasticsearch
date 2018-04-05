package com.yuchu.mselasticsearch.modules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @Author: yuchu
 * @Description:
 * @Date: Create in 16:16  2018/3/30
 * @Modified By:
 */

@Document(indexName = Article.INDEX, type = Article.TYPE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    public static final String INDEX = "teachnical_freebuf";
    public static final String TYPE = "freebuf";


    @Id
    @Field(type = FieldType.keyword)
    private String url_id;

    @Field(type = FieldType.text, searchAnalyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.text)
    private List<String> image_local;

    @Field(type = FieldType.Date)
    private Date create_time;
    @Field(type = FieldType.keyword)
    private String url;

    @Field(type = FieldType.keyword)
    private String author;

    @Field(type = FieldType.text, searchAnalyzer = "ik_max_word")
    private List<String> tags;

    @Field(type = FieldType.Integer)
    private Integer watch_nums;

    @Field(type = FieldType.Integer)
    private Integer comment_nums;

    @Field(type = FieldType.text, searchAnalyzer = "ik_max_word")
    private String content;

    private String highlightedMessage;

    private Float score;

}
