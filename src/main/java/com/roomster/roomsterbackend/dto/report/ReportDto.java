package com.roomster.roomsterbackend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Long reportId;
    private String content;
    private String name;
    private String phoneNumber;
    private Date createdDate;
    private Long idOfPost;
}
