package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.LibraryLibrarian;

import java.util.List;

/**
 * 图书管理员服务接口
 */
public interface ILibraryLibrarianService {
    /**
     * 列表查询
     * @param query 查询条件（用户名、状态等）
     * @return 列表
     */
    List<LibraryLibrarian> selectList(LibraryLibrarian query);

    /**
     * 任命图书管理员
     * @param userId 用户ID
     * @param operator 操作人
     * @return 影响行数
     */
    int appoint(Long userId, String operator);

    /**
     * 批量解除图书管理员
     * @param userIds 用户ID数组
     * @param operator 操作人
     * @return 影响行数
     */
    int dismiss(Long[] userIds, String operator);
}
