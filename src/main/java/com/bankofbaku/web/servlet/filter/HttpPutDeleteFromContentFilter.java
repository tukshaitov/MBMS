package com.bankofbaku.web.servlet.filter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public class HttpPutDeleteFromContentFilter extends OncePerRequestFilter {

    private final FormHttpMessageConverter formConverter = new XmlAwareFormHttpMessageConverter();

    public void setCharset(Charset charset) {
        this.formConverter.setCharset(charset);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException,
            IOException {
        String method = request.getMethod();

        if ("DELETE".equals(method) && isFormContentType(request)) {
            HttpInputMessage inputMessage = new ServletServerHttpRequest(request) {
                @Override
                public InputStream getBody() throws IOException {
                    return request.getInputStream();
                }
            };

            long contentLength = inputMessage.getHeaders().getContentLength();

            MultiValueMap<String, String> parameters = null;
            if (contentLength > 0)
                parameters = formConverter.read(null, inputMessage);

            HttpServletRequest wrapper = new HttpPutDeleteFormContentRequestWrapper(request, parameters);
            filterChain.doFilter(wrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }

    }

    private boolean isFormContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null) {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            return (MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType));
        } else {
            return false;
        }
    }

    private static class HttpPutDeleteFormContentRequestWrapper extends HttpServletRequestWrapper {

        private MultiValueMap<String, String> formParameters;

        public HttpPutDeleteFormContentRequestWrapper(HttpServletRequest request, MultiValueMap<String, String> parameters) {
            super(request);
            this.formParameters = (parameters != null) ? parameters : new LinkedMultiValueMap<String, String>();
        }

        @Override
        public String getParameter(String name) {
            String queryStringValue = super.getParameter(name);
            String formValue = this.formParameters.getFirst(name);
            return (queryStringValue != null) ? queryStringValue : formValue;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> result = new LinkedHashMap<String, String[]>();
            Enumeration<String> names = this.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                result.put(name, this.getParameterValues(name));
            }
            return result;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            Set<String> names = new LinkedHashSet<String>();
            names.addAll(Collections.list(super.getParameterNames()));
            names.addAll(this.formParameters.keySet());
            return Collections.enumeration(names);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] queryStringValues = super.getParameterValues(name);
            List<String> formValues = this.formParameters.get(name);
            if (formValues == null) {
                return queryStringValues;
            } else if (queryStringValues == null) {
                return formValues.toArray(new String[formValues.size()]);
            } else {
                List<String> result = new ArrayList<String>();
                result.addAll(Arrays.asList(queryStringValues));
                result.addAll(formValues);
                return result.toArray(new String[result.size()]);
            }
        }
    }
}
