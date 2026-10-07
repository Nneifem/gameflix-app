## Run Instructions
To set up the application, it will expect a database named gameflixdb on localhost:3306. Open MySQL Workbench, create a new connection, and once it loads, type ‘CREATE DATABASE gameflixdb’. Go into the project and find application.properties.example, change the file name to application-local.properties and fill it out with your SQL username and password and a jwt.secret that’s a random string 32 characters.

The user can expect that when they create a new user, the password will be hashed, and the hash won’t be returned. 201 indicates the new user was created successfully, 400 indicates invalid input, and 409 indicates the email is already registered. When the user logs in, the user can expect the credentials to be verified and a signed JWT to be returned with an expiration claim. 200 will indicate the user was able to log in successfully and the token was created, and 401 will indicate that the credentials failed. When the user calls to look at their profile, the user can expect the requested profile and token that identifies them. 200 will indicate that it was successfully getting the profile with a token, 401 indicates that something is missing, invalid, or expired token.

## Schema
CREATE TABLE `users` (
`user_id` bigint NOT NULL AUTO_INCREMENT,
  
`email` varchar(255) NOT NULL,
  
`first_name` varchar(255) DEFAULT NULL,
  
`last_name` varchar(255) DEFAULT NULL,
  
`password` varchar(255) NOT NULL,
  
`username` varchar(255) DEFAULT NULL,
  
PRIMARY KEY (`user_id`),
  
UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


## Hashing and Token Lifetime
For the hashing, I used DelegatingPasswordEncoder and set the strength to 12. What the strength means is that the algorithm will go through about four thousand rounds of hashing to generate a single password hash. This will make it harder for hackers to try to unhash the password. DelegatingPasswordEncoder will determine what algorithm to use at the moment rather than deciding on one hashing algorithm to use and never change.


For the token lifetime, I set it to an hour because I believe that it is a good amount of time for the user to not be logged out in the middle of a session and is short enough so that the token cannot be tampered with. 

For the DDL-AUTO, I used spring.jpa.hibernate.ddl-auto=update because with each new user I create, it will automatically execute the statement when the application starts up. It also keeps the existing data in the table rather than wiping out the data every time the server restarts. 

## Design-Change 
From the Module 3 Design, I changed the Architectural Diagram to show the arrows between the mircoservices and their databases and dragged another arrow from the subscription service to the payment service to show that it’s in use.

I also changed the ERD to reflect the Architectural Diagram, creating tables for users, games, subscriptions, and reviews. From the users to subscriptions tables, there is a one-to-one relationship because one user can only have one subscription at a time. From the users to reviews tables, there’s a one-to-many relationship, as one user can leave many reviews. Lastly, between the games and reviews tables, there is a one-to-many relationship, as one game can have many reviews under it.

## AI-use
For the project, I used Claude to help me write the code for JwtService, JwtAuthFilter, SecurityConfiguration, part of the AuthController, and make adjustments to the UserService class. One change I made to the code that Claude generated is in the AuthController class for the “/auth/register”, the AI combined checking the email and password into a big if statement and would send a message that a valid email and password are needed. I changed it so one if statement is for the email, checking to see if the email field is empty and is following the correct email format otherwise, a message will read enter a valid email. The other if statement for password checks to see if the password is empty and makes sure the password is between 8 and 64 characters long otherwise, a message will appear saying the password needs to be at least 8 characters long. I believe this is better for the user experience so the user can know what they need to fix rather than just guessing if the email or password is wrong.
