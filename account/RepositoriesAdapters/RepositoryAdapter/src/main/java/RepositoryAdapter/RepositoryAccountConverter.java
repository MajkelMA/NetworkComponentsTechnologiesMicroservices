package RepositoryAdapter;

import DataModel.AccountEnt;
import DataModel.RoleEnt;
import DomainModel.Account;
import DomainModel.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RepositoryAccountConverter {

    public static Account convertTo(AccountEnt obj) {
        if (obj == null)
            return null;
        List<Role> roles = new ArrayList<Role>();
        for (RoleEnt item : obj.getRoles()) {
            roles.add(new Role(item.getName()));
        }

        return new Account(
                UUID.fromString(obj.getId()),
                obj.getLogin(),
                obj.getPassword(),
                obj.getFullName().split(" ")[0],
                obj.getFullName().split(" ")[1],
                obj.isActive(),
                roles
        );
    }

    public static AccountEnt convertFrom(Account obj, List<RoleEnt> list) {
        if (obj == null || list == null)
            return null;

        return new AccountEnt(
                obj.getAccountId().toString(),
                obj.getLogin(),
                obj.getPassword(),
                obj.getName() + " " + obj.getSurname(),
                obj.isActive(),
                list
        );
    }
}
