/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 * Changes may cause incorrect behavior and will be lost if the code is
 * regenerated.
 */

package com.microsoft.azure.storage.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

/**
 * Storage Service Properties.
 */
@JacksonXmlRootElement(localName = "StorageServiceProperties")
public final class StorageServiceProperties {
    /**
     * Azure Analytics Logging settings.
     */
    @JsonProperty(value = "Logging")
    private Logging logging;

    /**
     * A summary of request statistics grouped by API in hourly aggregates for
     * blobs.
     */
    @JsonProperty(value = "HourMetrics")
    private Metrics hourMetrics;

    /**
     * a summary of request statistics grouped by API in minute aggregates for
     * blobs.
     */
    @JsonProperty(value = "MinuteMetrics")
    private Metrics minuteMetrics;

    private static final class CorsWrapper {
        @JacksonXmlProperty(localName = "CorsRule")
        private final List<CorsRule> items;

        @JsonCreator
        private CorsWrapper(@JacksonXmlProperty(localName = "CorsRule") List<CorsRule> items) {
            this.items = items;
        }
    }

    /**
     * The set of CORS rules.
     */
    @JsonProperty(value = "Cors")
    private CorsWrapper cors;

    /**
     * The default version to use for requests to the Blob service if an
     * incoming request's version is not specified. Possible values include
     * version 2008-10-27 and all more recent versions.
     */
    @JsonProperty(value = "DefaultServiceVersion")
    private String defaultServiceVersion;

    /**
     * The Delete Retention Policy for the service.
     */
    @JsonProperty(value = "DeleteRetentionPolicy")
    private RetentionPolicy deleteRetentionPolicy;

    /**
     * Get the logging value.
     *
     * @return the logging value.
     */
    public Logging logging() {
        return this.logging;
    }

    /**
     * Set the logging value.
     *
     * @param logging the logging value to set.
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withLogging(Logging logging) {
        this.logging = logging;
        return this;
    }

    /**
     * Get the hourMetrics value.
     *
     * @return the hourMetrics value.
     */
    public Metrics hourMetrics() {
        return this.hourMetrics;
    }

    /**
     * Set the hourMetrics value.
     *
     * @param hourMetrics the hourMetrics value to set.
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withHourMetrics(Metrics hourMetrics) {
        this.hourMetrics = hourMetrics;
        return this;
    }

    /**
     * Get the minuteMetrics value.
     *
     * @return the minuteMetrics value.
     */
    public Metrics minuteMetrics() {
        return this.minuteMetrics;
    }

    /**
     * Set the minuteMetrics value.
     *
     * @param minuteMetrics the minuteMetrics value to set.
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withMinuteMetrics(Metrics minuteMetrics) {
        this.minuteMetrics = minuteMetrics;
        return this;
    }

    /**
     * Get the cors value.
     *
     * @return the cors value.
     */
    public List<CorsRule> cors() {
        return this.cors.items;
    }

    /**
     * Set the cors value.
     *
     * @param cors the cors value to set.
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withCors(List<CorsRule> cors) {
        this.cors = new CorsWrapper(cors);
        return this;
    }

    /**
     * Get the defaultServiceVersion value.
     *
     * @return the defaultServiceVersion value.
     */
    public String defaultServiceVersion() {
        return this.defaultServiceVersion;
    }

    /**
     * Set the defaultServiceVersion value.
     *
     * @param defaultServiceVersion the defaultServiceVersion value to set.
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withDefaultServiceVersion(String defaultServiceVersion) {
        this.defaultServiceVersion = defaultServiceVersion;
        return this;
    }

    /**
     * Get the deleteRetentionPolicy value.
     *
     * @return the deleteRetentionPolicy value.
     */
    public RetentionPolicy deleteRetentionPolicy() {
        return this.deleteRetentionPolicy;
    }

    /**
     * Set the deleteRetentionPolicy value.
     *
     * @param deleteRetentionPolicy the deleteRetentionPolicy value to set.
     * @return the StorageServiceProperties object itself.
     */
    public StorageServiceProperties withDeleteRetentionPolicy(RetentionPolicy deleteRetentionPolicy) {
        this.deleteRetentionPolicy = deleteRetentionPolicy;
        return this;
    }
}
