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

    /**
     * @param title      - text to put in alert title
     * @param headerText - text to put in alert body
     *                   function to create alerts
     */
    public void doAlert(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    /**
     * function to create list view of querys and what docs they have
     */

    public void start() {
        try {
            //get results in string
            List<String> Qresult = controller.getQResult();
            //make sure its not empty
            if (Qresult.size() > 0) {
                String[] words = Qresult.get(0).split("\\s");
                String lastowrd = words[0];
                String ConnectedDocs = "";
                //do it nicly with Q-->doc|doc|doc
                for (int i = 0; i < Qresult.size(); i++) {
                    words = Qresult.get(i).split("\\s");
                    if (lastowrd.equals(words[0])) {
                        ConnectedDocs += words[2] + " | ";
                    } else {
                        list.getItems().add(lastowrd + " --> " + ConnectedDocs);
                        ConnectedDocs=words[2] + " | ";
                    }
                    lastowrd = words[0];
                }
                list.getItems().add(lastowrd + " --> " + ConnectedDocs);
            }
        } catch (IOException e) {
            doAlert("Error", "Problem O.O");
        } catch (BadPathException e) {
            doAlert("Error", "Problem with path, please make sure you ran the\nQuery file and saved it somewhere");
        }
    }
}