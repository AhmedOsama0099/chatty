package gov.iti.jets.commons.remoteinterfaces;

import gov.iti.jets.commons.dtos.AddContactDto;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AddContactService extends Remote {
    boolean addContacts( AddContactDto AddContactDto ) throws RemoteException;
}
