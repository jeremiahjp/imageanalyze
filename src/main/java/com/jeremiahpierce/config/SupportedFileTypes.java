package com.jeremiahpierce.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "heb.cloud.supported")
public class SupportedFileTypes {

    public SupportedFileTypes() {}

    private Map<String, List<String>> filetypes;

    public Map<String, List<String>> getFiletypes() {
        return filetypes;
    }

    public void setFiletypes(Map<String, List<String>> filetypes) {
        this.filetypes = filetypes;
    }

    public boolean validateFiletype(String filetype) {
        return filetypes.containsKey(filetype);
    }
}

