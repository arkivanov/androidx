/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.health.data.client.records

import androidx.health.data.client.aggregate.DoubleAggregateMetric
import androidx.health.data.client.metadata.Metadata
import java.time.Instant
import java.time.ZoneOffset

/** Captures the user's height in meters. */
public class Height(
    /** Height in meters. Required field. Valid range: 0-3. */
    public val heightMeters: Double,
    override val time: Instant,
    override val zoneOffset: ZoneOffset?,
    override val metadata: Metadata = Metadata.EMPTY,
) : InstantaneousRecord {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Height) return false

        if (heightMeters != other.heightMeters) return false
        if (time != other.time) return false
        if (zoneOffset != other.zoneOffset) return false
        if (metadata != other.metadata) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + heightMeters.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + (zoneOffset?.hashCode() ?: 0)
        result = 31 * result + metadata.hashCode()
        return result
    }

    internal companion object {
        /** Metric identifier to retrieve average height from [AggregateDataRow]. */
        @JvmStatic
        internal val HEIGHT_AVG: DoubleAggregateMetric =
            DoubleAggregateMetric("Height", "avg", "height")

        /** Metric identifier to retrieve minimum height from [AggregateDataRow]. */
        @JvmStatic
        internal val HEIGHT_MIN: DoubleAggregateMetric =
            DoubleAggregateMetric("Height", "min", "height")

        /** Metric identifier to retrieve maximum height from [AggregateDataRow]. */
        @JvmStatic
        internal val HEIGHT_MAX: DoubleAggregateMetric =
            DoubleAggregateMetric("Height", "max", "height")
    }
}
