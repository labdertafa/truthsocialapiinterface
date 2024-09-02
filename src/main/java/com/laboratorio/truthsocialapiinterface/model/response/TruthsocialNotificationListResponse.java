package com.laboratorio.truthsocialapiinterface.model.response;

import com.laboratorio.truthsocialapiinterface.model.TruthsocialNotification;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 25/07/2024
 * @updated 02/09/2024
 */
@Getter @Setter @AllArgsConstructor
public class TruthsocialNotificationListResponse {
    private String minId;
    private List<TruthsocialNotification> notifications;
}