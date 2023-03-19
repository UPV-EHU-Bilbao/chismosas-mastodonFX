package ehu.eus.chismosas.mastodonfx.domain;

import java.util.List;

public class Filter {
    public String id;
    public String title;
    public List<String> context;
    public String expires_at;
    public String filter_action;
    public List<FilterKeyword> keywords;
    public List<FilterStatus> statuses;

}
