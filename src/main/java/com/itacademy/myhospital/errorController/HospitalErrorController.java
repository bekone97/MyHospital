package com.itacademy.myhospital.errorController;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HospitalErrorController implements ErrorController {

    public String getErrorPage() {
        return "/error";
    }

    @GetMapping("/error")
    public String handlerError(HttpServletRequest request, Model model) {
        String errorPage = "error";
        String nameOfPage = "Error";
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                nameOfPage = "Page Not Found";
                errorPage = "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                nameOfPage = "Internal Server Error";
                errorPage = "error/500";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                nameOfPage = "Forbidden Error";
                errorPage = "error/403";
            }else  if (statusCode==HttpStatus.METHOD_NOT_ALLOWED.value()){
                nameOfPage="Method not allowed";
                errorPage="error/405";
            }else  if (statusCode==HttpStatus.BAD_REQUEST.value()){
                nameOfPage="Bad request";
                errorPage="error/400";
            }
        }
        model.addAttribute("nameOfPage", nameOfPage);
        return errorPage;
    }
    @GetMapping("/accessDenied")
    public String accessDenied(){
        return "exception";
    }
}
