package com.ruoyi.manage.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.domain.FacilityBooking;
import com.ruoyi.manage.domain.FacilityBookingUser;
import com.ruoyi.manage.domain.FacilityRoom;
import com.ruoyi.manage.domain.FacilitySetting;
import com.ruoyi.manage.mapper.FacilityBanMapper;
import com.ruoyi.manage.mapper.FacilityBookingMapper;
import com.ruoyi.manage.mapper.FacilityBookingUserMapper;
import com.ruoyi.manage.mapper.FacilityRoomMapper;
import com.ruoyi.manage.service.IFacilityBookingService;
import com.ruoyi.manage.service.IFacilitySettingService;
import com.ruoyi.manage.vo.TimelineSegmentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FacilityBookingServiceImpl implements IFacilityBookingService {

    @Resource
    private FacilityBookingMapper bookingMapper;
    @Resource
    private FacilityBookingUserMapper bookingUserMapper;
    @Resource
    private FacilityRoomMapper roomMapper;
    @Resource
    private FacilityBanMapper banMapper;
    @Resource
    private IFacilitySettingService settingService;

    private void assertUserListValid(List<Long> userIds, Long applicantId) {
        if (userIds == null) throw new ServiceException("使用人列表不能为空");
        // 统一包含申请人
        Set<Long> uniq = new LinkedHashSet<>(userIds);
        uniq.add(applicantId);
        if (uniq.size() < 3) throw new ServiceException("使用人不少于3人（含申请人）");
        int exist = bookingMapper.countUsersExist(new ArrayList<>(uniq));
        if (exist != uniq.size()) throw new ServiceException("存在无效的用户ID");
    }

    private int calcDurationMinutes(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return (int) Math.round(diff / 60000.0);
    }

    private void assertTimeWindowValid(Date start, Date end, int maxMinutes) {
        Date now = new Date();
        if (start == null || end == null) throw new ServiceException("开始/结束时间必填");
        if (!start.after(now)) throw new ServiceException("开始时间需晚于当前时间");
        int minutes = calcDurationMinutes(start, end);
        if (minutes <= 0) throw new ServiceException("结束时间需大于开始时间");
        if (minutes > maxMinutes) throw new ServiceException("超过全局最⻓时长限制");
    }

    private void assertNoConflict(Long roomId, Date start, Date end, Long excludeId) {
        int conflict = bookingMapper.countConflict(roomId, start, end, excludeId);
        if (conflict > 0) throw new ServiceException("时间段冲突，请查看时间轴");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(FacilityBooking data, List<Long> userIds, Long currentUserId, String username) {
        FacilityRoom room = roomMapper.selectById(data.getRoomId());
        if (room == null) throw new ServiceException("功能房不存在");
        if (!"0".equals(room.getStatus())) throw new ServiceException("房间已禁用，无法预约");

        if (banMapper.countActive(currentUserId) > 0) throw new ServiceException("申请人处于封禁状态");

        FacilitySetting setting = settingService.get();
        assertTimeWindowValid(data.getStartTime(), data.getEndTime(),
                setting.getMaxDurationMinutes() != null ? setting.getMaxDurationMinutes() : 4320);
        assertNoConflict(data.getRoomId(), data.getStartTime(), data.getEndTime(), null);
        assertUserListValid(userIds, currentUserId);

        data.setApplicantId(currentUserId);
        data.setDurationMinutes(calcDurationMinutes(data.getStartTime(), data.getEndTime()));
        data.setCreateBy(username);
        data.setStatus("1".equals(setting.getAuditRequired()) ? "0" : "1");
        int n = bookingMapper.insert(data);

        // 插入参与人（含申请人）
        Set<Long> uniq = new LinkedHashSet<>(userIds);
        uniq.add(currentUserId);
        List<FacilityBookingUser> items = new ArrayList<>();
        Date now = new Date();
        for (Long uid : uniq) {
            FacilityBookingUser u = new FacilityBookingUser();
            u.setBookingId(data.getId());
            u.setUserId(uid);
            u.setIsApplicant(Objects.equals(uid, currentUserId) ? "1" : "0");
            u.setCreateBy(username);
            u.setCreateTime(now);
            items.add(u);
        }
        if (!items.isEmpty()) bookingUserMapper.batchInsert(items);
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBeforeStart(Long id, FacilityBooking patch, List<Long> userIds, Long currentUserId, String username) {
        FacilityBooking origin = bookingMapper.selectById(id);
        if (origin == null) throw new ServiceException("预约不存在");
        if (!Objects.equals(origin.getApplicantId(), currentUserId)) throw new ServiceException("无权修改他人预约");
        if (!Arrays.asList("0", "1", "2").contains(origin.getStatus()))
            throw new ServiceException("当前状态不允许修改");
        if (!origin.getStartTime().after(new Date()))
            throw new ServiceException("已开始的预约不可在此修改，请使用提前结束");

        FacilitySetting setting = settingService.get();
        Date start = patch.getStartTime() != null ? patch.getStartTime() : origin.getStartTime();
        Date end = patch.getEndTime() != null ? patch.getEndTime() : origin.getEndTime();
        assertTimeWindowValid(start, end,
                setting.getMaxDurationMinutes() != null ? setting.getMaxDurationMinutes() : 4320);
        assertNoConflict(origin.getRoomId(), start, end, origin.getId());

        if (userIds != null) {
            assertUserListValid(userIds, currentUserId);
            bookingUserMapper.deleteByBookingId(origin.getId());
            Set<Long> uniq = new LinkedHashSet<>(userIds);
            uniq.add(currentUserId);
            List<FacilityBookingUser> items = new ArrayList<>();
            Date now = new Date();
            for (Long uid : uniq) {
                FacilityBookingUser u = new FacilityBookingUser();
                u.setBookingId(origin.getId());
                u.setUserId(uid);
                u.setIsApplicant(Objects.equals(uid, currentUserId) ? "1" : "0");
                u.setCreateBy(username);
                u.setCreateTime(now);
                items.add(u);
            }
            if (!items.isEmpty()) bookingUserMapper.batchInsert(items);
        }

        origin.setPurpose(patch.getPurpose() != null ? patch.getPurpose() : origin.getPurpose());
        origin.setStartTime(start);
        origin.setEndTime(end);
        origin.setDurationMinutes(calcDurationMinutes(start, end));
        // 若被驳回后重提：根据审核开关重新走待审或直接批准
        if ("2".equals(origin.getStatus())) {
            origin.setStatus("1".equals(setting.getAuditRequired()) ? "0" : "1");
            origin.setRejectReason(null);
        }
        origin.setUpdateBy(username);
        origin.setUpdateTime(new Date());
        return bookingMapper.update(origin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancel(Long id, Long currentUserId) {
        FacilityBooking origin = bookingMapper.selectById(id);
        if (origin == null) throw new ServiceException("预约不存在");
        if (!Objects.equals(origin.getApplicantId(), currentUserId)) throw new ServiceException("无权取消他人预约");
        if (!Arrays.asList("0", "1").contains(origin.getStatus())) throw new ServiceException("当前状态不可取消");
        if (!origin.getStartTime().after(new Date())) throw new ServiceException("预约已开始，不能取消");
        origin.setStatus("3");
        origin.setUpdateTime(new Date());
        return bookingMapper.update(origin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int endEarly(Long id, Date newEndTime, Long currentUserId, String username) {
        FacilityBooking origin = bookingMapper.selectById(id);
        if (origin == null) throw new ServiceException("预约不存在");
        if (!Objects.equals(origin.getApplicantId(), currentUserId)) throw new ServiceException("无权操作他人预约");
        if (!Arrays.asList("1", "4").contains(origin.getStatus()))
            throw new ServiceException("仅已批准/进行中允许提前结束");
        Date now = new Date();
        if (newEndTime.before(now)) newEndTime = now; // 对齐：不早于当前
        if (!newEndTime.after(origin.getStartTime())) throw new ServiceException("结束时间需大于开始时间");
        // 缩短结束时间无需冲突校验（只释放占用）
        int minutes = calcDurationMinutes(origin.getStartTime(), newEndTime);
        origin.setEndTime(newEndTime);
        origin.setDurationMinutes(minutes);
        origin.setStatus(newEndTime.after(now) ? origin.getStatus() : "5");
        origin.setUpdateBy(username);
        origin.setUpdateTime(now);
        return bookingMapper.update(origin);
    }

    @Override
    public List<FacilityBooking> myList(Long currentUserId, String status) {
        return bookingMapper.selectMyList(currentUserId, status);
    }

    @Override
    public List<TimelineSegmentVO> timeline(Long roomId, Date from, Date to) {
        return bookingMapper.selectTimeline(roomId, from, to);
    }

    @Override
    public List<FacilityBooking> auditList(Long bookingId, Long buildingId, Long roomId, Long applicantId, Date from, Date to) {
        return bookingMapper.selectAuditList(bookingId, buildingId, roomId, applicantId, from, to);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approve(Long id, String username) {
        FacilityBooking origin = bookingMapper.selectById(id);
        if (origin == null) throw new ServiceException("预约不存在");
        if (!"0".equals(origin.getStatus())) throw new ServiceException("仅待审核记录可通过");
        // 二次冲突校验 + 房间启用校验
        FacilityRoom room = roomMapper.selectById(origin.getRoomId());
        if (room == null || !"0".equals(room.getStatus())) throw new ServiceException("房间不可用，无法通过");
        assertNoConflict(origin.getRoomId(), origin.getStartTime(), origin.getEndTime(), origin.getId());
        return bookingMapper.doApprove(id, new Date(), username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reject(Long id, String username, String reason) {
        if (reason == null || reason.trim().isEmpty()) throw new ServiceException("驳回理由必填");
        FacilityBooking origin = bookingMapper.selectById(id);
        if (origin == null) throw new ServiceException("预约不存在");
        if (!"0".equals(origin.getStatus())) throw new ServiceException("仅待审核记录可驳回");
        return bookingMapper.doReject(id, new Date(), username, reason.trim());
    }
}
