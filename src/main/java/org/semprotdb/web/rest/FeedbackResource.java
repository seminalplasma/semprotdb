package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import org.semprotdb.domain.DBConfig;
import org.semprotdb.domain.User;
import org.semprotdb.repository.DBConfigRepository;
import org.semprotdb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

///https://levelup.gitconnected.com/rate-limiting-in-spring-boot-52220ba272c6
//@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.METHOD)
//public @interface WithRateLimitProtection {
//}

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

    @PostMapping("")
    public ResponseEntity<DBConfig> createDBConfig(@Valid @RequestBody DBConfig dBConfig) throws URISyntaxException {
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
}
