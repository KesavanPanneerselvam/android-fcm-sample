package com.interview.fcmpushnotification

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.interview.fcmpushnotification.ui.theme.FCMPushnotificationTheme
const val TAG = "fcmpushnotification_log"
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FCMPushnotificationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }

        /*FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d(TAG, "Firebase Token: $token")
            addDeviceToken(token)
        })*/
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Greeting() {
    Column {
        var userID by  remember {  mutableStateOf("") }
        OutlinedTextField(value = userID, onValueChange = {
            userID = it
        }, label = { Text(text = "User ID") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = { addDeviceToken(userID) },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(text = "Register User")
        }
        Spacer(modifier = Modifier.height(32.dp))
        var toUserId by  remember {  mutableStateOf("") }
        OutlinedTextField(value = toUserId, onValueChange = {
            toUserId = it
        }, label = { Text(text = "To User Id") }, modifier = Modifier.fillMaxWidth())
        var title by  remember {  mutableStateOf("") }
        OutlinedTextField(value = title, onValueChange = {
            title = it
        }, label = { Text(text = "Notification Title") }, modifier = Modifier.fillMaxWidth())
        var message by  remember {  mutableStateOf("") }
        OutlinedTextField(value = message, onValueChange = {
            message = it
        }, label = { Text(text = "Message") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = { sendNotification(toUserId, title, message) },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(text = "Send Notification")
        }
    }
}

private data class UserToken(val userId: String, val deviceId: String, val token: String)
private fun addDeviceToken(userId: String) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result
        Log.d(TAG, "Firebase Token: $token")
        val userToken = UserToken(userId, "Android", token)
        FirebaseFirestore
            .getInstance()
            .collection("UserTokens")
            .document(userId)
            .set(userToken)
    })
}

private data class NotificationData(val userId: String, val title: String, val message: String, val messageType: Int = 1, val url: String = "", val status: Int = 0)
private fun sendNotification(userId: String, title: String, message: String){
    val notificationData = NotificationData(userId, title,message)
    FirebaseFirestore
        .getInstance()
        .collection("NotificationCenter")
        .document()
        .set(notificationData)
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FCMPushnotificationTheme {
        Greeting()
    }
}