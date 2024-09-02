package com.laboratorio.truthsocialapiinterface.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.response.TruthsocialAccountListResponse;
import com.laboratorio.truthsocialapiinterface.utils.CookieManager;
import com.laboratorio.truthsocialapiinterface.utils.InstruccionInfo;
import com.laboratorio.truthsocialapiinterface.utils.TruthsocialApiConfig;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 24/07/2024
 * @updated 02/09/2024
 */
public class TruthsocialBaseApi {
    protected static final Logger log = LogManager.getLogger(TruthsocialBaseApi.class);
    protected final String accessToken;
    protected TruthsocialApiConfig apiConfig;

    public TruthsocialBaseApi(String accessToken) {
        this.accessToken = accessToken;
        this.apiConfig = TruthsocialApiConfig.getInstance();
    }
    
    protected void logException(Exception e) {
        log.error("Error: " + e.getMessage());
        if (e.getCause() != null) {
            log.error("Causa: " + e.getMessage());
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
    
    // Obtiene las cookies del website
    public List<String> getWebsiteCookies() {
        // Cargar las cookies almacenadas si existen
        Map<String, NewCookie> existingCookies = CookieManager.loadCookies();
        if (!existingCookies.isEmpty()) {
            return CookieManager.extractCookiesInformation(existingCookies);
        }
        
        ResteasyClient client = new ResteasyClientBuilderImpl()
                .enableCookieManagement()
                .build();
        Response response = null;
        
        try {
            // Realiza una primera solicitud para obtener las cookies
            String initialUrl = "https://truthsocial.com";
            ResteasyWebTarget target = client.target(initialUrl);
            response = target.request().get();

            return CookieManager.extractCookiesInformation(response.getCookies());
        } catch (Exception e) {
            logException(e);
            throw e;
        } finally {
            if (response != null) {
                response.close();
            }
            client.close();
        }
    }
    
    // Crea la HTTP request
    protected Invocation.Builder createRequest(Client client, WebTarget target, String url) {
        try {
            List<String> cookiesList = this.getWebsiteCookies();

            Invocation.Builder requestBuilder = target.request(MediaType.APPLICATION_JSON);
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
            requestBuilder.header("User-Agent", "PostmanRuntime/7.41.2");
            requestBuilder.header("Accept", "*/*");
            requestBuilder.header("Accept-Encoding", "gzip, deflate, br");
            requestBuilder.header("Connection", "keep-alive");

            for (String cookie : cookiesList) {
                requestBuilder.cookie(Cookie.valueOf(cookie));
            }

            return requestBuilder;
        } catch (Exception e) {
            logException(e);
            throw e;
        }
    }
    
    // Procesar la respuesta HTTP
    protected String processResponse(Response response) {
        try {
            // Obtén el InputStream de la entidad y descomprímelo si es necesario
            InputStream inputStream = response.readEntity(InputStream.class);
            String contentEncoding = response.getHeaderString("Content-Encoding");
            // Verifica si la respuesta está comprimida
            if ("gzip".equalsIgnoreCase(contentEncoding)) {
                inputStream = new GZIPInputStream(inputStream);
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder responseStrBuilder = new StringBuilder();
            
            String line;
            while ((line = reader.readLine()) != null) {
                responseStrBuilder.append(line);
            }

            reader.close();
            
            // Almacena las cookies de la respuesta
            CookieManager.saveCookies(response.getCookies());
            
            return responseStrBuilder.toString();
        } catch (Exception e) {
            throw new TruthsocialApiException(TruthsocialBaseApi.class.getName(), "Error descomprimiendo la respuesta recibida");
        }
    }
    
    protected List<TruthsocialAccount> getUserList(String url, String id, int limit, int okStatus) throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = null;
        
        try {
            WebTarget target = client.target(url)
                        .queryParam("limit", limit);
            
            response = this.createRequest(client, target, url).get();
            
            String jsonStr = processResponse(response);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new TruthsocialApiException(TruthsocialBaseApi.class.getName(), str);
            }
            
            log.info("Se ejecutó la query: " + url);
            log.info("Respuesta JSON recibida: " + jsonStr);
            
            Gson gson = new Gson();
            return gson.fromJson(jsonStr, new TypeToken<List<TruthsocialAccount>>(){}.getType());
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
    
    // Función que devuelve una página de seguidores o seguidos de una cuenta
    private TruthsocialAccountListResponse getAccountPage(String endpoint, String complemento, int okStatus, String id, int limit, String posicionInicial) throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = null;
        
        try {
            String url = endpoint + "/" + id + "/" + complemento;
            WebTarget target = client.target(url)
                        .queryParam("limit", limit);
            if (posicionInicial != null) {
                target = target.queryParam("max_id", posicionInicial);
            }
            
            response = this.createRequest(client, target, url).get();
            
            String jsonStr = processResponse(response);
            if (response.getStatus() != okStatus) {
                log.error(String.format("Respuesta del error %d: %s", response.getStatus(), jsonStr));
                String str = "Error ejecutando: " + url + ". Se obtuvo el código de error: " + response.getStatus();
                throw new TruthsocialApiException(TruthsocialBaseApi.class.getName(), str);
            }
            
            Gson gson = new Gson();
            List<TruthsocialAccount> accounts = gson.fromJson(jsonStr, new TypeToken<List<TruthsocialAccount>>(){}.getType());
            String maxId = null;
            if (!accounts.isEmpty()) {
                log.info("Se ejecutó la query: " + url);
                log.info("Resultados encontrados: " + accounts.size());

                String linkHeader = response.getHeaderString("link");
                log.info("Recibí este link: " + linkHeader);
                maxId = this.extractMaxId(linkHeader);
                log.info("Valor del max_id: " + maxId);
            }

            // return accounts;
            return new TruthsocialAccountListResponse(maxId, accounts);
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
            do {
                TruthsocialAccountListResponse accountListResponse = this.getAccountPage(endpoint, complemento, okStatus, id, limit, max_id);
                if (accounts == null) {
                    accounts = accountListResponse.getAccounts();
                } else {
                    accounts.addAll(accountListResponse.getAccounts());
                }
                
                max_id = accountListResponse.getMaxId();
                log.info("getTruthsocialAccountList. Cantidad: " + quantity + ". Recuperados: " + accounts.size() + ". Max_id: " + max_id);
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