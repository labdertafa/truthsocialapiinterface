package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialMediaAttachment;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialStatus;
import com.laboratorio.truthsocialapiinterface.utils.InstruccionInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataWriter;
import com.laboratorio.truthsocialapiinterface.TruthsocialStatusApi;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialAccountListResponse;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 24/07/2024
 * @updated 04/09/2024
 */
public class TruthsocialStatusApiImpl extends TruthsocialBaseApi implements TruthsocialStatusApi {
    public TruthsocialStatusApiImpl(String accessToken) {
        super(accessToken);
    }
    
    @Override
    public TruthsocialStatus getStatusById(String id) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String endpoint = this.apiConfig.getProperty("getStatusById_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getStatusById_ok_status"));
        
        try {
            String url = endpoint + "/" + id;
            WebTarget target = client.target(url);
            
            response = this.createRequest(client, target, url).get();
            
            String jsonStr = processResponse(response);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), str);
            }
            
            log.debug("Se ejecutó la query: " + url);
            log.debug("Respuesta JSON recibida: " + jsonStr);
            
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, TruthsocialStatus.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (TruthsocialApiException e) {
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
        }
    }

    @Override
    public TruthsocialStatus postStatus(String text) {
        return this.postStatus(text, null);
    }

    @Override
    public TruthsocialStatus deleteStatus(String id) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String endpoint = this.apiConfig.getProperty("deleteStatus_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("deleteStatus_ok_status"));
        
        try {
            String url = endpoint + "/" + id;
            WebTarget target = client.target(url);
            
            
            response = this.createRequest(client, target, url)
                    .delete();
            
            String jsonStr = processResponse(response);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), str);
            }
            
            log.debug("Se ejecutó la query: " + url);
            
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, TruthsocialStatus.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (TruthsocialApiException e) {
            throw  e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
        }
    }
    
    @Override
    public TruthsocialMediaAttachment uploadImage(String filePath) throws Exception {
        ResteasyClient client = (ResteasyClient)ResteasyClientBuilder.newBuilder().build();
        Response response = null;
        String endpoint = this.apiConfig.getProperty("UploadImage_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("UploadImage_ok_status"));
        
        try {
            String url = endpoint;
            WebTarget target = client.target(url)
                    .register(MultipartFormDataWriter.class);
            
            MultipartFormDataOutput formDataOutput = new MultipartFormDataOutput();
            File imageFile = new File(filePath);
            InputStream fileStream = new FileInputStream(imageFile);
            formDataOutput.addFormData("file", fileStream, MediaType.APPLICATION_OCTET_STREAM_TYPE, imageFile.getName());
            
            response = this.createRequest(client, target, url)
                    .post(Entity.entity(formDataOutput, MediaType.MULTIPART_FORM_DATA));
            
            String jsonStr = processResponse(response);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d. Detalle: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), str);
            }
            
            log.debug("Se ejecutó la query: " + url);
            log.debug("Respuesta recibida: " + jsonStr);
            
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, TruthsocialMediaAttachment.class);
        } catch (JsonSyntaxException | FileNotFoundException e) {
            logException(e);
            throw  e;
        } catch (TruthsocialApiException e) {
            throw  e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
        }
    }
    
    @Override
    public TruthsocialStatus postStatus(String text, String imagenId) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String endpoint = this.apiConfig.getProperty("postStatus_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("postStatus_ok_status"));
        
        try {
            String url = endpoint;
            WebTarget target = client.target(url)
                    .queryParam("status", text)
                    .queryParam("visibility", "public")
                    .queryParam("language", "es");
            
            if (imagenId != null) {
                target = target.queryParam("media_ids[]", imagenId);
            }
            
            
            response = this.createRequest(client, target, url)
                    .post(Entity.text(""));
            
            String jsonStr = processResponse(response);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), str);
            }
            
            log.debug("Se ejecutó la query: " + url);
            log.debug("Respuesta recibida: " + jsonStr);
            
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, TruthsocialStatus.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (TruthsocialApiException e) {
            throw  e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
        }
    }
    
    @Override
    public List<TruthsocialAccount> getRebloggedBy(String id) throws Exception {
        return this.getRebloggedBy(id, 0);
    }
    
    @Override
    public List<TruthsocialAccount> getRebloggedBy(String id, int limit) throws Exception {
        return this.getRebloggedBy(id, limit, 0);
    }
    
    @Override
    public List<TruthsocialAccount> getRebloggedBy(String id, int limit, int quantity) throws Exception {
        String endpoint = this.apiConfig.getProperty("getRebloggedBy_endpoint");
        String complementoUrl = this.apiConfig.getProperty("getRebloggedBy_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getRebloggedBy_ok_status"));
        int defaultLimit = Integer.parseInt(this.apiConfig.getProperty("getRebloggedBy_default_limit"));
        int maxLimit = Integer.parseInt(this.apiConfig.getProperty("getRebloggedBy_max_limit"));
        int usedLimit = limit;
        if ((limit == 0) || (limit > maxLimit)) {
            usedLimit = defaultLimit;
        }
        InstruccionInfo instruccionInfo = new InstruccionInfo(endpoint, complementoUrl, okStatus, usedLimit);
        TruthsocialAccountListResponse accountListResponse = this.getTruthsocialAccountList(instruccionInfo, id, quantity, null);
        return accountListResponse.getAccounts();
    }
    
    @Override
    public List<TruthsocialAccount> getFavouritedBy(String id) throws Exception {
        return this.getFavouritedBy(id, 0);
    }
    
    @Override
    public List<TruthsocialAccount> getFavouritedBy(String id, int quantity) throws Exception {
        String endpoint = this.apiConfig.getProperty("getFavouritedBy_endpoint");
        String complementoUrl = this.apiConfig.getProperty("getFavouritedBy_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getFavouritedBy_ok_status"));
        int limit = Integer.parseInt(this.apiConfig.getProperty("getFavouritedBy_max_limit"));
        if ((quantity == 0) || (quantity > limit)) {
            quantity = limit;
        }
        limit = Math.min(limit, quantity);
        String url = endpoint + "/" + id + "/" + complementoUrl;
        
        return this.getUserList(url, id, limit, okStatus);
    }
    
    private TruthsocialStatus executeSimplePost(String url, int okStatus) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        
        try {
            WebTarget target = client.target(url);
            
            response = this.createRequest(client, target, url)
                    .post(Entity.text(""));
            
            String jsonStr = processResponse(response);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), str);
            }
            
            log.debug("Se ejecutó la query: " + url);
            log.debug("Respuesta recibida: " + jsonStr);
            
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, TruthsocialStatus.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (TruthsocialApiException e) {
            throw  e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
        }
    }

    @Override
    public TruthsocialStatus reblogStatus(String id) {
        String endpoint = this.apiConfig.getProperty("reblogStatus_endpoint");
        String complementoUrl = this.apiConfig.getProperty("reblogStatus_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("reblogStatus_ok_status"));
        String url = endpoint + "/" + id + "/" + complementoUrl;
        return executeSimplePost(url, okStatus);
    }

    @Override
    public TruthsocialStatus unreblogStatus(String id) {
        String endpoint = this.apiConfig.getProperty("unreblogStatus_endpoint");
        String complementoUrl = this.apiConfig.getProperty("unreblogStatus_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("unreblogStatus_ok_status"));
        String url = endpoint + "/" + id + "/" + complementoUrl;
        return executeSimplePost(url, okStatus);
    }

    @Override
    public TruthsocialStatus favouriteStatus(String id) {
        String endpoint = this.apiConfig.getProperty("favouriteStatus_endpoint");
        String complementoUrl = this.apiConfig.getProperty("favouriteStatus_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("favouriteStatus_ok_status"));
        String url = endpoint + "/" + id + "/" + complementoUrl;
        return executeSimplePost(url, okStatus);
    }

    @Override
    public TruthsocialStatus unfavouriteStatus(String id) {
        String endpoint = this.apiConfig.getProperty("unfavouriteStatus_endpoint");
        String complementoUrl = this.apiConfig.getProperty("unfavouriteStatus_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("unfavouriteStatus_ok_status"));
        String url = endpoint + "/" + id + "/" + complementoUrl;
        return executeSimplePost(url, okStatus);
    }   
}