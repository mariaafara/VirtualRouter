/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//import sharedPackage.ObservaleStringBuffer;

/**
 *
 * @author maria afara
 */
public class VirtualRouter extends Application {


    public static TextArea buffer;
//192.168.182.1
    public Router router;

    @Override
    public void start(Stage primaryStage) {
        //  buffer = new ObservaleStringBuffer();
        VBox root = new VBox(2);
        HBox hostnameConnectionbox = new HBox();
        buffer = new TextArea();//for the feedbacks
        buffer.setEditable(false);
        // textArea.textProperty().bind(buffer);
        buffer.setWrapText(true);

        root.setVgrow(buffer, Priority.ALWAYS);
        TextField txtRegistryPort = new TextField();
        txtRegistryPort.setPrefWidth(120);
        TextField txtHostname = new TextField();

        txtHostname.setPrefWidth(150);
        Button btnConnect = new Button("Connect");
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                  
                    Registry registry = LocateRegistry.createRegistry(Integer.parseInt(txtRegistryPort.getText()));//1099
                    router = new Router(txtHostname.getText());
                    registry.rebind(txtHostname.getText(), router);
                    txtHostname.setDisable(true);
                    txtRegistryPort.setDisable(true);
                    // Process.Start("path/to/your/file")
                    primaryStage.setTitle("Router " + router.getHostname());
                
                } catch (RemoteException ex) {
                    Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(VirtualRouter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        hostnameConnectionbox.getChildren().addAll(txtHostname, txtRegistryPort, btnConnect);
        root.getChildren().addAll(hostnameConnectionbox, buffer);

        primaryStage.setScene(new Scene(root, 600, 400));

        primaryStage.setTitle("Router");

        primaryStage.show();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
