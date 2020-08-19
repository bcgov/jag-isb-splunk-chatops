package ca.bc.gov.notificationservice.service;

import ca.bc.gov.notificationservice.configuration.WebHookParams;
import ca.bc.gov.notificationservice.sources.notification.models.Notification;
import org.springframework.http.ResponseEntity;

public interface WebHookService {
    ResponseEntity<String> postMessage(Notification notification, WebHookParams webHookParams);
}
