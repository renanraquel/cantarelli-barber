package com.gamecatarellibarber.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Cookie da sessão com SameSite=None e Secure para funcionar em cross-origin no Render
 * (front e back em domínios diferentes).
 */
@Configuration
@Profile("prod")
public class SessionCookieConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSION");
        serializer.setCookiePath("/");
        serializer.setUseSecureCookie(true);
        serializer.setSameSite("None");
        return serializer;
    }
}
