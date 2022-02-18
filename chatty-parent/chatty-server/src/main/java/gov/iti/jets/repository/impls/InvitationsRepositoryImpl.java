package gov.iti.jets.repository.impls;

import gov.iti.jets.repository.ContactRepository;
import gov.iti.jets.repository.InvitationsRepository;
import gov.iti.jets.repository.entities.ContactEntity;
import gov.iti.jets.repository.util.ConnectionPool;
import gov.iti.jets.repository.util.RepositoryFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvitationsRepositoryImpl implements InvitationsRepository {
    @Override
    public Optional<List<ContactEntity>> getInvitations(String phoneNumber) {
        ContactRepository contactRepository = RepositoryFactory.getInstance().getContactRepository();
        Optional<List<ContactEntity>> optionalContactList = Optional.empty();

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement("select sender from invitations where receiver = ?");
        ) {
            statement.setString(1, phoneNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ContactEntity> contactList = new ArrayList<>();
                Optional<ContactEntity> contactEntity;
                while(resultSet.next()) {
                    contactEntity = contactRepository.getContact(resultSet.getString("sender"));
                    if (!contactEntity.isEmpty()) {
                        contactList.add(contactEntity.get());
                    }
                }
                    optionalContactList = Optional.of(contactList);
                }
            } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return optionalContactList;

    }
}