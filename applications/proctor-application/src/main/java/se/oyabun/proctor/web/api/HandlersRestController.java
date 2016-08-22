package se.oyabun.proctor.web.api;

import com.sun.tools.internal.ws.wsdl.document.http.HTTPConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.oyabun.proctor.handlers.ProctorRouteHandler;

import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(value = "/handlers")
public class HandlersRestController {

    @Autowired
    private List<ProctorRouteHandler> proctorRouteHandlers;

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProctorRouteHandler[]> getAllHandlers() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(proctorRouteHandlers.toArray(new ProctorRouteHandler[proctorRouteHandlers.size()]));

    }

}
