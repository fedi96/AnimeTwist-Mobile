Anime Twist (Android)
=========

[Anime Twist] hosts high quality anime series in 720p and 1080p, with beautifully responsive page and design, watch HD anime for free at [Anime Twist].

The Android application allows you to take this on the move with you, you can freely watch those series on the go with the realtime chat being in sync with the web application and all the services available to you within a mobile application.

Screenshots
---
![Login](http://i.imgur.com/sxYJqM1.png "Login Screen")
![Series](http://i.imgur.com/To0MfE7.png "Series Listing Screen")
![Chat](http://i.imgur.com/QEwVZfF.png "Realtime Chat")

Finished
----
 - Created login screen where user can login with his Anime Twist account
 - Caching the user details so the user would only need to login once (clearing can be done within the menu)
 - Added chat functionality so the user would be able to chat from within the app under his user
 - Created chat screen within a side drawer so the chat would always be accasible
 - Added notifications on username mention
 - Created series listing page where each series would be listed and accasible from within
 - Created custom video player used to watch the series episode in
 
Being worked on
----
 - Video page where the series episode would be listed and the custom video player would be visible in

Todo
----
 - Create settings page where notification settings for the chat can be set
 - Caching of thumbnails and series list
 - Move websocket into a singleton class considering that the instance would be accassible from multiple places

Version
----

0.6

Solutions Used
-----------

[Anime Twist] uses a number of solution to keep its services efficient and secure, these are as following:
* [WebSocket] - web based socket protocol used to provide realtime and simple chat services.
* [Go] - *C* based programming language used to run the server side of the website in including the WebSocket and the Server Side Applications.
* [MariaDB] - Fork of MySQL with many security, efficiency and feature patches used to store all the long term data in.
* [Gradle] - Automation of the android application with building, testing, publishing, deployment and dependency/package management.

As this is just the Android application, for more information in relation to the web application please refer to the official [Anime Twist Repository] of the web application available on [Azertify]'s account.

License
----

For Licensing information please refer to the official [Anime Twist Repository].

[Anime Twist]:https://animetwist.net
[WebSocket]:https://www.websocket.org
[Go]:http://golang.org/
[MariaDB]:https://mariadb.org/
[Gradle]:www.gradle.org
[Azertify]:https://github.com/azertify
[Anime Twist Repository]:https://github.com/azertify/animetwist.net
