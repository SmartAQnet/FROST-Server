package de.fraunhofer.iosb.ilt.frostserver.auth.basic;

/*
 * Copyright (C) 2018 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.DEF_AUTH_REALM_NAME;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.DEF_ROLE_DELETE;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.DEF_ROLE_GET;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.DEF_ROLE_PATCH;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.DEF_ROLE_POST;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.DEF_ROLE_PUT;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.TAG_AUTH_REALM_NAME;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.TAG_ROLE_DELETE;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.TAG_ROLE_GET;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.TAG_ROLE_PATCH;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.TAG_ROLE_POST;
import static de.fraunhofer.iosb.ilt.frostserver.auth.basic.BasicAuthProvider.TAG_ROLE_PUT;
import de.fraunhofer.iosb.ilt.frostserver.http.common.AbstractContextListener;
import de.fraunhofer.iosb.ilt.sta.settings.CoreSettings;
import static de.fraunhofer.iosb.ilt.sta.settings.CoreSettings.DEF_AUTH_ALLOW_ANON_READ;
import static de.fraunhofer.iosb.ilt.sta.settings.CoreSettings.TAG_AUTH_ALLOW_ANON_READ;
import de.fraunhofer.iosb.ilt.sta.settings.Settings;
import de.fraunhofer.iosb.ilt.sta.util.HttpMethod;
import de.fraunhofer.iosb.ilt.sta.util.StringHelper;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scf
 */
public class BasicAuthFilter implements Filter {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_REQUIRED_HEADER = "WWW-Authenticate";
    private static final String BASIC_PREFIX = "Basic ";

    /**
     * An interface for helper classes to check requests.
     */
    private static interface AuthChecker {

        /**
         * Check if the request is allowed.
         *
         * @param request The request to check.
         * @param response The response to use for sending errors back.
         * @return False if the request is not allowed.
         */
        public boolean isAllowed(HttpServletRequest request, HttpServletResponse response);
    }

    private Map<HttpMethod, AuthChecker> methodCheckers = new EnumMap<>(HttpMethod.class);

    private CoreSettings coreSettings;
    private DatabaseHandler databaseHandler;

    private String realmName;
    private String authHeaderValue;

    public static void createFilters(Object context, CoreSettings coreSettings) throws IllegalArgumentException {
        if (!(context instanceof ServletContext)) {
            throw new IllegalArgumentException("Context must be a ServletContext to add Filters.");
        }
        ServletContext servletContext = (ServletContext) context;
        Settings authSettings = coreSettings.getAuthSettings();
        String filterClass = BasicAuthFilter.class.getName();
        String filterName = "AuthFilterSta";
        FilterRegistration.Dynamic authFilterSta = servletContext.addFilter(filterName, filterClass);
        authFilterSta.setInitParameter(TAG_AUTH_ALLOW_ANON_READ, authSettings.getBoolean(TAG_AUTH_ALLOW_ANON_READ, DEF_AUTH_ALLOW_ANON_READ) ? "T" : "F");
        authFilterSta.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/v1.0", "/v1.0/*");

        filterName = "AuthFilterAdmin";
        FilterRegistration.Dynamic authFilterAdmin = servletContext.addFilter(filterName, filterClass);
        authFilterSta.setInitParameter(TAG_AUTH_ALLOW_ANON_READ, "F");
        authFilterAdmin.setInitParameter(TAG_ROLE_GET, "admin");
        authFilterAdmin.setInitParameter(TAG_ROLE_PATCH, "admin");
        authFilterAdmin.setInitParameter(TAG_ROLE_POST, "admin");
        authFilterAdmin.setInitParameter(TAG_ROLE_PUT, "admin");
        authFilterAdmin.setInitParameter(TAG_ROLE_DELETE, "admin");
        authFilterAdmin.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/DatabaseStatus");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("Turning on Basic authentication.");
        String roleGet = getInitParamWithDefault(filterConfig, TAG_ROLE_GET, DEF_ROLE_GET);
        String rolePost = getInitParamWithDefault(filterConfig, TAG_ROLE_POST, DEF_ROLE_POST);
        String rolePatch = getInitParamWithDefault(filterConfig, TAG_ROLE_PATCH, DEF_ROLE_PATCH);
        String rolePut = getInitParamWithDefault(filterConfig, TAG_ROLE_PUT, DEF_ROLE_PUT);
        String roleDelete = getInitParamWithDefault(filterConfig, TAG_ROLE_DELETE, DEF_ROLE_DELETE);
        String anonRead = getInitParamWithDefault(filterConfig, TAG_AUTH_ALLOW_ANON_READ, "F");

        ServletContext context = filterConfig.getServletContext();
        Object attribute = context.getAttribute(AbstractContextListener.TAG_CORE_SETTINGS);
        if (!(attribute instanceof CoreSettings)) {
            throw new IllegalArgumentException("Could not load core settings.");
        }
        coreSettings = (CoreSettings) attribute;
        Settings authSettings = coreSettings.getAuthSettings();

        databaseHandler = DatabaseHandler.getInstance();
        realmName = authSettings.get(TAG_AUTH_REALM_NAME, DEF_AUTH_REALM_NAME);
        authHeaderValue = "Basic realm=\"" + realmName + "\", charset=\"UTF-8\"";

        final AuthChecker allAllowed = (request, response) -> true;
        methodCheckers.put(HttpMethod.OPTIONS, allAllowed);
        methodCheckers.put(HttpMethod.HEAD, allAllowed);

        if ("T".equals(anonRead)) {
            methodCheckers.put(HttpMethod.GET, allAllowed);
        } else {
            methodCheckers.put(HttpMethod.GET, (request, response) -> requireRole(roleGet, request, response));
        }

        methodCheckers.put(HttpMethod.POST, (request, response) -> requireRole(rolePost, request, response));
        methodCheckers.put(HttpMethod.PATCH, (request, response) -> requireRole(rolePatch, request, response));
        methodCheckers.put(HttpMethod.PUT, (request, response) -> requireRole(rolePut, request, response));
        methodCheckers.put(HttpMethod.DELETE, (request, response) -> requireRole(roleDelete, request, response));
    }

    private boolean requireRole(String roleName, HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BASIC_PREFIX)) {
            LOGGER.debug("Rejecting request: no basic auth header.");
            throwAuthRequired(response);
            return false;
        }

        String userPassBase64 = authHeader.substring(BASIC_PREFIX.length());
        String userPassDecoded = new String(Base64.getDecoder().decode(userPassBase64), StringHelper.UTF8);
        if (!userPassDecoded.contains(":")) {
            LOGGER.debug("Rejecting request: no username:password in basic auth header.");
            throwAuthRequired(response);
            return false;
        }

        String[] split = userPassDecoded.split(":", 2);
        String userName = split[0];
        String userPass = split[1];
        if (!databaseHandler.userHasRole(userName, userPass, roleName)) {
            LOGGER.debug("Rejecting request: User {} does not have role {}.", userName, roleName);
            throwAuthRequired(response);
            return false;
        }
        LOGGER.debug("Accepting request: User {} has role {}.", userName, roleName);
        return true;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;

        final HttpMethod method;
        try {
            method = HttpMethod.valueOf(request.getMethod().toUpperCase());
        } catch (IllegalArgumentException exc) {
            LOGGER.debug("Rejecting request: Unknown method: {}.", request.getMethod());
            LOGGER.trace("", exc);
            throwAuthRequired(response);
            return;
        }

        AuthChecker checker = methodCheckers.get(method);
        if (checker == null) {
            LOGGER.debug("Rejecting request: No checker for method: {}.", request.getMethod());
            throwAuthRequired(response);
            return;
        }

        if (checker.isAllowed(request, response)) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    private void throwAuthRequired(HttpServletResponse response) {
        response.addHeader(AUTHORIZATION_REQUIRED_HEADER, authHeaderValue);
        try {
            response.sendError(401);
        } catch (IOException exc) {
            LOGGER.error("Exception sending back error.", exc);
        }
    }

    private static String getInitParamWithDefault(FilterConfig filterConfig, String paramName, String defValue) {
        String value = filterConfig.getInitParameter(paramName);
        if (value == null) {
            LOGGER.info("Filter setting {}, using default value: {}", paramName, defValue);
            return defValue;
        }
        LOGGER.info("Filter setting {}, set to value: {}", paramName, value);
        return value;
    }

}
