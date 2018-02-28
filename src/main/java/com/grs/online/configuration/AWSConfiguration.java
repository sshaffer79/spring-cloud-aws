package com.grs.online.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class AWSConfiguration {
    @Value("${aws.region:us-east-1}")
    private String region;

    @Autowired
    private AWSCredentialsProvider credentialProvider;

//    @Bean
//    public AmazonS3 amazonS3() {
//        System.out.println("STOP");
//        return AmazonS3ClientBuilder
//                .standard()
//                .withCredentials(credentialProvider)
//                .withRegion(Regions.fromName("us-east-1"))
//                .build();
//    }

    @Bean(destroyMethod = "shutdown")
    public AmazonSQSAsync amazonSQS() {
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withCredentials(credentialProvider)
                .withRegion(Regions.fromName(region))
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public AmazonSNSAsync amazonSNS() {
        return AmazonSNSAsyncClientBuilder
                .standard()
                .withCredentials(credentialProvider)
                .withRegion(Regions.fromName(region))
                .build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQS, ResourceIdResolver resourceIdResolver) {
        return new QueueMessagingTemplate(amazonSQS, resourceIdResolver);
    }

    @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNS amazonSNS, ResourceIdResolver resourceIdResolver) {
        return new NotificationMessagingTemplate(amazonSNS, resourceIdResolver);
    }
}
