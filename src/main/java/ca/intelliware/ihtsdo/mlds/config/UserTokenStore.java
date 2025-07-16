package ca.intelliware.ihtsdo.mlds.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserTokenStore {
    private final Map<String, String> userCookieMap = new ConcurrentHashMap<>();

    public void store(String username, String token) {
        userCookieMap.put(username, token);
    }

    public String get(String username) {
        return userCookieMap.get(username);
    }

    public void remove(String username) {
        userCookieMap.remove(username);
    }
}
