package com.jobhunt.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

  @Value("${cloudflare.r2.access.key.id}")
  private String accessKeyId;

  @Value("${cloudflare.r2.secret.access.key}")
  private String secretAccessKey;

  @Value("${cloudflare.r2.endpoint}")
  private String endpoint;

  @Value("${cloudflare.r2.region}")
  private String region;

  @Bean
  public AmazonS3 s3Client() {
    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
    return AmazonS3ClientBuilder.standard()
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withPathStyleAccessEnabled(true)
        .build();
  }
}