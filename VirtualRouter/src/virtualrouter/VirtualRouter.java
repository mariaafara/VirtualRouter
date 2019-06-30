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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sharedPackage.ObservaleStringBuffer;

/**
 *
 * @author maria afara
 */
public class VirtualRouter extends Application {

    public static ObservaleStringBuffer buffer;

    @Override
    public void start(Stage primaryStage) {
        buffer = new ObservaleStringBuffer();
        VBox root = new VBox(2);
        HBox hostnameConnectionbox = new HBox();
        TextArea textArea = new TextArea();//for the feedbacks
        textArea.setEditable(false);
        textArea.textProperty().bind(buffer);

        TextField txtHostname = new TextField();

        txtHostname.setPrefWidth(150);
        Button btnConnect = new Button("Connect");
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {

                    Registry registry = LocateRegistry.createRegistry(1091);//1099
                    Router router = new Router(txtHostname.getText());
                    registry.rebind(txtHostname.getText(), router);
                    txtHostname.setDisable(true);

                } catch (RemoteException ex) {
                    Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(VirtualRouter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        hostnameConnectionbox.getChildren().addAll(txtHostname, btnConnect);
        root.getChildren().addAll(hostnameConnectionbox, textArea);

        primaryStage.setScene(new Scene(root, 400, 400));

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
