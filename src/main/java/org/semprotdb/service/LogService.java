package org.semprotdb.service;

import java.io.IOException;
import java.util.Optional;
import org.semprotdb.domain.DBConfig;
import org.semprotdb.repository.DBConfigRepository;
import org.semprotdb.util.FileIO.Tail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final DBConfigRepository dBConfigRepository;

    public LogService(DBConfigRepository dBConfigRepository) {
        this.dBConfigRepository = dBConfigRepository;
    }

    ///pegar ultimos registros de log do sistema do ultimo minuto
    @Scheduled(cron = "1 * * * * ?")
    public void coletarLogs() {
        Optional<DBConfig> dbConfigByKey = dBConfigRepository.findDBConfigByKey("log.file.path");
        DBConfig logs = dBConfigRepository.findDBConfigByKey("log.tail").orElse(new DBConfig().key("log.tail").habilitado(true));

        dbConfigByKey.ifPresentOrElse(
            logF -> {
                if (logF.getHabilitado()) {
                    String msgs = "ERRO";
                    try {
                        int rows = dBConfigRepository.findDBConfigByKey("log.file.limit").map(v -> v.getVint()).orElse(1000);

                        msgs = String.join("\n", Tail.readLast(logF.getVstring(), rows));
                    } catch (IOException e) {
                        log.error("ERRO AO LER LOGS !!! {}", e);
                        msgs = e.toString();
                    } finally {
                        dBConfigRepository.save(logs.vtext(msgs));
                    }
                } else {
                    log.warn("CONFIGURAR LOCAL DO ARQUIVO DE LOG EM DBConfig !!!");
                }
            },
            () -> {
                dBConfigRepository.save(new DBConfig().key("log.file.path").habilitado(false));
                dBConfigRepository.save(
                    new DBConfig()
                        .key("log.tail")
                        .vtext("ERRO: precisa configurar o local do arquivo de log conforme logback.")
                        .habilitado(false)
                );
            }
        );
    }
}
