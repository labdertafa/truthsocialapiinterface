package com.laboratorio.truthsocialapiinterface.model;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 25/07/2024
 * @updated 01/09/2024
 */
public enum TruthsocialNotificationType {
    MENTION, STATUS, REBLOG, FOLLOW, FOLLOW_REQUEST, FAVOURITE, POLL, UPDATE, ADMIN_SIGN_UP, ADMIN_REPORT, SEVERED_RELATIONSHIPS, MODERATION_WARNING;
    
    public static TruthsocialNotificationType fromString(String value) {
        for (TruthsocialNotificationType type : TruthsocialNotificationType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant " + TruthsocialNotificationType.class.getCanonicalName() + "." + value);
    }
}