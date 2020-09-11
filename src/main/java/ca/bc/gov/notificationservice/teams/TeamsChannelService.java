package ca.bc.gov.notificationservice.teams;

import ca.bc.gov.notificationservice.configuration.NotificationBody;
import ca.bc.gov.notificationservice.configuration.NotificationServiceProperties;
import ca.bc.gov.notificationservice.configuration.WebHookParams;
import ca.bc.gov.notificationservice.configuration.WebHookUrls;
import ca.bc.gov.notificationservice.service.ChannelService;
import ca.bc.gov.notificationservice.service.ChatApp;
import ca.bc.gov.notificationservice.sources.notification.models.Notification;
import ca.bc.gov.notificationservice.teams.models.TeamsAction;
import ca.bc.gov.notificationservice.teams.models.TeamsCard;
import ca.bc.gov.notificationservice.teams.models.TeamsChoice;
import ca.bc.gov.notificationservice.teams.models.TeamsFact;
import ca.bc.gov.notificationservice.teams.models.TeamsInput;
import ca.bc.gov.notificationservice.teams.models.TeamsPotentialActions;
import ca.bc.gov.notificationservice.teams.models.TeamsSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(NotificationServiceProperties.class)
public class TeamsChannelService implements ChannelService {
    Logger logger = LoggerFactory.getLogger(TeamsChannelService.class);

    @Autowired
    NotificationServiceProperties notificationServiceProperties;

    @Override
    public ChatApp getChatApp() {
        return ChatApp.TEAMS;
    }

    @Override
    public Object generatePayload(Notification notification, String webHookUrl, String updateUrl) {

        TeamsCard teamsCard = TeamsCard.defaultNttCard(notification.getAppName(), webHookUrl);

        teamsCard.addSection(getTeamsSection(notification));

        TeamsPotentialActions potentialActionsStatus = TeamsPotentialActions.defaultTeamsPotentialActions("ActionCard","Update Status");

        potentialActionsStatus.addInput(getTeamsInput());

        TeamsAction statusAction = TeamsAction.defaultTeamAction("HttpPOST", "OK", notificationServiceProperties.getUpdateCardBase());

        potentialActionsStatus.addAction(statusAction);

        teamsCard.addPotentialAction(getTeamsPotentialActionLink("View in Splunk", notification.getReturnUrl()));

        teamsCard.addPotentialAction(potentialActionsStatus);

//        notificationBody.setNotification(notification);
        WebHookParams webHookParams = new WebHookParams();
        webHookParams.addWebHookUrls(new WebHookUrls(ChatApp.TEAMS, webHookUrl));
//        notificationBody.setWebHookParams(webHookParams);
//        notificationBody.setResponse("{{statusList.value}}");

        NotificationBody notificationBody = new NotificationBody(webHookParams, notification, updateUrl, "{{statusList.value}}");

        statusAction.setBody(notificationBody.toJson());

        return  teamsCard;
    }

    @SuppressWarnings("java:S1602")
    private TeamsSection getTeamsSection(Notification notification) {
        TeamsSection teamsSection = TeamsSection.defaultNttSection(notification.getAppName(), notification
            .getOrigin());

        teamsSection.addFact(new TeamsFact("App", notification.getAppName()));
        teamsSection.addFact(new TeamsFact("Search", notification.getOrigin()));
        teamsSection.addFact(new TeamsFact("Owner", notification.getOwner()));

        notification.getDetails().forEach((key, value) -> {
            teamsSection.addFact(new TeamsFact(key, value.toString()));
        });

        teamsSection.addFact(new TeamsFact("Message", notification.getMessage()));
        teamsSection.addFact(new TeamsFact("Status", "Open"));
        return teamsSection;
    }

    private TeamsPotentialActions getTeamsPotentialActionLink(String message, String url) {
        TeamsPotentialActions potentialActionsLink = TeamsPotentialActions.defaultTeamsPotentialActions("ViewAction", message);
        potentialActionsLink.addTarget(url);
        return potentialActionsLink;
    }

    private TeamsInput getTeamsInput() {
        TeamsInput statusInput = TeamsInput.defaultTeamsInput("statuslist", "MultichoiceInput", "Update Status");

        statusInput.addChoice(new TeamsChoice("In Progress","In Progress"));
        statusInput.addChoice(new TeamsChoice("In Review","In Review"));
        statusInput.addChoice(new TeamsChoice("Closed","Closed"));
        return statusInput;
    }
}
