package com.pollerianorkys.restaurant.config;

import com.pollerianorkys.restaurant.security.SessionTimeoutInterceptor;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SessionTimeoutInterceptor sessionTimeoutInterceptor;

    public WebConfig(SessionTimeoutInterceptor sessionTimeoutInterceptor) {
        this.sessionTimeoutInterceptor = sessionTimeoutInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
    }
     //para inicializar el ¡18n
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor
                = new LocaleChangeInterceptor();
        localeChangeInterceptor.
                setParamName("lang");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Interceptor para manejo de idiomas (i18n)
        registry.addInterceptor(localeChangeInterceptor());
        
        // Interceptor para timeout de sesión (aplicar solo a rutas autenticadas)
        registry.addInterceptor(sessionTimeoutInterceptor)
                .excludePathPatterns("/css/**", "/js/**", "/img/**", 
                                   "/auth/login", "/auth/register", "/", "/home",
                                   "/complaints/book", "/libro-reclamaciones", 
                                   "/carta", "/menu/carta", "/promociones", 
                                   "/locales", "/nosotros", "/trabaja-con-nosotros");
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver
                = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("es", "PE"));
        
        return localeResolver;
    }
        @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
