package reservasajedrez;

import reservasajedrez.model.Usuari;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;


public class UsuarisController implements Initializable {

    @FXML private TableView<Usuari> tablaUsuaris;
    @FXML private TableColumn<Usuari, String> colId;
    @FXML private TableColumn<Usuari, String> colNom;
    @FXML private TableColumn<Usuari, String> colRol;
    @FXML private TableColumn<Usuari, String> colEmail;
    private ObservableList<Usuari> listaUsuarios = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        

        obtenerUsuariosDesdeAPI();
    }    
    
    private void obtenerUsuariosDesdeAPI() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/jugadors/cerca"))
                .header("Accept", "application/json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {

                    JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
                    listaUsuarios.clear();

                    for (JsonElement elemento : jsonArray) {
                        JsonObject obj = elemento.getAsJsonObject();
                        listaUsuarios.add(new Usuari(
                            obj.get("id").getAsString(),
                            obj.get("nom").getAsString(),
                            obj.get("email").getAsString(),
                            obj.get("role").getAsString()
                            
                        ));
                    }

                    Platform.runLater(() -> tablaUsuaris.setItems(listaUsuarios));
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
    
    @FXML
private void eliminarUsuari() {
    Usuari seleccionat = tablaUsuaris.getSelectionModel().getSelectedItem();
    
    if (seleccionat == null) {
        mostrarAlerta("Atenció", "Selecciona un usuari per eliminar.");
        return;
    }

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/jugadors/delete" + seleccionat.getId()))
            .DELETE()
            .build();

    client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                Platform.runLater(() -> {
                    listaUsuarios.remove(seleccionat);
                    mostrarAlerta("Èxit", "Usuari eliminat correctament.");
                });
            });
}

@FXML
private void ferAdmin() {
    Usuari seleccionat = tablaUsuaris.getSelectionModel().getSelectedItem();
    
    if (seleccionat == null) {
        mostrarAlerta("Atenció", "Selecciona un usuari.");
        return;
    }


    String json = "{\"role\":\"ADMIN\"}";

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/jugadors/updateRole/" + seleccionat.getId()))
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(json)) 
            .build();

    client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                Platform.runLater(() -> {
                    obtenerUsuariosDesdeAPI(); 
                    mostrarAlerta("Èxit", seleccionat.getUsername() + " ara es ADMIN.");
                });
            });
}

private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
}
