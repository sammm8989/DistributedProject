package be.kuleuven.dsgt4.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class MyResponseRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> headerMap;

    public MyResponseRequestWrapper(HttpServletRequest request) {
        super(request);
        this.headerMap = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = headerMap.get(name);

        if (headerValue != null) {
            return headerValue;
        }
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // Create a set of all the header names
        HashSet<String> set = new HashSet<>(headerMap.keySet());
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
        return Collections.enumeration(set);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (headerMap.containsKey(name)) {
            return Collections.enumeration(Collections.singletonList(headerMap.get(name)));
        }
        return ((HttpServletRequest) getRequest()).getHeaders(name);
    }
}