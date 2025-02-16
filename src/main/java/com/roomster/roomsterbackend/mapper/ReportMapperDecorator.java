package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.report.ReportDto;
import com.roomster.roomsterbackend.entity.ReportEntity;
import com.roomster.roomsterbackend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class ReportMapperDecorator implements ReportMapper{

    @Autowired
    @Qualifier("delegate")
    private ReportMapper delegate;

    @Autowired
    private PostRepository repository;

    @Override
    public ReportDto entityToDto(ReportEntity reportEntity){
        ReportDto reportDto = delegate.entityToDto(reportEntity);
        reportDto.setIdOfPost(reportEntity.getPostId().getId());
        return reportDto;
    }

    @Override
    public ReportEntity dtoToEntity(ReportDto reportDto){
        ReportEntity reportEntity = delegate.dtoToEntity(reportDto);
        if(reportDto.getIdOfPost() != null){
            reportEntity.setPostId(repository.findById(reportDto.getIdOfPost()).orElse(null));
        }
        return reportEntity;
    }

}
