package org.example.healbackend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path=System.getProperty("user.dir")+ File.separator+"upload"+File.separator;
        registry
                .addResourceHandler("/file/upload/*")
                .addResourceLocations("file:"+path);
    }

}
