package com.drizzlepal.springboot.webstarter;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson2.filter.PropertyFilter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.drizzlepal.springboot.webstarter.props.DrizzlepalWebStarterProps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(DrizzlepalWebStarterProps.class)
@ComponentScan("com.drizzlepal.springboot.webstarter")
public class WebStarterConfig implements WebMvcConfigurer {

    /**
     * 配置消息转换器
     *
     * @param converters 消息转换器列表
     */
    @Override
    public void configureMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        // 创建并配置FastJson消息转换器
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setCharset(StandardCharsets.UTF_8);
        config.setWriterFilters(new NullFilter());
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        // 将自定义的转换器添加到列表中
        converters.add(0, converter);
    }

    // 定义一个过滤器，用于过滤掉空属性
    private static class NullFilter implements PropertyFilter {

        @Override
        public boolean apply(Object object, String name, Object value) {
            return value != null;
        }

    }

}
