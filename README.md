# Grade-Master-Spring-Boot-Project

GradeMaster is a Spring Boot project, manages students, courses, and scores. It offers web & PDF views for course and student scores, and score stats. Admins access a secure dashboard for operations, while students securely log in to view their results. It utilizes a single login page for both users where users are redirected to appropriate endpoint/URL on authentication.

How to run the project locally: 
-Download and Install java jdk 8 to 21  on your computer

-Download and install any java IDE, either Eclipse, Netbeans or Intelij Idea.

-install git

-set up your git and create the directory you would save the project

-open git bash on the directory and clone the project using this command '[git clone https://Iloke@bitbucket.org/obinnadanil/grademaster_spring_boot_project.git](https://github.com/obinnadanil/Grade-Master-Spring-Boot-Project.git)'.

-open the cloned project on your chosen IDE

-install Xampp for setting up the database.

-go to src/main/resources/application.properties. Append a choice name of a database to this line 'spring.datasource.url=jdbc:mysql://localhost:3306/' for exapmle, you can name your database 'demo' and the line would become 'spring.datasource.url=jdbc:mysql://localhost:3306/demo'.

-open Xampp and start apache tomcat server and then start mysql.

-now click on the button named 'admin' that is next to mysql on its row and mysql will be opened on your local browser.

-create the database with the chosen name, in the example i used, i will create a database named 'demo'.

-After successful creation of the database, go back to your IDE, navigate to src/main/java/obi/com/grademaster/Application.java and run the program.

-Enter this link 'http://localhost:8080/login' on you browser once the server is started

add admin and student user details using the endpoints defined in the user controller


NOTE: 
For Students, use their registration numbers as usernames.


if you want to retain the preconfigured database, 
then you will not be needing to create a new 
database as was instructed to be done in the application.properties.
But it is advised you create your
own database, populate it an explore the
features of the app as described above.
