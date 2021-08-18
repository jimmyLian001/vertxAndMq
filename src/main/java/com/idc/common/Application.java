package com.idc.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@ComponentScan(basePackages = "com.idc.common")
@EnableAspectJAutoProxy
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        Properties localProperties = readClassPathProperties();
        SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(localProperties);
        app.run(args);
    }


    private static Properties readClassPathProperties() throws Exception {
        Properties properties = new Properties();
        InputStream in = Application.class.getClassLoader().getResourceAsStream("config/local.properties");
        properties.load(in);
        return properties;
    }

}
