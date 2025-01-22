package org.technikum.dms.service;

import org.springframework.stereotype.Service;

@Service
public class ConnectionTestService {

    public String helloWorldWithDummyUrlParam(String dummyUrlParam) {
        return "Hello World! Link param was: <" + dummyUrlParam + ">";
    }
}
