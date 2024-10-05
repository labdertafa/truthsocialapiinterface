package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.JsonSyntaxException;
import com.laboratorio.clientapilibrary.exceptions.ApiClientException;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialMediaAttachment;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialStatus;
import com.laboratorio.truthsocialapiinterface.utils.InstruccionInfo;
import java.util.List;
import com.laboratorio.truthsocialapiinterface.TruthsocialStatusApi;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialAccountListResponse;

/**
 *
 * @author Rafael
 * @version 1.3
 * @created 24/07/2024
 * @updated 05/10/2024
 */
public class TruthsocialStatusApiImpl extends TruthsocialBaseApi implements TruthsocialStatusApi {
    public TruthsocialStatusApiImpl(String accessToken) {
        super(accessToken);
    }
    
    @Override
    public TruthsocialStatus getStatusById(String id) {
        String endpoint = this.apiConfig.getProperty("getStatusById_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getStatusById_ok_status"));
        
        try {
            String uri = endpoint + "/" + id;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request = this.addHeadersAndCookies(request, false);
            
            ApiResponse response = this.client.executeApiRequest(request);

            return this.gson.fromJson(response.getResponseStr(), TruthsocialStatus.class);
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            logException(e);
            throw new TruthsocialApiException(TruthsocialStatusApiImpl.class.getName(), e.getMessage());
        }
    }

    @Override
    public TruthsocialStatus postStatus(String text) {
        return this.postStatus(text, null);
    }

    @Override
    public TruthsocialStatus deleteStatus(String id) {
        String endpoint = this.apiConfig.getProperty("deleteStatus_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("deleteStatus_ok_status"));
        
        try {
            String uri = endpoint + "/" + id;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.DELETE);
            request = this.addHeadersAndCookies(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);

            return this.gson.fromJson(response.getResponseStr(), TruthsocialStatus.class);
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (Exception e) {
            logException(e);
            throw new TruthsocialApiException(TruthsocialStatusApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public TruthsocialStatus postStatusWithImage(String text, TruthsocialMediaAttachment mediaAttachment) {
        String endpoint = this.apiConfig.getProperty("postStatus_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("postStatus_ok_status"));
        
        try {
            String uri = endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.POST);
            request.addApiPathParam("status", text);
            request.addApiPathParam("visibility", "public");
            request.addApiPathParam("language", "es");
            if (mediaAttachment != null) {
                request.addApiPathParam("media_ids[]", mediaAttachment.getId());
            }
            request = this.addHeadersAndCookies(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            return this.gson.fromJson(response.getResponseStr(), TruthsocialStatus.class);
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (Exception e) {
            throw new TruthsocialApiException(TruthsocialStatusApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public TruthsocialStatus postStatus(String text, String filePath) {
        try {
            if (filePath != null) {
                TruthsocialMediaAttachment mediaAttachment = this.uploadImage(filePath);
                Thread.sleep(2500);
                return this.postStatusWithImage(text, mediaAttachment);
            }
            
            return this.postStatusWithImage(text, null);
        } catch (Exception e) {
            throw new TruthsocialApiException(TruthsocialStatusApiImpl.class.getName(), e.getMessage());
        }
    }
    
    @Override
    public TruthsocialMediaAttachment uploadImage(String filePath) throws Exception {
        String endpoint = this.apiConfig.getProperty("UploadImage_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("UploadImage_ok_status"));
        
        try {
            String uri = endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.POST);
            
            request = this.addHeadersAndCookies(request, true);
            request.addFileFormData("file", filePath);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            return this.gson.fromJson(response.getResponseStr(), TruthsocialMediaAttachment.class);
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (Exception e) {
            logException(e);
            throw new TruthsocialApiException(TruthsocialStatusApiImpl.class.getName(), e.getMessage());
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
        
        return this.getUserList(url, limit, okStatus);
    }
    
    private TruthsocialStatus executeSimplePost(String uri, int okStatus) {
        try {
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.POST);
            
            request = this.addHeadersAndCookies(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            return this.gson.fromJson(response.getResponseStr(), TruthsocialStatus.class);
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw  e;
        } catch (Exception e) {
            logException(e);
            throw new TruthsocialApiException(TruthsocialStatusApiImpl.class.getName(), e.getMessage());
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