package com.laboratorio.truthsocialapiinterface.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 10/07/2024
 * @updated 01/09/2024
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TruthsocialField {
    private String name;
    private String value;
    private String verified_at;
}
