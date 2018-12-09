package View;

import Controller.Controller;
import Model.Excpetions.SearcherException;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainPageView implements Initializable {
    public Button Browse_s; //browse button to load save path
    public Button Browse_w; //browse button to load working path
    public Button parse_b; //parse button
    public Button reset_b; //reset button
    public Button load_b; //load dictionary button
    public Button showdic_b; //show dictionary button
    public CheckBox stemming_option; //stemming checkbox
    public TextField work_path; //text field for working path
    public TextField save_path; //text field for saving path
    public SplitMenuButton splitMenuButton; //language menu
    protected boolean checkbox_value = false; //start checkbox as false, if marked change to true
    private Controller controller = Controller.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    //update work path textfield
    public void Browse_working(ActionEvent actionEvent) {
        UpdateTextField(work_path);
    }

    //update save path textfield
    public void Browse_saving(ActionEvent actionEvent) {
        UpdateTextField(save_path);
    }

    private void UpdateTextField(TextField save_path) {
        try {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose Folder");
            File selectedDirectory = chooser.showDialog(null);
            save_path.setText(selectedDirectory.getPath());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Please select a file");
            alert.showAndWait();
        }
    }

    //checkbox for stemming, updates the boolean value accordingly
    public void checkBox_stemmimg(ActionEvent actionEvent) {
        checkbox_value = stemming_option.isSelected();
    }

    public void merge_func(ActionEvent actionEvent) {
        String workPath = work_path.getText();
        String savePath = save_path.getText();
        controller.merg_func(workPath, savePath);
    }

    //parse function
    public void parse(ActionEvent actionEvent) {
        try {
            String workPath = work_path.getText();
            String savePath = save_path.getText();
            //if path left empty
            if (savePath.equals("") || workPath.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Please select a file in save and work path");
                alert.showAndWait();
            } else {
                double t = System.currentTimeMillis();
                controller.parse(workPath, savePath, checkbox_value);
//                double y = System.currentTimeMillis() - t;
//                System.out.println("total time to parse = " + ((y) / 1000) + " seconds");
//                double l = System.currentTimeMillis();
                controller.writeLastDocsToDisk(savePath);
                controller.mergePartialPosting(workPath, savePath);
                int coutIndexed = controller.getNumberOfIndexed();
                int uniqterms=controller.getDicSize();
//                System.out.println("total time to merge = " + ((System.currentTimeMillis() - l) / 1000) + " seconds");
//                System.out.println("total time to run program = " + ((System.currentTimeMillis() - t) / 1000) + " seconds");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Done");
                alert.setHeaderText("total time to run program : " + ((System.currentTimeMillis() - t) / 1000) + " seconds. \n" +
                        "total number of parsed documents is : "+coutIndexed);
                alert.showAndWait();
            }
        } catch (SearcherException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //reset function to clear post file and dictionary
    public void reset(ActionEvent actionEvent) {
        try {
            String savePath = save_path.getText();
            if (savePath.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Please select a file in save and work path");
                alert.showAndWait();
            } else
                controller.resetButton(savePath);
        } catch (SearcherException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();

        }

    }

    public void show_dic(ActionEvent actionEvent) {
        try {
            String savePath = save_path.getText();
            if (savePath.equals("")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Save Path does not have Dictionary file");
                alert.showAndWait();
            } else
                controller.showDic(savePath);
        } catch (SearcherException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();

        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fail");
            alert.setHeaderText("Dictionary does not exist");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //        Stage s = (Stage) showdic_b.getScene().getWindow();
//        s.close();
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("Show_Dic.fxml"));
//            Stage stage = new Stage();
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.setResizable(true);
//            stage.setTitle("Show Dictionary");
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void load_dic(ActionEvent actionEvent) {
        try {
            controller.loadDic(save_path.getText());
        } catch (SearcherException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(e.getMessage() + " to load dictionary");
            alert.showAndWait();

        } catch (RuntimeException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fail");
            alert.setHeaderText(" Dictionary does not exist");
            alert.showAndWait();
        }
    }
}


