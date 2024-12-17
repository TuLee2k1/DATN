package poly.com.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
         .addPathPatterns("/**")
         .excludePathPatterns("/auth/login", "/auth/authenticate", "/auth/register",
          "/css/**", "/js/**", "/images/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/logocompany/**")
                .addResourceLocations("file:./uploads/logocompany/");
        registry.addResourceHandler("/uploads/logoprofile/**")
                .addResourceLocations("file:./uploads/logoprofile/");
        registry.addResourceHandler("/Company/ApplicationProfile/download/**")
         .addResourceLocations("file:./uploads/fileCV/");

    }
}