/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.camera.viewfinder;

import static androidx.annotation.RequiresOptIn.Level.ERROR;

import static java.lang.annotation.RetentionPolicy.CLASS;

import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;

import androidx.annotation.RequiresOptIn;

import java.lang.annotation.Retention;

/**
 * Denotes that the annotated method uses the experimental methods which allow direct access to
 * camera viewfinder view modules.
 *
 * <p>The {@link CameraViewfinder} class exposes the API to request {@link Surface} for viewfinder.
 * User can select {@link SurfaceView} or {@link TextureView} by calling
 * {@link CameraViewfinder#setImplementationMode(CameraViewfinder.ImplementationMode)}
 * and apply the transform to the view the {@link Surface} by calling
 * {@link CameraViewfinder#setScaleType(CameraViewfinder.ScaleType)}.
 *
 * <p>These will be changed in future release possibly, hence add @RequiresOptIn annotation.
 */
@RequiresOptIn(level = ERROR)
@Retention(CLASS)
public @interface ExperimentalViewfinder {
}
