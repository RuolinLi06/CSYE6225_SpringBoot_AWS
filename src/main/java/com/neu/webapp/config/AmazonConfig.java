package com.neu.webapp.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Ruolin Li
 */
@Configuration
//@Profile("aws")
public class AmazonConfig {

    @Value("${amazonProperties.clientRegion}")
    private String clientRegion;
    @Bean
    public AmazonS3 s3() {

        return AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .build();
    }
}
