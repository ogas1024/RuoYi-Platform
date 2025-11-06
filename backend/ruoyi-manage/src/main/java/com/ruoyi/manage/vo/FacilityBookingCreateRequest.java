package com.ruoyi.manage.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FacilityBookingCreateRequest {
    private Long roomId;
    private String purpose;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private List<Long> userIdList; // 含申请人在内不少于3人；若未包含，服务端会自动加入申请人
}

