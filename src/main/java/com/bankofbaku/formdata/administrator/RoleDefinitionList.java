package com.bankofbaku.formdata.administrator;

import com.bankofbaku.formdata.FormItem;
import com.bankofbaku.model.Role;
import com.bankofbaku.model.RoleDefinition;
import com.bankofbaku.model.User;
import com.bankofbaku.record.CrudBase;
import com.bankofbaku.record.IRecordItem;
import com.bankofbaku.record.Record;
import com.bankofbaku.record.RecordSetFactory;
import com.bankofbaku.record.annotation.RecordPolicy;
import com.bankofbaku.record.annotation.RecordPolicy.DefaultPrefix;
import com.bankofbaku.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class RoleDefinitionList extends FormItem {
    private static final RecordSetFactory recordFactory = new RecordSetFactory(RoleDefinitionRecord.class);

    @RecordPolicy(defaultPrefix = DefaultPrefix.NONE)
    public static class RoleDefinitionRecord extends Record {
        private static final long serialVersionUID = -145218917599010753L;
        private RoleDefinition roleDefinition;

        public RoleDefinition getRoleDefinition() {
            return roleDefinition;
        }
    }

    public static class RoleDefinitionCrud extends CrudBase {
        private final UserService userService;
        private Record userRecord;

        public RoleDefinitionCrud(UserService userService, Record userRecord) {
            this.userService = userService;
            this.userRecord = userRecord;
        }

        @Override
        public List<IRecordItem[]> read() {
            List<IRecordItem[]> recordItems = new ArrayList<IRecordItem[]>();
            List<RoleDefinition> roleDefinitions = null;
            if (userRecord != null)
                roleDefinitions = userService.getRoleDefinitionsByUser((User) userRecord.getRecordItem(User.class));
            else
                roleDefinitions = userService.getAllRoleDefinitions();

            for (RoleDefinition roleDefinition : roleDefinitions)
                recordItems.add(new IRecordItem[]{roleDefinition});

            return recordItems;
        }

        @Override
        public void delete(Record record) {
            userService.deleteRole(userRecord.getRecordItem(User.class), record.getRecordItem(RoleDefinition.class));
        }

        @Override
        public void create(Record record) {
            Role role = new Role();
            role.setUser(userRecord.getRecordItem(User.class));
            role.setRoleDefinition(record.getRecordItem(RoleDefinition.class));
            userService.saveOrUpdateRole(role);
        }
    }

    public RoleDefinitionList(UserService userService, Record userRecord) {
        super(recordFactory.create(new RoleDefinitionCrud(userService, userRecord)));
    }

    public RoleDefinitionList(UserService userService) {
        super(recordFactory.create(new RoleDefinitionCrud(userService, null)));
    }
}