/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrouter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
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
import javafx.scene.control.Label;
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
    Registry registry;
    static String currentHostIpAddress = null;
    InetAddress ip;

    @Override
    public void start(Stage primaryStage) throws UnknownHostException {
        stage = primaryStage;
        //  buffer = new ObservaleStringBuffer();
        VBox root = new VBox(2);
        HBox hostnameConnectionbox = new HBox();
        buffer = new TextArea();//for the feedbacks
        buffer.setEditable(false);
        // textArea.textProperty().bind(buffer);
        buffer.setWrapText(true);
        Label lblip = new Label();
        root.setVgrow(buffer, Priority.ALWAYS);
        TextField txtRegistryPort = new TextField();
        txtRegistryPort.setPrefWidth(120);
        TextField txtHostname = new TextField();
    //    InetAddress localip = InetAddress.getLocalHost();;
        txtHostname.setPrefWidth(150);
        Button btnConnect = new Button("Connect");
        Button btnExport = new Button("Export Feedbacks");
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    if (btnConnect.getText().equals("Connect")) {
                        registry = LocateRegistry.createRegistry(Integer.parseInt(txtRegistryPort.getText()));//1099
//                        if (InetAddress.getByName(getCurrentEnvironmentNetworkIp()) == null) {
//                         ip = Inet4Address.getLocalHost();
//                            System.out.println(" this.ipAddress w= " + Inet4Address.getLocalHost());
//                        } else {
//                            ip = InetAddress.getByName(getCurrentEnvironmentNetworkIp()); //InetAddress.getLocalHost();
//                            System.out.println(" this.ipAddress l= " + InetAddress.getByName(getCurrentEnvironmentNetworkIp()));
//                        }

                        router = new Router(txtHostname.getText());
                        hostname = txtHostname.getText();

                        lblip.setText("     " + Router.ipAddress + "");
                        registry.rebind(txtHostname.getText(), router);
                        Platform.runLater(() -> {
                            buffer.appendText("Router created and rebinded to the registry with its name " + hostname + "\n");
                        });

//                        Platform.runLater(() -> {
//                            try {
//                                buffer.appendText("ip is" + getAddress());
//                            } catch (IOException ex) {
//                                Logger.getLogger(VirtualRouter.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        });
                        txtHostname.setDisable(true);
                        txtRegistryPort.setDisable(true);
                        // Process.Start("path/to/your/file")
                        primaryStage.setTitle("Router " + router.getHostname());
                        btnConnect.setText("Disconnect");
                        btnExport.setDisable(false);
                    } else if (btnConnect.getText().equals("Disconnect")) {

                        router.disconnet();
                        registry.unbind(hostname);

                        Platform.runLater(() -> {
                            buffer.appendText("Disconnected\n");
                        });

                    }
                } catch (RemoteException ex) {
//                    Platform.runLater(() -> {
//                        buffer.appendText("Sorry this port" + txtRegistryPort.getText() + " is taken\n");
//                    });
                    Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(VirtualRouter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Platform.runLater(() -> {
                        buffer.appendText("Sorry this port" + txtRegistryPort.getText() + " is taken\n");
                    });
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
        hostnameConnectionbox.getChildren().addAll(txtHostname, txtRegistryPort, btnConnect, btnExport, lblip);
        root.getChildren().addAll(hostnameConnectionbox, buffer);
        //buffer.appendText("kakjhas\nsdfdghj\nadsafdsgdhj\nadsafdsgf\n");
        primaryStage.setScene(new Scene(root, 650, 400));

        primaryStage.setTitle("Router");

        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public String getCurrentEnvironmentNetworkIp() {

        if (currentHostIpAddress == null) {
            Enumeration<NetworkInterface> netInterfaces = null;
            try {
                netInterfaces = NetworkInterface.getNetworkInterfaces();

                while (netInterfaces.hasMoreElements()) {
                    NetworkInterface ni = netInterfaces.nextElement();
                    //System.out.println(ni.getName());
                    if (!ni.getName().contains("wlan")) {
                        continue;
                    }
                    Enumeration<InetAddress> address = ni.getInetAddresses();
                    while (address.hasMoreElements()) {
                        InetAddress addr = address.nextElement();
                        //                      log.debug("Inetaddress:" + addr.getHostAddress() + " loop? " + addr.isLoopbackAddress() + " local? "
                        //                            + addr.isSiteLocalAddress());
                        if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                                && !(addr.getHostAddress().indexOf(":") > -1)) {
                            //System.out.println(addr);
                            currentHostIpAddress = addr.getHostAddress();
                            return currentHostIpAddress;
                        }
                    }
                }
                if (currentHostIpAddress == null) {
                    currentHostIpAddress = "127.0.0.1";
                }

            } catch (SocketException e) {
//                log.error("Somehow we have a socket error acquiring the host IP... Using loopback instead...");
                currentHostIpAddress = "127.0.0.1";
            }
        }
        return currentHostIpAddress;
    }

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
