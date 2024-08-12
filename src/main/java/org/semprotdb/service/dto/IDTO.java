package org.semprotdb.service.dto;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.semprotdb.domain.Organismo;

public interface IDTO<T> {
    Path[] getConstructorArgsPath(Root<T> root);
}
