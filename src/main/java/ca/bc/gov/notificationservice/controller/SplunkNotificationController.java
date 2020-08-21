package ca.bc.gov.notificationservice.controller;

import ca.bc.gov.notificationservice.configuration.NotificationServiceProperties;
import ca.bc.gov.notificationservice.configuration.WebHookParams;
import ca.bc.gov.notificationservice.service.WebHookService;
import ca.bc.gov.notificationservice.sources.notification.models.Notification;
import ca.bc.gov.notificationservice.sources.splunk.models.SplunkAlert;
import com.google.gson.Gson;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableConfigurationProperties(NotificationServiceProperties.class)
public class SplunkNotificationController {

    @Autowired
    WebHookService webHookService;

    @Autowired
    NotificationServiceProperties notificationServiceProperties;

    Logger logger = LoggerFactory.getLogger(SplunkNotificationController.class);

    @PostMapping(value = "splunk/{token}/{routes}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> alert(@PathVariable("token") String token,
                                        @PathVariable("routes") String routes,
                                        @RequestBody SplunkAlert splunkAlert) {
        if (!notificationServiceProperties.getTokens().contains(token)) {
            logger.error("Token failed to validate");
            return new ResponseEntity<>("Token validation failed", HttpStatus.UNAUTHORIZED);
        }

        Gson gson = new Gson();

        Notification notification = splunkAlert.convertToAlert();

        byte[] decodedRoutesBytes = Base64.getUrlDecoder().decode(routes);
        String decodedRoutesUrl = new String(decodedRoutesBytes);

        WebHookParams webHookParams = gson.fromJson(decodedRoutesUrl, WebHookParams.class);

        return webHookService.postMessage(notification, webHookParams);
    }
}
