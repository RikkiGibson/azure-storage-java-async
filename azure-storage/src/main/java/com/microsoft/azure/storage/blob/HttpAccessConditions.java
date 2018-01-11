/**
 * Copyright Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoft.azure.storage.blob;

import java.util.Date;

/**
 * HTTP Access Conditions
 */
public final class HttpAccessConditions {

    private static HttpAccessConditions defaultHttpAccessConditions;
    private final Date ifModifiedSince;
    private final Date ifUnmodifiedSince;
    private final ETag ifMatch;
    private final ETag ifNoneMatch;

    /**
     * Creates a {@link HttpAccessConditions} object.
     *
     * @param ifModifiedSince
     *      A {@code java.util.Date} if modified since condition.
     * @param ifUnmodifiedSince
     *      A {@code java.util.Date} if unmodified since condition.
     * @param ifMatch
     *      An {@link ETag} if match condition.
     * @param ifNoneMatch
     *      An {@link ETag} if none match condition.
     */
    public HttpAccessConditions(Date ifModifiedSince, Date ifUnmodifiedSince, ETag ifMatch, ETag ifNoneMatch) {
        this.ifModifiedSince = (ifModifiedSince != null) ? new Date(ifModifiedSince.getTime()) : null;
        this.ifUnmodifiedSince = (ifUnmodifiedSince != null) ? new Date(ifUnmodifiedSince.getTime()) : null;

        this.ifMatch = ifMatch == null ? ETag.getDefault() : ifMatch;
        this.ifNoneMatch = ifNoneMatch == null ? ETag.getDefault() : ifNoneMatch;
    }

    public Date getIfModifiedSince() {
        return ifModifiedSince;
    }

    public Date getIfUnmodifiedSince() {
        return ifUnmodifiedSince;
    }

    public ETag getIfMatch() {
        return ifMatch;
    }

    public ETag getIfNoneMatch() {
        return ifNoneMatch;
    }


    public static HttpAccessConditions getDefault() {
        if (defaultHttpAccessConditions == null) {
            defaultHttpAccessConditions = new HttpAccessConditions(null, null,
                    ETag.getDefault(), ETag.getDefault());
        }

        return defaultHttpAccessConditions;
    }
}
