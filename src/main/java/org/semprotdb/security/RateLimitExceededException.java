package org.semprotdb.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;

public class RateLimitExceededException extends ErrorResponseException {

    public RateLimitExceededException(final String mensagem) {
        super(
            HttpStatus.TOO_MANY_REQUESTS,
            ProblemDetailWithCause.ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.TOO_MANY_REQUESTS.value())
                .withTitle(mensagem)
                .withProperty("message", "error.ratelimit")
                .build(),
            null
        );
    }
}
