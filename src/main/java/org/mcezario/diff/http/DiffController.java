package org.mcezario.diff.http;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.mcezario.diff.http.json.DiffResponse;
import org.mcezario.diff.http.json.EncodedDataRequest;
import org.mcezario.diff.usecases.DiffAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/v1/diff/{id}")
@Api(tags = "Diff management", produces = MediaType.APPLICATION_JSON_VALUE)
public class DiffController {

    @Autowired
    private DiffAnalyser diffAnalyser;

    @ApiOperation(value = "Resource to receive the left side of comparison.", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "CREATED"),
            @ApiResponse(code = 400, message = "Fields validation."),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/left", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity leftSide(@PathVariable String id, @Valid @RequestBody EncodedDataRequest request) {

        diffAnalyser.left(id, request.getContent());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Resource to receive the right side of comparison.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "CREATED"),
            @ApiResponse(code = 400, message = "Fields validation."),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/right", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity rightSide(@PathVariable String id, @Valid @RequestBody EncodedDataRequest request) {

        diffAnalyser.right(id, request.getContent());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Resource to receive the right side of comparison.", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<DiffResponse> result(@PathVariable String id) {

        return new ResponseEntity<>(new DiffResponse(diffAnalyser.compare(id)), HttpStatus.OK);
    }

}
