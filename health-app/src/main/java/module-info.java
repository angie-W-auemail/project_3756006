module com.banking.system.bankingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;


    opens com.health.system.healthsystem.Controllers to javafx.fxml;
    opens com.health.system.healthsystem.Controllers.Admin to javafx.fxml;
    opens com.health.system.healthsystem.Controllers.Client to javafx.fxml;
    opens com.health.system.healthsystem.Controllers.Employee to javafx.fxml;


    opens com.health.system.healthsystem to javafx.fxml;
    exports com.health.system.healthsystem;
    exports com.health.system.healthsystem.Controllers;
    exports com.health.system.healthsystem.Controllers.Admin;
    exports com.health.system.healthsystem.Controllers.Client;
    exports com.health.system.healthsystem.Controllers.Employee;
    exports com.health.system.healthsystem.Models;
    exports com.health.system.healthsystem.Views;

}