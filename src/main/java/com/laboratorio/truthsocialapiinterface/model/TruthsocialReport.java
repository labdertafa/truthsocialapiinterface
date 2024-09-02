package com.laboratorio.truthsocialapiinterface.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 25/07/2024
 * @updated 01/09/2024
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TruthsocialReport {
    private String id;
    private boolean action_taken;
    private String action_taken_at;
    private String category;
    private String comment;
    private boolean forwarded;
    private String created_at;
    private String[] status_ids;
    private String[] rule_ids;
    private TruthsocialAccount target_account;
}