package com.example.UI;

import java.io.IOException;

import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        MainApp.setRoot("secondary");
    }
}
