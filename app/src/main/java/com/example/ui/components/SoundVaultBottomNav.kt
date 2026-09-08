package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ActiveTab
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultOnSurfaceVariant
import com.example.ui.theme.VaultPrimary

data class NavItem(
    val tab: ActiveTab,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun SoundVaultBottomNav(
    activeTab: ActiveTab,
    onTabSelected: (ActiveTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(ActiveTab.VAULT, "Vault", Icons.Default.FolderSpecial, "tab_vault"),
        NavItem(ActiveTab.IMPORT, "+ Import", Icons.Default.AddCircle, "tab_import"),
        NavItem(ActiveTab.PLAYER, "Player", Icons.Default.PlayCircle, "tab_player"),
        NavItem(ActiveTab.BUNDLES, "Bundles", Icons.Default.Share, "tab_bundles"),
        NavItem(ActiveTab.SETTINGS, "Settings", Icons.Default.Tune, "tab_settings")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(VaultBackground.copy(alpha = 0.95f))
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = activeTab == item.tab
                val tintColor by animateColorAsState(
                    targetValue = if (isSelected) VaultPrimary else VaultOnSurfaceVariant.copy(alpha = 0.6f),
                    animationSpec = tween(200),
                    label = "tabTint"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelected(item.tab)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag(item.testTag)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = tintColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        color = tintColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 16.dp, height = 2.5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) VaultPrimary else androidx.compose.ui.graphics.Color.Transparent)
                    )
                }
            }
        }
    }
}
