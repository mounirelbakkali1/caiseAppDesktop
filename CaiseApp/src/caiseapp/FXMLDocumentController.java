/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caiseapp;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 *
 * @author macbookpro
 */
public class FXMLDocumentController implements Initializable {
    Connection conn;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) {
        out.println(msg);
        String resp=null;
        try {
            resp = in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resp;
    }

    public void stopConnection() {
        try {
            in.close();
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.close();
        
    }
    int id = 0;
    
                @FXML
    private AnchorPane left;
                
    @FXML
    private HBox hBoxLeft;
    @FXML
    private TableView<Table> tableItems;
    @FXML
    private TableColumn<Table, JFXCheckBox> selectAllColumn;
    @FXML
    private TableColumn<Table, Integer> quantityCol;
    @FXML
    private TableColumn<Table, String> ItemCol;
    @FXML
    private TableColumn<Table, Double> priceCol;
    @FXML
    private TableColumn<Table, Double> amountCol;
    @FXML
    private JFXTextField searchbyIdTextField;
    
    @FXML
    private Label amountLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FXMLDocumentController client = new FXMLDocumentController();
        try {
            client.startConnection("192.168.1.97", 60001);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String response = client.sendMessage("21");
        
        try {
            // TODO
            getConnection();
        } catch (InstantiationException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
}
     public void getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        try {
            String userName = "root";
            String password = "root";
            String url = "jdbc:mysql://192.172.1.119:8001/foodeasy";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);
            System.err.println("--CONNECTED--");
        } catch ( SQLException e) {
            System.err.println(e.getMessage());
            System.err.println("--NOT CONNECTED--");
        }
    }
     
     

    //-------------------------------  DATA BASE CONNECTION METHODE -----------------------------------------//

    //-----------------------------  DATA BASE SELECT METHODE --------------------------------------//

    public ObservableList<Table> getItems(int id){
        ObservableList<Table> items = FXCollections.observableArrayList();
        String sql = "SELECT orders.id ,orders.`status`,orders.`comment`,orders.created_at ,orders.amount,"
                + " item_orders.variation_id,item_orders.properties,item_orders.quantity,item_orders.price,"
                + "item_orders.comment,items.name FROM item_orders,items,orders"
                + " WHERE orders.id=item_orders.order_id and items.id=item_orders.item_id AND item_orders.order_id="+id+";";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new Table(rs.getString(11),rs.getString(4),rs.getInt(8),rs.getDouble(9),rs.getDouble(5)));
                InsertInTable();
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.err.println("query is not excuted ! ");
        }
        return items;
    }
    
    public void InsertInTable(){
        
        ObservableList<Table> list = getItems(search());
        selectAllColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Table, JFXCheckBox>, ObservableValue<JFXCheckBox>>) new JFXCheckBox());
        quantityCol.setCellValueFactory(new PropertyValueFactory<Table,Integer>("quantity"));
        ItemCol.setCellValueFactory(new PropertyValueFactory<Table,String>("itemName"));
        priceCol.setCellValueFactory(new PropertyValueFactory<Table,Double>("price"));
        amountCol.setCellValueFactory(new PropertyValueFactory<Table,Double>("amount"));
        tableItems.setItems(list);

        
    }   

    @FXML
    private int search(){
      
        try {
            id = Integer.valueOf(searchbyIdTextField.getText());
            System.out.println("order ID is "+id);
        } catch (Exception e) {
            System.out.println("No DATA entred !!!!!");
        }
        searchbyIdTextField.clear();
        getItems(id);

        return id;
    
    }
}
