package gov.iti.jets.presentation.controllers;

import gov.iti.jets.commons.dtos.ContactDto;
import gov.iti.jets.commons.dtos.LoginDto;
import gov.iti.jets.presentation.customcontrols.AddContactTextField;
import gov.iti.jets.presentation.models.ContactModel;
import gov.iti.jets.presentation.models.UserModel;
import gov.iti.jets.presentation.util.ModelFactory;
import gov.iti.jets.presentation.util.StageCoordinator;
import gov.iti.jets.services.AddContactDao;
import gov.iti.jets.services.LoginDao;
import gov.iti.jets.services.util.DaoFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddContactController implements Initializable {
    private StageCoordinator stageCoordinator = StageCoordinator.getInstance();
    private ModelFactory modelFactory = ModelFactory.getInstance();
    private final DaoFactory daoFactory = DaoFactory.getInstance();
    private final AddContactDao addContactDao = daoFactory.getAddContactService();

    private UserModel userModel = modelFactory.getUserModel();
    private List<TextField> phoneNumberTextField = new ArrayList<>();
    private List<String> phoneNumbers = new ArrayList<>();

    @FXML
    private Button addContactButton;

    @FXML
    private VBox contactsVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contactsVBox.getChildren().add(new AddContactTextField(contactsVBox, phoneNumberTextField, addContactButton));
    }

    @FXML
    void onAddButtonAction(ActionEvent event) {
        getPhoneNumbers();
        if(isFriend())
            stageCoordinator.showErrorNotification( "Already friend ." );
        else{
             ContactDto contactDto = new ContactDto(userModel.getPhoneNumber(), phoneNumbers);
        try {
            boolean addContacts = addContactDao.addContacts(contactDto);
            if(addContacts){
            stageCoordinator.switchToMainScene();
            } else {
                stageCoordinator.showErrorNotification( "Invalid phone number or password." );
            }
        } catch (NotBoundException | RemoteException e) {
            stageCoordinator.showErrorNotification( "Failed to connect to server. Please try again later." );
            e.printStackTrace();
        }
            clear();

        }

    }

    @FXML
    void onCancelHyperLinkClick(ActionEvent event) {
        stageCoordinator.closeAddContactStage();
    }

    private boolean isFriend(){
        for (String contact : phoneNumbers){
            for (ContactModel contactModel: userModel.getContacts()){
                if(contact.equals(contactModel.getPhoneNumber()))
                    return true;
            }
        }
        return false;
    }
    private void getPhoneNumbers(){
        for (TextField textField: phoneNumberTextField) {
            if (!textField.getText().equals("") && !phoneNumbers.contains(textField.getText()))
                phoneNumbers.add(textField.getText());
        }
    }
    private void clear(){
        for (TextField textField: phoneNumberTextField)
            contactsVBox.getChildren().remove(textField);
        contactsVBox.getChildren().add(new AddContactTextField(contactsVBox, phoneNumberTextField, addContactButton));

    }

}
