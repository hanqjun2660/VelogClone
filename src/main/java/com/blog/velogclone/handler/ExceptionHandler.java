package com.blog.velogclone.handler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
public class ExceptionHandler implements ErrorController {

    private final String ERROR_404_PAGE = "/common/error/error404";

    private final String ERROR_400_PAGE = "/common/error/error400";

    private final String ERROR_500_PAGE = "/common/error/error500";

    private final String ERROR_REMAIN = "/common/error/error";

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        // 에러코드
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // HttpStatus
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.parseInt(status.toString()));

        if(status != null) {

            int statusCode = Integer.parseInt(status.toString());

            // 404
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("code", status.toString());
                model.addAttribute("msg", httpStatus.getReasonPhrase());
                model.addAttribute("timestamp", new Date());
                return ERROR_404_PAGE;
            }

            // 400
            if(statusCode == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("code", status.toString());
                model.addAttribute("msg", httpStatus.getReasonPhrase());
                model.addAttribute("timestamp", new Date());
                return ERROR_400_PAGE;
            }

            // 500
            if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return ERROR_500_PAGE;
            }
        }

        return ERROR_REMAIN;
    }

}
