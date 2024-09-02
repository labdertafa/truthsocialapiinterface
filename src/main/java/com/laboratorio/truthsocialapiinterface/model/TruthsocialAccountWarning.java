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
public class TruthsocialAccountWarning {
    private String id;
    private String action;
    private String text;
    private String[] status_ids;
    private TruthsocialAccount target_account;
    private TruthsocialAppeal appeal;
    private String created_at;
}