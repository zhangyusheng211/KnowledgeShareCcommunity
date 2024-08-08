package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.Problem;
import com.moxi.mogublog.commons.entity.ProblemUserRecord;
import com.moxi.mogublog.commons.vo.ProblemUserRecordVO;
import com.moxi.mogublog.commons.vo.ProblemVO;
import com.moxi.mogublog.utils.IpUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.mapper.ProblemUserRecordMapper;
import com.moxi.mogublog.xo.service.ProblemService;
import com.moxi.mogublog.xo.service.ProblemUserRecordService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EDegreeStatus;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 问答标签表 服务实现类
 *
 * @author 陌溪
 * @date 2022年3月12日09:08:43
 */
@Service
public class ProblemUserRecordServiceImpl extends SuperServiceImpl<ProblemUserRecordMapper, ProblemUserRecord> implements ProblemUserRecordService {

    @Resource
    private ProblemUserRecordService problemUserRecordService;
    @Resource
    private ProblemService problemService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public String addProblemUserRecord(ProblemUserRecordVO problemUserRecordVO) {
        String userUid = RequestHolder.getUserUid();
        // 判断状态是否是已阅读
        if (StringUtils.isEmpty(userUid)) {
            return ResultUtil.errorWithMessage("用户未登录");
        }
        EDegreeStatus degreeStatus = EDegreeStatus.getType(problemUserRecordVO.getDegree());
        Problem problem = problemService.getById(problemUserRecordVO.getProblemUid());
        if (problem == null) {
            return ResultUtil.errorWithMessage("问题不存在");
        }

        QueryWrapper<ProblemUserRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.PROBLEM_UID, problemUserRecordVO.getProblemUid());
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<ProblemUserRecord> problemUserRecordList = problemUserRecordService.list(queryWrapper);

        if (problemUserRecordList.size() > 0) {
            // 如果状态是阅读的，直接跳过
            if (!EDegreeStatus.VISIT.getType().equals(problemUserRecordVO.getProblemUid())) {
                // 已经有记录了，直接修改这些记录的状态
                for (ProblemUserRecord problemUserRecord : problemUserRecordList) {
                    problemUserRecord.setDegree(degreeStatus.getType());
                }
                // 更新掌握状态
                problemUserRecordService.updateBatchById(problemUserRecordList);
            }
        } else {
            ProblemUserRecord problemUserRecord = new ProblemUserRecord();
            problemUserRecord.setProblemUid(problemUserRecordVO.getProblemUid());
            problemUserRecord.setDegree(degreeStatus.getType());
            problemUserRecord.setUserUid(userUid);
            problemUserRecord.insert();
        }


        //从Redis取出数据，判断该用户是否点击过
        String ip = IpUtils.getIpAddr(RequestHolder.getRequest());
        String jsonResult = redisUtil.get("PROBLEM_CLICK:" + ip + "#" + problem.getUid());
        if (StringUtils.isEmpty(jsonResult)) {
            problem.setClickCount(problem.getClickCount() + 1);
            // 设置1天过期
            redisUtil.setEx("PROBLEM_CLICK:" + ip + "#" + problem.getUid(), "1", 24, TimeUnit.HOURS);
        }


        // 更新问题表中的记录
        if (EDegreeStatus.MASTERY.equals(degreeStatus)) {
            String passJsonResult = redisUtil.get("PROBLEM_PASS:" + ip + "#" + problem.getUid());
            if (StringUtils.isEmpty(passJsonResult)) {
                problem.setPassCount(problem.getPassCount() + 1);
                // 设置1天过期
                redisUtil.setEx("PROBLEM_PASS:" + ip + "#" + problem.getUid(), "1", 24, TimeUnit.HOURS);
            }
        } else if (EDegreeStatus.NO_MASTERY.equals(degreeStatus)) {
            String noPassJsonResult = redisUtil.get("PROBLEM_NO_PASS:" + ip + "#" + problem.getUid());
            if (StringUtils.isEmpty(noPassJsonResult)) {
                problem.setNoPassCount(problem.getNoPassCount() + 1);
                // 设置1天过期
                redisUtil.setEx("PROBLEM_NO_PASS:" + ip + "#" + problem.getUid(), "1", 24, TimeUnit.HOURS);
            }
        }
        boolean isSave = problem.updateById();
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.PROBLEM_HOLD, problem);
        }

        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public int getProblemUserRecordsCount(ProblemUserRecordVO problemUserRecordVO) {
        QueryWrapper<ProblemUserRecord> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(problemUserRecordVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, problemUserRecordVO.getUserUid());
        }
        if (StringUtils.isNotEmpty(problemUserRecordVO.getDegree())) {
            queryWrapper.eq(SQLConf.DEGREE, problemUserRecordVO.getDegree());
        }
        return problemUserRecordService.count(queryWrapper);
    }

    @Override
    public Map<String, Object> getUserProblemRecords() {
        int problemCount = problemService.getProblemCount(new ProblemVO());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("problemCount", problemCount);
        resultMap.put("masteryProblemCount", 0);
        resultMap.put("notMasteryProblemCount", 0);

        String userUid = RequestHolder.getUserUid();
        // 用户未登录，返回做题数为0
        if (StringUtils.isEmpty(userUid)) {
            return resultMap;
        }

        // 查询掌握数量和未掌握数量
        ProblemUserRecordVO problemUserRecordVO = new ProblemUserRecordVO();
        problemUserRecordVO.setUserUid(userUid);
        problemUserRecordVO.setDegree(EDegreeStatus.NO_MASTERY.getType());
        int notMasteryProblemCount = getProblemUserRecordsCount(problemUserRecordVO);

        problemUserRecordVO.setDegree(EDegreeStatus.MASTERY.getType());
        int masteryProblemCount = getProblemUserRecordsCount(problemUserRecordVO);
        resultMap.put("masteryProblemCount", masteryProblemCount);
        resultMap.put("notMasteryProblemCount", notMasteryProblemCount);

        return resultMap;
    }
}
