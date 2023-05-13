module com.example.starcraftarcadedemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.starcraftarcadedemo to javafx.fxml;
    exports com.example.starcraftarcadedemo;
}