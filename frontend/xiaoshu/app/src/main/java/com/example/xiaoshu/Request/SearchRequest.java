package com.example.xiaoshu.Request;

import com.example.xiaoshu.Response.SearchResponse;

public class SearchRequest {
    int id;
    String keyword;

    public SearchRequest(int id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }
}
