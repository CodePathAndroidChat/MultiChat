# Group #6 - Multi-language chat

Multi-language chat allows users who speak different languages to communicate.  


## Team member 

*   Nan Den
*   Alitsiya Yusupova
*   Jason Bautista


## User Stories

**required** 

* [x] com.example.jason.multichatapp.App has sign up screen where user can edit the following information
 * [x] Username
 * [x] Password 
 * [x] Choice of language that the user desire to recieve the chat massages 
 * [x] Location of the user  
* [x] com.example.jason.multichatapp.App has login screen where user can use his/her username and password to login to the app
* [x] User will see the "Global Chatroom" once the user login/sign up to the app
 * [x] "Global Chatroom" is a group chat room where all the app user can participate.
* [x] com.example.jason.multichatapp.App has burgure menu("Navigation menu") that contains "Global Chatroom", "Map of Users", "Edit Profile", "User in Chat"
 * [x] "Map of Users" displays the map view of all the app users 
 * [x] User can change his/her account setting at "Edit Profile" (same UI as sign up screen)
 * [x] "User in Chat" displays list of users who are using the application 
* [x] User can start a direct message with other user by clicking a user in "User in Chat" screen
* [x] Each chat is clickable and will take user to "Alternate Translation" screen 
 * [x] "Alternate Translation" contains the chat message in it's original language
* [ ] User recieves a push notificaiton when he/she recieves a direct message from the other user 
* [x] User can recieve chat messages of their choosen language even sender of the message use different language.
* [x] User can send the chat message to other users in both "Global Chatroom" and "Direct Message" 
* [ ] The app handles text abbreviations 
  
**optional**
* [ ] User can view the chat history 
* [ ] com.example.jason.multichatapp.App supports offline mode.
* [x] com.example.jason.multichatapp.App supports localizations. Different country displays different languages.
* [ ] The Application (UI) and keyboard are in different langauges
* [ ] User can share his/her location in the chat message 
* [ ] User can add image/picture to the chat message
* [ ] User can use voice recognition to type the message
* [ ] com.example.jason.multichatapp.App contains "Notification" page where user can view history of notifications
* [ ] User can click the original language to lean how to pronunce the language in "Alternate Translation" screen 

## Open-source libraries used

- [Translate API](https://cloud.google.com/translate/docs/) - provides access to the Google online machine translation service. It supports more than 100 languages and can translate separate words or complete texts.
- [Firebase](https://firebase.google.com/) - push notificaitons, stores the chat histories 
- [Google Map](https://developers.google.com/maps/android/) - to displays the map view of the app users
- [Firebase Authentication](https://firebase.google.com/docs/auth/)  - UI libraries to authenticate users to the app
- [Cloud Functions for Firebase](https://firebase.google.com/docs/functions/) - to translate messages on a backend
## To setup Fabric key:

Edit:
AndroidManifiest.xml

replace -> API_KEY_HERE

Get key here: 
https://fabric.io/kits/android/crashlytics/install

## Wireframe

Files of the wireframe contains in app/main/src/v2

<img src='https://i.imgur.com/x13ys3I.gif' title='Video Walkthrough' alt='Video Walkthrough' />

## Week 5 Video Walkthrough

* [Chat Screen](https://i.imgur.com/sP24CfY.gif)

* [Log in](https://i.imgur.com/7uOaPsh.gif)


* [Burger Menu](https://i.imgur.com/IqlSDPK.gif)


* [Sign up](https://i.imgur.com/PQRiO96.gif)

## Week 7 Video Walkthrough

* [Localization](https://i.imgur.com/NRyFWvM.gif)

* [Direct message](https://i.imgur.com/RHfRSTR.gif)

* [Map location](https://i.imgur.com/1mvOpqQ.gif)
