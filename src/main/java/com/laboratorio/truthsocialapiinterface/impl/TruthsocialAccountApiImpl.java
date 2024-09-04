package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialRelationship;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialFollowResponse;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import com.laboratorio.truthsocialapiinterface.TruthsocialAccountApi;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 10/07/2024
 * @updated 04/09/2024
 */
public class TruthsocialAccountApiImpl extends TruthsocialBaseApi implements TruthsocialAccountApi {
    public TruthsocialAccountApiImpl(String accessToken) {
        super(accessToken);
    }
    
    @Override
    public TruthsocialAccount getAccountById(String id) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String endpoint = this.apiConfig.getProperty("getAccountById_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getAccountById_ok_status"));
        
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
            
            log.info("Se ejecutó la query: " + url);
            log.debug("Respuesta JSON recibida: " + jsonStr);
            
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, TruthsocialAccount.class);
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
    public TruthsocialAccount getAccountByUsername(String username) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String endpoint = this.apiConfig.getProperty("getAccountByUsername_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getAccountByUsername_ok_status"));
        
        try {
            String url = endpoint;
            WebTarget target = client.target(url)
                    .queryParam("acct", username);
            
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
            return gson.fromJson(jsonStr, TruthsocialAccount.class);
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
    public List<TruthsocialAccount> getFollowers(String id) throws Exception {
        return this.getFollowers(id, 0);
    }
    
    @Override
    public List<TruthsocialAccount> getFollowers(String id, int quantity) throws Exception {
        String endpoint = this.apiConfig.getProperty("getFollowers_endpoint");
        String complementoUrl = this.apiConfig.getProperty("getFollowers_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getFollowers_ok_status"));
        int limit = Integer.parseInt(this.apiConfig.getProperty("getFollowers_max_limit"));
        if ((quantity == 0) || (quantity > limit)) {
            quantity = limit;
        }
        limit = Math.min(limit, quantity);
        String url = endpoint + "/" + id + "/" + complementoUrl;
            
        return this.getUserList(url, id, limit, okStatus);
    }
    
    @Override
    public List<TruthsocialAccount> getFollowings(String id) throws Exception {
        return this.getFollowings(id, 0);
    }
    
    @Override
    public List<TruthsocialAccount> getFollowings(String id, int quantity) throws Exception {
        String endpoint = this.apiConfig.getProperty("getFollowings_endpoint");
        String complementoUrl = this.apiConfig.getProperty("getFollowings_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getFollowings_ok_status"));
        int limit = Integer.parseInt(this.apiConfig.getProperty("getFollowings_max_limit"));
        if ((quantity == 0) || (quantity > limit)) {
            quantity = limit;
        }
        limit = Math.min(limit, quantity);
        String url = endpoint + "/" + id + "/" + complementoUrl;
        
        return this.getUserList(url, id, limit, okStatus);
    }
    
    @Override
    public boolean followAccount(String id) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String endpoint = this.apiConfig.getProperty("followAccount_endpoint");
        String complementoUrl = this.apiConfig.getProperty("followAccount_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("followAccount_ok_status"));
        
        try {
            String url = endpoint + "/" + id + "/" + complementoUrl;
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
            log.debug("Respuesta JSON recibida: " + jsonStr);
            
            Gson gson = new Gson();
            TruthsocialFollowResponse followResponse = gson.fromJson(jsonStr, TruthsocialFollowResponse.class);
            return followResponse.isFollowing();
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
    public boolean unfollowAccount(String id) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String endpoint = this.apiConfig.getProperty("unfollowAccount_endpoint");
        String complementoUrl = this.apiConfig.getProperty("unfollowAccount_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("unfollowAccount_ok_status"));
        
        try {
            String url = endpoint + "/" + id + "/" + complementoUrl;
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
            log.debug("Respuesta JSON recibida: " + jsonStr);
            
            Gson gson = new Gson();
            TruthsocialFollowResponse followResponse = gson.fromJson(jsonStr, TruthsocialFollowResponse.class);
            return !followResponse.isFollowing();
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
    public List<TruthsocialRelationship> checkrelationships(List<String> ids) {
        Client client = ClientBuilder.newClient();
        Response response = null;
        String endpoint = this.apiConfig.getProperty("checkrelationships_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("checkrelationships_ok_status"));
        
        try {
            String url = endpoint;
            WebTarget target = client.target(url);
            for (String id : ids) {
                target = target.queryParam("id[]", id);
            }
            
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
            return gson.fromJson(jsonStr, new TypeToken<List<TruthsocialRelationship>>(){}.getType());
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
}