package eus.ehu.chismosas.mastodonfx.domain;


import java.util.List;

public class Status {
    public String id;
    public String uri;
    public String created_at;
    public Account account;
    public String content;
    public String visibility;
    public Boolean sensitive;
    public String spoiler_text;
    public List<MediaAttachment> media_attachments;
    public Application application;
    public List<Mention> mentions;
    public List<Tag> tags;
    public List<CustomEmoji> emojis;
    public Integer reblogs_count;
    public Integer favourites_count;
    public Integer replies_count;
    public String url;
    public String in_reply_to_id;
    public String in_reply_to_account_id;
    public Status reblog;
    public Poll poll;
    public PreviewCard card;
    public String language;
    public String text;
    public String edited_at;
    public Boolean favourited;
    public Boolean reblogged;
    public Boolean muted;
    public Boolean bookmarked;
    public Boolean pinned;
    public List<FilterResult> filtered;


    public class Mention {
        public String id;
        public String username;
        public String url;
        public String acct;
    }

}
