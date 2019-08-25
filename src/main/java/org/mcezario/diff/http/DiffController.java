package org.mcezario.diff.http;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.mcezario.diff.http.json.DiffResponse;
import org.mcezario.diff.http.json.EncodedDataRequest;
import org.mcezario.diff.http.json.ExceptionJson;
import org.mcezario.diff.usecases.DiffAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/diff/{id}")
@Api(tags = "Diff API management", produces = MediaType.APPLICATION_JSON_VALUE)
public class DiffController {

    @Autowired
    private DiffAnalyser diffAnalyser;

    @ApiOperation(value = "Resource to receive the left side of comparison.", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Request Validation", response = ExceptionJson.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ExceptionJson.class)
    })
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/left", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void leftSide(@PathVariable String id, @Valid @RequestBody EncodedDataRequest request) {

        log.debug("Received the left side with id {}", id);
        diffAnalyser.left(id, request.getContent());

    }

    @ApiOperation(value = "Resource to receive the right side of comparison.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Request Validation", response = ExceptionJson.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ExceptionJson.class)
    })
    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/right", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void rightSide(@PathVariable String id, @Valid @RequestBody EncodedDataRequest request) {

        log.debug("Received the right side with id {}", id);
        diffAnalyser.right(id, request.getContent());

    }

    @ApiOperation(value = "Resource to compare the left and right side.", response = DiffResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Diff not found", response = ExceptionJson.class),
            @ApiResponse(code = 422, message = "Business Validation", response = ExceptionJson.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ExceptionJson.class)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public DiffResponse result(@PathVariable String id) {

        log.debug("Received a request to compare the result of diff id {}", id);
        return new DiffResponse(diffAnalyser.compare(id));
    }

}
