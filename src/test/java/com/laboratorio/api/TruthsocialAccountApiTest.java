package com.laboratorio.api;

import com.laboratorio.truthsocialapiinterface.exception.TruthsocialApiException;
import com.laboratorio.truthsocialapiinterface.impl.TruthsocialAccountApiImpl;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialAccount;
import com.laboratorio.truthsocialapiinterface.model.TruthsocialRelationship;
import com.laboratorio.truthsocialapiinterface.utils.TruthsocialApiConfig;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.laboratorio.truthsocialapiinterface.TruthsocialAccountApi;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 10/07/2024
 * @updated 17/09/2024
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TruthsocialAccountApiTest {
    private String accessToken;
    private TruthsocialAccountApi accountApi;

    @BeforeEach
    private void initTest() {
        this.accessToken = TruthsocialApiConfig.getInstance().getProperty("access_token");
        this.accountApi = new TruthsocialAccountApiImpl(this.accessToken);
    }
    
    @Test
    public void findAccountById() {
        String id = "110998224199195544";
        String resultado = "camora_13";
        
        TruthsocialAccount account = this.accountApi.getAccountById(id);
        assertEquals(id, account.getId());
        assertEquals(resultado, account.getAcct());
    }
    
    @Test
    public void findAccountByInvalidId() {
        String id = "1125349753AAABBB60";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.accountApi.getAccountById(id);
        });
    }
    
    @Test
    public void findAccountByAcct() {
        String acct = "camora_13";
        String resultado = "110998224199195544";
        
        TruthsocialAccount account = this.accountApi.getAccountByUsername(acct);
        assertEquals(acct, account.getAcct());
        assertEquals(resultado, account.getId());
    }
    
    @Test
    public void findAccountByInvalidAcct() {
        String acct = "@ZZZWWWWPPPSSSDDGGGFF";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.accountApi.getAccountByUsername(acct);
        });
    }
    
    @Test
    public void get40Followers() throws Exception {
        String id = "110998224199195544";
        int cantidad = 40;
        
        List<TruthsocialAccount> accountList = this.accountApi.getFollowers(id, cantidad);

        assertEquals(cantidad, accountList.size());
    }
    
    @Test
    public void getAllFollowers() throws Exception {
        String id = "110998224199195544";
        
        List<TruthsocialAccount> accountList = this.accountApi.getFollowers(id);

        assertEquals(80, accountList.size());
    }
    
    @Test
    public void getFollowersInvalidQuantity() throws Exception {
        String id = "110998224199195544";
        int cantidad = 200;
        
        List<TruthsocialAccount> accountList = this.accountApi.getFollowers(id, cantidad);

        assertEquals(80, accountList.size());
    }
    
    @Test
    public void get80Followers() throws Exception {
        String id = "110998224199195544";
        int cantidad = 80;
        
        List<TruthsocialAccount> accountList = this.accountApi.getFollowers(id, cantidad);

        assertEquals(cantidad, accountList.size());
    }
    
    @Test
    public void getFollowersInvalidId() {
        String id = "1125349753AAABBB60";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.accountApi.getFollowers(id);
        });
    }
    
    @Test
    public void get40Followings() throws Exception {
        String id = "107845204224825099";
        int cantidad = 40;
        
        List<TruthsocialAccount> accountList = this.accountApi.getFollowings(id, cantidad);

        assertEquals(cantidad, accountList.size());
    }
    
    @Test
    public void getAllFollowings() throws Exception {
        String id = "107845204224825099";
        
        List<TruthsocialAccount> accountList = this.accountApi.getFollowings(id);

        assertEquals(80, accountList.size());
    }
    
    @Test
    public void get80Followings() throws Exception {
        String id = "107845204224825099";
        int cantidad = 80;
        
        List<TruthsocialAccount> accountList = this.accountApi.getFollowings(id, cantidad);

        assertEquals(cantidad, accountList.size());
    }
    
    @Test
    public void get200Followings() throws Exception {
        String id = "107845204224825099";
        int cantidad = 200;
        
        List<TruthsocialAccount> accountList = this.accountApi.getFollowings(id, cantidad);

        assertEquals(80, accountList.size());
    }
    
    @Test
    public void getFollowingsInvalidId() {
        String id = "1125349753AAABBB60";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.accountApi.getFollowings(id);
        });
    }
    
    @Test @Order(1)
    public void followAccount() {
        String id = "107845204224825099";
        
        boolean result = this.accountApi.followAccount(id);
        
        assertTrue(result);
    }
    
    @Test
    public void followInvalidAccount() {
        String id = "1125349753AAABBB60";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.accountApi.followAccount(id);
        });
    }
    
    @Test @Order(2)
    public void unfollowAccount() {
        String id = "107845204224825099";
        
        boolean result = this.accountApi.unfollowAccount(id);
        
        assertTrue(result);
    }
    
    @Test
    public void unfollowInvalidAccount() {
        String id = "1125349753AAABBB60";
        
        assertThrows(TruthsocialApiException.class, () -> {
            this.accountApi.unfollowAccount(id);
        });
    }
    
    @Test
    public void checkRelationships() {
        List<String> ids = List.of("110998224199195544", "107845204224825099");
        
        List<TruthsocialRelationship> list = this.accountApi.checkrelationships(ids);
        assertTrue(list.size() == 2);
    }
    
    @Test @Order(3)
    public void checkNullMutualRelationship() {
        List<String> ids = List.of("107845204224825099");
        
        List<TruthsocialRelationship> list = this.accountApi.checkrelationships(ids);
        assertFalse(list.get(0).isFollowing());
        assertFalse(list.get(0).isFollowed_by());
    }
    
    @Test
    public void getSuggestionsWithoutLimit() {
        int defaultLimit = Integer.parseInt(TruthsocialApiConfig.getInstance().getProperty("getSuggestions_default_limit"));
        
        List<TruthsocialAccount> accounts = this.accountApi.getSuggestions();
        
        assertEquals(defaultLimit, accounts.size());
    }
    
    @Test
    public void getSuggestionsWithLimit() {
        int limit = 10;
        
        List<TruthsocialAccount> accounts = this.accountApi.getSuggestions(limit);
        
        assertEquals(limit, accounts.size());
    }
}