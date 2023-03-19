package ehu.eus.chismosas.mastodonfx.domain;

import java.util.List;

public class Poll {
    public String id;
    public String expires_at;
    public Boolean expired;
    public Boolean multiple;
    public Integer votes_count;
    public Integer voters_count;
    public List<Option> options;
    public List<CustomEmoji> emojis;
    public Boolean voted;
    public List<Integer> own_votes;

    public class Option {
        public String title;
        public Integer votes_count;
    }
}
