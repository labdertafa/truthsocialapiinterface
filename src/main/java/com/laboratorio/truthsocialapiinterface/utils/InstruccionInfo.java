package com.laboratorio.truthsocialapiinterface.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 16/08/2024
 * @updated 16/08/2024
 */

@Getter @Setter @AllArgsConstructor
public class InstruccionInfo {
    private String endpoint;
    private String complementoUrl;
    private int okStatus;
    private int limit;
}