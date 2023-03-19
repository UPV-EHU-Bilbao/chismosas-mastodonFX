package ehu.eus.chismosas.mastodonfx.domain;

import java.util.List;

public class Account {
    public String id;
    public String username;
    public String acct;
    public String url;
    public String display_name;
    public String note;
    public String avatar;
    public String avatar_static;
    public String header;
    public String header_static;
    public Boolean locked;
    public List<Field> fields;
    public List<CustomEmoji> emojis;
    public Boolean bot;
    public Boolean group;
    public Boolean discoverable;
    public Boolean noindex;
    public Account moved;
    public Boolean suspended;
    public String created_at;
    public String last_status_at;
    public Integer statuses_count;
    public Integer followers_count;
    public Integer following_count;


    public class Field {
        public String name;
        public String value;
        public String verified_at;

    }

}
