package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import org.semprotdb.domain.DBConfig;
import org.semprotdb.domain.User;
import org.semprotdb.repository.DBConfigRepository;
import org.semprotdb.security.RateLimitingAspect.RateLimitedByIP;
import org.semprotdb.service.UserService;
import org.semprotdb.service.dto.DBConfigDTO;
import org.semprotdb.service.dto.FeedbackDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

@RestController
@RequestMapping("/api/feedbacks")
@Transactional
public class FeedbackResource {

    private static final Logger log = LoggerFactory.getLogger(FeedbackResource.class);

    private final UserService userService;
    private final DBConfigRepository dBConfigRepository;

    public FeedbackResource(DBConfigRepository dBConfigRepository, UserService userService) {
        this.dBConfigRepository = dBConfigRepository;
        this.userService = userService;
    }

    @PostMapping
    @RateLimitedByIP
    public ResponseEntity<DBConfig> createFeedback(@Valid @RequestBody DBConfig dBConfig) throws URISyntaxException {
        log.debug("REST request to register FEEDBACK: {}", dBConfig);

        String txt = dBConfig.getVtext() != null ? dBConfig.getVtext() : "";
        txt = txt.length() > 1000 ? txt.substring(0, 1000) : txt;

        DBConfig dbc = new DBConfig()
            .key("feedbacks")
            .vtext(txt)
            .vdate(new Date().toInstant())
            .vstring(userService.getUserWithAuthorities().map(User::getEmail).orElse("ANONIMO"));

        log.info("Novo feedback: {}", dbc);
        dbc.setHabilitado(true);
        dbc = dBConfigRepository.save(dbc);
        return ResponseEntity.created(new URI("/semprotdb"))
            .headers(HeaderUtil.createEntityCreationAlert("SemprotDB", true, "Feedback", dbc.getId().toString()))
            .body(dbc);
    }

    @GetMapping
    List<FeedbackDTO> getFeedbacks() {
        return dBConfigRepository.findFirst100ByHabilitadoIsTrueAndAndKeyIsOrderByVdateDesc("feedbacks");
    }
}
