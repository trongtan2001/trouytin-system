package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.report.ReportDto;
import com.roomster.roomsterbackend.entity.ReportEntity;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(ReportMapperDecorator.class)
public interface ReportMapper {
    @Mapping(target = "reportId", source = "id")
    ReportDto entityToDto(ReportEntity reportEntity);
    @Mapping(target = "id", source = "reportId")
    ReportEntity dtoToEntity(ReportDto postTypeDto);
}
