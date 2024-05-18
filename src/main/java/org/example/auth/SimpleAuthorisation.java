package org.example.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.PlainObject;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.ClientAuthentication;
import io.micronaut.security.filters.AuthenticationFetcher;
import io.reactivex.Flowable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

@Singleton
@Primary
public class SimpleAuthorisation implements AuthenticationFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleAuthorisation.class);
    private static final Pattern BEARER = Pattern.compile("^Bearer\\s(?<token>.+)");
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ALLOWED = "ALLOWED";

    private final Map<String, String> scopesToRoles = Map.ofEntries(
            entry("accounts", "ACCOUNTS_READ"),
            entry("credit-card", "CREDIT_CARD_READ"),
            entry("consents", "CONSENTS_MANAGE")
    );

    private final ApplicationContext applicationContext;

    @Inject
    public SimpleAuthorisation(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Publisher<Authentication> fetchAuthentication(Object request) {
        return Optional.ofNullable(handleHttpRequest((HttpRequest<?>) request))
                    .map(this::toFlow)
                    .orElse(Flowable.empty());
    }

    private Flowable<Authentication> toFlow(Authentication authentication) {
        return Flowable.just(authentication);
    }

    private Authentication handleHttpRequest(HttpRequest<?> request) {
        LOG.info("Looking for scopes on a {} request to {}", request.getMethod(), request.getPath());
        String token = request.getHeaders().get("Authorization");
        if(token == null) {
            LOG.info("No Authorization header");
            return null;
        }
        Matcher matcher = BEARER.matcher(token);
        if(!matcher.matches()) {
            LOG.info("Authorization header doesn't appear to be a bearer token");
            return null;
        }
        token = matcher.group("token");
        PlainObject parsed = null;
        try {
            parsed = PlainObject.parse(token);
            Map<String, Object> payload = parsed.getPayload().toJSONObject();
            String scopesValue = payload.getOrDefault("scope", "").toString();
            String[] scopes = scopesValue.split(" ");
            String clientId = String.valueOf(payload.get("client_id"));
            setRequestCallerInfo(request, scopes, clientId);
            List<String> roles = getRoles(scopes);
            return makeClientAuthenticationWithRoles(roles);
        } catch (ParseException e) {
            return null;
        }

    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    private List<String> getRoles (String[] scopes) {
        LOG.info("Scopes in token: {}", String.join(",", scopes));
        List<String> roles = Arrays.stream(scopes)
                .map(scopesToRoles::get)
                .filter(Objects::nonNull)
                .toList();
        LOG.info("Roles inferred: {}", String.join(",", roles));
        return roles;
    }

    private void setRequestCallerInfo(HttpRequest<?> request, String[] scopes, String clientId){
        String consentId = Arrays.stream(scopes)
                .filter(Objects::nonNull)
                .filter(a -> !a.isEmpty())
                .filter(a -> a.startsWith("consent:urn:bank:"))
                .findFirst().map(s -> s.replace("consent:", "")).orElse(null);
        LOG.info("Consent Id inferred: {}", consentId);
        LOG.info("Setting clientId: {}", clientId);
        request.setAttribute("clientId", clientId);
        request.setAttribute("consentId", consentId);
    }

    private ClientAuthentication makeClientAuthenticationWithRoles(List<String> roles) {
        return new ClientAuthentication(ALLOWED, Map.of("roles", roles));
    }
}

