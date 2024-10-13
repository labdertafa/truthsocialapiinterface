package com.laboratorio.truthsocialapiinterface.model.response;

import com.laboratorio.truthsocialapiinterface.model.TruthsocialStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 13/10/2024
 * @updated 13/10/2024
 */
@Getter @Setter @AllArgsConstructor
public class TruthsocialStatusListResponse {
    private List<TruthsocialStatus> statuses;
    private String nextPage;
}