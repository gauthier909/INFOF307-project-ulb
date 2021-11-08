package be.ac.ulb.infof307.g03.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class UtilsTest {

    @Test
    void checkValidity(){
        String userName = "userName";// required
        String password = "password";// required
        String passwordConfirm  = "password";// required
        String email = "ga.ga@gmail.com";
        String firstName = "fname";
        String lastName = "lname";

        // Correct login
        assertEquals(Utils.checkValidity(userName,password,passwordConfirm,email,firstName,lastName), "");
        // incorrect confirm password
        assertEquals(Utils.checkValidity(userName,password,"",email,firstName,lastName),
                "At least one required field is empty.\n"
                +"Passwords doesn't match.\n");
        // incorrect password
        assertEquals(Utils.checkValidity(userName,"",passwordConfirm,email,firstName,lastName),
                "At least one required field is empty.\n"
                        +"Passwords doesn't match.\n");
        // incorrect userName (number)
        assertEquals(Utils.checkValidity("abc123",password,passwordConfirm,email,firstName,lastName), "Username must contain only letters.\n");
        // incorrect fname
        assertEquals(Utils.checkValidity(userName,password,passwordConfirm,email,"abc123",lastName), "First Name must contain only letters.\n");
        // incorrect lname
        assertEquals(Utils.checkValidity(userName,password,passwordConfirm,email,firstName,"abc123"), "Last Name must contain only letters.\n");
        // incorrect required fields
        assertEquals(Utils.checkValidity("","","",email,firstName, lastName),
                "At least one required field is empty.\n"
                +"Username must contain only letters.\n"
        );
        // incorrect required fields with wrong pwd
        assertEquals(Utils.checkValidity("","abc","",email,firstName, lastName),
                "At least one required field is empty.\n"
                        +"Passwords doesn't match.\n"
                        +"Username must contain only letters.\n"
        );

    }

    @Test
    void checkLogicalValidity(){
        //checkLogicalValidity(String username, String firstName, String lastName)
        // OK -> no error msg
        assertEquals(Utils.checkLogicalValidity("ABCDEFG", "ABCDEFG", "ABCDEFG"), "");
        assertEquals(Utils.checkLogicalValidity("ABC", "ABC", "ABC"), "");

        // Username KO
        assertEquals(Utils.checkLogicalValidity("123ABC", "ABC", "ABC"), "Username must contain only letters.\n");

        // Firstname KO
        assertEquals(Utils.checkLogicalValidity("ABC", "ABC123", "ABC"), "First Name must contain only letters.\n");

        // Lastname KO
        assertEquals(Utils.checkLogicalValidity("ABC", "ABC", "ABC123"), "Last Name must contain only letters.\n");


        // Username + firstname KO
        assertEquals(Utils.checkLogicalValidity("123ABC", "123ABC", "ABC"),
                "Username must contain only letters.\n"
                        +"First Name must contain only letters.\n");

        // Username + lastname KO
        assertEquals(Utils.checkLogicalValidity("123ABC", "ABC", "123ABC"),
                "Username must contain only letters.\n"
                        +"Last Name must contain only letters.\n");

        // firstname + lastname KO
        assertEquals(Utils.checkLogicalValidity("ABC", "123ABC", "123ABC"),
                "First Name must contain only letters.\n"
                        +"Last Name must contain only letters.\n");

        // ALL KO
        assertEquals(Utils.checkLogicalValidity("123ABC", "123ABC", "123ABC"),
                "Username must contain only letters.\n"
                +"First Name must contain only letters.\n"
                +"Last Name must contain only letters.\n");

    }

    @Test
    void checkLogsValidity(){

        // Username missing
        assertEquals(Utils.checkLogsValidity("", "ABC", "ABC"), "At least one required field is empty.\n");

        // pwd missing
        assertEquals(Utils.checkLogsValidity("ABC", "", "ABC"),
                "At least one required field is empty.\n"
                        +"Passwords doesn't match.\n");

        // confirmPwd missing
        assertEquals(Utils.checkLogsValidity("ABC", "ABC", ""),
                "At least one required field is empty.\n"
                        +"Passwords doesn't match.\n");

        // pwd doesn't match
        assertEquals(Utils.checkLogsValidity("ABC", "ABC", "ACB"), "Passwords doesn't match.\n");

        // Username missing + pwdDoens't match
        assertEquals(Utils.checkLogsValidity("", "ABC", ""),
                "At least one required field is empty.\n"
                        +"Passwords doesn't match.\n");

        // ALl field missing
        assertEquals(Utils.checkLogsValidity("", "", ""), "At least one required field is empty.\n");
    }

    @Test
    void checkEmailValidity(){

        // Email ok
        assertEquals(Utils.checkEmailValidity("gaga.agag@gmail.be"), "");
        assertEquals(Utils.checkEmailValidity("gaga_agag@gmail.be"), "");
        assertEquals(Utils.checkEmailValidity("gaga-agag@gmail.be"), "");

        // First part missing
        assertEquals(Utils.checkEmailValidity("@gmail.be"), "Email not valid.\n");
        // @ missing
        assertEquals(Utils.checkEmailValidity("gagaagaggmail.be"), "Email not valid.\n");
        // .xx missing
        assertEquals(Utils.checkEmailValidity("gaga.agag@gmail"), "Email not valid.\n");
        // Eveything missing
        assertEquals(Utils.checkEmailValidity("gagaagagmailbe"), "Email not valid.\n");

    }

    @Test
    void blankIfNull(){
        assertEquals("", Utils.blankIfNull(""));
        assertEquals( "", Utils.blankIfNull(null));
        assertEquals("blabla", Utils.blankIfNull("blabla"));
    }
}