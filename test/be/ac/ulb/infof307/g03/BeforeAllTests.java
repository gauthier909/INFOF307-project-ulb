package be.ac.ulb.infof307.g03;

import be.ac.ulb.infof307.g03.database.CreateTables;
import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;

public class BeforeAllTests{

    @BeforeAll
    static void beforeAllInit() {

        try {
            Database.init("TestDataBase.db");
            CreateTables.createUserTable();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashedPwdUser1 = encoder.encode("strongpassword123");
            String hashedPwdUser2 = encoder.encode("groupe03LESBESTS");
            String hashedPwdUser3 = encoder.encode("osef");
            String insertFred = "INSERT OR IGNORE INTO user VALUES(1, 'Fred', '"+  hashedPwdUser1 +"', '','', 'fred@gmail.com', 0);";
            String insertGian = "INSERT OR IGNORE INTO user VALUES(2, 'GianMarco', '"+  hashedPwdUser2 +"', '', '', 'email@gmail.com', 0);";
            String insertJaco = "INSERT OR IGNORE INTO user VALUES(3, 'Jacopo', '"+  hashedPwdUser3 +"', '', '', 'e@e.be', 0);";
            Database.getPreparedStatement(insertFred).executeUpdate();
            Database.getPreparedStatement(insertGian).executeUpdate();
            Database.getPreparedStatement(insertJaco).executeUpdate();
            Database.commitStatement();
            User currentUser = UserGetterDao.getUserFromDb("Fred", "strongpassword123");
            Helper.setCurrentUser(currentUser);
        } catch (DaoException | SQLException e) {
            try {
                Database.rollbackStatement();
            } catch (DaoException daoException) {
                daoException.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
