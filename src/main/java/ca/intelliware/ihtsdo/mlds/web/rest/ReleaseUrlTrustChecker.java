package ca.intelliware.ihtsdo.mlds.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class ReleaseUrlTrustChecker {

    private static final Pattern S3_ENDPOINT_PATTERN =
        Pattern.compile("^s3([.-](?:dualstack\\.)?[a-z0-9-]+)?\\.amazonaws\\.com$");

    private final List<String> internalHosts;
    private final List<String> internalBuckets;

    public ReleaseUrlTrustChecker(
        @Value("${mlds.releases.internal-hosts:snomed.org,ihtsdo.org}")
        String internalHosts,
        @Value(
            "${mlds.releases.internal-buckets:" +
                "snomed-releases,snomed-versioned-content," +
                "release-ihtsdo-prod-published," +
                "ire.published.release.ihtsdo,ihtsdo-mlds.*}"
        )
        String internalBuckets
    ) {
        this.internalHosts = parseEntries(internalHosts);
        this.internalBuckets = parseEntries(internalBuckets);
    }

    /**
     * Returns true only when the URL belongs to a trusted domain
     * or a configured trusted S3 bucket.
     */
    public boolean isInternalUrl(String downloadUrl) {

        if (downloadUrl == null || downloadUrl.isBlank()) {
            return false;
        }

        try {
            URI uri = parseUri(downloadUrl);
            String scheme = uri.getScheme();
            if (scheme == null) {
                return false;
            }

            scheme = scheme.toLowerCase(Locale.ROOT);
            if ("s3".equals(scheme)) {
                return isInternalS3Uri(uri);
            }

            if ("http".equals(scheme) || "https".equals(scheme)) {
                return isInternalHttpUri(uri);
            }

            return false;

        } catch (IllegalArgumentException _) {
            // Malformed URLs must never bypass the disclaimer.
            return false;
        }
    }

    private URI parseUri(String url) {
        String sanitizedUrl = url.trim();
        if (sanitizedUrl.contains(" ")) {
            sanitizedUrl = sanitizedUrl.replace(" ", "%20");
        }
        return URI.create(sanitizedUrl);
    }

    private boolean isInternalS3Uri(URI uri) {
        String bucket = uri.getHost();
        if (bucket == null || bucket.isBlank()) {
            bucket = uri.getAuthority();
        }
        return bucket != null && isTrustedBucket(bucket);
    }

    private boolean isInternalHttpUri(URI uri) {
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            return false;
        }

        String normalizedHost = normalizeHost(host);
        if (isTrustedHost(normalizedHost)) {
            return true;
        }

        String bucket = extractS3Bucket(normalizedHost, uri.getPath());
        return bucket != null && isTrustedBucket(bucket);
    }

    private String normalizeHost(String host) {

        String normalized = host.toLowerCase(Locale.ROOT);

        if (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    private boolean isTrustedHost(String host) {

        return internalHosts.stream()
            .anyMatch(domain ->
                host.equals(domain)
                    || host.endsWith("." + domain)
            );
    }

    private boolean isTrustedBucket(String bucket) {

        String normalizedBucket =
            bucket.toLowerCase(Locale.ROOT);

        return internalBuckets.stream()
            .anyMatch(pattern -> matchesPattern(
                normalizedBucket,
                pattern
            ));
    }

    /**
     * Supports both virtual-host-style and path-style S3 URLs:
     * - bucket.s3.amazonaws.com
     * - bucket.s3.region.amazonaws.com
     * - bucket.s3-region.amazonaws.com
     * - s3.amazonaws.com/bucket/key
     * - s3.region.amazonaws.com/bucket/key
     * - s3-region.amazonaws.com/bucket/key
     */
    private String extractS3Bucket(String host, String path) {

        if (isS3Endpoint(host)) {
            return extractPathStyleBucket(path);
        }

        return extractVirtualHostBucket(host);
    }

    private String extractVirtualHostBucket(String host) {

        int s3Index = host.lastIndexOf(".s3");

        if (s3Index > 0) {
            String suffix = host.substring(s3Index + 1);

            if (isS3Endpoint(suffix)) {
                return host.substring(0, s3Index);
            }
        }

        return null;
    }

    private String extractPathStyleBucket(String path) {

        if (path == null || path.isBlank() || "/".equals(path)) {
            return null;
        }

        String normalizedPath = path.startsWith("/")
            ? path.substring(1)
            : path;

        int slashIndex = normalizedPath.indexOf('/');

        String bucket = slashIndex >= 0
            ? normalizedPath.substring(0, slashIndex)
            : normalizedPath;

        return bucket.isBlank() ? null : bucket;
    }

    private boolean isS3Endpoint(String host) {

        if (host == null || host.isBlank()) {
            return false;
        }

        return "s3.amazonaws.com".equals(host)
            || S3_ENDPOINT_PATTERN.matcher(host).matches();
    }

    private boolean matchesPattern(
        String value,
        String configuredPattern
    ) {
        if (configuredPattern.contains("*")) {
            String regex = Pattern.quote(configuredPattern)
                .replace("*", "\\E.*\\Q");

            return value.matches("(?i)" + regex);
        }

        return value.equalsIgnoreCase(configuredPattern);
    }

    private List<String> parseEntries(String value) {

        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(","))
            .map(String::trim)
            .filter(entry -> !entry.isEmpty())
            .map(entry -> entry.startsWith(".") ? entry.substring(1) : entry)
            .map(entry -> entry.toLowerCase(Locale.ROOT))
            .toList();
    }
}
