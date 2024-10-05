package com.laboratorio.api;

import com.laboratorio.clientapilibrary.exceptions.ApiClientException;
import com.laboratorio.truthsocialapiinterface.impl.TruthsocialNotificationApiImpl;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialNotificationListResponse;
import com.laboratorio.truthsocialapiinterface.utils.TruthsocialApiConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import com.laboratorio.truthsocialapiinterface.TruthsocialNotificationApi;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 25/07/2024
 * @updated 05/10/2024
 */
public class TruthsocialNotificationApiTest {
    private String accessToken;
    private TruthsocialNotificationApi notificationApi;
    
    @BeforeEach
    private void initNotificationApi() {
        this.accessToken = TruthsocialApiConfig.getInstance().getProperty("access_token");
        this.notificationApi = new TruthsocialNotificationApiImpl(this.accessToken);
    }
    
    @Test
    public void get20Notifications() throws Exception { // Con default limit
        int cantidad  = 20;
        
        TruthsocialNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications(0, cantidad);

        assertTrue(notificationListResponse.getNotifications().size() >= 0);
    }
    
    @Test
    public void get20NotificationsWithLimit() throws Exception { // Con limit
        int cantidad  = 20;
        int limit = 50;
        
        TruthsocialNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications(limit, cantidad);

        assertTrue(notificationListResponse.getNotifications().size() >= 0);

    }
    
    @Test
    public void get110Notifications() throws Exception {
        int cantidad  = 110;
        
        TruthsocialNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications(0, cantidad);

        assertTrue(notificationListResponse.getNotifications().size() >= 0);
    }
    
    @Test
    public void getAllNotifications() throws Exception {
        TruthsocialNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications();

        assertTrue(notificationListResponse.getNotifications().size() >= 0);
    }
    
    @Test
    public void getNotificationError() {
        this.notificationApi = new TruthsocialNotificationApiImpl("INVALID_TOKEN");

        assertThrows(ApiClientException.class, () -> {
            this.notificationApi.getAllNotifications();
        });
    }
    
    /* @Test
    public void getNotificationsWithSinceId() throws Exception {
        String sinceId = "282577256";

        TruthsocialNotificationListResponse notificationListResponse = this.notificationApi.getAllNotifications(0, 0, sinceId);

        assertTrue(notificationListResponse.getNotifications().size() >= 0);
        assertTrue(notificationListResponse.getMaxId() != null);
    } */
}