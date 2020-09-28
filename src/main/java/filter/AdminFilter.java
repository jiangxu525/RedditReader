/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Xu
 * @date Jul 3, 2020
 */
public class AdminFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        Object account = httpServletRequest.getSession().getAttribute("account");
        if (account == null) {
            httpServletRequest.getRequestDispatcher("/LoginServlet").forward(request, response);
        } else {
            chain.doFilter(request, response);
        }

    }

}
