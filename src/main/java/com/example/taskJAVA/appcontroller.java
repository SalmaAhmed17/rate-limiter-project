package com.example.taskJAVA;

import com.example.taskJAVA.rateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rate-limiter")
public class appcontroller {// an endpoint that uses GET req to call the rateLimiter and
    //check if the user allowed/exist in the db
    @Autowired
    private rateLimiter rateLimiter;

    // GET endpoint to check if a user can perform an action
    @GetMapping("/check/{userId}")
    public String checkRateLimit(@PathVariable int userId) {
        if (rateLimiter.isAllowed(userId)) {
            return "Action allowed for user " + userId;
        } else {
            return "Action not allowed for user " + userId;
        }
    }
}

