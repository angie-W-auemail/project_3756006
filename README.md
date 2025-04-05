#This is project code for COSC3756 001 25W Group 6 Health application
Newest developed code in branch dev0.6, please git clone -b dev0.6_summary_base_master_angie https://github.com/angie-W-auemail/project_3756006.git
You will need to build maven pom.xml setting file to load all libraries included for this project before running the application
health-app/src/main/java/com/health/system/healthsystem/HealthApplication.java is the main run file
you can also run the .jar file to execute the application
#Code structure
##Model
Structure of ClientData (ClientData class)
Structure of Activity (Activity class)
SQL connection (DatabaseInitializer, DatabaseConnection)
Model class (stores instance)
##Views
Enum of roles, activities, menu items, etc.
ViewFactory (getter and setter connecting to each view, loaded from target/fxml files)
##Controller
Main control class for each UI component (loginController, SignupController, ProfileController, etc.)


