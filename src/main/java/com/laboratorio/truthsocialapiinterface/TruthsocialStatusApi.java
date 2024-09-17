package com.laboratorio.truthsocialapiinterface;

import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialMediaAttachment;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialStatus;
import java.util.List;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 24/07/2024
 * @updated 16/09/2024
 */
public interface TruthsocialStatusApi {
    // Consultar un status por su id
    TruthsocialStatus getStatusById(String id);
    
    // Postear o eliminar un status.
    TruthsocialStatus postStatus(String text);
    TruthsocialStatus deleteStatus(String id);
    TruthsocialStatus postStatusWithImage(String text, TruthsocialMediaAttachment mediaAttachment);
    TruthsocialStatus postStatus(String text, String filePath);
    
    // Subir una imagen
    TruthsocialMediaAttachment uploadImage(String filePath) throws Exception;
    
    // Ver las cuentas que han impulsado o marcado como favorito un status
    List<TruthsocialAccount> getRebloggedBy(String id) throws Exception;
    List<TruthsocialAccount> getRebloggedBy(String id, int limit) throws Exception;
    List<TruthsocialAccount> getRebloggedBy(String id, int limit, int quantity) throws Exception;
    List<TruthsocialAccount> getFavouritedBy(String id) throws Exception;
    List<TruthsocialAccount> getFavouritedBy(String id, int quantity) throws Exception;
    
    // Impulsar o dejar de impulsar un status
    TruthsocialStatus reblogStatus(String id);
    TruthsocialStatus unreblogStatus(String id);
    
    // Marcar y desmarcar un status como favorito
    TruthsocialStatus favouriteStatus(String id);
    TruthsocialStatus unfavouriteStatus(String id);
}