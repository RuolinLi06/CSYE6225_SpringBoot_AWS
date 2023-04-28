package com.neu.webapp.controller;

import com.neu.webapp.model.User;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ruolin Li
 */
@RestController
@RequestMapping("/healthz")
public class HealthController {
    @Autowired
    private StatsDClient statsDClient;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public ResponseEntity<String> health(){
        statsDClient.incrementCounter("endpoint.healthz.get");
        logger.info("health");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
