package com.itacademy.myhospital.errorController;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HospitalErrorController implements ErrorController {

    public String getErrorPage() {
        return "/error";
    }

    @GetMapping("/error")
    public String handlerError(HttpServletRequest request) {
        String errorPage = "error";
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorPage = "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorPage = "error/500";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorPage = "error/403";
            }else  if (statusCode==HttpStatus.METHOD_NOT_ALLOWED.value()){
                errorPage="error/405";
            }else  if (statusCode==HttpStatus.BAD_REQUEST.value()){
                errorPage="error/400";
            }
        }
        return errorPage;
    }

}
