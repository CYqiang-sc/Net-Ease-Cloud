package com.sicnu.cloudmusic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class CloudMusicApplicationTests
{

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Test
    void contextLoads()
    {
        String password = "2624250238";
        //加密
        String encode = passwordEncoder.encode(password);

        System.out.println(encode);
        //看看是否匹配
        boolean matches = passwordEncoder.matches(password, "$2a$10$KB/KVSHhnVdhDilJoI91BedgF3amyPAxiMbI2x1CAzcSlRVN/iLA.");
        System.out.println(matches);
    }

}
