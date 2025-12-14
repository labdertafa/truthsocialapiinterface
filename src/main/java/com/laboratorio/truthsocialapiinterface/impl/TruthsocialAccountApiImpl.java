package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.reflect.TypeToken;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialRelationship;
import java.util.List;
import com.laboratorio.truthsocialapiinterface.TruthsocialAccountApi;
import static com.laboratorio.truthsocialapiinterface.impl.TruthsocialBaseApi.log;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialSuggestion;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialSuggestionsListResponse;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author Rafael
 * @version 1.6
 * @created 10/07/2024
 * @updated 14/12/2025
 */
public class TruthsocialAccountApiImpl extends TruthsocialBaseApi implements TruthsocialAccountApi {
    public TruthsocialAccountApiImpl(String accessToken) {
        super(accessToken);
    }
    
    @Override
    public TruthsocialAccount getAccountById(String id) {
        String endpoint = this.apiConfig.getProperty("getAccountById_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getAccountById_ok_status"));
        
        try {
            String uri = endpoint + "/" + id;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response getAccountById: {}", response.getResponseStr());
            
            return this.gson.fromJson(response.getResponseStr(), TruthsocialAccount.class);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error recuperando los datos de la cuenta Truthsocial con id: " + id, e);
        }
    }
    
    @Override
    public TruthsocialAccount getAccountByUsername(String username) {
        String endpoint = this.apiConfig.getProperty("getAccountByUsername_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getAccountByUsername_ok_status"));
        
        try {
            String uri = endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request.addApiPathParam("acct", username);
            request = this.addHeaders(request, false);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response getAccountByUsername: {}", response.getResponseStr());
            
            return this.gson.fromJson(response.getResponseStr(), TruthsocialAccount.class);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error recuperando los datos de la cuenta Truthsocial con username: " + username, e);
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
            
        return this.getUserList(url, limit, okStatus);
    }
    
    @Override
    public List<String> getFollowersIds(String userId) throws Exception {
        List<TruthsocialAccount> accounts = this.getFollowers(userId, 0);
        return accounts.stream()
                .map(account -> account.getId())
                .collect(Collectors.toList());
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
        
        return this.getUserList(url, limit, okStatus);
    }
    
    @Override
    public List<String> getFollowingsIds(String userId) throws Exception {
        List<TruthsocialAccount> accounts = this.getFollowings(userId, 0);
        return accounts.stream()
                .map(account -> account.getId())
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean followAccount(String id) {
        String endpoint = this.apiConfig.getProperty("followAccount_endpoint");
        String complementoUrl = this.apiConfig.getProperty("followAccount_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("followAccount_ok_status"));
        
        try {
            String uri = endpoint + "/" + id + "/" + complementoUrl;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.POST);
            request = this.addHeaders(request, true);

            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response followAccount: {}", response.getResponseStr());
            
            TruthsocialRelationship relationship = this.gson.fromJson(response.getResponseStr(), TruthsocialRelationship.class);
            return relationship.isFollowing();
        } catch (Exception e) {
            throw new TruthsocialApiException("Error siguiendo a la cuenta Truthsocial con id: " + id, e);
        }
    }
    
    @Override
    public boolean unfollowAccount(String id) {
        String endpoint = this.apiConfig.getProperty("unfollowAccount_endpoint");
        String complementoUrl = this.apiConfig.getProperty("unfollowAccount_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("unfollowAccount_ok_status"));
        
        try {
            String uri = endpoint + "/" + id + "/" + complementoUrl;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.POST);
            
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response unfollowAccount: {}", response.getResponseStr());
            
            TruthsocialRelationship relationship = this.gson.fromJson(response.getResponseStr(), TruthsocialRelationship.class);
            return !relationship.isFollowing();
        } catch (Exception e) {
            throw new TruthsocialApiException("Error dejando de seguir a la cuenta Truthsocial con id: " + id, e);
        }
    }
    
    @Override
    public List<TruthsocialRelationship> checkrelationships(List<String> ids) {
        String endpoint = this.apiConfig.getProperty("checkrelationships_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("checkrelationships_ok_status"));
        
        try {
            String uri = endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            for (String id : ids) {
                request.addApiPathParam("id[]", id);
            }
            
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response checkrelationships: {}", response.getResponseStr());

            return this.gson.fromJson(response.getResponseStr(), new TypeToken<List<TruthsocialRelationship>>(){}.getType());
        } catch (Exception e) {
            throw new TruthsocialApiException("Error comprobando el estado de la relación entre cuentas Truthsocial", e);
        }
    }
    
    private TruthsocialSuggestionsListResponse getSuggestionsPage(String uri, int okStatus, String nextPage) {
        try {
            ApiRequest request;
            if (nextPage == null) {
                request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            } else {
                request = new ApiRequest(nextPage, okStatus, ApiMethodType.GET);
            }
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            List<TruthsocialSuggestion> suggestions = this.gson.fromJson(response.getResponseStr(), new TypeToken<List<TruthsocialSuggestion>>(){}.getType());
            String newNextPage = null;
            if (!suggestions.isEmpty()) {
                log.debug("Se ejecutó la query: " + uri);
                log.debug("Resultados encontrados: " + suggestions.size());

                List<String> linkHeaderList = response.getHttpHeaders().get("link");
                if ((linkHeaderList != null) && (!linkHeaderList.isEmpty())) {
                    String linkHeader = linkHeaderList.get(0);
                    log.debug("Recibí este link: " + linkHeader);
                    newNextPage = this.getNextPageLink(linkHeader);
                    log.debug("Valor del newNextPage: " + newNextPage);
                }
            }

            return new TruthsocialSuggestionsListResponse(suggestions, newNextPage);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error recuperando una páginma de sugerencias a seguir en Truthsocial", e);
        }
    }

    @Override
    public List<TruthsocialAccount> getSuggestions(int quantity) {
        String endpoint = this.apiConfig.getProperty("getSuggestions_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getSuggestions_ok_status"));
        
        List<TruthsocialAccount> accounts = new ArrayList<>();
        boolean continuar = true;
        String nextPage = null;
        
        String uri = endpoint;

        do {
            TruthsocialSuggestionsListResponse suggestionsListResponse = this.getSuggestionsPage(uri, okStatus, nextPage);
            log.debug("Elementos recuperados total: " + suggestionsListResponse.getSuggestions().size());

            for (TruthsocialSuggestion suggestion : suggestionsListResponse.getSuggestions()) {
                TruthsocialAccount account = this.getAccountById(suggestion.getAccount_id());
                accounts.add(account);
                log.debug("Account info: " + account.toString());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.warn("No se pudo completar la espera durante la consulta de sugerencias de seguimiento");
                }
            }

            nextPage = suggestionsListResponse.getNextPage();
            log.debug("getGlobalTimeline. Recuperados: " + accounts.size() + ". Next page: " + nextPage);
            if (suggestionsListResponse.getSuggestions().isEmpty()) {
                continuar = false;
            } else {
                if ((nextPage == null) || (accounts.size() >= quantity)) {
                    continuar = false;
                }
            }
        } while (continuar);

        return accounts.subList(0, Math.min(quantity, accounts.size()));
    }

    @Override
    public boolean deleteSuggestion(String userId) {
        String endpoint = this.apiConfig.getProperty("deleteSuggestion_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("deleteSuggestion_ok_status"));
        
        try {
            String uri = endpoint + "/" + userId;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.DELETE);
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            
            this.client.executeApiRequest(request);
            
            return true;
        } catch (Exception e) {
            throw new TruthsocialApiException("Error eliminado la sugerencia de seguimiento de Truthsocial con id: " + userId, e);
        }
    }
}