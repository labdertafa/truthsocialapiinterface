package com.laboratorio.api;

import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.impl.TruthsocialStatusApiImpl;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialMediaAttachment;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialStatus;
import com.laboratorio.truthsocialapiinterface.utils.TruthsocialApiConfig;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.laboratorio.truthsocialapiinterface.TruthsocialStatusApi;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 24/07/2024
 * @updated 02/09/2024
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TruthsocialStatusApiTest {
    private String accessToken;
    private TruthsocialStatusApi statusApi;
    private static String idElim = "";
    
    @BeforeEach
    private void initTests() {
        this.accessToken = TruthsocialApiConfig.getInstance().getProperty("access_token");
        this.statusApi = new TruthsocialStatusApiImpl(this.accessToken);
    }
    
    @Test
    public void getStatusById() {
        String id = "113060871346252247";
        String uriResult = "https://truthsocial.com/@labrafa/113060871346252247";
        
        TruthsocialStatus status = this.statusApi.getStatusById(id);
        assertEquals(id, status.getId());
        assertEquals(uriResult, status.getUri());
    }
    
    @Test
    public void getStatusByInvalidId() {
        String id = "1125AXR11TRE9WQW63";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.statusApi.getStatusById(id);
        });
    }
    
    @Test @Order(1)
    public void postStatus() {
        String text = "Hola, les saludo desde El laboratorio de Rafa. Post automático";
        
        TruthsocialStatus status = this.statusApi.postStatus(text);
        idElim = status.getId();
        assertTrue(!status.getId().isEmpty());
        assertTrue(status.getContent().contains(text));
    }
    
    @Test
    public void postInvalidStatus() {
        assertThrows(TruthsocialApiException.class, () -> {
            this.statusApi.postStatus("");
        });
    }
    
    @Test @Order(2)
    public void deleteStatus() throws InterruptedException {
        String text = "Hola, les saludo desde El laboratorio de Rafa. Post automático";
        
        // Esperar para que los cambios se reflejen en base de datos
        Thread.sleep(2500);
        
        TruthsocialStatus status = this.statusApi.deleteStatus(idElim);
        assertTrue(!status.getId().isEmpty());
        assertTrue(status.getText().contains(text));
    }
    
    @Test
    public void deleteInvalidStatus() {
        String id = "1125AXR11TRE9WQW63";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.statusApi.deleteStatus(id);
        });
    }
    
    @Test @Order(7)
    public void postImage() throws Exception {
        String imagen = "C:\\Users\\rafa\\Pictures\\Formula_1\\Spa_1950.jpg";
        String text = "Hola, les saludo desde El laboratorio de Rafa. Post automático";
        
        TruthsocialMediaAttachment media = this.statusApi.uploadImage(imagen);
        assertTrue(media.getPreview_url() != null);
        
        Thread.sleep(2500);
        
        TruthsocialStatus status = this.statusApi.postStatus(text, media.getId());
        idElim = status.getId();
        assertTrue(!status.getId().isEmpty());
        assertTrue(status.getContent().contains(text));
    }
    
    @Test @Order(8)
    public void deletePostImage() throws InterruptedException {
        String text = "Hola, les saludo desde El laboratorio de Rafa. Post automático";
        
        // Esperar para que los cambios se reflejen en base de datos
        Thread.sleep(2500);
        
        TruthsocialStatus status = this.statusApi.deleteStatus(idElim);
        assertTrue(!status.getId().isEmpty());
        assertTrue(status.getText().contains(text));
    }
    
    @Test
    public void getRebloggedBy() { // Usa el default limit
        String id = "112989850918042189";
        
        try {
            List<TruthsocialAccount> accounts = this.statusApi.getRebloggedBy(id);
            assertTrue(accounts.size() > 50);
        } catch (Exception e) {
            fail("Ocurrió una excepción: " + e.getMessage());
        }
    }
    
    @Test
    public void getRebloggedByWithLimit() { // Define el  limit
        String id = "112989850918042189";
        int limit = 80;
        
        try {
            List<TruthsocialAccount> accounts = this.statusApi.getRebloggedBy(id, limit);
            assertTrue(accounts.size() > 50);
        } catch (Exception e) {
            fail("Ocurrió una excepción: " + e.getMessage());
        }
    }
    
    @Test
    public void getInvalidRebloggedBy() {
        String id = "QQQ109412553445428617";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.statusApi.getRebloggedBy(id);
        });
    }
    
    @Test
    public void getFavouritedBy() throws Exception { // Usa el default limit
        String id = "112989850918042189";
        
        List<TruthsocialAccount> accounts = this.statusApi.getFavouritedBy(id);
        assertEquals(80, accounts.size());
    }
    
    @Test
    public void getFavouritedByWithLimit() throws Exception { // Define un limit
        String id = "112989850918042189";
        int cantidad = 80;
        
        List<TruthsocialAccount> accounts = this.statusApi.getFavouritedBy(id, cantidad);
        assertEquals(cantidad, accounts.size());
    }
    
    @Test
    public void get200Favourited() throws Exception { // Define un limit
        String id = "112989850918042189";
        int cantidad = 200;
        
        List<TruthsocialAccount> accounts = this.statusApi.getFavouritedBy(id, cantidad);
        assertEquals(80, accounts.size());
    }
    
    @Test
    public void getInvalidFavouritedBy() {
        String id = "QQQ109412553445428617";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.statusApi.getFavouritedBy(id);
        });
    }
    
    @Test @Order(3)
    public void reblogStatus() {
        String id = "113060871346252247";
        
        TruthsocialStatus status = this.statusApi.reblogStatus(id);
        assertEquals(id, status.getReblog().getId());
    }
    
    @Test
    public void reblogInvalidStatus() {
        String id = "QQ112836040801154212";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.statusApi.reblogStatus(id);
        });
    }
    
    @Test @Order(4)
    public void unreblogStatus() throws InterruptedException {
        String id = "113060871346252247";
        
        // Esperar para que los cambios se reflejen en base de datos
        Thread.sleep(2500);
        
        TruthsocialStatus status = this.statusApi.unreblogStatus(id);
        assertEquals(id, status.getId());
    }
    
    @Test
    public void unreblogInvalidStatus() {
        String id = "QQ112836040801154212";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.statusApi.unreblogStatus(id);
        });
    }
    
    @Test @Order(5)
    public void favouriteStatus() {
        String id = "113060871346252247";
        
        TruthsocialStatus status = this.statusApi.favouriteStatus(id);
        assertEquals(id, status.getId());
    }
    
    @Test
    public void favouriteInvalidStatus() {
        String id = "QQ112836040801154212";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.statusApi.favouriteStatus(id);
        });
    }
    
    @Test @Order(6)
    public void unfavouriteStatus() throws InterruptedException {
        String id = "113060871346252247";
        
        // Esperar para que los cambios se reflejen en base de datos
        Thread.sleep(2500);
        
        TruthsocialStatus status = this.statusApi.unfavouriteStatus(id);
        assertEquals(id, status.getId());
    }
    
    @Test
    public void unfavouriteInvalidStatus() {
        String id = "QQ112836040801154212";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.statusApi.unfavouriteStatus(id);
        });
    }
}