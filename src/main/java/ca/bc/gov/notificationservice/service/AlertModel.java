package ca.bc.gov.notificationservice.service;

import ca.bc.gov.notificationservice.sources.notification.models.Notification;

public interface AlertModel {

  Notification convertToAlert();

}
