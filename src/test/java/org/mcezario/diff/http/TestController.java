package org.mcezario.diff.http;

import org.mcezario.diff.domains.TestBusinessException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/test")
@Validated
public class TestController {

    @PostMapping
    public void postMethod(@RequestBody @Valid TestJson json) {
        throw new TestBusinessException();
    }

    @GetMapping
    public void getMethod() {
        throw new RuntimeException();
    }

    @PutMapping
    public void putMethod() {
        throw new TestBusinessException();
    }

    @GetMapping(value = "/header")
    public void validateHeader(@RequestHeader(name = "validateHeader", required = true) String header) {
        //
    }

    @GetMapping(value = "/request")
    public void validateRequestParam(@RequestParam(name = "validateRequest", required = true) String request) {
        //
    }

}
