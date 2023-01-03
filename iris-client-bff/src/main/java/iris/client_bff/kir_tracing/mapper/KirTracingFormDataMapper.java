package iris.client_bff.kir_tracing.mapper;

import iris.client_bff.config.MapStructCentralConfig;

import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.KirTracingFormDto;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(config = MapStructCentralConfig.class, imports = UUID.class)
public interface KirTracingFormDataMapper {

    KirTracingForm toEntity(KirTracingFormDto formDto);

    KirTracingForm.Person toEntity(KirTracingFormDto.PersonDto personDto);

    @Mapping(source = "status", target = "status")
    @Mapping(source= "targetDisease", target="targetDisease",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    KirTracingFormDto toDto(KirTracingForm form);

    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    KirTracingForm update(@MappingTarget KirTracingForm entity, KirTracingFormDto updateEntity);

}
