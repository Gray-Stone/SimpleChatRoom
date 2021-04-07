# Simple Network Chatroom.

This is a class project for learning the basic about computer networks.

The project is not designed to be used directly. It is simply a practice for creating a multi client server software using socket. Thus, things like server address and port are hard coded into the software.

The project is split into two pieces: 
- server side software: heavy on networks, almost no interface
- client side software: simple network with a very basic GUI. 


## Project Layout

- ChatRoomGUI contains the GUI client. 
- ChatRoomServer contains the server app of the chatroom 

- ChatRoomClient is a testing client that sends pre-coded packages without any interface (for quick testing). 

### Jar: 
Both Server and GUI client are packed into jar and can be launched by directly running the jar file. 

## Features and structure: 

- Any character supported by UTF-8 are allowed in username and message content. This is achieved by warping every network package with a header. 

- Server supports multiple client connection. Each client is split into one thread, the limit is the amount of thread the server can create.

- Each client thread can buffer two user messages. No messages will be stored apart from the temporary buffer. 

- Apart from threads for each user, two additional threads are used: 
	- Acceptor thread: wait for new user connection and validate its username before adding this client to the chat
	- Manager thread: manages a shared user hashmap. Also pass through message received from each client and manage disconnected users. 

- A hashmap (class: ClientData) is used to store all info about connected user. This is also the only shared data between acceptor thread and manager thread. 
	- Due to the data sharing. All modification access to the hashmap is protected by a flag variable to prevent data hazard.     

- When ever a user logs in or out, server will resend the list of users to each client. (this design is not optimized for large number of user or high throughput.)

- Private messages are allowed between connected user.  
 

