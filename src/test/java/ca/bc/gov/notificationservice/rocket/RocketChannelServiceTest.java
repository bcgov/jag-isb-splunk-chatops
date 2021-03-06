package ca.bc.gov.notificationservice.rocket;

import ca.bc.gov.notificationservice.rocket.models.RocketMessage;
import ca.bc.gov.notificationservice.sources.notification.models.Notification;
import ca.bc.gov.notificationservice.sources.splunk.models.SplunkAlert;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RocketChannelServiceTest {

  private RocketChannelService sur;

  private static final String splunkAlertJson = "{\n" +
      "\t\"result\": {\n" +
      "\t\t\"message\" : \"message\",\n" +
      "\t\t\"other\" : \"other\",\n" +
      "\t\t\"_raw\" : \"_raw\",\n" +
      "\t\t\"source\": \"source\"\n" +
      "\t},\n" +
      "\t\"sid\" : \"sid\",\n" +
      "\t\"results_link\" : \"result_links\",\n" +
      "\t\"search_name\" : \"search_name\",\n" +
      "\t\"owner\" : \"owner\",\n" +
      "\t\"app\" : \"app\"\n" +
      "}";

  private static final String rocketText = "App: source \n"
      + "Search: search_name \n"
      + "Owner: owner \n"
      + "other: other \n"
      + "Message: message \n"
      + "Search Link: result_links \n";

  @BeforeAll
  public void setUp() { sur = new RocketChannelService(); }

  @Test
  @DisplayName("CASE 1: with valid splunk notification")
  public void withValidSplunkNotificationShouldProduceRocketObject() {
    Gson gson = new Gson();
    SplunkAlert splunkAlert = gson.fromJson(splunkAlertJson, SplunkAlert.class);
    splunkAlert.getResult().getDetails().put("other","other");

    Notification notification = splunkAlert.convertToAlert();

    RocketMessage actual = (RocketMessage) sur.generatePayload(notification, "TESTURL", "updateUrl");

    Assertions.assertEquals("source", actual.getAlias());
    Assertions.assertEquals("https://user-images.githubusercontent.com/60439815/92022236-ceeaad00-ed0f-11ea-8396-11fd19180c2c.png", actual.getAvatar());
    Assertions.assertEquals(rocketText, actual.getText());
  }
}
