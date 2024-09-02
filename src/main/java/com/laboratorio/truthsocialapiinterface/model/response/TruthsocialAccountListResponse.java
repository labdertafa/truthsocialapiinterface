package com.laboratorio.truthsocialapiinterface.model.response;

import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 10/07/2024
 * @updated 01/09/2024
 */
@Getter @Setter @AllArgsConstructor
public class TruthsocialAccountListResponse {
    private String maxId;
    private List<TruthsocialAccount> accounts;
}