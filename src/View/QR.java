package View;

import Controller.Controller;
import Model.Excpetions.BadPathException;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class QR implements Initializable {

    public ListView<String> list;

    private Controller controller = Controller.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        start();
    }


    public void doAlert(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    public void start() {
        try {
            List<String> Qresult = controller.getQResult();
            if (Qresult.size() > 0) {
                String[] words = Qresult.get(0).split("\\s");
                String lastowrd = words[0];
                String ConnectedDocs = "";
                for (int i = 0; i < Qresult.size(); i++) {
                    words = Qresult.get(i).split("\\s");
                    if (lastowrd.equals(words[0])) {
                        ConnectedDocs += words[2] + " | ";
                    } else
                        list.getItems().add(words[0] + " --> " + ConnectedDocs);
                    lastowrd = words[0];
                }
            }

        } catch (IOException e) {
            doAlert("Error", "Problem O.O");
        } catch (BadPathException e) {
            doAlert("Error", "Problem with path, please make sure you ran the\nQuery file and saved it somewhere");
        }

    }


}