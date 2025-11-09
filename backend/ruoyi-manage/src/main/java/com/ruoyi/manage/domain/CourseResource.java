package com.ruoyi.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class CourseResource extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 专业ID（tb_major.id） */
    private Long majorId;
    /** 课程ID（tb_course.id） */
    private Long courseId;
    /** 资源名称（展示用） */
    private String resourceName;
    /** 资源类型：0-文件 1-外链 */
    private Integer resourceType; // 0-文件 1-外链
    /** 文件直链（resourceType=0 时有效） */
    private String fileUrl;
    /** 文件哈希（去重/防篡改，建议 sha256:xxx 格式） */
    private String fileHash;
    /** 文件大小（字节） */
    private Long fileSize;
    /** 外链 URL（resourceType=1 时有效） */
    private String linkUrl;
    /** 资源简介/备注 */
    private String description;
    /** 状态：0待审 1已上架 2驳回 3已下架 */
    private Integer status; // 0待审 1通过 2驳回 3下架
    /** 是否最佳：0否 1是（由最佳表计算得到） */
    private Integer isBest; // 0否 1是
    /** 设为最佳的操作人 */
    private String bestBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 设为最佳的时间 */
    private Date bestTime;
    /** 审核人 */
    private String auditBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 审核时间 */
    private Date auditTime;
    /** 驳回/下架原因 */
    private String auditReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 首次上架时间 */
    private Date publishTime;
    /** 下载次数（成功次数） */
    private Integer downloadCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /** 最近一次下载时间 */
    private Date lastDownloadTime;
    /** 上传者ID */
    private Long uploaderId;
    /** 上传者用户名 */
    private String uploaderName;
    /** 删除标志：0存在 2删除 */
    private String delFlag; // 0存在 2删除

    // 冗余展示
    /** 冗余：专业名称 */
    private String majorName;
    /** 冗余：课程名称 */
    private String courseName;
}
