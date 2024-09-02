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
class TruthsocialMention {
    private String id;
    private String username;
    private String url;
    private String acct;
}