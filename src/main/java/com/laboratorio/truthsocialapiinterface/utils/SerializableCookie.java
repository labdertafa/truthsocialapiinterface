package com.laboratorio.truthsocialapiinterface.utils;

import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 01/09/2024
 * @updated 01/09/2024
 */

@Getter @Setter @AllArgsConstructor
public class SerializableCookie implements Serializable {
    private String name;
    private String value;
    private String domain;
    private int version;
    private String path;
    private String comment;
    private ZonedDateTime expiry;
}