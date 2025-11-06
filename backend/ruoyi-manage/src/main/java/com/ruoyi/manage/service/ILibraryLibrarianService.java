package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.LibraryLibrarian;

import java.util.List;

public interface ILibraryLibrarianService {
    List<LibraryLibrarian> selectList(LibraryLibrarian query);
    int appoint(Long userId, String operator);
    int dismiss(Long[] userIds, String operator);
}

