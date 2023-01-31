# OtaWilmaMobile

OtawilmaMobile is an android interface for OtaWilma. 

This allows for thigs like AN USABLE INTERFACE and notifications

## Features:

compared to the normal Wilma app this app can:

- Cache your schedule so that you can see where you should be and when even without internet, or the oh so reliable Wilma-servers

compared to the normal Wilma app this app can't:

- Do most of the things Wilma can

## Things of concern:

### Storing of critical data

If you check the option to autologin in preferences, the app WILL STORE YOUR WILMA LOGIN CREDENTIALS ENCRYPTED WITH AES256.
This SHOULD be safe, but to be safe the user shall check the safe of the code in [EncryptedPreferenceStorage](/app/src/main/java/com/otawilma/mobileclient/EncryptedPreferenceStorage.kt) beforehand.

### Storing of noncritical data

The application will store schedules (for now) unencrypted for caching reasons.

## Short TOS

 This app stores and handles data which is the property of Espoo. The dev takes not responsibility for the data you use this application to store. Download and install only at your own risk. The developer does not take any responsibility for the misuse of the malfunctioning of this app. 
