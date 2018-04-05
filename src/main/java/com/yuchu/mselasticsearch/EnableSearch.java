package com.yuchu.mselasticsearch;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: yuchu
 * @Description:
 * @Date: Create in 16:28  2018/3/30
 * @Modified By:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({SearchConfig.class})
public @interface EnableSearch {
}
