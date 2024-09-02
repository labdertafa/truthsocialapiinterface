package com.laboratorio.truthsocialapiinterface.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 24/07/2024
 * @updated 01/09/2024
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
class TruthsocialFilter {
    private String id;
    private String title;
    private String[] context;
    private String expires_at;
    private String filter_action;
    private TruthsocialFilterKeyword[] keywords;
    private TruthsocialFilterStatus[] statuses;
}