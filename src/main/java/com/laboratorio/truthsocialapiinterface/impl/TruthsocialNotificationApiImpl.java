package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.reflect.TypeToken;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialNotification;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialNotificationListResponse;
import java.util.List;
import com.laboratorio.truthsocialapiinterface.TruthsocialNotificationApi;

/**
 *
 * @author Rafael
 * @version 1.4
 * @created 25/07/2024
 * @updated 14/12/2025
 */
public class TruthsocialNotificationApiImpl extends TruthsocialBaseApi implements TruthsocialNotificationApi {
    public TruthsocialNotificationApiImpl(String accessToken) {
        super(accessToken);
    }
    
    @Override
    public TruthsocialNotificationListResponse getAllNotifications() throws Exception {
        return this.getAllNotifications(0);
    }
    
    @Override
    public TruthsocialNotificationListResponse getAllNotifications(int limit) throws Exception {
        return this.getAllNotifications(limit, 0);
    }

    @Override
    public TruthsocialNotificationListResponse getAllNotifications(int limit, int quantity) throws Exception {
        return this.getAllNotifications(limit, quantity, null);
    }
    
    // Función que devuelve una página de notificaciones de una cuenta
    private TruthsocialNotificationListResponse getNotificationPage(String uri, int limit, int okStatus, String posicionInicial) throws Exception {
        try {
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request.addApiPathParam("limit", Integer.toString(limit));
            if (posicionInicial != null) {
                request.addApiPathParam("min_id", posicionInicial);
            }
            
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            String minId = posicionInicial;
            List<TruthsocialNotification> notifications = gson.fromJson(response.getResponseStr(), new TypeToken<List<TruthsocialNotification>>(){}.getType());
            if (!notifications.isEmpty()) {
                log.debug("Se ejecutó la query: " + uri);
                log.debug("Resultados encontrados: " + notifications.size());

            List<String> linkHeaderList = response.getHttpHeaders().get("link");
                if ((linkHeaderList != null) && (!linkHeaderList.isEmpty())) {
                    String linkHeader = linkHeaderList.get(0);
                    log.debug("Recibí este link: " + linkHeader);
                    minId = this.extractMinId(linkHeader);
                    log.debug("Valor del min_id: " + minId);
                }
            }

            return new TruthsocialNotificationListResponse(minId, notifications);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error recuperando una página de notificaciones en Truthsocial", e);
        }
    }
    
    private boolean isContinuar(int quantity, List<TruthsocialNotification> notifications, String minId,
            TruthsocialNotificationListResponse notificationListResponse, int usedLimit) {
        log.debug("getAllNotifications. Cantidad: " + quantity + ". Recuperados: " + notifications.size() + ". Min_id: " + minId);
        if (notificationListResponse.getNotifications().isEmpty()) {
            return false;
        } else {
            if (quantity > 0) {
                if ((notifications.size() >= quantity) || (minId == null)) {
                    return false;
                }
            } else {
                if ((minId == null) || (notificationListResponse.getNotifications().size() < usedLimit)) {
                    return false;
                }
            }
        }
            
        return true;
    }

    @Override
    public TruthsocialNotificationListResponse getAllNotifications(int limit, int quantity, String posicionInicial) throws Exception {
        String endpoint = this.apiConfig.getProperty("getNotifications_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getNotifications_ok_status"));
        int defaultLimit = Integer.parseInt(this.apiConfig.getProperty("getNotifications_default_limit"));
        int maxLimit = Integer.parseInt(this.apiConfig.getProperty("getNotifications_max_limit"));
        int usedLimit = limit;
        if ((limit == 0) || (limit > maxLimit)) {
            usedLimit = defaultLimit;
        }
        List<TruthsocialNotification> notifications = null;
        boolean continuar;
        String minId = posicionInicial;
        
        if (quantity > 0) {
            usedLimit = Math.min(usedLimit, quantity);
        }
        
        do {
            TruthsocialNotificationListResponse notificationListResponse = getNotificationPage(endpoint, usedLimit, okStatus, minId);
            if (notifications == null) {
                notifications = notificationListResponse.getNotifications();
            } else {
                notifications.addAll(notificationListResponse.getNotifications());
            }

            minId = notificationListResponse.getMinId();
            continuar = this.isContinuar(quantity, notifications, minId, notificationListResponse, usedLimit);
        } while (continuar);

        if (quantity == 0) {
            return new TruthsocialNotificationListResponse(minId, notifications);
        }

        return new TruthsocialNotificationListResponse(minId, notifications.subList(0, Math.min(quantity, notifications.size())));
    }
}