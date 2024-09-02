package com.laboratorio.truthsocialapiinterface;

import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialNotificationListResponse;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 25/07/2024
 * @updated 01/09/2024
 */
public interface TruthsocialNotificationApi {
    // Obtiene las notificaciones del usuario. Quantity indica el número de registros a recuperar (cero significa todos). Se puede indicar una posición inicial (en su ausencia se asume que es nula).
    TruthsocialNotificationListResponse getAllNotifications() throws Exception;
    TruthsocialNotificationListResponse getAllNotifications(int limit) throws Exception;
    TruthsocialNotificationListResponse getAllNotifications(int limit, int quantity) throws Exception;
    TruthsocialNotificationListResponse getAllNotifications(int limit, int quantity, String posicionInicial) throws Exception;
}