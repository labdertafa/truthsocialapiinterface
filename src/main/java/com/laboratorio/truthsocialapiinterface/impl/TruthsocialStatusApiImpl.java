package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
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
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialStatusListResponse;

/**
 *
 * @author Rafael
 * @version 1.4
 * @created 24/07/2024
 * @updated 06/06/2025
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
            request = this.addHeaders(request, false);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response getStatusById: {}", response.getResponseStr());

            return this.gson.fromJson(response.getResponseStr(), TruthsocialStatus.class);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error recuperando el estado Truthsocial con id: " + id, e);
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
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response deleteStatus: {}", response.getResponseStr());

            return this.gson.fromJson(response.getResponseStr(), TruthsocialStatus.class);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error eliminado el estado Truthsocial con id: " + id, e);
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
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response postStatusWithImage: {}", response.getResponseStr());
            
            return this.gson.fromJson(response.getResponseStr(), TruthsocialStatus.class);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error publicando un estado en Truthsocial: " + text, e);
        }
    }
    
    @Override
    public TruthsocialStatus postStatus(String text, String filePath) {
        if (filePath != null) {
            TruthsocialMediaAttachment mediaAttachment = this.uploadImage(filePath);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                log.warn("No se logró cumplir la interrupción mientras se posteaba una imagen");
            }
            return this.postStatusWithImage(text, mediaAttachment);
        }

        return this.postStatusWithImage(text, null);
    }
    
    @Override
    public TruthsocialMediaAttachment uploadImage(String filePath) {
        String endpoint = this.apiConfig.getProperty("UploadImage_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("UploadImage_ok_status"));
        
        try {
            String uri = endpoint;
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.POST);
            
            request = this.addHeaders(request, true);
            request.addFileFormData("file", filePath);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response uploadImage: {}", response.getResponseStr());
            
            return this.gson.fromJson(response.getResponseStr(), TruthsocialMediaAttachment.class);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error subiendo una imagen a Truthsocial: " + filePath, e);
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
            
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response executeSimplePost: {}", response.getResponseStr());
            
            return this.gson.fromJson(response.getResponseStr(), TruthsocialStatus.class);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error ejecutando un POST en Truthsocial: " + uri, e);
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
    
    private TruthsocialStatusListResponse getTimelinePage(String uri, int okStatus, String nextPage) {
        try {
            ApiRequest request;
            if (nextPage == null) {
                request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            } else {
                request = new ApiRequest(nextPage, okStatus, ApiMethodType.GET);
            }
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            List<TruthsocialStatus> statuses = this.gson.fromJson(response.getResponseStr(), new TypeToken<List<TruthsocialStatus>>(){}.getType());
            String newNextPage = null;
            if (!statuses.isEmpty()) {
                log.debug("Se ejecutó la query: " + uri);
                log.debug("Resultados encontrados: " + statuses.size());

                List<String> linkHeaderList = response.getHttpHeaders().get("link");
                if ((linkHeaderList != null) && (!linkHeaderList.isEmpty())) {
                    String linkHeader = linkHeaderList.get(0);
                    log.debug("Recibí este link: " + linkHeader);
                    newNextPage = this.getNextPageLink(linkHeader);
                    log.debug("Valor del newNextPage: " + newNextPage);
                }
            }

            return new TruthsocialStatusListResponse(statuses, newNextPage);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error recuperando una página del timeline de Truthsocial", e);
        }
    }

    @Override
    public List<TruthsocialStatus> getGlobalTimeline(int quantity) {
        String endpoint = this.apiConfig.getProperty("getGlobalTimeLine_endpoint");
        int okStatus = Integer.parseInt(this.apiConfig.getProperty("getGlobalTimeLine_ok_status"));
        
        List<TruthsocialStatus> statuses = null;
        boolean continuar = true;
        String nextPage = null;
        
        try {
            String uri = endpoint;
            
            do {
                TruthsocialStatusListResponse statusListResponse = this.getTimelinePage(uri, okStatus, nextPage);
                log.debug("Elementos recuperados total: " + statusListResponse.getStatuses().size());
                if (statuses == null) {
                    statuses = statusListResponse.getStatuses();
                } else {
                    statuses.addAll(statusListResponse.getStatuses());
                }
                
                nextPage = statusListResponse.getNextPage();
                log.debug("getGlobalTimeline. Recuperados: " + statuses.size() + ". Next page: " + nextPage);
                if (statusListResponse.getStatuses().isEmpty()) {
                    continuar = false;
                } else {
                    if ((nextPage == null) || (statuses.size() >= quantity)) {
                        continuar = false;
                    }
                }
            } while (continuar);
            
            return statuses.subList(0, Math.min(quantity, statuses.size()));
        } catch (Exception e) {
            throw e;
        }
    }
}