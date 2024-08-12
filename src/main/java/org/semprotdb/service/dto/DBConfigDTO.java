package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import org.semprotdb.domain.DBConfig;

public class DBConfigDTO extends DBConfig implements IDTO<DBConfigDTO> {

    @JsonIgnore
    private String vtext;

    @JsonIgnore
    private byte[] vimg;

    @JsonIgnore
    private String vimgContentType;

    public DBConfigDTO() {}

    public DBConfigDTO(Long id, String key, Boolean habilitado, String vstring, Boolean vbol, Instant vdate, Integer vint) {
        setId(id);
        setKey(key);
        setHabilitado(habilitado);
        setVstring(vstring);
        setVbol(vbol);
        setVdate(vdate);
        setVint(vint);
    }

    @Override
    public Path[] getConstructorArgsPath(Root<DBConfigDTO> root) {
        return new Path[] {
            root.get("id"),
            root.get("key"),
            root.get("habilitado"),
            root.get("vstring"),
            root.get("vbol"),
            root.get("vdate"),
            root.get("vint"),
        };
    }
}
