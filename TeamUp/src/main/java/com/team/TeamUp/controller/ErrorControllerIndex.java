package com.team.TeamUp.controller;

import com.team.TeamUp.utils.FilesUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ErrorControllerIndex implements ErrorController {

    private static final String ERRORPATH = "/error";

    @RequestMapping(value = ERRORPATH, method = RequestMethod.GET)
    public String errorPage() throws IOException {
        String file = "static/404.html";
        return FilesUtils.readAllFile(file);
    }

    @Override
    public String getErrorPath() {
        return ERRORPATH;
    }
}
