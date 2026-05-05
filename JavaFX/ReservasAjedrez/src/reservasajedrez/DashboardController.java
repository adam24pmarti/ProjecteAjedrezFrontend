package reservasajedrez;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mostrarEstadisticas();
    }

    @FXML
    private void mostrarReserves() {
        cargarVista("/reservasajedrez/ReservesTable.fxml");
    }

    @FXML
    private void mostrarMesas() {
        cargarVista("/reservasajedrez/MesaTable.fxml");
    }

    @FXML
    private void mostrarUsuaris() {
        cargarVista("/reservasajedrez/Usuaris.fxml");
    }

    @FXML
    private void mostrarEstadisticas() {
        cargarVista("/reservasajedrez/Stats.fxml");
    }

    private void cargarVista(String fxml) {
        try {
            contentArea.getChildren().clear();
            Parent view = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
