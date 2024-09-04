package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialNotification;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialNotificationListResponse;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import com.laboratorio.truthsocialapiinterface.TruthsocialNotificationApi;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 25/07/2024
 * @updated 04/09/2024
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
    private TruthsocialNotificationListResponse getNotificationPage(String url, int limit, int okStatus, String posicionInicial) throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = null;
        
        try {
            WebTarget target = client.target(url)
                        .queryParam("limit", limit);
            if (posicionInicial != null) {
                target = target.queryParam("min_id", posicionInicial);
            }
            
            response = this.createRequest(client, target, url)
                    .get();
            
            String jsonStr = processResponse(response);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new TruthsocialApiException(TruthsocialNotificationApiImpl.class.getName(), str);
            }
            
            Gson gson = new Gson();
            String minId = posicionInicial;
            List<TruthsocialNotification> notifications = gson.fromJson(jsonStr, new TypeToken<List<TruthsocialNotification>>(){}.getType());
            if (!notifications.isEmpty()) {
                log.debug("Se ejecutó la query: " + url);
                log.debug("Resultados encontrados: " + notifications.size());

                String linkHeader = response.getHeaderString("link");
                log.debug("Recibí este link: " + linkHeader);
                minId = this.extractMinId(linkHeader);
                log.debug("Valor del min_id: " + minId);
            }

            // return accounts;
            return new TruthsocialNotificationListResponse(minId, notifications);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (TruthsocialApiException e) {
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
        }
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
        boolean continuar = true;
        String min_id = posicionInicial;
        
        if (quantity > 0) {
            usedLimit = Math.min(usedLimit, quantity);
        }
        
        try {
            do {
                TruthsocialNotificationListResponse notificationListResponse = getNotificationPage(endpoint, usedLimit, okStatus, min_id);
                if (notifications == null) {
                    notifications = notificationListResponse.getNotifications();
                } else {
                    notifications.addAll(notificationListResponse.getNotifications());
                }
                
                min_id = notificationListResponse.getMinId();
                log.info("getAllNotifications. Cantidad: " + quantity + ". Recuperados: " + notifications.size() + ". Min_id: " + min_id);
                if (notificationListResponse.getNotifications().isEmpty()) {
                    continuar = false;
                } else {
                    if (quantity > 0) {
                        if ((notifications.size() >= quantity) || (min_id == null)) {
                            continuar = false;
                        }
                    } else {
                        if ((min_id == null) || (notificationListResponse.getNotifications().size() < usedLimit)) {
                            continuar = false;
                        }
                    }
                }
            } while (continuar);

            if (quantity == 0) {
                return new TruthsocialNotificationListResponse(min_id, notifications);
            }
            
            return new TruthsocialNotificationListResponse(min_id, notifications.subList(0, Math.min(quantity, notifications.size())));
        } catch (Exception e) {
            throw e;
        }
    }
}