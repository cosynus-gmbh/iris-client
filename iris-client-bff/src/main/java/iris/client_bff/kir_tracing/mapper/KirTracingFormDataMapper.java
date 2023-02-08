package iris.client_bff.kir_tracing.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import iris.client_bff.config.MapStructCentralConfig;

import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.eps.KirTracingFormDto;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(config = MapStructCentralConfig.class, imports = UUID.class)
public interface KirTracingFormDataMapper {



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    KirTracingForm toEntity(KirTracingFormDto formDto);

    KirTracingForm.Person toEntity(KirTracingFormDto.PersonDto personDto);

    @Mapping(source = "status", target = "status")
    @Mapping(source= "targetDisease", target="targetDisease",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    KirTracingFormDto toDto(KirTracingForm form);

    default JsonNode map(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, JsonNode.class);
        } catch (Exception e) {
            return null;
        }
    }

    default String map(JsonNode json) {
      return json.toString();
    }

    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    KirTracingForm update(@MappingTarget KirTracingForm entity, KirTracingFormDto updateEntity);

}
