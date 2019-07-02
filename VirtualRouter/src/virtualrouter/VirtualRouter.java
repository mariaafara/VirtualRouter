/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
    Stage stage;
    String filename;
    String hostname;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
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
        Button btnExport = new Button("Export Feedbacks");
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    if (btnConnect.getText().equals("Connect")) {
                        Registry registry = LocateRegistry.createRegistry(Integer.parseInt(txtRegistryPort.getText()));//1099
                        router = new Router(txtHostname.getText());
                        hostname = txtHostname.getText();
                        registry.rebind(txtHostname.getText(), router);
                        txtHostname.setDisable(true);
                        txtRegistryPort.setDisable(true);
                        // Process.Start("path/to/your/file")
                        primaryStage.setTitle("Router " + router.getHostname());
                        btnConnect.setText("Disconnect");
                        btnExport.setDisable(false);
                    } else if (btnConnect.getText().equals("Disconnect")) {
                        router.disconnet();
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(VirtualRouter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        btnExport.setDisable(true);
        btnExport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    extractFeedback();
                } catch (IOException ex) {
                    Logger.getLogger(VirtualRouter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        hostnameConnectionbox.getChildren().addAll(txtHostname, txtRegistryPort, btnConnect, btnExport);
        root.getChildren().addAll(hostnameConnectionbox, buffer);
        buffer.appendText("kakjhas\nsdfdghj\nadsafdsgdhj\nadsafdsgf\n");
        primaryStage.setScene(new Scene(root, 600, 400));

        primaryStage.setTitle("Router");

        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public void extractFeedback() throws FileNotFoundException, IOException {
        if (buffer.getText() == "") {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("No FeedBacks");

            alert1.setContentText("Oups, There's Nothing To Save...");
            alert1.showAndWait();
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Specify a file to save the Feedback!");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory == null) {
            //saveResults.setText("No Directory selected");
        } else {
            String filename = selectedDirectory.getAbsolutePath();
            File f2 = new File(filename + "\\" + hostname + "Feedbacks.txt");

            f2.delete();
            File f1 = new File(filename + "\\" + hostname + "Feedbacks.txt");

            PrintWriter writer = new PrintWriter(f1);
            writer.println("Feebacks exported at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            String[] contents = buffer.getText().split("\n");

            for (int i = 0; i < contents.length; i++) {

                writer.println(contents[i] + "\n");
            }
            writer.close();

        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
