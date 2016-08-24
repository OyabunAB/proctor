package se.oyabun.proctor.web.admin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;

import java.util.Optional;
import java.util.Set;

/**
 * Proctor Handlers REST API
 */
@RestController
@RequestMapping(value = "/handler")
public class HandlersRestController {

    @Autowired
    private ProctorRouteHandlerManager proctorRouteHandlerManager;

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProctorRouteHandler[]> getAllRegisteredRouteHandlers() {

        final Set<ProctorRouteHandler> registeredProctorRouteHandlers =
                proctorRouteHandlerManager.getRegisteredRouteHandlers();

        if(proctorRouteHandlerManager.getRegisteredRouteHandlers().isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ProctorRouteHandler[]{});

        } else {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(registeredProctorRouteHandlers.toArray(
                            new ProctorRouteHandler[registeredProctorRouteHandlers.size()]));

        }

    }

    @RequestMapping(
            value = "/{handlerName}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProctorRouteHandler> getSpecificRouteHandlerFor(@PathVariable("handlerName")
                                                                          final String handlerName) {

        final Set<ProctorRouteHandler> registeredProctorRouteHandlers =
                proctorRouteHandlerManager.getRegisteredRouteHandlers();

        if(registeredProctorRouteHandlers.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(null);

        } else {

            Optional<ProctorRouteHandler> optionalRouteHandler =
                    registeredProctorRouteHandlers
                            .stream()
                            .filter(proctorRouteHandler ->
                                    proctorRouteHandler.getHandleNames().contains(handlerName))
                            .findFirst();

            if(optionalRouteHandler.isPresent()) {

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(optionalRouteHandler.get());

            } else {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(null);

            }

        }

    }


}
