package com.gamecatarellibarber.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class UploadsConfig implements WebMvcConfigurer {

    private final String uploadsDir;

    public UploadsConfig(@Value("${app.uploads.dir:./uploads}") String uploadsDir) {
        this.uploadsDir = uploadsDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Path.of(uploadsDir).toAbsolutePath().normalize();
        String location = path.toUri().toString(); // file:/...

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location + "/");
    }
}

