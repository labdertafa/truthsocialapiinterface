package com.laboratorio.truthsocialapiinterface.model;

import com.laboratorio.truthsocialapiinterface.utils.TruthsocialApiConfig;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.1
 * @created 10/07/2024
 * @updated 13/10/2024
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TruthsocialAccount {
    private String id;
    private String username;
    private String acct;
    private String url;
    private String display_name;
    private String note;
    private String avatar;
    private String avatar_static;
    private String header;
    private String header_static;
    private boolean locked;
    private TruthsocialField[] fields;
    private TruthsocialCustomEmoji[] emojis;
    private boolean bot;
    private boolean group;
    private boolean discoverable;
    private boolean indexable;
    private boolean suspended;
    private boolean limited;
    private String created_at;
    private String last_status_at;
    private boolean hide_collections;
    private int statuses_count;
    private int followers_count;
    private int following_count;

    public boolean isSeguidorPotencial() {
        if (this.locked) {
            return false;
        }
        
        if (this.following_count < 2) {
            return false;
        }

        if (2 * following_count < this.followers_count) {
            return false;
        }

        if (this.last_status_at != null) {
            LocalDate ultimaActividad;
            try {
                ultimaActividad = LocalDate.parse(this.last_status_at, DateTimeFormatter.ISO_DATE);
            } catch (Exception e) {
                return false;
            }
            TruthsocialApiConfig config = TruthsocialApiConfig.getInstance();
            int maxInactividad = Integer.parseInt(config.getProperty("dias_inactividad_cuenta"));
            long nDays = ChronoUnit.DAYS.between(ultimaActividad, LocalDate.now());
            return Math.abs(nDays) <= maxInactividad;
        }
        
        return true;
    }

    public boolean isFuenteSeguidores() {
        if (this.hide_collections) {
            return false;
        }
        
        TruthsocialApiConfig config = TruthsocialApiConfig.getInstance();
        int umbral = Integer.parseInt(config.getProperty("umbral_fuente_seguidores"));
        return this.followers_count >= umbral;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.username);
        hash = 29 * hash + Objects.hashCode(this.acct);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TruthsocialAccount other = (TruthsocialAccount) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.username, other.username);
    }

    @Override
    public String toString() {
        return "TruthsocialAccount{" + "id=" + id + ", username=" + username + ", acct=" + acct + ", display_name=" + display_name + ", discoverable=" + discoverable + ", created_at=" + created_at + ", last_status_at=" + last_status_at + ", statuses_count=" + statuses_count + ", followers_count=" + followers_count + ", following_count=" + following_count + '}';
    }
}