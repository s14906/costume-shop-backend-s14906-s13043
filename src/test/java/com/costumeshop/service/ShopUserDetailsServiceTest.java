package com.costumeshop.service;

import com.costumeshop.core.sql.entity.User;
import com.costumeshop.core.sql.repository.UserRepository;
import com.costumeshop.data.GivenUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ShopUserDetailsServiceTest {
    private AutoCloseable closeable;
    @Mock
    private UserRepository userRepository;
    private final GivenUser givenUser = new GivenUser();
    private ShopUserDetailsService shopUserDetailsService;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        shopUserDetailsService = new ShopUserDetailsService(userRepository);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }
    @Test
    void loadUserByUsernameTest() {
        User user = givenUser.withTestUsernameAndEmptyUserRoles();
        String testUsername = user.getUsername();

        when(userRepository.findByUsername(testUsername)).thenReturn(user);

        UserDetails userDetails = shopUserDetailsService.loadUserByUsername(testUsername);

        verify(userRepository, times(1)).findByUsername(any());

        assertEquals(testUsername, userDetails.getUsername());
    }
}
