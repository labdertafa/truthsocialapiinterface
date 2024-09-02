package com.laboratorio.truthsocialapiinterface.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 24/07/2024
 * @updated 01/09/2024
 */
@Getter @Setter @AllArgsConstructor
public class TruthsocialFollowResponse {
    private String id;
    private boolean following;
    private boolean showing_reblogs;
    private boolean notifying;
    private String languages;
    private boolean followed_by;
    private boolean blocking;
    private boolean blocked_by;
    private boolean muting;
    private boolean muting_notifications;
    private boolean requested;
    private boolean requested_by;
    private boolean domain_blocking;
    private boolean endorsed;
    private String note;
}