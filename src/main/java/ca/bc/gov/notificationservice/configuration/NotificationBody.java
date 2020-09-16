package ca.bc.gov.notificationservice.configuration;

import com.google.gson.Gson;
import ca.bc.gov.notificationservice.sources.notification.models.Notification;

public class NotificationBody {
  private WebHookParams webHookParams = new WebHookParams();
  private Notification notification = new Notification();
  private String updateUrl;
  private String response;

  public WebHookParams getWebHookParams() {
    return webHookParams;
  }

  public void setWebHookParams(
      WebHookParams webHookParams) {
    this.webHookParams = webHookParams;
  }

  public Notification getNotification() {
    return notification;
  }

  public void setNotification(
      Notification notification) {
    this.notification = notification;
  }

  public String getUpdateUrl() {
    return updateUrl;
  }

  public void setUpdateUrl(String updateUrl) {
    this.updateUrl = updateUrl;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public NotificationBody() {
    super();
  }

  public NotificationBody(WebHookParams webHookParams,
      Notification notification, String updateUrl, String response) {
    this.webHookParams = webHookParams;
    this.notification = notification;
    this.updateUrl = updateUrl;
    this.response = response;
  }

  public NotificationBody(WebHookParams webHookParams,
      Notification notification, String updateUrl) {
    this.webHookParams = webHookParams;
    this.notification = notification;
    this.updateUrl = updateUrl;
  }

  public String toJson() {
    Gson jsonParser = new Gson();
    return jsonParser.toJson(this, NotificationBody.class);
  }

}
