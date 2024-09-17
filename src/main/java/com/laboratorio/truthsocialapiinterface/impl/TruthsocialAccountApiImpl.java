package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialRelationship;
import java.util.List;
import com.laboratorio.truthsocialapiinterface.TruthsocialAccountApi;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialSuggestion;
import java.util.stream.Collectors;

/**
 *
 * @author Rafael
 * @version 1.2
 * @created 10/07/2024
 * @updated 17/09/2024
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
            ApiRequest request = new ApiRequest(uri, okStatus);
            
            request = this.addHeadersAndCookies(request, true);
            String jsonStr = this.client.executeGetRequest(request);
            
            return this.gson.fromJson(jsonStr, TruthsocialAccount.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public TruthsocialAccount getAccountByUsername(String username) {
        String endpoint = this.apiConfig.getProperty("getAccountByUsername_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getAccountByUsername_ok_status"));
        
        try {
            String uri = endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus);
            request.addApiPathParam("acct", username);
            
            request = this.addHeadersAndCookies(request, false);
            String jsonStr = this.client.executeGetRequest(request);
            
            return this.gson.fromJson(jsonStr, TruthsocialAccount.class);
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), e.getMessage());
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
    public boolean followAccount(String id) {
        String endpoint = this.apiConfig.getProperty("followAccount_endpoint");
        String complementoUrl = this.apiConfig.getProperty("followAccount_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("followAccount_ok_status"));
        
        try {
            String uri = endpoint + "/" + id + "/" + complementoUrl;
            ApiRequest request = new ApiRequest(uri, okStatus);
            
            request = this.addHeadersAndCookies(request, true);
            String jsonStr = this.client.executePostRequest(request);
            
            TruthsocialRelationship relationship = this.gson.fromJson(jsonStr, TruthsocialRelationship.class);
            return relationship.isFollowing();
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (Exception e) {
            throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public boolean unfollowAccount(String id) {
        String endpoint = this.apiConfig.getProperty("unfollowAccount_endpoint");
        String complementoUrl = this.apiConfig.getProperty("unfollowAccount_complemento_url");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("unfollowAccount_ok_status"));
        
        try {
            String uri = endpoint + "/" + id + "/" + complementoUrl;
            ApiRequest request = new ApiRequest(uri, okStatus);
            
            request = this.addHeadersAndCookies(request, true);
            
            String jsonStr = this.client.executePostRequest(request);
            
            TruthsocialRelationship relationship = this.gson.fromJson(jsonStr, TruthsocialRelationship.class);
            return !relationship.isFollowing();
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (Exception e) {
            throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public List<TruthsocialRelationship> checkrelationships(List<String> ids) {
        String endpoint = this.apiConfig.getProperty("checkrelationships_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("checkrelationships_ok_status"));
        
        try {
            String uri = endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus);
            for (String id : ids) {
                request.addApiPathParam("id[]", id);
            }
            
            request = this.addHeadersAndCookies(request, true);
            
            String jsonStr = this.client.executeGetRequest(request);

            return this.gson.fromJson(jsonStr, new TypeToken<List<TruthsocialRelationship>>(){}.getType());
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), e.getMessage());
        }
    }

    @Override
    public List<TruthsocialAccount> getSuggestions() {
        return this.getSuggestions(0);
    }

    @Override
    public List<TruthsocialAccount> getSuggestions(int limit) {
        String endpoint = this.apiConfig.getProperty("getSuggestions_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getSuggestions_ok_status"));
        int defaultLimit = Integer.parseInt(this.apiConfig.getProperty("getSuggestions_default_limit"));
        int maxLimit = Integer.parseInt(this.apiConfig.getProperty("getSuggestions_max_limit"));
        int usedLimit = limit;
        if ((limit == 0) || (limit > maxLimit)) {
            usedLimit = defaultLimit;
        }
        
        try {
            String uri = endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus);
            request.addApiPathParam("limit", Integer.toString(usedLimit));
         
            request = this.addHeadersAndCookies(request, true);
            
            String jsonStr = this.client.executeGetRequest(request);
            List<TruthsocialSuggestion> suggestions = this.gson.fromJson(jsonStr, new TypeToken<List<TruthsocialSuggestion>>(){}.getType());
            
            return suggestions.stream()
                    .map(s -> s.getAccount())
                    .collect(Collectors.toList());
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            throw new TruthsocialApiException(TruthsocialAccountApiImpl.class.getName(), e.getMessage());
        }
    }
}