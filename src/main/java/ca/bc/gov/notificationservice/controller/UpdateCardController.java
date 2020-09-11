package ca.bc.gov.notificationservice.controller;

import ca.bc.gov.notificationservice.configuration.WebHookUrls;
import com.google.gson.Gson;
import ca.bc.gov.notificationservice.configuration.NotificationBody;
import ca.bc.gov.notificationservice.configuration.NotificationServiceProperties;
import ca.bc.gov.notificationservice.teams.TeamsChannelService;
import ca.bc.gov.notificationservice.teams.models.TeamsCard;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UpdateCardController {
  Logger logger = LoggerFactory.getLogger(UpdateCardController.class);

  @Autowired
  NotificationServiceProperties notificationServiceProperties;

  @Autowired
  TeamsChannelService teamsChannelService;

  @PostMapping(value = "update/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> update(@PathVariable("token") String token, @RequestBody NotificationBody teamsUpdate) {
      if (!notificationServiceProperties.getTokens().contains(token)) {
        logger.error("Token failed to validate");
        return new ResponseEntity<>("Token validation failed", HttpStatus.UNAUTHORIZED);
      }

      logger.info("Received message from teams");

      Optional<WebHookUrls> webHookUrl = teamsUpdate.getWebHookParams().getWebHookUrls().stream().findFirst();

      if (!webHookUrl.isPresent()) {
        return ResponseEntity.badRequest().body("Missing webHook Url.");
      }

      TeamsCard obj = (TeamsCard) teamsChannelService.generatePayload(teamsUpdate.getNotification(), webHookUrl.get().getUrl(), teamsUpdate.getUpdateUrl());

      obj.getSections().stream().findFirst().ifPresent(section -> {
        section.updateFact("Status", teamsUpdate.getResponse());
      });

      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.set("CARD-UPDATE-IN-BODY",
              "true");

      Gson postJson = new Gson();

      return ResponseEntity.ok()
              .headers(responseHeaders)
              .body(postJson.toJson(obj, Object.class));
  }
}
