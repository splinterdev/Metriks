package com.github.isaacmartinscode.metriks.util;

import com.github.isaacmartinscode.metriks.application.Program;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ChangeView {
    public synchronized static void change(String absoluteName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Program.class.getResource(absoluteName));
            AnchorPane newAnchorPane = fxmlLoader.load();

            Scene mainScene = Program.getMainScene();
            AnchorPane mainAP = (AnchorPane) mainScene.getRoot();
            mainAP.getChildren().clear();
            mainAP.getChildren().addAll(newAnchorPane);
        }
        catch(IOException e) {
           System.out.print("Error: " + e.getMessage());
        }
    }
}
