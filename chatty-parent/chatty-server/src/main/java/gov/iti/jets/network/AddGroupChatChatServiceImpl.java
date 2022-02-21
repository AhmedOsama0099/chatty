package gov.iti.jets.network;

import gov.iti.jets.commons.dtos.AddGroupChatDto;
import gov.iti.jets.commons.dtos.GroupChatDto;
import gov.iti.jets.commons.remoteinterfaces.AddGroupChatService;
import gov.iti.jets.network.util.ImageDecoder;
import gov.iti.jets.network.util.ImageDecoderImpl;
import gov.iti.jets.repository.GroupChatRepository;
import gov.iti.jets.repository.entities.GroupChatEntity;
import gov.iti.jets.repository.util.RepositoryFactory;
import gov.iti.jets.repository.util.mappers.GroupChatMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

public class AddGroupChatChatServiceImpl extends UnicastRemoteObject implements AddGroupChatService {
    private Clients clients = Clients.getInstance();
    private GroupChatRepository groupChatRepository = RepositoryFactory.getInstance().getGroupChatRepository();
    private ImageDecoder imageDecoder = new ImageDecoderImpl();

    protected AddGroupChatChatServiceImpl() throws RemoteException {
    }

    @Override
    public boolean addGroup(AddGroupChatDto addGroupChatDto) throws RemoteException {
        GroupChatEntity groupChatEntity = GroupChatMapper.INSTANCE.addGroupChatDtoToEntity(addGroupChatDto);
        int groupId = groupChatRepository.addGroup(groupChatEntity);
        System.out.println("----------------------/n----------------------------");
        System.out.println(addGroupChatDto.getGroupChatPicture());
        try {
            Path currentRelativePath = Paths.get("");
            String picURL = currentRelativePath.toAbsolutePath().toString() + "/DB/group-pic/" + groupId + ".bmp";
            imageDecoder.save(addGroupChatDto.getGroupChatPicture(), picURL);
            groupChatEntity.setGroupChatPicture(picURL);
            groupChatRepository.updatePicture(groupId, picURL);

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (groupId != -1) {
            Optional<GroupChatEntity> addedGroupEntity = groupChatRepository.getById(groupId);
            if (addedGroupEntity.isPresent()) {
                GroupChatDto groupChatDto = GroupChatMapper.INSTANCE.groupChatEntityToDto(addedGroupEntity.get());

                clients.addGroup(groupChatDto);
            }
        }
        return groupId != -1;
    }
}
