package com.solmore.simplenote.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.solmore.simplenote.R

//import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericAppBar(
    title: String,
    navController: NavController,
    onIconClick : (() -> Unit)?,
    icon: @Composable (() -> Unit)?,
    iconState: MutableState<Boolean>
){
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    if(title.compareTo(stringResource(R.string.simple_notes)) == 0){
        TopAppBar(
            title = {Text(title)},
            /*colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),*/
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier,
            actions = {
                IconButton(
                    onClick = {onIconClick?.invoke()},
                    content = {
                        if (iconState.value){
                            icon?.invoke()
                        }
                    }
                )
            },
            scrollBehavior = scrollBehavior
        )
    }else{
        TopAppBar(
            title = {Text(title)},
            /*colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),*/
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
            navigationIcon = { IconButton(onClick = { navController.popBackStack()}) {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Main"
                )
            }},
            modifier = Modifier,
            actions = {
                IconButton(
                    onClick = {onIconClick?.invoke()},
                    content = {
                        if (iconState.value){
                            icon?.invoke()
                        }
                    }
                )
            },
            scrollBehavior = scrollBehavior
        )
    }

}