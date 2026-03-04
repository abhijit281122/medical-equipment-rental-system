package allController;

import dbUtils.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {


    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginBtn;

    @FXML
    private Button close;

    // DATABASE TOOLS
    private Connection connect;


    /**
     * Initialize SQLite connection and run Flyway migrations
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connect = Database.getInstance().getConnection();
    }

    public void loginAdmin() {

        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try {
            PreparedStatement prepare = connect.prepareStatement(sql);
            prepare.setString(1, username.getText());
            prepare.setString(2, password.getText());

            ResultSet result = prepare.executeQuery();

            if (username.getText().isEmpty() || password.getText().isEmpty()) {

                new Alert(AlertType.ERROR, "Please fill all blank fields").showAndWait();
                return;
            }

            if (result.next()) {

                new Alert(AlertType.INFORMATION, "Successfully Login!").showAndWait();

                Stage stage = (Stage) loginBtn.getScene().getWindow();

                Parent root = FXMLLoader.load(
                        Objects.requireNonNull(
                                getClass().getResource("/com/equipment/rental/equipmentrentalsystem2025/Dashboard.fxml")
                        )
                );

                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.setTitle("Machine Rental System");
                stage.setMaximized(true);   // ✅ FULL SCREEN
                stage.centerOnScreen();

            } else {
                new Alert(AlertType.ERROR, "Wrong Username/Password").showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeApp() {
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        stage.close();
    }
}