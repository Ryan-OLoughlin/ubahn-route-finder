module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires jmh.core;

    opens com.example.UI to javafx.fxml;
    opens com.example.Controller to javafx.fxml;

    exports com.example.UI;
    exports com.example.Controller;
    exports com.example.Model;
    exports com.example.Util;
}
