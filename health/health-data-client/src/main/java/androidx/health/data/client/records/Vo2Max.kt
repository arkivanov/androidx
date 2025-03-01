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

import androidx.annotation.RestrictTo
import androidx.health.data.client.metadata.Metadata
import java.time.Instant
import java.time.ZoneOffset

/** Capture user's VO2 max score and optionally the measurement method. */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Vo2Max(
    /** Maximal aerobic capacity (VO2 max) in milliliters. Required field. Valid range: 0-100. */
    public val vo2MillilitersPerMinuteKilogram: Double,
    /** VO2 max measurement method. Optional field. Allowed values: [Vo2MaxMeasurementMethod]. */
    @property:Vo2MaxMeasurementMethod public val measurementMethod: String? = null,
    override val time: Instant,
    override val zoneOffset: ZoneOffset?,
    override val metadata: Metadata = Metadata.EMPTY,
) : InstantaneousRecord {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vo2Max) return false

        if (vo2MillilitersPerMinuteKilogram != other.vo2MillilitersPerMinuteKilogram) return false
        if (measurementMethod != other.measurementMethod) return false
        if (time != other.time) return false
        if (zoneOffset != other.zoneOffset) return false
        if (metadata != other.metadata) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + vo2MillilitersPerMinuteKilogram.hashCode()
        result = 31 * result + measurementMethod.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + (zoneOffset?.hashCode() ?: 0)
        result = 31 * result + metadata.hashCode()
        return result
    }
}
