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

package androidx.compose.material3

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.tokens.ShapeKeyTokens
import androidx.compose.material3.tokens.ShapeTokens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Material surfaces can be displayed in different shapes. Shapes direct attention, identify
 * components, communicate state, and express brand.
 *
 * There are different sizes of shapes:
 * - Extra Small
 * - Small
 * - Medium
 * - Large
 * - Extra Large
 *
 * You can customize the shape system for all components in the [MaterialTheme] or you can do it
 * on a per component basis.
 *
 * Shape refers to the corners of a component. You can change the shape that a component has
 * by overriding the shape parameter for that component. For example, by default, buttons use the
 * shape style “full.” If your product requires a smaller amount of roundedness, you can override
 * the shape parameter with a different shape value like [MaterialTheme.shapes.small].
 *
 * @param extraSmall A shape style with 4 same-sized corners whose size are bigger than
 * [Shapes.None] and smaller than [Shapes.small]. By default autocomplete menu, select menu,
 * snackbars, standard menu, and text fields use this shape.
 * @param small A shape style with 4 same-sized corners whose size are bigger than
 * [Shapes.extraSmall] and smaller than [Shapes.medium]. By default chips use this shape.
 * @param medium A shape style with 4 same-sized corners whose size are bigger than [Shapes.small]
 * and smaller than [Shapes.large]. By default cards and small FABs use this shape.
 * @param large A shape style with 4 same-sized corners whose size are bigger than [Shapes.medium]
 * and smaller than [Shapes.extraLarge]. By default extended FABs, FABs, and navigation drawers use
 * this shape.
 * @param extraLarge A shape style with 4 same-sized corners whose size are bigger than
 * [Shapes.large] and smaller than [Shapes.Full]. By default large FABs use this shape.
 */
@Immutable
class Shapes(
    val extraSmall: CornerBasedShape = ShapeTokens.CornerExtraSmall,
    val small: CornerBasedShape = ShapeTokens.CornerSmall,
    val medium: CornerBasedShape = ShapeTokens.CornerMedium,
    val large: CornerBasedShape = ShapeTokens.CornerLarge,
    val extraLarge: CornerBasedShape = ShapeTokens.CornerExtraLarge,
) {
    companion object {
        /** A shape with no rounded corners (a rectangle shape). */
        val None = ShapeTokens.CornerNone

        /** A shape with fully extended rounded corners (a circular shape). */
        val Full = ShapeTokens.CornerFull
    }

    /** Returns a copy of this Shapes, optionally overriding some of the values. */
    fun copy(
        extraSmall: CornerBasedShape = this.extraSmall,
        small: CornerBasedShape = this.small,
        medium: CornerBasedShape = this.medium,
        large: CornerBasedShape = this.large,
        extraLarge: CornerBasedShape = this.extraLarge,
    ): Shapes = Shapes(
        extraSmall = extraSmall,
        small = small,
        medium = medium,
        large = large,
        extraLarge = extraLarge,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Shapes) return false
        if (extraSmall != other.extraSmall) return false
        if (small != other.small) return false
        if (medium != other.medium) return false
        if (large != other.large) return false
        if (extraLarge != other.extraLarge) return false
        return true
    }

    override fun hashCode(): Int {
        var result = extraSmall.hashCode()
        result = 31 * result + small.hashCode()
        result = 31 * result + medium.hashCode()
        result = 31 * result + large.hashCode()
        result = 31 * result + extraLarge.hashCode()
        return result
    }

    override fun toString(): String {
        return "Shapes(" +
            "extraSmall=$extraSmall, " +
            "small=$small, " +
            "medium=$medium, " +
            "large=$large, " +
            "extraLarge=$extraLarge)"
    }
}

/** Helper function for component shape tokens. Used to grab the top values of a shape parameter. */
internal fun CornerBasedShape.top(): CornerBasedShape {
    return copy(bottomStart = CornerSize(0.0.dp), bottomEnd = CornerSize(0.0.dp))
}

/** Helper function for component shape tokens. Used to grab the end values of a shape parameter. */
internal fun CornerBasedShape.end(): CornerBasedShape {
    return copy(topStart = CornerSize(0.0.dp), bottomStart = CornerSize(0.0.dp))
}

/**
 * Helper function for component shape tokens. Here is an example on how to use component color
 * tokens:
 * ``MaterialTheme.shapes.fromToken(FabPrimarySmallTokens.ContainerShape)``
 */
internal fun Shapes.fromToken(value: ShapeKeyTokens): Shape {
    return when (value) {
        ShapeKeyTokens.CornerExtraLarge -> extraLarge
        ShapeKeyTokens.CornerExtraLargeTop -> extraLarge.top()
        ShapeKeyTokens.CornerExtraSmall -> extraSmall
        ShapeKeyTokens.CornerExtraSmallTop -> extraSmall.top()
        ShapeKeyTokens.CornerFull -> Shapes.Full
        ShapeKeyTokens.CornerLarge -> large
        ShapeKeyTokens.CornerLargeEnd -> large.end()
        ShapeKeyTokens.CornerLargeTop -> large.top()
        ShapeKeyTokens.CornerMedium -> medium
        ShapeKeyTokens.CornerNone -> Shapes.None
        ShapeKeyTokens.CornerSmall -> small
    }
}

/** Converts a shape token key to the local shape provided by the theme */
@Composable
internal fun ShapeKeyTokens.toShape(): Shape {
    return MaterialTheme.shapes.fromToken(this)
}

/** CompositionLocal used to specify the default shapes for the surfaces. */
internal val LocalShapes = staticCompositionLocalOf { Shapes() }
