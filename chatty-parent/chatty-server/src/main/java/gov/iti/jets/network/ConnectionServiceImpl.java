package gov.iti.jets.network;

import gov.iti.jets.commons.callback.Client;
import gov.iti.jets.commons.dtos.SingleMessageDto;
import gov.iti.jets.commons.dtos.StatusNotificationDto;
import gov.iti.jets.commons.dtos.UserDto;
import gov.iti.jets.commons.remoteinterfaces.ConnectionService;
import gov.iti.jets.repository.entities.SingleMessageEntity;
import gov.iti.jets.repository.entities.UserEntity;
import gov.iti.jets.repository.util.RepositoryFactory;
import gov.iti.jets.repository.util.mappers.SingleMessageMapper;
import gov.iti.jets.repository.util.mappers.UserMapper;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ConnectionServiceImpl extends UnicastRemoteObject implements ConnectionService {

    private static final RepositoryFactory repositoryFactory = RepositoryFactory.getInstance();
    private transient Clients clients = Clients.getInstance();
    protected ConnectionServiceImpl() throws RemoteException {
    }

    @Override
    public void registerClient(String phoneNumber, Client client) throws RemoteException {

        Optional<UserEntity> userEntity = repositoryFactory.getUserRepository().getUserByPhoneNumber(phoneNumber);

        UserDto userDto = UserMapper.INSTANCE.userEntityToDto(userEntity.get());

        // Must be called before loading messages from db!
        client.loadUserModel(userDto);

        Map<String,List<SingleMessageEntity>> messagesMapEntity = repositoryFactory.getSingleMessageRepository().getMessage(phoneNumber);
        Map<String,List<SingleMessageDto>> messagesMapDto = new HashMap<>();
        messagesMapEntity.forEach((k, v) -> {
            List<SingleMessageDto> messageDto = SingleMessageMapper.INSTANCE.entityListToDtoList(v);
            messagesMapDto.put(k,messageDto);
        });
        client.loadSingleMessages(messagesMapDto);

        // Must be called last!
        clients.addClient(phoneNumber,client);
    }

    @Override
    public void unregisterClient(String phoneNumber) throws RemoteException {
        clients.removeClient(phoneNumber);
    }

    @Override
    public void notifyOthersOfStatusUpdate( StatusNotificationDto statusNotificationDto, List<String> contactsToNotifyPhoneNumbers ) throws RemoteException {

        for (var contactPhoneNumber : contactsToNotifyPhoneNumbers){
            clients.getClient( contactPhoneNumber ).ifPresent( client -> {
                try {
                    client.notifyOfStatusUpdate( statusNotificationDto );
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } );
        }
    }

    @Override
    public List<String> getOfflineContacts( List<String> contactsPhoneNumbers ) throws RemoteException {

        List<String> offlineContactNumbers = new ArrayList<>();

        for (String phoneNumber : contactsPhoneNumbers){
           if( clients.getClient( phoneNumber ).isEmpty()){
               offlineContactNumbers.add( phoneNumber );
           }
        }

        return offlineContactNumbers;
    }
}
