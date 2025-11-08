package com.ruoyi.manage.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.*;
import com.ruoyi.manage.mapper.*;
import com.ruoyi.manage.service.ISurveyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@Service
public class SurveyServiceImpl implements ISurveyService {

    @Autowired
    private SurveyMapper surveyMapper;
    @Autowired
    private SurveyItemMapper itemMapper;
    @Autowired
    private SurveyOptionMapper optionMapper;
    @Autowired
    private SurveyScopeMapper scopeMapper;
    @Autowired
    private SurveyAnswerMapper answerMapper;
    @Autowired
    private SurveyAnswerItemMapper answerItemMapper;

    private static final ObjectMapper M = new ObjectMapper();

    @Override
    public List<Survey> list(Survey query) {
        // 管理侧列表：不过滤可见范围
        Long uid = SecurityUtils.getUserId();
        // 自动取消置顶（过期）
        surveyMapper.unpinExpired();
        return surveyMapper.selectList(query, uid, 1);
    }

    @Override
    public Survey detail(Long id, boolean withScopes) {
        Survey s = surveyMapper.selectById(id);
        if (s == null || Objects.equals("2", s.getDelFlag())) return null;
        List<SurveyItem> items = itemMapper.selectBySurveyId(id);
        Map<Long, List<SurveyOption>> optionsByItem = new HashMap<>();
        List<SurveyOption> opts = optionMapper.selectBySurveyId(id);
        for (SurveyOption o : opts) {
            optionsByItem.computeIfAbsent(o.getItemId(), k -> new ArrayList<>()).add(o);
        }
        for (SurveyItem it : items) {
            it.setOptions(optionsByItem.get(it.getId()));
        }
        s.setItems(items);
        if (withScopes) {
            s.setScopes(scopeMapper.selectBySurveyId(id));
        }
        return s;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Survey survey) {
        if (survey == null) throw new ServiceException("参数不能为空");
        if (StringUtils.isEmpty(survey.getTitle())) throw new ServiceException("问卷标题不能为空");
        if (survey.getItems() == null || survey.getItems().isEmpty()) throw new ServiceException("至少添加一个题目");
        // 默认保存为草稿；若明确传入 1 则直接发布
        survey.setStatus(survey.getStatus() == null ? 0 : survey.getStatus());
        survey.setCreateBy(SecurityUtils.getUsername());
        survey.setCreateTime(DateUtils.getNowDate());
        survey.setUpdateBy(SecurityUtils.getUsername());
        survey.setUpdateTime(DateUtils.getNowDate());
        if (survey.getVisibleAll() == null) survey.setVisibleAll(1);
        int rows = surveyMapper.insert(survey);
        int sort = 0;
        for (SurveyItem it : survey.getItems()) {
            // 题目提示非空
            if (it == null || it.getTitle() == null || it.getTitle().trim().isEmpty()) {
                throw new ServiceException("题目提示不能为空");
            }
            // 选择题边界校验：至少一个选项，且选项文本不能为空
            if (it.getType() != null && (it.getType() == 2 || it.getType() == 3)) {
                if (it.getOptions() == null || it.getOptions().isEmpty()) {
                    throw new ServiceException("选择题至少需要一个选项");
                }
                for (SurveyOption op : it.getOptions()) {
                    if (op == null || op.getLabel() == null || op.getLabel().trim().isEmpty()) {
                        throw new ServiceException("选项文本不能为空");
                    }
                }
            }
            initChild(it);
            it.setSurveyId(survey.getId());
            if (it.getSortNo() == null) it.setSortNo(sort++);
            if (it.getType() == null) it.setType(1);
            if (it.getRequired() == null) it.setRequired(0);
            itemMapper.insert(it);
            if (it.getOptions() != null) {
                for (SurveyOption op : it.getOptions()) {
                    initChild(op);
                    op.setItemId(it.getId());
                    optionMapper.insert(op);
                }
            }
        }
        if (Objects.equals(0, survey.getVisibleAll())) {
            List<SurveyScope> scopes = normalizeScopes(survey.getScopes());
            if (scopes.isEmpty()) throw new ServiceException("自定义范围至少选择一个角色/部门/岗位");
            for (SurveyScope sc : scopes) {
                initChild(sc);
                sc.setSurveyId(survey.getId());
                scopeMapper.insert(sc);
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int edit(Survey survey) {
        if (survey == null || survey.getId() == null) throw new ServiceException("ID 不能为空");
        Survey db = surveyMapper.selectById(survey.getId());
        if (db == null || Objects.equals("2", db.getDelFlag())) return -404;
        if (!Objects.equals(0, db.getStatus())) {
            throw new ServiceException("仅草稿状态可编辑");
        }
        // 顶层可改：title/deadline/remark/visibleAll
        survey.setUpdateBy(SecurityUtils.getUsername());
        survey.setUpdateTime(DateUtils.getNowDate());
        surveyMapper.update(survey);

        // 全量重建 items/options
        optionMapper.deleteBySurveyId(survey.getId());
        itemMapper.deleteBySurveyId(survey.getId());
        if (survey.getItems() == null || survey.getItems().isEmpty()) throw new ServiceException("至少添加一个题目");
        int sort = 0;
        for (SurveyItem it : survey.getItems()) {
            if (it == null || it.getTitle() == null || it.getTitle().trim().isEmpty()) {
                throw new ServiceException("题目提示不能为空");
            }
            if (it.getType() != null && (it.getType() == 2 || it.getType() == 3)) {
                if (it.getOptions() == null || it.getOptions().isEmpty()) {
                    throw new ServiceException("选择题至少需要一个选项");
                }
                for (SurveyOption op : it.getOptions()) {
                    if (op == null || op.getLabel() == null || op.getLabel().trim().isEmpty()) {
                        throw new ServiceException("选项文本不能为空");
                    }
                }
            }
            initChild(it);
            it.setSurveyId(survey.getId());
            if (it.getSortNo() == null) it.setSortNo(sort++);
            if (it.getType() == null) it.setType(1);
            if (it.getRequired() == null) it.setRequired(0);
            itemMapper.insert(it);
            if (it.getOptions() != null) {
                for (SurveyOption op : it.getOptions()) {
                    initChild(op);
                    op.setItemId(it.getId());
                    optionMapper.insert(op);
                }
            }
        }

        // 可见范围
        scopeMapper.deleteBySurveyId(survey.getId());
        if (survey.getVisibleAll() != null && survey.getVisibleAll() == 0) {
            List<SurveyScope> scopes = normalizeScopes(survey.getScopes());
            if (scopes.isEmpty()) throw new ServiceException("自定义范围至少选择一个角色/部门/岗位");
            for (SurveyScope sc : scopes) {
                initChild(sc);
                sc.setSurveyId(survey.getId());
                scopeMapper.insert(sc);
            }
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int archive(Long id) {
        Survey s = surveyMapper.selectById(id);
        if (s == null || Objects.equals("2", s.getDelFlag())) return -404;
        if (Objects.equals(2, s.getStatus())) return 1; // 幂等
        return surveyMapper.archive(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int extend(Long id, Date newDeadline) {
        if (newDeadline == null) throw new ServiceException("新截止时间不能为空");
        Survey s = surveyMapper.selectById(id);
        if (s == null || Objects.equals("2", s.getDelFlag())) return -404;
        if (s.getDeadline() != null && !newDeadline.after(s.getDeadline())) {
            throw new ServiceException("新截止时间必须晚于当前截止时间");
        }
        if (Objects.equals(2, s.getStatus())) {
            throw new ServiceException("归档后不可延期");
        }
        return surveyMapper.extend(id, newDeadline);
    }

    @Override
    public List<Survey> portalList(Survey query, boolean includeExpired) {
        if (query == null) query = new Survey();
        query.setIncludeExpired(includeExpired);
        Long uid = SecurityUtils.getUserId();
        boolean admin = SecurityUtils.isAdmin(uid);
        // 自动取消置顶（过期）
        surveyMapper.unpinExpired();
        return surveyMapper.selectList(query, uid, admin ? 1 : 0);
    }

    @Override
    public Map<Long, Object> loadMyAnswers(Long surveyId, Long userId) {
        SurveyAnswer ans = answerMapper.selectOne(surveyId, userId);
        if (ans == null) return Collections.emptyMap();
        List<SurveyAnswerItem> items = answerItemMapper.selectByAnswerId(ans.getId());
        Map<Long, Object> map = new HashMap<>();
        for (SurveyAnswerItem it : items) {
            if (StringUtils.isNotEmpty(it.getValueText())) {
                map.put(it.getItemId(), it.getValueText());
            } else if (StringUtils.isNotEmpty(it.getValueOptionIds())) {
                try {
                    List<Long> ids = M.readValue(it.getValueOptionIds(), new TypeReference<List<Long>>() {
                    });
                    map.put(it.getItemId(), ids);
                } catch (Exception e) {
                    // 解析失败则作为原始字符串返回
                    map.put(it.getItemId(), it.getValueOptionIds());
                }
            }
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submit(Long surveyId, Long userId, List<Map<String, Object>> answers) {
        Survey s = surveyMapper.selectById(surveyId);
        if (s == null || Objects.equals("2", s.getDelFlag())) throw new ServiceException("问卷不存在");
        if (!Objects.equals(1, s.getStatus())) throw new ServiceException("问卷未发布或已归档");
        if (s.getDeadline() != null && new Date().after(s.getDeadline())) {
            throw new ServiceException("已过截止时间，无法提交");
        }
        // 加载题目定义用于校验
        List<SurveyItem> items = itemMapper.selectBySurveyId(surveyId);
        Map<Long, SurveyItem> itemMap = new HashMap<>();
        for (SurveyItem it : items) itemMap.put(it.getId(), it);

        SurveyAnswer existing = answerMapper.selectOne(surveyId, userId);
        Date now = DateUtils.getNowDate();
        if (existing == null) {
            SurveyAnswer ans = new SurveyAnswer();
            ans.setSurveyId(surveyId);
            ans.setUserId(userId);
            ans.setSubmitTime(now);
            ans.setUpdateTime2(now);
            ans.setCreateBy(SecurityUtils.getUsername());
            ans.setCreateTime(now);
            ans.setUpdateBy(SecurityUtils.getUsername());
            ans.setUpdateTime(now);
            answerMapper.insertIgnore(ans);
            existing = answerMapper.selectOne(surveyId, userId);
        } else {
            existing.setSubmitTime(now);
            existing.setUpdateTime2(now);
            existing.setUpdateBy(SecurityUtils.getUsername());
            existing.setUpdateTime(now);
            answerMapper.updateById(existing);
            answerItemMapper.deleteByAnswerId(existing.getId());
        }

        // 写入答案
        if (answers != null) {
            for (Map<String, Object> a : answers) {
                if (a == null) continue;
                Long itemId = toLong(a.get("itemId"));
                if (itemId == null) continue;
                SurveyItem def = itemMap.get(itemId);
                if (def == null) continue; // 非法题目忽略
                SurveyAnswerItem row = new SurveyAnswerItem();
                row.setAnswerId(existing.getId());
                row.setItemId(itemId);
                row.setCreateBy(SecurityUtils.getUsername());
                row.setCreateTime(now);
                row.setUpdateBy(SecurityUtils.getUsername());
                row.setUpdateTime(now);
                // 值处理
                if (def.getType() != null && def.getType() == 1) {
                    String v = objToString(a.get("valueText"));
                    if (def.getRequired() != null && def.getRequired() == 1 && StringUtils.isEmpty(v)) {
                        throw new ServiceException("必填题未填写：" + def.getTitle());
                    }
                    row.setValueText(v);
                } else { // 单选/多选
                    Object opt = a.get("optionIds");
                    List<Long> ids = castToLongList(opt);
                    if (ids == null) ids = Collections.emptyList();
                    if (def.getRequired() != null && def.getRequired() == 1 && ids.isEmpty()) {
                        throw new ServiceException("必选题未选择：" + def.getTitle());
                    }
                    try {
                        row.setValueOptionIds(M.writeValueAsString(ids));
                    } catch (Exception e) {
                        row.setValueOptionIds(null);
                    }
                }
                answerItemMapper.insert(row);
            }
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pin(Long id, boolean pinned) {
        Survey s = surveyMapper.selectById(id);
        if (s == null || Objects.equals("2", s.getDelFlag())) return -404;
        if (pinned) {
            if (!Objects.equals(1, s.getStatus())) {
                // 仅已发布可置顶
                return -409;
            }
            return surveyMapper.pin(id);
        } else {
            return surveyMapper.unpin(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publish(Long id) {
        Survey s = surveyMapper.selectById(id);
        if (s == null || Objects.equals("2", s.getDelFlag())) return -404;
        if (!Objects.equals(0, s.getStatus())) return -409; // 仅草稿可发布
        Survey patch = new Survey();
        patch.setId(id);
        patch.setStatus(1);
        patch.setUpdateBy(SecurityUtils.getUsername());
        patch.setUpdateTime(DateUtils.getNowDate());
        return surveyMapper.update(patch);
    }

    private void initChild(Object obj) {
        Date now = DateUtils.getNowDate();
        if (obj instanceof SurveyItem) {
            SurveyItem x = (SurveyItem) obj;
            x.setCreateBy(SecurityUtils.getUsername());
            x.setCreateTime(now);
            x.setUpdateBy(SecurityUtils.getUsername());
            x.setUpdateTime(now);
        } else if (obj instanceof SurveyOption) {
            SurveyOption x = (SurveyOption) obj;
            x.setCreateBy(SecurityUtils.getUsername());
            x.setCreateTime(now);
            x.setUpdateBy(SecurityUtils.getUsername());
            x.setUpdateTime(now);
        } else if (obj instanceof SurveyScope) {
            SurveyScope x = (SurveyScope) obj;
            x.setCreateBy(SecurityUtils.getUsername());
            x.setCreateTime(now);
            x.setUpdateBy(SecurityUtils.getUsername());
            x.setUpdateTime(now);
        }
    }

    private List<SurveyScope> normalizeScopes(List<SurveyScope> scopes) {
        if (scopes == null || scopes.isEmpty()) return Collections.emptyList();
        List<SurveyScope> list = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (SurveyScope s : scopes) {
            if (s == null) continue;
            Integer t = s.getScopeType();
            Long r = s.getRefId();
            if (r == null) continue;
            if (t == null || (t != 0 && t != 1 && t != 2)) continue;
            String key = t + ":" + r;
            if (set.add(key)) list.add(s);
        }
        return list;
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    private String objToString(Object v) {
        return v == null ? null : String.valueOf(v);
    }

    @SuppressWarnings("unchecked")
    private List<Long> castToLongList(Object v) {
        if (v == null) return null;
        try {
            if (v instanceof List) {
                List<?> raw = (List<?>) v;
                List<Long> out = new ArrayList<>();
                for (Object o : raw) {
                    Long id = toLong(o);
                    if (id != null) out.add(id);
                }
                return out;
            }
            // 允许传字符串（逗号分隔）
            String s = String.valueOf(v);
            if (StringUtils.isEmpty(s)) return Collections.emptyList();
            String[] parts = s.split(",");
            List<Long> out = new ArrayList<>();
            for (String p : parts) {
                Long id = toLong(p.trim());
                if (id != null) out.add(id);
            }
            return out;
        } catch (Exception e) {
            return null;
        }
    }

    // ===== 新增：门户与管理聚合能力，移除 Controller 对 Mapper 的直接依赖 =====

    @Override
    public Survey portalDetail(Long id, Long userId) {
        Survey s = detail(id, false);
        if (s == null || s.getStatus() == null || (s.getStatus() != 1 && s.getStatus() != 2)) {
            throw new ServiceException("问卷不存在或未发布");
        }
        Map<Long, Object> my = loadMyAnswers(id, userId);
        s.setMyAnswers(my);
        boolean ended = (s.getDeadline() != null && new Date().after(s.getDeadline())) || (s.getStatus() != null && s.getStatus() == 2);
        if (ended) {
            fillOptionVoteCounts(s);
        }
        return s;
    }

    @Override
    public List<Map<String, Object>> selectMyAnswerSummaries(Long userId) {
        List<SurveyAnswer> list = answerMapper.selectMyAnswers(userId);
        Map<Long, String> titleMap = new HashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SurveyAnswer a : list) {
            if (!titleMap.containsKey(a.getSurveyId())) {
                Survey s = surveyMapper.selectById(a.getSurveyId());
                titleMap.put(a.getSurveyId(), s == null ? "-" : s.getTitle());
            }
            Map<String, Object> m = new HashMap<>();
            m.put("surveyId", a.getSurveyId());
            m.put("title", titleMap.get(a.getSurveyId()));
            m.put("submitTime", a.getSubmitTime());
            rows.add(m);
        }
        return rows;
    }

    @Override
    public Survey manageDetailWithStats(Long id) {
        Survey s = detail(id, true);
        if (s == null) throw new ServiceException("问卷不存在");
        fillOptionVoteCounts(s);
        return s;
    }

    @Override
    public List<Map<String, Object>> selectSubmitUsers(Long surveyId) {
        return answerMapper.selectSubmitUsers(surveyId);
    }

    @Override
    public Survey surveyWithUserAnswers(Long id, Long userId) {
        Survey s = detail(id, false);
        if (s == null) throw new ServiceException("问卷不存在");
        Map<Long, Object> my = loadMyAnswers(id, userId);
        s.setMyAnswers(my);
        return s;
    }

    /**
     * 统计每个选项的票数并回填到 Survey 对象（仅选择题）。
     */
    private void fillOptionVoteCounts(Survey s) {
        try {
            List<Map<String, Object>> rows = answerItemMapper.countOptionVotes(s.getId());
            Map<Long, Integer> cntMap = new HashMap<>();
            for (Map<String, Object> r : rows) {
                Object oid = r.get("optionId");
                Object c = r.get("cnt");
                if (oid != null && c != null) {
                    Long key = oid instanceof Number ? ((Number) oid).longValue() : Long.parseLong(String.valueOf(oid));
                    Integer val = c instanceof Number ? ((Number) c).intValue() : Integer.parseInt(String.valueOf(c));
                    cntMap.put(key, val);
                }
            }
            if (s.getItems() != null) {
                for (SurveyItem it : s.getItems()) {
                    if (it.getOptions() != null) {
                        for (SurveyOption op : it.getOptions()) {
                            Integer v = cntMap.get(op.getId());
                            if (v != null) op.setVoteCount(v);
                        }
                    }
                }
            }
        } catch (Exception ignore) { }
    }
}
