package com.bankofbaku.formdata.administrator;

import com.bankofbaku.formdata.FormItem;
import com.bankofbaku.model.User;
import com.bankofbaku.model.User.Status;
import com.bankofbaku.record.ICrud;
import com.bankofbaku.record.IRecordItem;
import com.bankofbaku.record.Record;
import com.bankofbaku.record.RecordSetFactory;
import com.bankofbaku.record.annotation.RecordPolicy;
import com.bankofbaku.record.annotation.RecordPolicy.DefaultPrefix;
import com.bankofbaku.service.UserService;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static com.bankofbaku.formdata.administrator.UserGrid.UserRecord.UserExtra;

public class UserGrid extends FormItem {
    private static final RecordSetFactory recordFactory = new RecordSetFactory(UserRecord.class);

    @RecordPolicy(defaultPrefix = DefaultPrefix.NONE)
    public static class UserRecord extends Record {
        private static final long serialVersionUID = -145218917599010753L;
        private User user;
        private UserExtra userExtra;

        public static class UserExtra implements IRecordItem {
            @Override
            public UserExtra clone() throws CloneNotSupportedException {
                return (UserExtra) super.clone();
            }

            private String password;

            public UserExtra(Integer employeeId) {
                this.password = "";
            }

            public UserExtra() {
                this.password = "";
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

        }

        public UserExtra getUserExtra() {
            return this.userExtra;
        }

        public User getUser() {
            return this.user;
        }

    }

    public static class UserCrud implements ICrud {
        private final UserService userService;
        StandardPasswordEncoder passwordEncoder;

        public UserCrud(UserService userService, StandardPasswordEncoder passwordEncoder) {
            this.userService = userService;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void update(Record record) {
            User user = record.getRecordItem(User.class);
            UserExtra userExtra = record.getRecordItem(UserExtra.class);
            String password = userExtra.getPassword();
            if (password != null && password.length() >= 8)
                user.setPassword(passwordEncoder.encode(password));

            userService.saveOrUpdateUser(user);
        }

        @Override
        public List<IRecordItem[]> read() {
            List<IRecordItem[]> recordItems = new ArrayList<IRecordItem[]>();
            List<User> users = userService.getAllUsers();

            for (User user : users)
                recordItems.add(new IRecordItem[]{user});

            return recordItems;
        }

        @Override
        public void delete(Record record) {
            User user = record.getRecordItem(User.class);
            user.setStatus(Status.DELETED);
            userService.saveOrUpdateUser(user);
        }

        @Override
        public void create(Record record) {
            User user = record.getRecordItem(User.class);
            UserExtra userExtra = record.getRecordItem(UserExtra.class);
            String password = userExtra.getPassword();
            if (password != null && password.length() >= 8)
                user.setPassword(passwordEncoder.encode(password));

            userService.saveOrUpdateUser(user);
        }
    }

    public UserGrid(UserService userService, StandardPasswordEncoder passwordEncoder) {
        super(recordFactory.create(new UserCrud(userService, passwordEncoder)));
    }
}