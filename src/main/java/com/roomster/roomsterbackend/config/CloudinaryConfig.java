package com.roomster.roomsterbackend.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig  {
    private final String CLOUD_NAME = "dqj1yqlkb";
    private final String API_KEY = "422286958622479";
    private final String API_SECRET = "BP8FFoFqJ9z8o1wmJe87F0HjNgw";
    @Bean
    public Cloudinary getCloudinary(){
        Map<String, String> config = new HashMap();
        config.put("cloud_name",CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret",API_SECRET);
        return new Cloudinary(config);
    }
}
