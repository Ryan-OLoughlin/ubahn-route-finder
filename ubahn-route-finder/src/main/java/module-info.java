module com.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.Controller to javafx.fxml;
    
    exports com.example.UI;
    exports com.example.Controller;
    exports com.example.Model;
    exports com.example.Util;
    exports com.example.Data;
}
