# mail-dispatcher
The application that allows you to enter the email address of the recipient, subject, text and attach such files as:<br>
document, picture, video, audio, and any combination of the above.<br>
If the file is not allowed to be sent to, the user will be notified.<br>
The e-mail should correspond to the standard and all fields must be filled<br>
The NoSQL database MongoDb was chosen for fast recording and receiving messages.<br>
All files along with messages are stored in a database for further history and statistics.<br>
It also takes into account the case of the queue overflow, when all unnecessary messages <br>
will be written to the database and be passed to the queue as it is cleared.
