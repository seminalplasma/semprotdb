package org.semprotdb.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.semprotdb.domain.DBConfig;
import org.semprotdb.repository.DBConfigRepository;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class LogServiceTest {

    @ParameterizedTest
    @ValueSource(strings = { "docker", "prod,docker", "prod" })
    void fileLogCollectorIsOnlyRegisteredOutsideDocker(String profiles) {
        DBConfigRepository repository = mock(DBConfigRepository.class);
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles(profiles.split(","));
            context.registerBean(DBConfigRepository.class, () -> repository);
            context.register(LogService.class);
            context.refresh();

            assertThat(context.getBeansOfType(LogService.class)).hasSize(profiles.contains("docker") ? 0 : 1);
            verifyNoInteractions(repository);
        }
    }

    @Test
    void disabledFileLoggingDoesNotWarnOrSaveLogs() {
        DBConfigRepository repository = mock(DBConfigRepository.class);
        when(repository.findDBConfigByKey("log.file.path")).thenReturn(Optional.of(new DBConfig().key("log.file.path").habilitado(false)));
        Logger logger = (Logger) LoggerFactory.getLogger(LogService.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            new LogService(repository).coletarLogs();

            assertThat(appender.list).isEmpty();
            verify(repository, never()).save(any(DBConfig.class));
            verify(repository, never()).findDBConfigByKey("log.file.limit");
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void enabledFileLoggingStillCollectsTheConfiguredFile(@TempDir Path directory) throws Exception {
        Path file = directory.resolve("application.log");
        Files.writeString(file, "first line\nlast line\n");
        DBConfigRepository repository = mock(DBConfigRepository.class);
        when(repository.findDBConfigByKey("log.file.path")).thenReturn(
            Optional.of(new DBConfig().key("log.file.path").habilitado(true).vstring(file.toString()))
        );
        DBConfig tail = new DBConfig().key("log.tail").habilitado(true);
        when(repository.findDBConfigByKey("log.tail")).thenReturn(Optional.of(tail));
        when(repository.findDBConfigByKey("log.file.limit")).thenReturn(Optional.of(new DBConfig().vint(1)));

        new LogService(repository).coletarLogs();

        verify(repository).save(tail);
        assertThat(tail.getVtext()).isEqualTo("last line");
    }
}
