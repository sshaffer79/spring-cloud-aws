package com.grs.online.controller;

import com.amazonaws.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/s3")
public class S3Controller {
    private static final Logger LOG = LoggerFactory.getLogger(S3Controller.class);
    private static final String S3_PATH_FORMAT = "s3://%s/%s/%s";

    private ResourceLoader resourceLoader;

    @Value("${aws.s3.bucket.name}")
    private String S3_BUCKET_NAME;

    @Value("${aws.s3.file.name}")
    private String S3_FILE_NAME;

    @Autowired
    public S3Controller(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(value = "/get/{uuid}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void sendMessageToMessageProcessingQueue(@PathVariable final UUID uuid) throws IOException {
        LOG.info("Getting S3 Resource");

        Resource resource = resourceLoader.getResource(String.format(S3_PATH_FORMAT, S3_BUCKET_NAME, uuid.toString(), S3_FILE_NAME));
        InputStream inputStream = resource.getInputStream();
        LOG.info(IOUtils.toString(inputStream));
    }
}
