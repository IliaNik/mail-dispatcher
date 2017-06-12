# ✉ Mail-dispatcher ✉
The application that allows you to send messages.<br>
<hr>
Enter the email address of the recipient, subject, text and attach such files as:<br>
• Document;<br>
• Picture;<br>
• Video;<br>
• Audio;<br>
• Picture;<br>
 and any combination of the above.<br>
<hr>
If the file is not allowed to be sent to, the user will be notified.<br>
The e-mail should correspond to the standard and all fields must be filled.<br><br>
The NoSQL database <b>MongoDb</b> was chosen for fast recording and receiving messages.<br><br>
All files along with messages are stored in a database for further history and statistics.<br>
<hr>
It also takes into account the case of the queue <b>overflow</b>, when all unnecessary messages <br>
will be written to the database and be passed to the queue as it is cleared.
