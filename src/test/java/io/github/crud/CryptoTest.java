package io.github.crud;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class CryptoTest {

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void testCrypto() throws Exception {
        String password = "password";

        List<String> hashs = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            hashs.add(passwordEncoder.encode(password));
        }

        for (String hash : hashs) {
            Assert.assertTrue(passwordEncoder.matches(password, hash));
        }
    }
}
