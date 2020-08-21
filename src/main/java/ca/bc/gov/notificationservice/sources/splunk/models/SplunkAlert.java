package ca.bc.gov.notificationservice.sources.splunk.models;

import ca.bc.gov.notificationservice.service.AlertModel;
import ca.bc.gov.notificationservice.sources.notification.models.Notification;
import com.google.gson.annotations.SerializedName;

public class SplunkAlert implements AlertModel {
    private String 	sid;
    @SerializedName("results_link")
    private String resultsLink;
    @SerializedName("search_name")
    private String searchName;
    private String owner;
    private SplunkResult result;

    public SplunkAlert(String sid, String results_link, String search_name, String owner,
        SplunkResult result) {
        this.sid = sid;
        this.resultsLink = results_link;
        this.searchName = search_name;
        this.owner = owner;
        this.result = result;
    }

    public String getSid() {return sid;}

    public String getResultsLink() {return resultsLink;}

    public String getSearchName() {return searchName;}

    public String getOwner() {return owner;}

    public SplunkResult getResult() {return result;}

    @Override
    public Notification convertToAlert() {
        Notification notification = new Notification();

        notification.setAppName(this.getResult().getSource());
        notification.setOrigin(this.getSearchName());
        notification.setOwner(this.getOwner());
        notification.setMessage(this.getResult().getMessage());
        notification.setReturnUrl(this.getResultsLink());

        notification.addDetails(this.getResult().getDetails());

        return notification;
    }
}
