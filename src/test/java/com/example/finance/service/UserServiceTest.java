package com.example.finance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.finance.Service.UserService;
import com.example.finance.entity.UserStatus;
import com.example.finance.entity.Users;
import com.example.finance.repository.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private UserService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserDefaultsToActiveWhenStatusMissing() {
        Users user = new Users();
        user.setEmail("new.user@example.com");
        user.setStatus(null);

        when(repo.existsByEmail("new.user@example.com")).thenReturn(false);
        when(repo.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Users created = service.create(user);

        assertEquals(UserStatus.ACTIVE, created.getStatus());
    }

    @Test
    void testDeactivateDoesNotSoftDeleteUser() {
        Users user = new Users();
        user.setId(1L);
        user.setDeleted(false);
        user.setStatus(UserStatus.ACTIVE);

        when(repo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));

        service.deactivate(1L);

        assertEquals(UserStatus.INACTIVE, user.getStatus());
        assertFalse(user.isDeleted());
        verify(repo, times(1)).save(user);
    }

    @Test
    void testSoftDeleteMarksDeletedAndInactive() {
        Users user = new Users();
        user.setId(2L);
        user.setDeleted(false);
        user.setStatus(UserStatus.ACTIVE);

        when(repo.findByIdAndDeletedFalse(2L)).thenReturn(Optional.of(user));

        service.softDelete(2L);

        assertTrue(user.isDeleted());
        assertEquals(UserStatus.INACTIVE, user.getStatus());
        verify(repo, times(1)).save(user);
    }

    @Test
    void testGetInactiveAndDeletedUsersSeparate() {
        Users inactive = new Users();
        inactive.setStatus(UserStatus.INACTIVE);
        inactive.setDeleted(false);

        Users deleted = new Users();
        deleted.setDeleted(true);

        when(repo.findAllByDeletedFalseAndStatus(UserStatus.INACTIVE)).thenReturn(List.of(inactive));
        when(repo.findAllByDeletedTrue()).thenReturn(List.of(deleted));

        List<Users> inactiveUsers = service.getInactiveUsers();
        List<Users> deletedUsers = service.getDeletedUsers();

        assertEquals(1, inactiveUsers.size());
        assertEquals(1, deletedUsers.size());
        assertFalse(inactiveUsers.get(0).isDeleted());
        assertTrue(deletedUsers.get(0).isDeleted());
    }
}
