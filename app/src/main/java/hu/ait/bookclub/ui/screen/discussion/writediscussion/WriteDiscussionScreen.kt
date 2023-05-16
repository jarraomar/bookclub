package hu.ait.bookclub.ui.screen.discussion.writediscussion

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WriteDiscussionScreen(
    onWriteDiscussionSuccess: () -> Unit = {},
    writeDiscussionViewModel: WriteDiscussionViewModel = viewModel()
) {
    var postTitle by remember { mutableStateOf("") }
    var postBody by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        OutlinedTextField(value = postTitle,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Post title") },
            onValueChange = {
                postTitle = it
            }
        )
        OutlinedTextField(value = postBody,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Post body") },
            onValueChange = {
                postBody = it
            }
        )
        Button(onClick = {
        writeDiscussionViewModel.uploadPost(postTitle,postBody)
        }
        ) {
            Text(text = "Upload")
        }
    }
}