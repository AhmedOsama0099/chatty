package gov.iti.jets.network;

import gov.iti.jets.commons.callback.Client;
import gov.iti.jets.commons.dtos.ContactDto;
import gov.iti.jets.commons.dtos.GroupChatDto;
import gov.iti.jets.commons.dtos.GroupMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.rmi.RemoteException;
import java.util.*;

public class Clients {
    private static final Clients INSTANCE = new Clients();
    private final Map<String, Client> clientMap = new HashMap<>();
    private final Map<Integer, List<Client>> groupMap = new HashMap<>();


    Logger logger = LoggerFactory.getLogger(Clients.class);

    public static Clients getInstance() {
        return INSTANCE;
    }

    public Optional<Client> getClient(String phoneNumber) {
        return Optional.ofNullable(clientMap.get(phoneNumber));
    }

    public List<Client> getGroupClients(int groupId) {
        return groupMap.get(groupId);
    }

    public void addGroup(GroupChatDto groupChatDto) throws RemoteException {
        logger.info("new group added: " + groupChatDto.getGroupChatId());
        List<Client> clients = getClients(groupChatDto.getGroupMembersList());
        groupMap.put(groupChatDto.getGroupChatId(), clients);
        addGroupTo(clients, groupChatDto);
        logger.info("Client that attempted to group: " + groupMap.get(groupChatDto.getGroupChatId()));
    }

    public List<Client> getClients(List<ContactDto> contactDtos) {
        List<Client> clients = new ArrayList<>();
        for (ContactDto contactDto : contactDtos) {
            Optional<Client> optionalClient = getClient(contactDto.getPhoneNumber());
            optionalClient.ifPresent(clients::add);

        }
        return clients;
    }

    public void addGroupTo(List<Client> clients, GroupChatDto groupChatDto) throws RemoteException {
        for (Client client : clients) {
            client.addGroupChat(groupChatDto);
        }
    }

    public void addMessagesTo(GroupChatDto groupChatDto, GroupMessageDto groupMessageDto) throws RemoteException {
        List<Client> clients = getClients(groupChatDto.getGroupMembersList());
        for (Client client : clients) {
            client.receiveGroupMessage(groupChatDto, groupMessageDto);

        }
    }


    // We add the phone number as an argument to avoid having to make a remote call to the client just to get their phone number!
    public void addClient(String phoneNumber, Client client) {
        logger.info("A client attempted to register its handler: " + phoneNumber);

        clientMap.put(phoneNumber, client);

        logger.info("Client that attempted: " + clientMap.get(phoneNumber));
    }

    public Optional<Client> removeClient(String phoneNumber) throws RemoteException {
        logger.info("A client attempted to log out: " + phoneNumber);
        if (clientMap.get(phoneNumber) != null){
            unregisterClientGroups(clientMap.get(phoneNumber));
        }
        return Optional.ofNullable(clientMap.remove(phoneNumber));
    }

    public void registerClientGroups(List<Integer> groupIds, Client client){
        for (Integer groupId:groupIds){
            if(groupMap.get(groupId) == null){
                List<Client> clientList = new ArrayList<>();
                clientList.add(client);
                groupMap.put(groupId, clientList);
            }
            groupMap.get(groupId).add(client);

        }
    }
    public void unregisterClientGroups(Client client){
        for (Integer groupId:groupMap.keySet()){
            if(groupMap.get(groupId).contains(client)){
                groupMap.get(groupId).remove(client);
            }
        }
    }

}
