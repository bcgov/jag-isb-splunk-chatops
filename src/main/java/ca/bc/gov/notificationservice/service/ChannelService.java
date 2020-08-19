package ca.bc.gov.notificationservice.service;

import ca.bc.gov.notificationservice.sources.notification.models.Notification;

public interface ChannelService {

    ChatApp getChatApp();

    Object generatePayload(Notification notification, String webHookUrl);

}
