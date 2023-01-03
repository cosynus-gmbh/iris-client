package iris.client_bff.kir_tracing.mapper;

import iris.client_bff.config.MapStructCentralConfig;

import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.KirTracingFormDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.UUID;

@Mapper(config = MapStructCentralConfig.class, imports = UUID.class)
public interface KirTracingFormDataMapper {

    KirTracingForm toEntity(KirTracingFormDto formDto);

    KirTracingForm.Person toEntity(KirTracingFormDto.PersonDto personDto);

    @Mapping(source = "status", target = "status")
    @Mapping(source= "targetDisease", target="targetDisease",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    KirTracingFormDto toDto(KirTracingForm form);

}
