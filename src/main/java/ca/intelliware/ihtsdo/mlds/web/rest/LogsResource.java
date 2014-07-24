package ca.intelliware.ihtsdo.mlds.web.rest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.codahale.metrics.annotation.Timed;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.LoggerDTO;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/app")
public class LogsResource {

    @RequestMapping(value = "/rest/logs",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<LoggerDTO> getList() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<LoggerDTO> loggers = new ArrayList<>();
        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            loggers.add(new LoggerDTO(logger));
        }
        return loggers;
    }

    @RequestMapping(value = "/rest/logs",
            method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public void changeLevel(@RequestBody LoggerDTO jsonLogger) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
    }
}
