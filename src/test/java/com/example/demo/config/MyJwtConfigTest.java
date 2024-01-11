package com.example.demo.config;

import com.example.demo.config.envBeanConfig.jwt.MyJwtSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class MyJwtConfigTest {

    @Autowired
    MyJwtSource myJwtSource;

    @Value("${my.jwt.secret-key}")
    private String secretKey;

    @Value("${my.jwt.refresh-key}")
    private String refreshKey;

    @Test
    public void jwtEnvBeanTest() {
        assertEquals(secretKey, myJwtSource.getSecretKey());
        assertEquals(refreshKey, myJwtSource.getRefreshKey());
    }
}
