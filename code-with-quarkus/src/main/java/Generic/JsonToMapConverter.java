package Generic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;
import java.util.Map;

@Converter
public class JsonToMapConverter implements AttributeConverter<Map<String, String>, String> {
    private static final Logger log = LoggerFactory.getLogger(JsonToMapConverter.class);

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(Map<String, String> data) {
        if (null == data) {
            return "{}";
        }

        try {
            return mapper.writeValueAsString(data);

        } catch (Exception e) {
            log.error("Error converting map to JSON");
            return null;
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String s) {
        if (null == s) {
            return new HashMap<>();
        }

        try {
            return mapper.readValue(s, new TypeReference<Map<String, String>>() {});

        } catch (Exception e) {
            log.error("Error converting JSON to map");
            return null;
        }
    }
}