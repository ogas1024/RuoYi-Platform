package com.ruoyi.manage.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.domain.Library;
import com.ruoyi.manage.domain.LibraryAsset;
import com.ruoyi.manage.domain.LibraryDownloadLog;
import com.ruoyi.manage.domain.LibraryFavorite;
import com.ruoyi.manage.mapper.LibraryAssetMapper;
import com.ruoyi.manage.mapper.LibraryDownloadLogMapper;
import com.ruoyi.manage.mapper.LibraryFavoriteMapper;
import com.ruoyi.manage.mapper.LibraryMapper;
import com.ruoyi.manage.service.impl.LibraryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceImplTest {

    @Mock
    private LibraryMapper libraryMapper;
    @Mock
    private LibraryAssetMapper assetMapper;
    @Mock
    private LibraryFavoriteMapper favoriteMapper;
    @Mock
    private LibraryDownloadLogMapper downloadLogMapper;

    @InjectMocks
    private LibraryServiceImpl service;

    @BeforeEach
    void setUpSecurity() {
        SysUser user = new SysUser();
        user.setUserId(2001L);
        user.setUserName("alice");
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(2001L);
        loginUser.setUser(user);
        loginUser.setPermissions(Collections.emptySet());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void insert_success_with_valid_isbn13() {
        Library data = new Library();
        data.setIsbn13("9787110000028");
        data.setTitle("Java 核心技术I");
        data.setAuthor("Cay");
        when(libraryMapper.existsIsbn13(anyString())).thenReturn(0);
        when(libraryMapper.insert(any(Library.class))).thenAnswer(inv -> { Library arg = inv.getArgument(0); arg.setId(1L); return 1;});

        int rows = service.insert(data);
        assertEquals(1, rows);
        assertEquals(0, data.getStatus());
        verify(libraryMapper).insert(any(Library.class));
    }

    @Test
    void insert_fail_on_invalid_isbn13() {
        Library data = new Library();
        data.setIsbn13("9787110000020"); // 错误的校验位
        data.setTitle("X");
        data.setAuthor("Y");
        assertThrows(ServiceException.class, () -> service.insert(data));
        verify(libraryMapper, never()).insert(any());
    }

    @Test
    void update_fail_on_duplicate_isbn13() {
        Library data = new Library();
        data.setId(9L);
        data.setIsbn13("9787110000028");
        when(libraryMapper.existsIsbn13ExcludeId(eq("9787110000028"), eq(9L))).thenReturn(1);
        assertThrows(ServiceException.class, () -> service.update(data));
        verify(libraryMapper, never()).update(any());
    }

    @Test
    void addAsset_denied_when_not_owner() {
        Library book = new Library();
        book.setId(100L);
        book.setUploaderId(3003L); // 不是当前用户 2001
        when(libraryMapper.selectById(100L)).thenReturn(book);
        LibraryAsset a = new LibraryAsset();
        a.setBookId(100L);
        assertThrows(ServiceException.class, () -> service.addAsset(a));
        verify(assetMapper, never()).insert(any());
    }

    @Test
    void addAsset_denied_when_published() {
        Library book = new Library();
        book.setId(101L);
        book.setUploaderId(2001L); // 本人
        book.setStatus(1); // 已上架
        when(libraryMapper.selectById(101L)).thenReturn(book);
        LibraryAsset a = new LibraryAsset();
        a.setBookId(101L);
        assertThrows(ServiceException.class, () -> service.addAsset(a));
        verify(assetMapper, never()).insert(any());
    }

    @Test
    void favorite_idempotent() {
        when(favoriteMapper.exists(eq(7L), eq(2001L))).thenReturn(1); // 已收藏
        int ok = service.setFavorite(7L, 2001L, true, "alice");
        assertEquals(1, ok);
        verify(favoriteMapper, never()).insert(any(LibraryFavorite.class));

        when(favoriteMapper.exists(eq(7L), eq(2001L))).thenReturn(1);
        service.setFavorite(7L, 2001L, false, "alice");
        verify(favoriteMapper, times(1)).delete(eq(7L), eq(2001L));
    }

    @Test
    void incrDownload_should_write_log() {
        when(libraryMapper.incrDownload(eq(5L), any())).thenReturn(1);
        when(downloadLogMapper.insert(any(LibraryDownloadLog.class))).thenReturn(1);
        int rows = service.incrDownload(5L, null);
        assertEquals(1, rows);
        verify(downloadLogMapper, atLeastOnce()).insert(any(LibraryDownloadLog.class));
    }
}

