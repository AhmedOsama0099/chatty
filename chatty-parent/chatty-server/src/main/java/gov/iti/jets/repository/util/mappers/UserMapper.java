package gov.iti.jets.repository.util.mappers;

import gov.iti.jets.commons.dtos.UserDto;
import gov.iti.jets.repository.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ContactMapper.class, ContactMapper.class, InvitationMapper.class, GroupChatMapper.class, InvitationMapper.class, UserStatusMapper.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserEntity userDtoToEntity(UserDto userDto);
    UserDto userEntityToDto(UserEntity userEntity);
}