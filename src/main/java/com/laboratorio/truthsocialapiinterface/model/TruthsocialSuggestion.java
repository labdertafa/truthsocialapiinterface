package com.laboratorio.truthsocialapiinterface.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 16/09/2024
 * @updated 13/10/2024
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TruthsocialSuggestion {
    private String account_id;
    private String account_avatar;
    private String acct;
    private String note;
    private boolean verified;
    private String display_name;
}