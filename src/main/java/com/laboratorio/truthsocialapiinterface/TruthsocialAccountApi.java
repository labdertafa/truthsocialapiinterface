package com.laboratorio.truthsocialapiinterface;

import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialRelationship;
import java.util.List;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 10/07/2024
 * @updated 16/09/2024
 */
public interface TruthsocialAccountApi {
    // Obtiene la información de un usuario a partir de su ID
    TruthsocialAccount getAccountById(String id);
    // Obtiene la información de un usuario a partir de su username
    TruthsocialAccount getAccountByUsername(String username);
    // Obtiene los seguidores de un usuario a partir de su id. Quantity indica el número de registros a recuperar (cero significa todos). Se puede indicar una posición inicial.
    List<TruthsocialAccount> getFollowers(String id) throws Exception;
    List<TruthsocialAccount> getFollowers(String id, int quantity) throws Exception;
    // Obtiene los seguidos de un usuario a partir de su id. Quantity indica el número de registros a recuperar (cero significa todos). Se puede indicar una posición inicial.
    List<TruthsocialAccount> getFollowings(String id) throws Exception;
    List<TruthsocialAccount> getFollowings(String id, int quantity) throws Exception;
    // Seguir a un usuario
    boolean followAccount(String id);
    // Dejar de seguir a un usuario
    boolean unfollowAccount(String id);
    // Chequea la relación con un listado de cuentas identificadas por su id
    List<TruthsocialRelationship> checkrelationships(List<String> ids);
    // Consultar las sugerencias de seguimiento
    List<TruthsocialAccount> getSuggestions();
    List<TruthsocialAccount> getSuggestions(int limit);
    
    boolean deleteSuggestion(String userId);
}