package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laboratorio.clientapilibrary.ApiClient;
import com.laboratorio.clientapilibrary.model.ApiMethodType;
import com.laboratorio.clientapilibrary.model.ApiRequest;
import com.laboratorio.clientapilibrary.model.ApiResponse;
import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialAccountListResponse;
import com.laboratorio.truthsocialapiinterface.utils.InstruccionInfo;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael
 * @version 1.7
 * @created 24/07/2024
 * @updated 14/12/2025
 */
public class TruthsocialBaseApi {
    protected static final Logger log = LogManager.getLogger(TruthsocialBaseApi.class);
    protected final ApiClient client;
    protected final String accessToken;
    protected ReaderConfig apiConfig;
    protected final Gson gson;

    public TruthsocialBaseApi(String accessToken) {
        this.apiConfig = new ReaderConfig("config//truthsocial_api.properties");
        this.accessToken = accessToken;
        this.gson = new Gson();
        String proxyHost = this.apiConfig.getProperty("truthsocial_proxy_host");
        String proxyPortStr = this.apiConfig.getProperty("truthsocial_proxy_port");
        String certificatePath = this.apiConfig.getProperty("truthsocial_proxy_certificate");
        if (proxyHost != null && !proxyHost.isBlank() && proxyPortStr != null && !proxyPortStr.isBlank()
                && certificatePath != null && !certificatePath.isBlank()) {
            int proxyPort = Integer.parseInt(proxyPortStr);
            this.client = new ApiClient(proxyHost, proxyPort, certificatePath);
        } else {
            this.client = new ApiClient();
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
    
    protected String getNextPageLink(String input) {
        // Expresión regular para buscar la URL de "rel=next"
        String regex = "<([^>]+)>;\\s*rel=\"next\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    protected ApiRequest addHeaders(ApiRequest request, boolean needAuthorization) {
        String userAgent = this.apiConfig.getProperty("userAgent");

        if (needAuthorization) {
            request.addApiHeader("Authorization", "Bearer " + this.accessToken);
        }
        request.addApiHeader("Accept", "application/json, text/plain, */*");
        request.addApiHeader("Accept-Encoding", "gzip, deflate, br, zstd");
        request.addApiHeader("Cache-Control", "no-cache");
        request.addApiHeader("Host", "truthsocial.com");
        request.addApiHeader("Pragma", "no-cache");
        request.addApiHeader("User-Agent", userAgent);

        return request;
    }
    
    protected List<TruthsocialAccount> getUserList(String uri, int limit, int okStatus) throws TruthsocialApiException {
        try {
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request.addApiPathParam("limit", Integer.toString(limit));
            
            request = this.addHeaders(request, true);
            
            ApiResponse response = this.client.executeApiRequest(request);
            log.debug("Response getUserList: {}", response.getResponseStr());

            return this.gson.fromJson(response.getResponseStr(), new TypeToken<List<TruthsocialAccount>>(){}.getType());
        } catch (Exception e) {
            throw new TruthsocialApiException("Error recuperando una lista de usuarios: " + uri, e);
        }
    }
    
    // Función que devuelve una página de seguidores o seguidos de una cuenta
    private TruthsocialAccountListResponse getAccountPage(String uri, int okStatus,
            int limit, String posicionInicial) throws TruthsocialApiException {
        try {
            ApiRequest request = new ApiRequest(uri, okStatus, ApiMethodType.GET);
            request.addApiPathParam("limit", Integer.toString(limit));
            if (posicionInicial != null) {
                request.addApiPathParam("max_id", posicionInicial);
            }
            
            request = this.addHeaders(request, true);
            
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

            return new TruthsocialAccountListResponse(maxId, accounts);
        } catch (Exception e) {
            throw new TruthsocialApiException("Error recuperando una página de cuentas: " + uri, e);
        }
    }
    
    private boolean isContinuar(int quantity, List<TruthsocialAccount> accounts, String maxId,
            TruthsocialAccountListResponse accountListResponse, int limit) {
        log.debug("getTruthsocialAccountList. Cantidad: " + quantity + ". Recuperados: " + accounts.size() + ". Max_id: " + maxId);
        if (quantity > 0) {
            if ((accounts.size() >= quantity) || (maxId == null)) {
                return false;
            }
        } else {
            if ((maxId == null) || (accountListResponse.getAccounts().size() < limit)) {
                return false;
            }
        }
            
        return true;
    }
    
    protected TruthsocialAccountListResponse getTruthsocialAccountList(InstruccionInfo instruccionInfo,
            String id, int quantity, String posicionInicial) throws TruthsocialApiException {
        List<TruthsocialAccount> accounts = null;
        boolean continuar;
        String endpoint = instruccionInfo.getEndpoint();
        String complemento = instruccionInfo.getComplementoUrl();
        int limit = instruccionInfo.getLimit();
        int okStatus = instruccionInfo.getOkStatus();
        String maxId = posicionInicial;
        
        if (quantity > 0) {
            limit = Math.min(limit, quantity);
        }
        
        String uri = endpoint + "/" + id + "/" + complemento;

        do {
            TruthsocialAccountListResponse accountListResponse = this.getAccountPage(uri, okStatus, limit, maxId);
            if (accounts == null) {
                accounts = accountListResponse.getAccounts();
            } else {
                accounts.addAll(accountListResponse.getAccounts());
            }

            maxId = accountListResponse.getMaxId();
            continuar = this.isContinuar(quantity, accounts, maxId, accountListResponse, limit);
        } while (continuar);

        if (quantity == 0) {
            return new TruthsocialAccountListResponse(maxId, accounts);
        }

        return new TruthsocialAccountListResponse(maxId, accounts.subList(0, Math.min(quantity, accounts.size())));
    }
}