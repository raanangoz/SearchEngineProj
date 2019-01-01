package View;

import Controller.Controller;
import Model.Excpetions.BadPathException;
import Model.Excpetions.SearcherException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class MainPageView implements Initializable {

    //<editor-fold desc="buttons">
    public Button Browse_s; //browse button to load save path
    public Button Browse_w; //browse button to load working path
    public Button parse_b; //parse button
    public Button reset_b; //reset button
    public Button load_b; //load dictionary button
    public Button showdic_b; //show dictionary button
    public Button showQR;
    public Button get_entities;
    public CheckBox stemming_option; //stemming checkbox
    public CheckBox semantic_option; //semantic checkbox
    public TextField work_path; //text field for working path
    public TextField save_path; //text field for saving path
    public TextField query_path; //query path
    public TextField query_text; //query text
    public TextField entities_text; //entities text
    public SplitMenuButton LangaugeButton; //language menu
    public SplitMenuButton cityMenu; //language menu

    List<CheckMenuItem> allCityList = new ArrayList<>(); //list of citys
    List<CheckMenuItem> languageDocList = new ArrayList<>(); //list of citys
    public boolean checkRunAgain = false;
    public boolean checkRunAgain2 = false;
    public Menu menu;
    protected boolean checkbox_stemming = false; //start checkbox as false, if marked change to true
    protected boolean checkbox_semantic = false; //start checkbox as false, if marked change to true

    //</editor-fold>

    private Controller controller = Controller.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cityMenu.setText("Cities");
    }

    //update work path textfield
    public void Browse_working(ActionEvent actionEvent) {
        UpdateTextField(work_path);
    }

    //update save path textfield
    public void Browse_saving(ActionEvent actionEvent) {
        UpdateTextField(save_path);
    }

    public void doAlert(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    //update query path textfield
    public void Browse_query(ActionEvent actionEvent) {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose Folder");
            File selectedDirectory = chooser.showOpenDialog(null);
            query_path.setText(selectedDirectory.getPath());
        } catch (Exception e) {
            doAlert("Error", "Please select a folder");
        }
    }

    //gets path from filechoser and updates the field text
    private void UpdateTextField(TextField Path) {
        try {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose Folder");
            File selectedDirectory = chooser.showDialog(null);
            Path.setText(selectedDirectory.getPath());
        } catch (Exception e) {
            doAlert("Error", "Please select a folder");
        }
    }

    //checkbox for stemming, updates the boolean value accordingly
    public void checkBox_stemmimg(ActionEvent actionEvent) {
        checkbox_stemming = stemming_option.isSelected();
    }

    //checkbox for semantic, updates the boolean value accodingly
    public void checkBox_semantic(ActionEvent actionEvent) {
        checkbox_semantic = semantic_option.isSelected();
        if (checkbox_semantic == true) {
            stemming_option.setSelected(true);
            checkBox_stemmimg(actionEvent);
        }
    }


    //merge function
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
                doAlert("Error", "Please select a folder");
            } else {
                double t = System.currentTimeMillis();
                controller.parse(workPath, savePath, checkbox_stemming);
//                double y = System.currentTimeMillis() - t;
//                System.out.println("total time to parse = " + ((y) / 1000) + " seconds");
//                double l = System.currentTimeMillis();
                controller.writeLastDocsToDisk(savePath);
                controller.mergePartialPosting(workPath, savePath);
                controller.writeEntitiesToDisk(workPath, savePath);

                HashMap<String, String> countryDocsList = controller.getCountryList();
                HashMap<String, String> languageDocList = controller.getlanguageDocList();
                city_pick(countryDocsList);
                language_pick(languageDocList);

                int coutIndexed = controller.getNumberOfIndexed();
                int uniqterms = controller.getDicSize();
//                System.out.println("total time to merge = " + ((System.currentTimeMillis() - l) / 1000) + " seconds");
//                System.out.println("total time to run program = " + ((System.currentTimeMillis() - t) / 1000) + " seconds");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Done");
                alert.setHeaderText("total time to run program : " + ((System.currentTimeMillis() - t) / 1000) + " seconds. \n" +
                        "total number of parsed documents is : " + coutIndexed);
                alert.showAndWait();
            }
        } catch (SearcherException e) {
            doAlert("Error", e.getMessage());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //reset function to clear post file and dictionary
    public void reset(ActionEvent actionEvent) {
        try {
            String savePath = save_path.getText();
            if (savePath.equals("")) {
                doAlert("Error", "Please select a folder in save and work path");
            } else
                controller.resetButton(savePath);
        } catch (SearcherException e) {
            doAlert("Done", "Reset Succsefully");
        }
    }

    //show dictionary function
    public void show_dic(ActionEvent actionEvent) {
        try {
            String savePath = save_path.getText();
            if (savePath.equals("")) {
                doAlert("Error", "Save Path does not have Dictionary file");
            } else
                controller.showDic(savePath);
        } catch (SearcherException e) {
            AlertLoadDic();
        } catch (RuntimeException e) {
            doAlert("Fail", "Dictionary does not exist");
        } catch (IOException e) {
            doAlert("Fail", "Failed");

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

    //load dictionary
    public void load_dic(ActionEvent actionEvent) {
        if (AlertLoadDic() == true) {
            doAlert("Success", "Loaded dictionary");
        }
    }


    //run query from text
    public void run_query(ActionEvent actionEvent) {

        String queryText = query_text.getText();
        String workPath = work_path.getText();
        String savePath = save_path.getText();
        //if path left empty
        if (savePath.equals("") || workPath.equals("") || queryText.equals("")) {
            doAlert("Error", "Please select a folder in save and work path");
        } else {
            try {
                List<String> chosenCities = getCountryForSearch(allCityList);
                boolean tosave;
                String saveFolder = "";
                Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save the results?",
                        ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = alertConfirm.showAndWait();
                if (result.get() == ButtonType.YES) {
                    DirectoryChooser chooser = new DirectoryChooser();
                    chooser.setTitle("Choose Folder");
                    File selectedDirectory = chooser.showDialog(null);
                    saveFolder = (selectedDirectory.getPath());
                    tosave = true;
                } else {
                    tosave = false;
                }
                boolean check = AlertLoadDic();
                if (check==false)
                    throw new IOException();
                controller.runQueryString(queryText, workPath, savePath, checkbox_semantic, checkbox_stemming, chosenCities, tosave, saveFolder);
                if (tosave == true) {
                    doAlert("Done", "Query ran successfully.\n Open results.txt file to see them.");
                } else {
                    doAlert("Done", "Done");
                }
            } catch (IOException e) {
                doAlert("Error", "Failed to run Query");
            } catch (BadPathException e) {
                doAlert("Error", "Bad Path Selected.");
            }
        }
    }

    //run query from file
    public void run_query_file(ActionEvent actionEvent) {

        String queryText = query_path.getText();
        String workPath = work_path.getText();
        String savePath = save_path.getText();
        if (savePath.equals("") || workPath.equals("") || query_path.equals("")) {
            doAlert("Error", "Please select a folder in save and work path");
        }
        List<String> chosenCities = getCountryForSearch(allCityList);
        boolean tosave;
        String saveFolder = "";
        try {
            boolean check = AlertLoadDic();
            if (check==false)
                throw new IOException();
            Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save the results?",
                    ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alertConfirm.showAndWait();
            if (result.get() == ButtonType.YES) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Choose Folder");
                File selectedDirectory = chooser.showDialog(null);
                saveFolder = (selectedDirectory.getPath());
                tosave = true;
            } else {
                tosave = false;
            }
            controller.runQueryFile(queryText, workPath, savePath, checkbox_semantic, checkbox_stemming, chosenCities, tosave, saveFolder);
            if (tosave == true) {
                doAlert("Done", "Query ran successfully.\n Open results.txt file to see them.");
            } else {
                doAlert("Done", "Query ran successfully.");
            }
        } catch (IOException e) {
            doAlert("Error", "Failed to run Query");
        } catch (BadPathException e) {
            doAlert("Error", e.getMessage() + "of Query");
        } catch (RuntimeException e) {
            doAlert("Error", "No folder has been selected");
        }
    }

    private boolean AlertLoadDic() {
        try {
            controller.loadDic(save_path.getText());
            return true;

        } catch (SearcherException e) {
            doAlert("Error", e.getMessage());
            return false;

        } catch (RuntimeException | IOException e) {
            doAlert("Error", "Dictionary does not exist");
            return false;
        }
    }


    public void language_pick(HashMap<String, String> languageList) {
        for (Map.Entry<String, String> entry : languageList.entrySet())
            languageDocList.add(new CheckMenuItem(entry.getKey()));
        if (checkRunAgain2 == false)
            LangaugeButton.getItems().addAll(languageDocList);
        checkRunAgain2 = true;
//        if((CheckMenuItem)cityMenu.getItems().get(1))
    }

    public void city_pick(HashMap<String, String> cityListDoc) {
        for (Map.Entry<String, String> entry : cityListDoc.entrySet())
            allCityList.add(new CheckMenuItem(entry.getKey()));
        if (checkRunAgain == false)
            cityMenu.getItems().addAll(allCityList);
        checkRunAgain = true;
//        if((CheckMenuItem)cityMcity_pickenu.getItems().get(1))
    }

    //return selected countrys
    public List<String> getCountryForSearch(List<CheckMenuItem> allCountryList) {
        List<String> selectedCountrys = new LinkedList<>();
        for (int i = 0; i < allCountryList.size(); i++) {
            if (allCountryList.get(i).isSelected() == true)
                selectedCountrys.add(allCountryList.get(i).getText());
        }
//        for (int i = 0; i < selectedCountrys.size(); i++)
//            System.out.println(selectedCountrys.get(i));
        return selectedCountrys;
    }

    public void load_cities(ActionEvent actionEvent) {
        try {
            List<String> selectedCountrys = controller.loadCity(save_path.getText());
            allCityList.clear();
            for (int i = 0; i < selectedCountrys.size(); i++)
                allCityList.add(new CheckMenuItem(selectedCountrys.get(i)));
            cityMenu.getItems().removeAll();
            cityMenu.getItems().clear();
//            cityMenu.getItems().remove(0,5);
            cityMenu.getItems().addAll(allCityList);
            doAlert("Sucsses", "Loaded city files");
        } catch (IOException e) {
            doAlert("Error", "city file does not exist");
        }

    }

    public void get_Entities(ActionEvent actionEvent) {
        try {
            HashMap<String, String> allEntities = controller.getEntities(save_path.getText());
            String entity_text = entities_text.getText();
            String Entities = allEntities.get(entity_text);
            String words[] = Entities.split("@");
            Entities = "";
            for (int i = 0; i < words.length; i++)
                Entities += words[i] + " \n";
            doAlert("Entities", Entities);
        } catch (IOException e) {
            doAlert("Error", "docEntities file does not exist");
        } catch (RuntimeException e) {
            doAlert("Error", "Error");
        }
    }

    public void load_lanugage(ActionEvent actionEvent) {
        try {
            List<String> selectedLang = controller.loadLang(save_path.getText());
            languageDocList.clear();
            for (int i = 0; i < selectedLang.size(); i++)
                languageDocList.add(new CheckMenuItem(selectedLang.get(i)));
            LangaugeButton.getItems().removeAll();
            LangaugeButton.getItems().clear();
//            cityMenu.getItems().remove(0,5);
            LangaugeButton.getItems().addAll(languageDocList);
            doAlert("Sucsses", "Loaded language files");
        } catch (IOException e) {
            doAlert("Error", "language file does not exist");
        } catch (RuntimeException e) {
            doAlert("Error", "file does not exthist");
        }
    }

    public void newwindow(ActionEvent actionEvent){
        Stage s = (Stage) showQR.getScene().getWindow();
//        s.close();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("QR.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Query Result");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}