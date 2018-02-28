package com.grs.online.controller;

import com.grs.online.domain.SNSNotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationSubject;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.aws.messaging.endpoint.NotificationStatus;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationMessageMapping;
import org.springframework.cloud.aws.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sns")
public class SNSController {
    private static final Logger logger = LoggerFactory.getLogger(SNSController.class);

    @Value("${aws.sns.name}")
    private String QUEUE_NAME;

    private final NotificationMessagingTemplate notificationMessagingTemplate;

    @Autowired
    public SNSController(NotificationMessagingTemplate notificationMessagingTemplate) {
        this.notificationMessagingTemplate = notificationMessagingTemplate;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void sendNotification(@RequestBody SNSNotificationMessage notification) {
        logger.debug("Going to send notification {}", notification);

        this.notificationMessagingTemplate.sendNotification(QUEUE_NAME, notification.getMessage(),
                notification.getSubject());
    }

    @NotificationSubscriptionMapping
    public void confirmSubscription(NotificationStatus notificationStatus) {
        notificationStatus.confirmSubscription();
    }

    @NotificationMessageMapping
    public void receiveNotification(@NotificationMessage String message, @NotificationSubject String subject) {
        logger.debug("Received SNS message {} with subject {}", message, subject);
    }
}
