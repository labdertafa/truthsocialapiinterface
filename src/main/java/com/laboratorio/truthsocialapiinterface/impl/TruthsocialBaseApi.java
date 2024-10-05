package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.laboratorio.clientapilibrary.ApiClient;
import com.laboratorio.clientapilibrary.exceptions.ApiClientException;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.clientapilibrary.utils.CookieManager;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialAccountListResponse;
import com.laboratorio.truthsocialapiinterface.utils.InstruccionInfo;
import com.laboratorio.truthsocialapiinterface.utils.TruthsocialApiConfig;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael
 * @version 1.3
 * @created 24/07/2024
 * @updated 04/10/2024
 */
public class TruthsocialBaseApi {
    protected static final Logger log = LogManager.getLogger(TruthsocialBaseApi.class);
    protected final ApiClient client;
    protected final String accessToken;
    protected TruthsocialApiConfig apiConfig;
    protected final Gson gson;

    public TruthsocialBaseApi(String accessToken) {
        this.apiConfig = TruthsocialApiConfig.getInstance();
        String cookiesFilePath = this.apiConfig.getProperty("cookies_file");
        this.client = new ApiClient(cookiesFilePath);
        this.accessToken = accessToken;
        this.gson = new Gson();
    }
    
    protected void logException(Exception e) {
        log.error("Error: " + e.getMessage());
        if (e.getCause() != null) {
            log.error("Causa: " + e.getCause().getMessage());
        }
    }
    
    // Función que extrae el max_id de la respuesta
    protected String extractMaxId(String str) {
        String maxId = null;
        String regex = "max_id=(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        
        if (matcher.find()) {
            maxId = matcher.group(1); // El primer grupo de captura contiene el valor de max_id
        }
        
        return maxId;
    }
    
    // Función que extrae el max_id de la respuesta
    protected String extractMinId(String str) {
        String minId = null;
        String regex = "min_id=(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        
        if (matcher.find()) {
            minId = matcher.group(1); // El primer grupo de captura contiene el valor de max_id
        }
        
        return minId;
    }
    
    protected ApiRequest addHeadersAndCookies(ApiRequest request, boolean needAuthorization) {
        try {
            String website = this.apiConfig.getProperty("truthsocial_website");
            String cookiesFilePath = this.apiConfig.getProperty("cookies_file");
            String userAgent = this.apiConfig.getProperty("userAgent");
            List<String> cookiesList = CookieManager.getWebsiteCookies(cookiesFilePath, website, userAgent);

            if (needAuthorization) {
                request.addApiHeader("Authorization", "Bearer " + this.accessToken);
            }
            request.addApiHeader("Accept", "application/json, text/plain, */*");
            request.addApiHeader("Accept-Encoding", "gzip, deflate, br, zstd");
            request.addApiHeader("User-Agent", userAgent);
            
            for (String cookie : cookiesList) {
                request.addApiCookie(cookie);
            }

            return request;
        } catch (Exception e) {
            logException(e);
            throw new TruthsocialApiException(TruthsocialBaseApi.class.getName(), e.getMessage());
        }
    }
    
    protected List<TruthsocialAccount> getUserList(String uri, int limit, int okStatus) throws Exception {
        try {
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request.addApiPathParam("limit", Integer.toString(limit));
            
            request = this.addHeadersAndCookies(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);

            return this.gson.fromJson(response.getResponseStr(), new TypeToken<List<TruthsocialAccount>>(){}.getType());
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            logException(e);
            throw new TruthsocialApiException(TruthsocialBaseApi.class.getName(), e.getMessage());
        }
    }
    
    // Función que devuelve una página de seguidores o seguidos de una cuenta
    private TruthsocialAccountListResponse getAccountPage(String uri, int okStatus, int limit, String posicionInicial) throws Exception {
        try {
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request.addApiPathParam("limit", Integer.toString(limit));
            if (posicionInicial != null) {
                request.addApiPathParam("max_id", posicionInicial);
            }
            
            request = this.addHeadersAndCookies(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            
            List<TruthsocialAccount> accounts = this.gson.fromJson(response.getResponseStr(), new TypeToken<List<TruthsocialAccount>>(){}.getType());
            String maxId = null;
            if (!accounts.isEmpty()) {
                log.debug("Se ejecutó la query: " + uri);
                log.debug("Resultados encontrados: " + accounts.size());

                List<String> linkHeaderList = response.getHttpHeaders().get("link");
                if ((linkHeaderList != null) && (!linkHeaderList.isEmpty())) {
                    String linkHeader = linkHeaderList.get(0);
                    log.debug("Recibí este link: " + linkHeader);
                    maxId = this.extractMaxId(linkHeader);
                    log.debug("Valor del max_id: " + maxId);
                }
            }

            // return accounts;
            return new TruthsocialAccountListResponse(maxId, accounts);
        } catch (ApiClientException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            logException(e);
            throw e;
        } catch (Exception e) {
            logException(e);
            throw new TruthsocialApiException(TruthsocialBaseApi.class.getName(), e.getMessage());
        }
    }
    
    protected TruthsocialAccountListResponse getTruthsocialAccountList(InstruccionInfo instruccionInfo, String id, int quantity, String posicionInicial) throws Exception {
        List<TruthsocialAccount> accounts = null;
        boolean continuar = true;
        String endpoint = instruccionInfo.getEndpoint();
        String complemento = instruccionInfo.getComplementoUrl();
        int limit = instruccionInfo.getLimit();
        int okStatus = instruccionInfo.getOkStatus();
        String max_id = posicionInicial;
        
        if (quantity > 0) {
            limit = Math.min(limit, quantity);
        }
        
        try {
            String uri = endpoint + "/" + id + "/" + complemento;
            
            do {
                TruthsocialAccountListResponse accountListResponse = this.getAccountPage(uri, okStatus, limit, max_id);
                if (accounts == null) {
                    accounts = accountListResponse.getAccounts();
                } else {
                    accounts.addAll(accountListResponse.getAccounts());
                }
                
                max_id = accountListResponse.getMaxId();
                log.debug("getTruthsocialAccountList. Cantidad: " + quantity + ". Recuperados: " + accounts.size() + ". Max_id: " + max_id);
                if (quantity > 0) {
                    if ((accounts.size() >= quantity) || (max_id == null)) {
                        continuar = false;
                    }
                } else {
                    if ((max_id == null) || (accountListResponse.getAccounts().size() < limit)) {
                        continuar = false;
                    }
                }
            } while (continuar);

            if (quantity == 0) {
                return new TruthsocialAccountListResponse(max_id, accounts);
            }
            
            return new TruthsocialAccountListResponse(max_id, accounts.subList(0, Math.min(quantity, accounts.size())));
        } catch (Exception e) {
            throw e;
        }
    }
}