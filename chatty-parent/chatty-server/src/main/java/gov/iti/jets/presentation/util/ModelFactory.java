package gov.iti.jets.presentation.util;

import gov.iti.jets.presentation.models.DashboardDataModel;
import gov.iti.jets.presentation.models.UserModel;

public class ModelFactory {

    private static ModelFactory modelFactory = new ModelFactory();

    private UserModel userModel = new UserModel();
    private DashboardDataModel dashboardDataModel = new DashboardDataModel();

    private ModelFactory(){
        
    }

    public static ModelFactory getInstance() {
        return modelFactory;
    }

    public UserModel getUserModel(){
        return userModel;
    }

    public DashboardDataModel dashboardDataModel() {
        return dashboardDataModel;
    }
}
