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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

/**
 * An enumeration of containers.
 */
@JacksonXmlRootElement(localName = "EnumerationResults")
public class ListContainersResponse {
    /**
     * The serviceEndpoint property.
     */
    @JacksonXmlProperty(localName = "ServiceEndpoint", isAttribute = true)
    private String serviceEndpoint;

    /**
     * The prefix property.
     */
    @JsonProperty(value = "Prefix", required = true)
    private String prefix;

    /**
     * The marker property.
     */
    @JsonProperty(value = "Marker")
    private String marker;

    /**
     * The maxResults property.
     */
    @JsonProperty(value = "MaxResults", required = true)
    private int maxResults;

    /**
     * The containers property.
     */
    @JacksonXmlElementWrapper(localName = "Containers")
    @JsonProperty("Container")
    private List<Container> containers;

    /**
     * The nextMarker property.
     */
    @JsonProperty(value = "NextMarker", required = true)
    private String nextMarker;

    /**
     * Get the serviceEndpoint value.
     *
     * @return the serviceEndpoint value.
     */
    public String serviceEndpoint() {
        return this.serviceEndpoint;
    }

    /**
     * Set the serviceEndpoint value.
     *
     * @param serviceEndpoint the serviceEndpoint value to set.
     * @return the ListContainersResponse object itself.
     */
    public ListContainersResponse withServiceEndpoint(String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
        return this;
    }

    /**
     * Get the prefix value.
     *
     * @return the prefix value.
     */
    public String prefix() {
        return this.prefix;
    }

    /**
     * Set the prefix value.
     *
     * @param prefix the prefix value to set.
     * @return the ListContainersResponse object itself.
     */
    public ListContainersResponse withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Get the marker value.
     *
     * @return the marker value.
     */
    public String marker() {
        return this.marker;
    }

    /**
     * Set the marker value.
     *
     * @param marker the marker value to set.
     * @return the ListContainersResponse object itself.
     */
    public ListContainersResponse withMarker(String marker) {
        this.marker = marker;
        return this;
    }

    /**
     * Get the maxResults value.
     *
     * @return the maxResults value.
     */
    public int maxResults() {
        return this.maxResults;
    }

    /**
     * Set the maxResults value.
     *
     * @param maxResults the maxResults value to set.
     * @return the ListContainersResponse object itself.
     */
    public ListContainersResponse withMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    /**
     * Get the containers value.
     *
     * @return the containers value.
     */
    public List<Container> containers() {
        return this.containers;
    }

    /**
     * Set the containers value.
     *
     * @param containers the containers value to set.
     * @return the ListContainersResponse object itself.
     */
    public ListContainersResponse withContainers(List<Container> containers) {
        this.containers = containers;
        return this;
    }

    /**
     * Get the nextMarker value.
     *
     * @return the nextMarker value.
     */
    public String nextMarker() {
        return this.nextMarker;
    }

    /**
     * Set the nextMarker value.
     *
     * @param nextMarker the nextMarker value to set.
     * @return the ListContainersResponse object itself.
     */
    public ListContainersResponse withNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
        return this;
    }
}
