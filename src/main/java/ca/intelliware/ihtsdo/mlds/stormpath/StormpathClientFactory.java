/*
 * Copyright 2013 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.intelliware.ihtsdo.mlds.stormpath;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.ApiKeyBuilder;
import com.stormpath.sdk.client.ApiKeys;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.spring.security.cache.SpringCacheManager;
/**
 * Clone by MB - there seems to have been a breaking api change from 0.9 to 1.0-beta.
 * 
 * @see com.stormpath.spring.security.client.ClientFactory
 */
public class StormpathClientFactory extends AbstractFactoryBean<Client> {

    private ClientBuilder clientBuilder;
	private ApiKeyBuilder apiKeyBuilder;

    public StormpathClientFactory() {
        super();
        this.clientBuilder = Clients.builder();
		this.apiKeyBuilder = ApiKeys.builder();
    }

    @Override
    protected Client createInstance() throws Exception {
        return clientBuilder.setApiKey(apiKeyBuilder.build()).build();
    }

    @Override
    public Class<?> getObjectType() {
        return Client.class;
    }

    public ClientBuilder getClientBuilder() {
        return clientBuilder;
    }

    public void setClientBuilder(ClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyFileLocation(String) setApiKeyFileLocation(location)}.
     * See that JavaDoc for expected syntax/format.
     *
     * @param apiKeyFileLocation the file, classpath or url location of the API Key {@code .properties} file to load when
     *                           constructing the API Key to use for communicating with the Stormpath REST API.
     * @see ClientBuilder#setApiKeyFileLocation(String)
     */
    public void setApiKeyFileLocation(String apiKeyFileLocation) {
        this.apiKeyBuilder.setFileLocation(apiKeyFileLocation);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyInputStream(java.io.InputStream) setApiKeyInputStream}.
     *
     * @param apiKeyInputStream the InputStream to use to construct a configuration Properties instance.
     * @see ClientBuilder#setApiKeyInputStream(java.io.InputStream)
     */
    public void setApiKeyInputStream(InputStream apiKeyInputStream) {
        this.apiKeyBuilder.setInputStream(apiKeyInputStream);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyReader(java.io.Reader) setApiKeyReader}.
     *
     * @param apiKeyReader the reader to use to construct a configuration Properties instance.
     * @see ClientBuilder#setApiKeyReader(java.io.Reader)
     */
    public void setApiKeyReader(Reader apiKeyReader) {
        this.apiKeyBuilder.setReader(apiKeyReader);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyProperties(java.util.Properties)}.
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @see ClientBuilder#setApiKeyProperties(java.util.Properties)
     */
    public void setApiKeyProperties(Properties properties) {
        this.apiKeyBuilder.setProperties(properties);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyIdPropertyName(String) setApiKeyIdPropertyName}.
     *
     * @param apiKeyIdPropertyName the name used to query for the API Key ID from a Properties instance.
     * @see ClientBuilder#setApiKeyIdPropertyName(String)
     */
    public void setApiKeyIdPropertyName(String apiKeyIdPropertyName) {
        this.apiKeyBuilder.setIdPropertyName(apiKeyIdPropertyName);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeySecretPropertyName(String) setApiKeySecretPropertyName}.
     *
     * @param apiKeySecretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @see ClientBuilder#setApiKeySecretPropertyName(String)
     */
    public void setApiKeySecretPropertyName(String apiKeySecretPropertyName) {
        this.apiKeyBuilder.setSecretPropertyName(apiKeySecretPropertyName);
    }

    /**
    * Uses the specified Spring {@link CacheManager} instance as the Stormpath SDK Client's CacheManager, allowing both
    * Spring and Stormpath SDK to share the same cache mechanism.
    * <p/>
    * If for some reason you don't want to share the same cache mechanism, you can explicitly set a Stormpath SDK-only
    * {@link com.stormpath.sdk.cache.CacheManager CacheManager} instance via the
    * {@link #setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager) setStormpathCacheManager} method.
    *
    * @param cacheManager the Spring CacheManager to use for the Stormpath SDK Client's caching needs.
    * @since 0.2.0
    * @see #setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager)
    */
    public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
        CacheManager stormpathCacheManager = new SpringCacheManager(cacheManager);
        this.clientBuilder.setCacheManager(stormpathCacheManager);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setCacheManager(com.stormpath.sdk.cache.CacheManager) setCacheManager}
     * using the specified Stormpath {@link com.stormpath.sdk.cache.CacheManager CacheManager} instance, but <b>note:</b>
     * This method should only be used if the Stormpath SDK should use a <em>different</em> CacheManager than what
     * Spring uses.
     * <p/>
     * If you prefer that Spring and the Stormpath SDK use the same cache mechanism to reduce complexity/configuration,
     * configure your preferred Spring {@code cacheManager} first and then use that cacheManager by calling the
     * {@link #setCacheManager(org.springframework.cache.CacheManager) setCacheManager(springCacheManager)} method
     * instead of this one.
     *
     * @param cacheManager the Storpmath SDK-specific CacheManager to use for the Stormpath SDK Client's caching needs.
     * @since 0.2.0
     * @see #setCacheManager(org.springframework.cache.CacheManager)
     */
    public void setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager cacheManager) {
        this.clientBuilder.setCacheManager(cacheManager);
    }
}