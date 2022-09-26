# Security Computer Project 55047 55315

This project allows you to chat in a secure way. To achieve this, the theory of the Security course was used to send signed messages.

<br/>

## How to use
First, go into the folder `MessagingApp` and compile the project by using the following command : 

```bash
mvn clean install 
 ```

<br/>

### Setup Server

To setup the application a server must be created, to do this use the following command. 

```bash
java -cp target/SECG4-1.0-SNAPSHOT.jar Server
 ``` 

<br/>

### Setup Clients
To run a client that will run the application first run the following command.

```bash
java -cp target/SECG4-1.0-SNAPSHOT.jar Client
 ``` 

 After this, you must choose your unique username. If you already exist in the Server, you will be logged in, otherwise a new user will be created.


### Add contact 

To add a contact write: 
```
add:contactName
 ``` 

### Delete contact 

To delete a contact write: 
```
delete:contactName
 ``` 

### Send a message

To send message write:
```
@ContactName Message
 ``` 

#### Group

Marika Winska 55047 D112

Oscar Tison 55315 D112

