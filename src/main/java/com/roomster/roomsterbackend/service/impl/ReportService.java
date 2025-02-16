package com.roomster.roomsterbackend.service.impl;

import com.roomster.roomsterbackend.base.BaseResultWithDataAndCount;
import com.roomster.roomsterbackend.base.BaseResponse;
import com.roomster.roomsterbackend.dto.report.ReportDto;
import com.roomster.roomsterbackend.mapper.ReportMapper;
import com.roomster.roomsterbackend.repository.ReportRepository;
import com.roomster.roomsterbackend.service.IService.IReportService;
import com.roomster.roomsterbackend.util.message.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService implements IReportService {
    @Autowired
    private ReportRepository repository;

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public ReportDto addReport(ReportDto reportDto) {
        reportDto.setCreatedDate(new Date());
        return reportMapper.entityToDto(repository.save(reportMapper.dtoToEntity(reportDto)));
    }

    @Override
    public List<ReportDto> getAllReportByPostId(Long postId) {
        return repository.getAllByPostId_Id(postId).stream().map(reportEntity -> reportMapper.entityToDto(reportEntity)).collect(Collectors.toList());
    }

    @Override
    public void deleteReportById(Long[] reportId) {
        for (Long item : reportId
        ) {
            repository.deleteById(item);
        }
    }

    @Override
    public ResponseEntity<?> getAllReport(Pageable pageable) {
        ResponseEntity<?> response = null;
        BaseResultWithDataAndCount<List<ReportDto>> resultWithDataAndCount = new BaseResultWithDataAndCount<>();
        try {
            List<ReportDto> reportDtos = repository.findAll(pageable)
                    .stream()
                    .map(reportEntity -> reportMapper.entityToDto(reportEntity))
                    .collect(Collectors.toList());
            Long count = repository.count();
            resultWithDataAndCount.set(reportDtos, count);
            response = new ResponseEntity<>(resultWithDataAndCount, HttpStatus.OK);
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Override
    public ResponseEntity<?> deleteReportByIds(Long[] reportId) {
        ResponseEntity<?> response = null;
        try {
            for (Long item : reportId
            ) {
                repository.deleteById(item);
                response = new ResponseEntity<>(BaseResponse.success(MessageUtil.MSG_DELETE_SUCCESS), HttpStatus.OK);
            }
        } catch (Exception ex) {
            response = new ResponseEntity<>(BaseResponse.error(MessageUtil.MSG_SYSTEM_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
