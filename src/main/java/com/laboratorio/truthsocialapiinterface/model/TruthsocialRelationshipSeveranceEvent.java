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
public class TruthsocialRelationshipSeveranceEvent {
    private String id;
    private String type;
    private boolean purged;
    private String target_name;
    private int relationships_count;
    private String created_at;
}