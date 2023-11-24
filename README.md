<h1 align="center">
Сar Sharing Service
</h1>
<p align="center">
    <img width="500" src="https://robbreport.com/wp-content/uploads/2023/02/30-1.jpg?w=1000" alt="Car service logo">
</p>

### Intro
The goal of this project is to provide a user, with easy solution in the sphere of car renting. 
The project allows the client to quickly browse available cars, rent one and make a payment for the rental. 
Additionally, the project has a Telegram bot support with notifications, and an email notifications support. 
The technology used for payment processing is "Stripe".

### Technologies used in the project

<p align="left">
    <img width="25" src="https://upload.wikimedia.org/wikipedia/uk/8/85/%D0%9B%D0%BE%D0%B3%D0%BE%D1%82%D0%B8%D0%BF_Java.png" alt="Java Logo">
    Java
</p>

<p align="left">
    <img width="25" src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQwsq-7f5BWyog4cdeT1sQaYLVzhJ0o37Up8TjHvVU08WUgfyyMMRMHTVwJ5XReSjyhZa0&usqp=CAU" alt="Spring logo">
    Spring
</p>

<p align="left">
    <img width="25" src="https://pbs.twimg.com/profile_images/1235868806079057921/fTL08u_H_400x400.png" alt="Spring Boot logo">
    Spring Boot
</p>

<p align="left">
    <img width="25" src="https://pbs.twimg.com/profile_images/1235945452304031744/w55Uc_O9_400x400.png" alt="Spring Data Jpa logo">
    Spring Data Jpa
</p>

<p align="left">
    <img width="25" src="https://pbs.twimg.com/profile_images/1235983944463585281/AWCKLiJh_400x400.png" alt="Spring Security logo">
    Spring Security
</p>

<p align="left">
    <img width="25" src="https://blog.kakaocdn.net/dn/bA0QdM/btqQCzxS7vv/RTB3bbZsu7EMKPBefuTn80/img.jpg" alt="Lombok logo">
    Lombok
</p>

<p align="left">
    <img width="25" src="https://dashboard.snapcraft.io/site_media/appmedia/2020/08/liquibase.jpeg.png" alt="Liquibase logo">
    Liquibase
</p>

<p align="left">
    <img width="25" src="https://logowik.com/content/uploads/images/mysql8604.logowik.com.webp" alt="MySql logo">
    MySql 
</p>

<p align="left">
     <img width="25" src="https://mapstruct.github.io/mapstruct.org.new/images/favicon.ico" alt="Mapstruct logo">
    Mapstruct
</p>

<p align="left">
     <img width="25" src="https://cdn.icon-icons.com/icons2/2699/PNG/512/stripe_logo_icon_167962.png" alt="Stripe logo">
    Stripe API
</p>

<p align="left">
     <img width="25" src="https://flowxo.com/wp-content/uploads/2021/03/Telegram-Logo-512x512.png" alt="Telegram logo">
    Telegram API
</p>

<p align="left">
     <img width="25" src="https://seeklogo.com/images/S/swagger-logo-A49F73BAF4-seeklogo.com.png" alt="Swagger logo">
    Swagger ui
</p>

<p align="left">
    <img width="25" src="https://cdn4.iconfinder.com/data/icons/logos-and-brands/512/97_Docker_logo_logos-512.png" alt="Docker logo">
    Docker
</p>

### Available endpoints
#### Authentication controller

| Request type | Endpoint                     | Role  | Description                                                          |
|--------------|------------------------------|-------|----------------------------------------------------------------------|
| POST         | /register                    | ALL   | New user registration                                                |
| POST         | /login                       | ALL   | Receive access to user's profile                                     |

#### User controller

| Request type | Endpoint                     | Role  | Description                                                          |
|--------------|------------------------------|-------|----------------------------------------------------------------------|
| PUT          | /users/{id}/role             | ADMIN | Update user's role                                                   |
| GET          | /users/me                    | USER  | Receive information about user's profile                             |
| PUT          | /users/me                    | USER  | Update user's profile info                                           |

#### Car controller

| Request type | Endpoint                     | Role  | Description                                                          |
|--------------|------------------------------|-------|----------------------------------------------------------------------|
| GET          | /cars                        | ALL   | Get a list of available cars                                         |
| GET          | /cars/{id}                   | USER  | Get a car by specific id                                             |
| POST         | /cars                        | ADMIN | Create a new car in the DB                                           |
| PUT          | /cars/{id}                   | ADMIN | Update info for a car with a specific id                             |
| DELETE       | /cars/{id}                   | ADMIN | Delete a car with a specific id                                      |

#### Rental controller

| Request type | Endpoint                     | Role  | Description                                                          |
|--------------|------------------------------|-------|----------------------------------------------------------------------|
| POST         | /rentals                     | USER  | Create a new rental                                                  |
| GET          | /rentals?userId=?&isActive=? | ADMIN | Get rentals by user ID and whether the rental is still active or not |
| GET          | /rentals/{id}                | ADMIN | Get specific rental by rental's id                                   |
| POST         | /rentals/{id}/return         | ADMIN | Set actual return date for a rental                                  |

#### Payment controller

| Method type | Endpoint                       | Role   | Description                                                        |
|-------------|--------------------------------|--------|--------------------------------------------------------------------|
| POST        | /payments                      | USER   | Create payment session                                             |
| POST        | /payments                      | ADMIN  | Get payments by user ID                                            |
| POST        | /payments/success?session_id=? | STRIPE | Check successful stripe payments (Endpoint for stripe redirection) |
| POST        | /payments/cancel?session_id=?  | STRIPE | Return payment paused message (Endpoint for stripe redirection)    |

### Email notifications
The project supports the following types of email notifications:
- Greeting email after user's registration
- An info email with the data regarding a newly created rental 

### Telegram notifications
The project also supports such types of telegram notifications:
- A notification regarding a newly registered user
- Successful payment notification
- Notifications regarding overdue rentals ("No rentals overdue today!" message in case there are no overdue rentals)
- Advertisement notifications for special offers 

### Project launch guide
The following steps are required in order to setup the project on your device:
1. You should have an installed docker instance on your platform
2. Clone the GitHub repository
3. Create a new `.env` file with the necessary environment variables.(You can use an **.env.sample** file for example)
4. Run `mvn clean package` command in the console
5. Run `docker-compose up --build` command to build and start the Docker containers
6. The application should now be running at `http://localhost:8082`

