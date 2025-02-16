package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.dto.post.PostDtoWithFilter;
import com.roomster.roomsterbackend.dto.post.SearchResult;
import com.roomster.roomsterbackend.repository.PostTypeRepository;
import com.roomster.roomsterbackend.service.IService.IDatabaseSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseSearch implements IDatabaseSearch {
    @Autowired
    private PostTypeRepository repository;
    @Override
    public SearchResult searchFilter(Pageable pageable, LinkedHashMap<String, Object> map) throws SQLException {
        String url = "jdbc:mysql://srv1098.hstgr.io/u898129453_testdb";
        String username = "u898129453_root25";
        String password = "123456789Aaa!";
        String tableName = "posts";
        String joinTable = "infor_rooms";

        if (!map.isEmpty() && map.containsKey("post_type")) {
            Long postTypeId = repository.getPostEntityByName((String) map.get("post_type")).getId();
            map.put("post_type_id", postTypeId);
            map.remove("post_type");
        }

        final Connection connection = DriverManager.getConnection(url, username, password);
        StringBuilder filterQuery = new StringBuilder();
        StringBuilder totalResultQuery = new StringBuilder();
        totalResultQuery.append("SELECT count(*) as total FROM ").append(tableName).append(" inner join ").append(joinTable).append(" on posts.room_id = infor_rooms.id").append(" where posts.is_deleted = false ");
        filterQuery.append("Select p.id, p.title, p.address, p.created_date,p.modified_date, ir.price, p.is_deleted,p.status, max(pimg.image_url_list) as image")
                .append(" from posts p")
                .append(" left join post_entity_image_url_list pimg")
                .append(" on p.id = pimg.post_entity_id")
                .append(" inner join infor_rooms ir on ir.id = p.room_id")
                .append(" where p.is_deleted = false");
        int count = 0;
        if(!map.isEmpty()) {
            totalResultQuery.append(" AND ");
            filterQuery.append(" AND ");
            for (String key : map.keySet()) {
                if (count > 0) {
                    filterQuery.append(" AND ");
                    totalResultQuery.append(" AND ");
                }
                switch (key) {
                    case "price" -> {
                        filterQuery.append("price BETWEEN ? AND ?");
                        totalResultQuery.append("price BETWEEN ? AND ?");
                    }
                    case "acreage" -> {
                        filterQuery.append("acreage BETWEEN ? AND ?");
                        totalResultQuery.append("acreage BETWEEN ? AND ?");
                    }
                    default -> {
                        if (map.get(key) instanceof String) {
                            filterQuery.append(key).append(" LIKE ?");
                            totalResultQuery.append(key).append(" LIKE ?");
                        } else if (map.get(key) instanceof Number) {
                            filterQuery.append(key).append(" = ?");
                            totalResultQuery.append(key).append(" = ?");
                        }
                    }
                }
                count++;
            }
        }
//        filterQuery.append(" and p.status = 'APPROVED'");
//        totalResultQuery.append(" and posts.status = 'APPROVED'");
        // Use Group By on filter query
        filterQuery.append(" group by p.id, p.title, p.address, p.created_by, p.created_date, ir.price, p.is_deleted");

        //Order by Created data DESC

        filterQuery.append(" order by p.created_date DESC");

        // Use Pageable to determine limit and offset
        filterQuery.append(" LIMIT ? OFFSET ?");

        System.out.println(filterQuery);
        System.out.println(totalResultQuery);

        // Prepare the statement and pass the search parameter values
        PreparedStatement stmtToFilter = connection.prepareStatement(filterQuery.toString());
        PreparedStatement stmtToTotalResult = connection.prepareStatement(totalResultQuery.toString());
        int parameterIndex = 0;
        if(!map.isEmpty()) {
            for (String key : map.keySet()) {
                if (map.get(key) instanceof int[]) {
                    int[] range = (int[]) map.get(key);
                    if (range.length >= 2) {
                        stmtToFilter.setObject(parameterIndex + 1, range[0]);
                        stmtToFilter.setObject(parameterIndex + 2, range[1]);

                        stmtToTotalResult.setObject(parameterIndex + 1, range[0]);
                        stmtToTotalResult.setObject(parameterIndex + 2, range[1]);
                    }
                    parameterIndex += 2;
                } else {
                    if (map.get(key) instanceof String) {
                        stmtToFilter.setString(parameterIndex + 1, "%" + map.get(key) + "%");

                        stmtToTotalResult.setString(parameterIndex + 1, "%" + map.get(key) + "%");
                    } else if (map.get(key) instanceof Number) {
                        stmtToFilter.setObject(parameterIndex + 1, map.get(key));

                        stmtToTotalResult.setObject(parameterIndex + 1, map.get(key));
                    }
                    parameterIndex++;
                }
            }
        }

        stmtToFilter.setInt(parameterIndex + 1, pageable.getPageSize());
        stmtToFilter.setLong(parameterIndex + 2, pageable.getOffset());
        System.out.println(stmtToFilter);
        System.out.println(stmtToTotalResult);

//        getTotalResult
        int totalCount = 0;
        try (ResultSet countResultSet = stmtToTotalResult.executeQuery()){
            if (countResultSet.next()) {
                totalCount = countResultSet.getInt(1);
            }
        }

        System.out.println(totalCount);

        List<PostDtoWithFilter> postDTOs = new ArrayList<>();
        try (ResultSet rs = stmtToFilter.executeQuery()) {
            while (rs.next()) {
                PostDtoWithFilter postDtoWithFilter = new PostDtoWithFilter();
                postDtoWithFilter.setId(rs.getLong("id"));
                postDtoWithFilter.setTitle(rs.getString("title"));
                postDtoWithFilter.setAddress(rs.getString("address"));
                postDtoWithFilter.setCreatedDate(rs.getDate("created_date"));
                postDtoWithFilter.setModifiedDate(rs.getDate("modified_date"));
                postDtoWithFilter.setPrice(rs.getBigDecimal("price"));
                postDtoWithFilter.setDeleted(rs.getBoolean("is_deleted"));
                postDtoWithFilter.setStatus(rs.getString("status"));
                postDtoWithFilter.setImage(rs.getString("image"));
                postDTOs.add(postDtoWithFilter);
            }
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setTotal(totalCount);
        searchResult.setData(postDTOs.stream().filter(postDtoWithFilter -> !postDtoWithFilter.isDeleted()).collect(Collectors.toList()));
        return searchResult;
    }
}
