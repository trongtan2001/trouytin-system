package com.roomster.roomsterbackend.mapper;

import com.roomster.roomsterbackend.dto.service.servicePackage.ServicePackageDto;
import com.roomster.roomsterbackend.entity.ServicePackageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServicePackageMapper {

    @Mapping(target = "servicePackageId", source = "id")
    ServicePackageDto entityToDto(ServicePackageEntity servicePackage);

    @Mapping(target = "id", source = "servicePackageId")
    ServicePackageEntity dtoToEntity(ServicePackageDto servicePackageDto);
}
