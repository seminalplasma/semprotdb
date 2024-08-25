package org.semprotdb.service.dto;

import java.time.Instant;

public interface FeedbackDTO {
    Long getId();
    Boolean getHabilitado();
    Instant getVdate();
    String getVtext();
}
