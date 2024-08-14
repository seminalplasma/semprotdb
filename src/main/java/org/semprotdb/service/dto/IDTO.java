package org.semprotdb.service.dto;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public interface IDTO<T> {
    Path[] getConstructorArgsPath(Root<T> root);
}
