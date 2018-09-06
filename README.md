# TruckBro
An on-demand truck hailing platform (Android app).

The server
The server component of the TruckBros software was built using Spring framework in Java. Here are some of the other relevant technologies used:
●	AliCloud virtual instance with latest Ubuntu operating system
●	Running Apache Tomcat as the server
●	Hibernate framework for mapping objects into (write) and from (read) data rows in a relational SQL database
●	GeTui third-party SDK integration for push notifications to devices
●	Built a web interface for admins/support, data visualization (e.g. Gaode Map SDK for visualizing events or users) and database management (e.g. changing passwords, reviewing profiles)
●	OpenFire real-time chat system running on the same AliCloud instance
●	Storing hashed password with SHA-256 and unique salt per user to prevent offline dictionary attack (provides extra security)
●	Each request is served by a “controller” class (Spring framework) that maps to a specific URL and expects certain request params
●	Typical request will involve reading from or writing to the database based on the request (and request parameters) and formatting a response
●	The response will be a JSON object to the user

The app
The Android app of the TruckBros software was built using standard Android practices, including the following technology pieces:
●	AliCloud object storage for uploading and downloading saved images
●	Smack (igniterealtime) client SDK that talks to server instance to achieve real-time instant messaging and group chats
●	Record audio on device and upload to AliCloud for audio chat messages
●	Gaode 2D Map client SDK for showing pannable map component in app
●	Push notification with GeTui SDK that allows for customized sound and banner content
●	SMS verification for phone numbers during account registration
●	Integrated third-party customized UI elements, e.g. Android Wheel at https://code.google.com/archive/p/android-wheel/


