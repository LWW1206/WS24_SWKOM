package org.technikum.dms;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectionTestResource {
    @Autowired
    private ConnectionTestService connectionTestService;

    @GetMapping("/hello")
    @Operation(
            description = "Hello World to test the connection"
    )
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/hello/{dummyUrlParam}")
    @Operation(
            description = "Hello World to test the connection with a url param"
    )
    public String helloWithParam(@PathVariable String dummyUrlParam) {
        return connectionTestService.helloWorldWithDummyUrlParam(dummyUrlParam);
    }
}
