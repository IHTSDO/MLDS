package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorityConverter;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CentralAuthUserInfo;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.HttpAuthAdaptor;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.RemoteUserDetails;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Hidden
public class SwaggerAuthenticationController {

    private static final String SUCCESS_HTML = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>MLDS Authentication</title>
        </head>
        <body>
            <p>Authentication successful.</p>
            <p>Returning to Swagger...</p>

            <script>
                if (window.opener) {
                    window.opener.postMessage(
                        {
                            type: 'MLDS_AUTHENTICATED'
                        },
                        window.location.origin
                    );
                }
                setTimeout(function() {
                    window.close();
                }, 500);
            </script>
        </body>
        </html>
        """;

    private final Logger log = LoggerFactory.getLogger(SwaggerAuthenticationController.class);

    private final HttpAuthAdaptor httpAuthAdaptor;
    private final AuditEventRepository auditEventRepository;

    public SwaggerAuthenticationController(
        HttpAuthAdaptor httpAuthAdaptor,
        @Autowired(required = false) AuditEventRepository auditEventRepository) {
        this.httpAuthAdaptor = httpAuthAdaptor;
        this.auditEventRepository = auditEventRepository;
    }

    /**
     * Entry point used by the Swagger Login button.
     * Serves the Swagger Login page that authenticates directly against /app/authentication.
     */
    @GetMapping(value = "/swagger-login", produces = "text/html;charset=UTF-8")
    public String login() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>MLDS Swagger Login</title>
                <style>
                    body {
                        margin: 0;
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        background: #f4f6f8;
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
                    }
                    .login-container {
                        width: 380px;
                        padding: 36px;
                        background: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                    }
                    .login-container h1 {
                        margin: 0 0 8px 0;
                        font-size: 22px;
                        text-align: center;
                        color: #1a202c;
                    }
                    .login-container p {
                        text-align: center;
                        color: #64748b;
                        margin: 0 0 24px 0;
                        font-size: 14px;
                    }
                    .form-group {
                        margin-bottom: 18px;
                    }
                    .form-group label {
                        display: block;
                        margin-bottom: 6px;
                        font-weight: 600;
                        font-size: 13px;
                        color: #334155;
                    }
                    .form-group input {
                        box-sizing: border-box;
                        width: 100%;
                        padding: 10px 12px;
                        border: 1px solid #cbd5e1;
                        border-radius: 6px;
                        font-size: 14px;
                        outline: none;
                        transition: border-color 0.2s;
                    }
                    .form-group input:focus {
                        border-color: #2563eb;
                    }
                    .remember-me {
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        margin-bottom: 20px;
                        font-size: 13px;
                        color: #475569;
                    }
                    .login-button {
                        width: 100%;
                        padding: 12px;
                        border: 0;
                        border-radius: 6px;
                        background: #16a34a;
                        color: white;
                        font-size: 15px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: background 0.2s;
                    }
                    .login-button:hover {
                        background: #15803d;
                    }
                    .login-button:disabled {
                        opacity: 0.6;
                        cursor: not-allowed;
                    }
                    .error {
                        display: none;
                        margin-bottom: 16px;
                        padding: 10px;
                        border-radius: 6px;
                        background: #fee2e2;
                        color: #991b1b;
                        font-size: 13px;
                        text-align: center;
                    }
                </style>
            </head>
            <body>
            <div class="login-container">
                <h1>🔐 MLDS Swagger Login</h1>
                <p>Sign in using your MLDS / IMS account</p>
                <div id="errorMessage" class="error">
                    Authentication failed. Please check your username and password.
                </div>
                <form id="swaggerLoginForm">
                    <div class="form-group">
                        <label for="username">Username</label>
                        <input id="username" name="j_username" type="text" autocomplete="username" required autofocus>
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input id="password" name="j_password" type="password" autocomplete="current-password" required>
                    </div>
                    <div class="remember-me">
                        <input id="rememberMe" name="remember-me" type="checkbox">
                        <label for="rememberMe">Remember me</label>
                    </div>
                    <button id="loginButton" class="login-button" type="submit">Sign In</button>
                </form>
            </div>
            <script>
                const form = document.getElementById('swaggerLoginForm');
                const loginButton = document.getElementById('loginButton');
                const errorMessage = document.getElementById('errorMessage');

                form.addEventListener('submit', async function (event) {
                    event.preventDefault();
                    errorMessage.style.display = 'none';
                    loginButton.disabled = true;
                    loginButton.textContent = 'Signing in...';

                    const formData = new URLSearchParams();
                    formData.set('j_username', document.getElementById('username').value);
                    formData.set('j_password', document.getElementById('password').value);
                    formData.set('remember-me', document.getElementById('rememberMe').checked);

                    try {
                        const response = await fetch('/app/authentication', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            credentials: 'include',
                            body: formData.toString()
                        });

                        if (response.ok) {
                            if (window.opener) {
                                try {
                                    window.opener.postMessage({ type: 'MLDS_AUTHENTICATED' }, window.location.origin);
                                } catch (e) {}
                                setTimeout(function() { window.close(); }, 300);
                            } else {
                                window.location.replace('/swagger-ui/index.html');
                            }
                            return;
                        }
                        errorMessage.style.display = 'block';
                    } catch (error) {
                        console.error('Swagger authentication failed:', error);
                        errorMessage.style.display = 'block';
                    } finally {
                        loginButton.disabled = false;
                        loginButton.textContent = 'Sign In';
                    }
                });
            </script>
            </body>
            </html>
            """;
    }

    /**
     * Callback after IMS authentication.
     */
    @GetMapping("/swagger-authenticated")
    public ResponseEntity<String> authenticated(
        HttpServletRequest request,
        HttpServletResponse response) {

        // If session is already established and authenticated, return success
        if (isAlreadyAuthenticated()) {
            return ResponseEntity.ok(SUCCESS_HTML);
        }

        // Look for the IMS authentication cookie
        String cookieName = httpAuthAdaptor.getAuthenticatedCookieName();
        String cookieValue = extractCookieValue(request, cookieName);

        if (cookieValue == null || cookieValue.trim().isEmpty()) {
            log.warn("Swagger login callback: IMS cookie not found");
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("MLDS session was not established. IMS cookie was not found.");
        }

        try {
            CentralAuthUserInfo remoteUserInfo =
                httpAuthAdaptor.getUserAccountInfoByCookie(cookieValue);

            if (remoteUserInfo == null || remoteUserInfo.getLogin() == null) {
                log.warn("Swagger login callback: Invalid IMS cookie");
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("MLDS session was not established. Invalid IMS cookie.");
            }

            List<GrantedAuthority> authorities =
                AuthorityConverter.buildAuthoritiesList(remoteUserInfo.getRoles());

            if (authorities.isEmpty()) {
                log.warn("Swagger login callback: User '{}' authenticated with no roles", remoteUserInfo.getLogin());
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User authenticated but has no permissions assigned.");
            }

            establishSecurityContext(remoteUserInfo, authorities, request, response);
            recordAuditSuccess(remoteUserInfo.getLogin(), request);

            log.info("Swagger authentication successful for user: {}", remoteUserInfo.getLogin());

            return ResponseEntity.ok(SUCCESS_HTML);

        } catch (IOException e) {
            log.error("Swagger login callback error verifying IMS cookie: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to verify authentication with IMS: " + e.getMessage());
        }
    }

    private boolean isAlreadyAuthenticated() {
        SecurityContext existingContext = SecurityContextHolder.getContext();
        return existingContext != null
            && existingContext.getAuthentication() != null
            && existingContext.getAuthentication().isAuthenticated();
    }

    private String extractCookieValue(HttpServletRequest request, String cookieName) {
        if (cookieName == null || request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void establishSecurityContext(
        CentralAuthUserInfo remoteUserInfo,
        List<GrantedAuthority> authorities,
        HttpServletRequest request,
        HttpServletResponse response) {
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));

        RemoteUserDetails userDetails =
            new RemoteUserDetails(remoteUserInfo, authorities);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                authorities
            );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        request.getSession(true);

        HttpSessionSecurityContextRepository secRepo =
            new HttpSessionSecurityContextRepository();
        secRepo.saveContext(context, request, response);
    }

    private void recordAuditSuccess(String login, HttpServletRequest request) {
        if (auditEventRepository != null) {
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("remoteAddress", request.getRemoteAddr());
            auditEventRepository.add(
                new AuditEvent(
                    login,
                    "SWAGGER_AUTHENTICATION_SUCCESS",
                    auditData
                )
            );
        }
    }

    /**
     * Serves the dynamic JavaScript helper for Swagger UI.
     */
    @GetMapping(value = "/swagger-custom.js", produces = "application/javascript;charset=UTF-8")
    public String customJs() {
        return """
            (function () {
                if (window.__MLDS_SWAGGER_INITIALIZED__) {
                    return;
                }
                window.__MLDS_SWAGGER_INITIALIZED__ = true;

                console.log("[MLDS Swagger Auth] Initializing dynamic Auth & Logout Manager...");

                window.MLDS_SWAGGER_AUTHENTICATED = false;
                window.MLDS_CURRENT_USER = null;

                // Inject custom styling for clean modal and buttons
                if (!document.getElementById("mlds-swagger-custom-styles")) {
                    const style = document.createElement("style");
                    style.id = "mlds-swagger-custom-styles";
                    style.textContent = `
                        /* Hide all default Swagger auth form elements, headers, and wrappers */
                        .auth-container > *:not(#mlds-custom-auth-card):not(.mlds-custom-auth-card),
                        .auth-container .auth-wrapper,
                        .auth-container .wrapper,
                        .auth-container .renderedMarkdown,
                        .auth-container p:not(.mlds-custom-auth-card p),
                        .auth-container label,
                        .auth-container h4,
                        .auth-container h3:not(.mlds-custom-auth-card h3),
                        .auth-container h5,
                        .auth-container h6,
                        .auth-container form,
                        .auth-container .scope-def,
                        .auth-container .btn.modal-btn.auth {
                            display: none !important;
                        }

                        /* Dynamic wording toggle based on auth state */
                        .mlds-logged-out-msg {
                            display: block;
                            margin: 10px 0;
                            padding: 10px 14px;
                            background: #eff6ff;
                            border-left: 4px solid #3b82f6;
                            border-radius: 4px;
                            color: #1e3a8a;
                        }
                        .mlds-logged-in-msg {
                            display: none;
                            margin: 10px 0;
                            padding: 10px 14px;
                            background: #f0fdf4;
                            border-left: 4px solid #22c55e;
                            border-radius: 4px;
                            color: #14532d;
                        }
                        body.mlds-authenticated .mlds-logged-out-msg {
                            display: none !important;
                        }
                        body.mlds-authenticated .mlds-logged-in-msg {
                            display: block !important;
                        }
                        body:not(.mlds-authenticated) .mlds-logged-out-msg {
                            display: block !important;
                        }
                        body:not(.mlds-authenticated) .mlds-logged-in-msg {
                            display: none !important;
                        }
                        .mlds-desc-login-link {
                            color: #2563eb !important;
                            text-decoration: underline !important;
                            cursor: pointer !important;
                        }
                        .mlds-desc-logout-link {
                            color: #dc2626 !important;
                            text-decoration: underline !important;
                            cursor: pointer !important;
                        }

                        /* Custom MLDS Auth Card in Modal */
                        .mlds-custom-auth-card {
                            padding: 24px;
                            background: #ffffff;
                            border-radius: 8px;
                            text-align: center;
                            box-shadow: 0 1px 4px rgba(0,0,0,0.08);
                            margin-bottom: 15px;
                            border: 1px solid #e2e8f0;
                        }
                        .mlds-custom-auth-card h3 {
                            margin: 0 0 10px 0;
                            font-size: 18px;
                            color: #1e293b;
                        }
                        .mlds-custom-auth-card p {
                            display: block !important;
                            margin: 0 0 18px 0 !important;
                            color: #64748b !important;
                            font-size: 14px !important;
                        }
                        .mlds-btn-login {
                            background: #16a34a !important;
                            color: white !important;
                            font-weight: 600 !important;
                            padding: 10px 24px !important;
                            border-radius: 6px !important;
                            border: none !important;
                            cursor: pointer !important;
                            font-size: 14px !important;
                            display: inline-flex !important;
                            align-items: center !important;
                            gap: 8px !important;
                            transition: background 0.2s !important;
                        }
                        .mlds-btn-login:hover {
                            background: #15803d !important;
                        }
                        .mlds-btn-logout {
                            background: #dc2626 !important;
                            color: white !important;
                            font-weight: 600 !important;
                            padding: 10px 24px !important;
                            border-radius: 6px !important;
                            border: none !important;
                            cursor: pointer !important;
                            font-size: 14px !important;
                            display: inline-flex !important;
                            align-items: center !important;
                            gap: 8px !important;
                            transition: background 0.2s !important;
                        }
                        .mlds-btn-logout:hover {
                            background: #b91c1c !important;
                        }
                        .swagger-ui .btn.authorize.mlds-logout-btn {
                            background: #ef4444 !important;
                            color: white !important;
                            border-color: #dc2626 !important;
                        }
                        .swagger-ui .btn.authorize.mlds-logout-btn svg {
                            fill: white !important;
                        }
                    `;
                    document.head.appendChild(style);
                }

                function openImsLoginPopup() {
                    const width = 600;
                    const height = 700;
                    const left = (window.screen.width - width) / 2;
                    const top = (window.screen.height - height) / 2;

                    try {
                        const popup = window.open(
                            "/swagger-login",
                            "mlds-ims-login",
                            [
                                "width=" + width,
                                "height=" + height,
                                "left=" + left,
                                "top=" + top,
                                "resizable=yes",
                                "scrollbars=yes"
                            ].join(",")
                        );
                        if (popup) {
                            popup.focus();
                        } else {
                            window.location.href = "/swagger-login";
                        }
                    } catch (e) {
                        console.warn("[MLDS Swagger Auth] Popup blocked or failed, navigating directly:", e);
                        window.location.href = "/swagger-login";
                    }
                }

                async function checkAuthStatus() {
                    try {
                        const response = await fetch("/api/authenticate", {
                            method: "GET",
                            credentials: "include"
                        });

                        if (response.ok) {
                            const username = await response.text();
                            if (username && username.trim().length > 0 && username.trim() !== "anonymousUser") {
                                window.MLDS_SWAGGER_AUTHENTICATED = true;
                                window.MLDS_CURRENT_USER = username.replace(/"/g, "").trim();

                                if (window.ui && typeof window.ui.preauthorizeApiKey === "function") {
                                    try {
                                        window.ui.preauthorizeApiKey("mldsSession", "authenticated");
                                    } catch (e) {}
                                }
                                updateUiState();
                                return true;
                            }
                        }
                    } catch (e) {
                        console.warn("[MLDS Swagger Auth] Error checking auth status:", e);
                    }

                    window.MLDS_SWAGGER_AUTHENTICATED = false;
                    window.MLDS_CURRENT_USER = null;
                    updateUiState();
                    return false;
                }

                async function performLogout() {
                    try {
                        await fetch("/app/logout", {
                            method: "POST",
                            credentials: "include"
                        });
                    } catch (e) {
                        console.warn("[MLDS Swagger Auth] Error during logout:", e);
                    }

                    window.MLDS_SWAGGER_AUTHENTICATED = false;
                    window.MLDS_CURRENT_USER = null;

                    if (window.ui && window.ui.authActions && typeof window.ui.authActions.logout === "function") {
                        try {
                            window.ui.authActions.logout(["mldsSession"]);
                        } catch (e) {}
                    }

                    // Close any open modal
                    const closeBtn = document.querySelector(".modal-ux .btn-done, .modal-ux .close-modal");
                    if (closeBtn) {
                        closeBtn.click();
                    }

                    updateUiState();
                    console.log("[MLDS Swagger Auth] Logged out successfully.");
                }

                // Listen for authentication message from the login popup
                window.addEventListener("message", async function (event) {
                    if (event.origin !== window.location.origin) {
                        return;
                    }

                    if (event.data && event.data.type === "MLDS_AUTHENTICATED") {
                        console.log("[MLDS Swagger Auth] Received MLDS_AUTHENTICATED signal.");
                        await checkAuthStatus();

                        // Close modal if open
                        const closeBtn = document.querySelector(".modal-ux .btn-done, .modal-ux .close-modal");
                        if (closeBtn) {
                            closeBtn.click();
                        }
                    }
                });

                function updateDescriptionWording() {
                    // 1. Toggle by class if preserved
                    document.querySelectorAll(".mlds-logged-out-msg").forEach(el => {
                        const targetDisplay = window.MLDS_SWAGGER_AUTHENTICATED ? "none" : "block";
                        if (el.style.display !== targetDisplay) {
                            el.style.setProperty("display", targetDisplay, "important");
                        }
                    });
                    document.querySelectorAll(".mlds-logged-in-msg").forEach(el => {
                        const targetDisplay = window.MLDS_SWAGGER_AUTHENTICATED ? "block" : "none";
                        if (el.style.display !== targetDisplay) {
                            el.style.setProperty("display", targetDisplay, "important");
                        }
                        const userSpan = el.querySelector(".mlds-username-placeholder");
                        if (userSpan && window.MLDS_CURRENT_USER && userSpan.textContent !== window.MLDS_CURRENT_USER) {
                            userSpan.textContent = window.MLDS_CURRENT_USER;
                        }
                    });

                    // 2. Text-matching fallback across Swagger UI info / description rendered blocks
                    const containers = document.querySelectorAll(".swagger-ui .info, .swagger-ui .description, .swagger-ui .renderedMarkdown");
                    containers.forEach(container => {
                        const items = container.querySelectorAll("p, div, li, span");
                        items.forEach(el => {
                            const text = el.textContent || "";
                            // Login wording check
                            if (text.includes("Login via IMS") || text.includes("start an authenticated session")) {
                                if (window.MLDS_SWAGGER_AUTHENTICATED) {
                                    if (el.style.display !== "none") {
                                        el.style.setProperty("display", "none", "important");
                                    }
                                } else {
                                    if (el.style.display === "none") {
                                        el.style.removeProperty("display");
                                    }
                                }
                            }
                            // Logout / signed-in wording check
                            if (text.includes("You are currently signed in") || (text.includes("Logout") && text.includes("session"))) {
                                if (window.MLDS_SWAGGER_AUTHENTICATED) {
                                    if (el.style.display === "none") {
                                        el.style.removeProperty("display");
                                    }
                                    if (window.MLDS_CURRENT_USER && text.includes("User")) {
                                        el.innerHTML = el.innerHTML.replace(/\bUser\b/g, window.MLDS_CURRENT_USER);
                                    }
                                } else {
                                    if (el.style.display !== "none") {
                                        el.style.setProperty("display", "none", "important");
                                    }
                                }
                            }
                        });
                    });
                }

                let isUpdating = false;
                function updateUiState() {
                    if (isUpdating) return;
                    isUpdating = true;

                    try {
                        if (window.MLDS_SWAGGER_AUTHENTICATED) {
                            if (!document.body.classList.contains("mlds-authenticated")) {
                                document.body.classList.add("mlds-authenticated");
                            }
                        } else {
                            if (document.body.classList.contains("mlds-authenticated")) {
                                document.body.classList.remove("mlds-authenticated");
                            }
                        }

                        // 1. Update main Authorize button in Swagger UI header
                        const authBtns = document.querySelectorAll(".swagger-ui .btn.authorize");
                        authBtns.forEach(btn => {
                            const span = btn.querySelector("span");
                            if (window.MLDS_SWAGGER_AUTHENTICATED) {
                                if (!btn.classList.contains("mlds-logout-btn")) {
                                    btn.classList.add("mlds-logout-btn");
                                }
                                const expectedText = "Logout (" + (window.MLDS_CURRENT_USER || "Session") + ")";
                                if (span && span.textContent !== expectedText) {
                                    span.textContent = expectedText;
                                }
                            } else {
                                if (btn.classList.contains("mlds-logout-btn")) {
                                    btn.classList.remove("mlds-logout-btn");
                                }
                                if (span && span.textContent !== "Authorize") {
                                    span.textContent = "Authorize";
                                }
                            }
                        });

                        // 2. Update description wording elements
                        updateDescriptionWording();

                        // 3. Refresh the modal content if currently open
                        renderModalContent();
                    } finally {
                        isUpdating = false;
                    }
                }

                function renderModalContent() {
                    const authContainer = document.querySelector(".auth-container");
                    if (!authContainer) return;

                    let card = document.getElementById("mlds-custom-auth-card");
                    if (!card) {
                        card = document.createElement("div");
                        card.id = "mlds-custom-auth-card";
                        card.className = "mlds-custom-auth-card";
                        authContainer.insertBefore(card, authContainer.firstChild);
                    }

                    const desiredState = window.MLDS_SWAGGER_AUTHENTICATED ? "auth" : "unauth";
                    if (card.getAttribute("data-state") !== desiredState) {
                        card.setAttribute("data-state", desiredState);
                        if (window.MLDS_SWAGGER_AUTHENTICATED) {
                            card.innerHTML = `
                                <h3>✅ Session Authenticated</h3>
                                <p>You are logged in as <strong>` + (window.MLDS_CURRENT_USER || "MLDS User") + `</strong>. Your requests will automatically include your session credentials.</p>
                                <button type="button" class="mlds-btn-logout">
                                    🚪 Logout
                                </button>
                            `;
                        } else {
                            card.innerHTML = `
                                <h3>🔐 MLDS Session Authentication</h3>
                                <p>Sign in using your MLDS / IMS credentials to test secured API endpoints.</p>
                                <button type="button" class="mlds-btn-login">
                                    🔑 Login via IMS
                                </button>
                            `;
                        }
                    }
                }

                // Intercept clicks on links and buttons
                document.addEventListener("click", function (e) {
                    // 1. Logout actions (button in modal or description)
                    const logoutBtn = e.target.closest(".swagger-ui .btn.authorize.mlds-logout-btn, .mlds-btn-logout, .mlds-desc-logout-link");
                    if (logoutBtn) {
                        e.preventDefault();
                        e.stopPropagation();
                        if (confirm("Do you want to log out of your MLDS session?")) {
                            performLogout();
                        }
                        return;
                    }

                    // 2. Login actions (button in modal or description)
                    const loginBtn = e.target.closest(".mlds-btn-login, #mlds-modal-action-btn, .mlds-desc-login-link");
                    if (loginBtn) {
                        e.preventDefault();
                        e.stopPropagation();
                        openImsLoginPopup();
                        return;
                    }

                    // 3. Generic anchor links
                    const link = e.target.closest("a");
                    if (link) {
                        const href = link.getAttribute("href") || "";
                        const text = link.textContent || "";
                        if (href.includes("swagger-login") || text.includes("Login via IMS")) {
                            e.preventDefault();
                            e.stopPropagation();
                            openImsLoginPopup();
                            return;
                        }
                        if (href.includes("logout") || (text.includes("Logout") && link.closest(".info, .description, .renderedMarkdown"))) {
                            e.preventDefault();
                            e.stopPropagation();
                            if (confirm("Do you want to log out of your MLDS session?")) {
                                performLogout();
                            }
                            return;
                        }
                    }
                }, true);

                // Debounced MutationObserver to prevent infinite loops
                let updateTimeout = null;
                const observer = new MutationObserver(function () {
                    if (isUpdating) return;
                    if (updateTimeout) clearTimeout(updateTimeout);
                    updateTimeout = setTimeout(function () {
                        updateUiState();
                    }, 50);
                });

                function init() {
                    checkAuthStatus();
                    const target = document.getElementById("swagger-ui") || document.body;
                    observer.observe(target, { childList: true, subtree: true });
                }

                if (document.readyState === "loading") {
                    document.addEventListener("DOMContentLoaded", init);
                } else {
                    init();
                }
            })();
            """;
    }
}
