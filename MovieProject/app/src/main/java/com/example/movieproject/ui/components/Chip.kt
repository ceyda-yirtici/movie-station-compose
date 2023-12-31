package com.example.movieproject.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.movieproject.ui.theme.MovieTheme

@Composable
fun Genre(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MovieTheme.colors.brandSecondary,
    contentColor: Color = MovieTheme.colors.textSecondary,
    border: BorderStroke? = null,
    elevation: Dp = 10.dp,
    content: @Composable () -> Unit
) {
    ListItemSurface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        elevation = 10.dp,
        border = border,
        content = content
    )
}

@Composable
fun Date(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MovieTheme.colors.textLink,
    contentColor: Color = MovieTheme.colors.textSecondary,
    border: BorderStroke? = null,
    elevation: Dp = 10.dp,
    content: @Composable () -> Unit
) {
    ListItemSurface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        elevation = elevation,
        border = border,
        content = content
    )
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun CardPreview() {
    MovieTheme {
        Genre {
            Text(text = "Demo", modifier = Modifier.padding(16.dp))
        }
    }
}