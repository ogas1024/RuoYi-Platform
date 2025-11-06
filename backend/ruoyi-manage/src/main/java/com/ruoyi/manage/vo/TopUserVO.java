package com.ruoyi.manage.vo;

import lombok.Data;

@Data
public class TopUserVO {
    private Long userId;
    private String username;
    private String nickname;
    private Long passedCount;
}

