```
How to Create Firebase Cloud Functions to Send Push Notifications with Firestore Triggers
Step 1: Set Up Firebase Project
Open the Firebase Console and go to your Firebase project.
Enable Firestore:
In the Firebase Console, click on "Firestore Database" in the left-hand menu.
Click "Create database" and follow the prompts to set up Firestore.
Step 2: Set Up Firebase Cloud Functions
Install Firebase CLI:

Open your terminal or command prompt.
Install the Firebase CLI using npm:
sh
Copy code
npm install -g firebase-tools
Initialize Firebase in your project:

Navigate to your project directory in the terminal.
Run:
sh
Copy code
firebase login
Authenticate with your Google account.
Initialize Firebase in your project:
sh
Copy code
firebase init functions
Follow the prompts to set up Cloud Functions, select the project you created, and choose JavaScript or TypeScript as the language for writing your functions.
Step 3: Install Dependencies
Navigate to the functions directory:
sh
Copy code
cd functions
Install Firebase Admin SDK and other required packages:
sh
Copy code
npm install firebase-admin firebase-functions
Step 4: Write the Cloud Function
Open index.js or index.ts in your functions directory and add the following code:

javascript
Copy code
const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

exports.sendPushNotification = functions.firestore
    .document('notifications/{notificationId}')
    .onCreate((snapshot, context) => {
        const notificationData = snapshot.data();
        
        const payload = {
            notification: {
                title: notificationData.title,
                body: notificationData.body,
            }
        };

        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24 // 1 day
        };

        return admin.messaging().sendToDevice(notificationData.token, payload, options)
            .then(response => {
                console.log("Successfully sent message:", response);
                return null;
            })
            .catch(error => {
                console.log("Error sending message:", error);
            });
    });
This function triggers when a new document is created in the notifications collection in Firestore.
The document should contain title, body, and token fields.
Step 5: Deploy the Cloud Function
Deploy your functions to Firebase:

sh
Copy code
firebase deploy --only functions
Verify the deployment:

Go to the Firebase Console.
Click on "Functions" in the left-hand menu to see your deployed function.
Step 6: Test the Cloud Function
Add a document to the Firestore notifications collection:

In the Firebase Console, navigate to "Firestore Database".
Click on "Start collection" and name it notifications.
Add a document with fields title, body, and token (token refers to the device's Firebase Cloud Messaging (FCM) token).
Check logs for the function:

In the Firebase Console, go to "Functions" and check the logs to see if the push notification was sent successfully.







/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendFCMNotifications = functions.firestore.document('NotificationCenter/{userId}').onCreate(
  async (snapshot) => {  
  	console.log('Triggered Notification function');
  	const title = snapshot.data().title;
  	const message = snapshot.data().message;
  	const userId = snapshot.data().userId;
    console.log('Notification function step 1 - '+ userId);
    const payload = {
      notification: {
        title: title,
        body: message
      }
    };
    const user = await admin.firestore().collection('UserTokens').doc(userId).get();
    console.log('Notification function step 2');
    console.log(JSON.stringify(user.data()));
    if(user.exists){
	    //const tokens = ['eatcl0C1S22opa3bWipbxP:APA91bFfVM6v-wV5Juu1wp1280rx5WQ7t2qnjJxY4D-Yt5UnwyNzW6zHCD4reHHnjz8AOujVilyn8_qyGPLvKVh3FtNURSnAOijQ2gLwO6QrCNnKRsruO4iCbuxwoAHPTF8qQcu6Z6J7'];
	    const tokens = [user.data().token];
		console.log('Notification function step 3');
	    const response = await admin.messaging().sendToDevice(tokens, payload);
	    console.log('Notification function step 4');
	    console.log('Notification send: ' +  response);
	    console.log(JSON.stringify(response));
	} else{
		console.log('Notification function step 5');
	}
});
```
