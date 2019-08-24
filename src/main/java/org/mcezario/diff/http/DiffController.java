package org.mcezario.diff.http;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.mcezario.diff.usecases.DiffAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/v1/diff/{id}")
@Api(tags = "Diff management", produces = MediaType.APPLICATION_JSON_VALUE)
public class DiffController {

    @Autowired
    private DiffAnalyser diffAnalyser;

    @ApiOperation(value = "Resource to receive the left side of comparison.", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/left", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> leftSide(@PathVariable String id, @RequestBody String data) {
        return new ResponseEntity<>(diffAnalyser.left(id, data), HttpStatus.OK);
    }

    @ApiOperation(value = "Resource to receive the right side of comparison.", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/right", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rightSide(@PathVariable String id, @RequestBody String data) {
        return new ResponseEntity<>(diffAnalyser.right(id, data), HttpStatus.OK);
    }

}
