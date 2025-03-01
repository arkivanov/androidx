/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.build

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

/**
 * Setting this property enables multiplatform builds of Compose
 */
const val COMPOSE_MPP_ENABLED = "androidx.compose.multiplatformEnabled"

/**
 * Setting this property enables JS compiler tests of Compose
 */
const val COMPOSE_JS_COMPILER_TESTS_ENABLED = "jetbrains.compose.jsCompilerTestsEnabled"  // TODO don't merge this to aosp
class Multiplatform {
    companion object {
        fun Project.isMultiplatformEnabled(): Boolean {
            return properties.get(COMPOSE_MPP_ENABLED)?.toString()?.toBoolean() ?: false
        }

        fun Project.isJsCompilerTestsEnabled(): Boolean {
            return properties.get(COMPOSE_JS_COMPILER_TESTS_ENABLED)?.toString()?.toBoolean() ?: false
        }

        fun setEnabledForProject(project: Project, enabled: Boolean) {
            project.extra.set(COMPOSE_MPP_ENABLED, enabled)
        }
    }
}
