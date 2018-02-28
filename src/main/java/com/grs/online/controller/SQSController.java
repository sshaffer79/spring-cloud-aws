package com.grs.online.controller;

import com.grs.online.domain.MessageToProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/sqs")
public class SQSController {
    private static final Logger logger = LoggerFactory.getLogger(SQSController.class);

    @Value("${aws.sqs.name}")
    private String QUEUE_NAME;

    @Value("${aws.sqs.fifo.name}")
    private String FIFO_QUEUE_NAME;

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public SQSController(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @RequestMapping(value = "/message-processing-queue", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void sendMessageToMessageProcessingQueue(@RequestBody MessageToProcess message) {
        logger.info("Going to send message {} over SQS", message);

        this.queueMessagingTemplate.convertAndSend(QUEUE_NAME, message);
    }

    @SqsListener("${aws.sqs.name}")
    private void receiveMessage(MessageToProcess message, @Header("ApproximateFirstReceiveTimestamp") String approximateFirstReceiveTimestamp) {
        logger.info("Received SQS message {}", message);
    }

    @RequestMapping(value = "/message-processing-fifo-queue", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void sendMessageToMessageProcessingFifoQueue(@RequestBody MessageToProcess message) {
        logger.info("Going to send message {} over SQS FIFO", message);

        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("MessageGroupId", "testGroup-1");
        headerMap.put(SqsMessageHeaders.SQS_GROUP_ID_HEADER, "id-5");
        headerMap.put(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, "id-"+ UUID.randomUUID().toString());

        this.queueMessagingTemplate.convertAndSend(FIFO_QUEUE_NAME, message, new MessageHeaders(headerMap), new TestMessagePostProcessor());
    }

    @SqsListener("${aws.sqs.fifo.name}")
    private void receiveFifoMessage(MessageToProcess message, @Header("ApproximateFirstReceiveTimestamp") String approximateFirstReceiveTimestamp) {
        logger.info("Received SQS FIFO message {}", message);
    }

    public class TestMessagePostProcessor implements MessagePostProcessor {
        private final Logger logger = LoggerFactory.getLogger(TestMessagePostProcessor.class);

        public TestMessagePostProcessor() {
        }

        @Override
        public Message<?> postProcessMessage(Message<?> message) {
            logger.info(message.toString());
            return message;
        }
    }
}
