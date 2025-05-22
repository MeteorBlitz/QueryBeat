package com.example.querybeat.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.querybeat.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || currentRoute == Screen.Country.route,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "App Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(
                        text = "QueryBeat",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(thickness = 1.dp)

                // Navigation Items
                DrawerItem(
                    label = "Countries (Home)",
                    icon = Icons.Default.Place,
                    selected = currentRoute == Screen.Country.route,
                    onClick = {
                        navController.navigate(Screen.Country.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )

                DrawerItem(
                    label = "Query Editor",
                    icon = Icons.Default.Edit,
                    selected = currentRoute == Screen.QueryEditor.route,
                    onClick = {
                        navController.navigate(Screen.QueryEditor.route)
                        scope.launch { drawerState.close() }
                    }
                )

                DrawerItem(
                    label = "Schema Explorer",
                    icon = Icons.AutoMirrored.Filled.List,
                    selected = currentRoute == Screen.SchemaExplorer.route,
                    onClick = {
                        navController.navigate(Screen.SchemaExplorer.route)
                        scope.launch { drawerState.close() }
                    }
                )

                DrawerItem(
                    label = "Saved Queries",
                    icon = Icons.Default.Star,
                    selected = currentRoute == Screen.SavedQueries.route,
                    onClick = {
                        Toast.makeText(context, "Saved Queries Clicked", Toast.LENGTH_SHORT).show()
                        scope.launch { drawerState.close() }
                    }
                )

                DrawerItem(
                    label = "History",
                    icon = Icons.Default.Info,
                    selected = currentRoute == Screen.History.route,
                    onClick = {
                        Toast.makeText(context, "History Clicked", Toast.LENGTH_SHORT).show()
                        scope.launch { drawerState.close() }
                    }
                )

                Spacer(modifier = Modifier.padding(vertical = 12.dp))
                HorizontalDivider(thickness = 1.dp)

                DrawerItem(
                    label = "Settings",
                    icon = Icons.Default.Settings,
                    selected = currentRoute == Screen.Settings.route,
                    onClick = {
                        Toast.makeText(context, "Settings Clicked", Toast.LENGTH_SHORT).show()
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        AppNavHost(
            navController = navController,
            currentRoute = currentRoute,
            drawerState = drawerState
        )
    }
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        shape = RectangleShape,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}
