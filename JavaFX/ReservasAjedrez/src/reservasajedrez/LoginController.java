package reservasajedrez;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Base64;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        validarUsuario(user, pass);
    }

    private void validarUsuario(String user, String pass) {

        String json = "{\"username\":\"" + user + "\", \"password\":\"" + pass + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auth/login")) // Ajusta tu URL
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    System.out.println("Respuesta del servidor: " + response);
                    analizarRespuesta(response);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void analizarRespuesta(String response) {
        try {
            System.out.println("Respuesta del servidor: " + response);
            String jwt = response.trim().replace("\"", "");

            String[] partes = jwt.split("\\.");
            if (partes.length < 2) {
                Platform.runLater(() -> mostrarAlerta("Error", "El servidor no envió un token válido."));
                return;
            }

            String payloadBase64 = partes[1];

            byte[] decodedBytes = Base64.getUrlDecoder().decode(payloadBase64);
            String payloadJson = new String(decodedBytes);

            System.out.println("Payload decodificado: " + payloadJson);

            JsonObject res = JsonParser.parseString(payloadJson).getAsJsonObject();

            if (res.has("role")) {
                String rol = res.get("role").getAsString();

                Platform.runLater(() -> {
                    if (rol.equalsIgnoreCase("ADMIN")) {
                        irAPantalla("/reservasajedrez/Dashboard.fxml", "Panel de Administración");
                    } else {
                        mostrarAlerta("Login Correcto", "Bienvenido " + res.get("sub").getAsString() + " (Rol: " + rol + ")");
                    }
                });
            }

        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO EN PROCESADO:");
            e.printStackTrace();

            Platform.runLater(() -> mostrarAlerta("Error", "Fallo al procesar respuesta: " + e.getMessage()));
        }
    }

    private void irAPantalla(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxml);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
