# Group #6 - Multi-language chat

Multi-language chat allows users who speak different languages to communicate.  


## Team member 

*   Nan Den
*   Alitsiya Yusupova
*   Jason Bautista


## User Stories

**required** 

* [ ] App has sign up screen where user can edit the following information 
 * [ ] Username
 * [ ] Password 
 * [ ] Choice of language that the user desire to recieve the chat massages 
 * [ ] Location of the user  
* [ ] App has login screen where user can use his/her username and password to login to the app
* [ ] User will see the "Global Chatroom" once the user login/sign up to the app
 * [ ] "Global Chatroom" is a group chat room where all the app user can participate.
* [ ] App has burgure menu("Navigation menu") that contains "Global Chatroom", "Map of Users", "Edit Profile", "User in Chat" 
 * [ ] "Map of Users" displays the map view of all the app users 
 * [ ] User can change his/her account setting at "Edit Profile" (same UI as sign up screen)
 * [ ] "User in Chat" displays list of users who are using the application 
* [ ] User can start a direct message with other user by clicking a user in "User in Chat" screen
* [ ] Each chat is clickable and will take user to "Alternate Translation" screen 
 * [ ] "Alternate Translation" contains the chat message in it's original language
* [ ] User recieves a push notificaiton when he/she recieves a direct message from the other user 
* [ ] User can recieve chat messages of their choosen language even sender of the message use different language.
* [ ] User can send the chat message to other users in both "Global Chatroom" and "Direct Message" 
* [ ] The app handles text abbreviations 
  
**optional**
* [ ] User can view the chat history 
* [ ] App supports offline mode. 
* [ ] App supports localizations. Different country displays different languages. 
* [ ] The Application (UI) and keyboard are in different langauges
* [ ] User can share his/her location in the chat message 
* [ ] User can add image/picture to the chat message
* [ ] User can use voice recognition to type the message
* [ ] App contains "Notification" page where user can view history of notifications
* [ ] User can click the original language to lean how to pronunce the language in "Alternate Translation" screen 

## Open-source libraries used

- [Translate API](https://tech.yandex.com/translate/) - provides access to the Yandex online machine translation service. It supports more than 90 languages and can translate separate words or complete texts.
- [Firebase](https://firebase.google.com/) - push notificaitons, stores the chat histories 
- [Google Map](https://developers.google.com/maps/android/) - to displays the map view of the app users
- [Goole Signin](https://developers.google.com/identity/sign-in/android/start-integrating) 
## To setup Fabric key:

Edit:
AndroidManifiest.xml

replace -> API_KEY_HERE

Get key here: 
https://fabric.io/kits/android/crashlytics/install

## Wireframe

Files of the wireframe contains in app/main/src/v2

<img src='https://i.imgur.com/x13ys3I.gif' title='Video Walkthrough' alt='Video Walkthrough' />

