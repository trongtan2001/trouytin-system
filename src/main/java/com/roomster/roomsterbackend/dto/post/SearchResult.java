package com.roomster.roomsterbackend.dto.post;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    public int total;
    public List<PostDtoWithFilter> data;
}
