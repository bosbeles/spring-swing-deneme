package org.example;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@org.springframework.context.annotation.Configuration
public class Configuration {


    @Bean
    @Qualifier("menuList")
    public List<String> menuList() {
        return Arrays.asList("Window 1", "Window 2", "Window 3");
    }
}
