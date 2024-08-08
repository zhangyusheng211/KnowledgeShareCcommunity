package com.moxi.mogublog.web.restapi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.annotion.UserAuth.UserAuth;
import com.moxi.mogublog.commons.entity.Problem;
import com.moxi.mogublog.commons.entity.ProblemTagRelation;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.annotion.PublishLimitVerify.PublishLimitVerify;
import com.moxi.mogublog.web.annotion.SubmitVerify.SubmitVerify;
import com.moxi.mogublog.web.annotion.checkRegexVerify.CheckRegexVerify;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.ProblemService;
import com.moxi.mogublog.xo.service.ProblemTagService;
import com.moxi.mogublog.xo.service.ProblemUserRecordService;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.GetOne;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 问题相关 RestApi
 *
 * @author 陌溪
 * @date 2021年5月5日18:13:52
 */
@Api(value = "问题相关接口", tags = {"问题相关接口"})
@RestController
@RequestMapping("/problem")
@Slf4j
public class ProblemRestApi {

    @Resource
    private ProblemService problemService;
    @Resource
    private ProblemTagService problemTagService;
    @Resource
    private ProblemUserRecordService problemUserRecordService;

    @ApiOperation(value = "刷数", notes = "刷数", response = String.class)
    @GetMapping("/flushData")
    public String flushData() {
        CsvReader reader = CsvUtil.getReader();
        //从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file("E:\\study_work_space\\mywork5\\mogu_blog_v2_business\\mogu_web\\src\\main\\java\\com\\moxi\\mogublog\\web\\restapi\\22.csv"));
        List<CsvRow> rows = data.getRows();
        //遍历行
        int count = 0;
        for (CsvRow csvRow : rows) {
            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
            if (count != 0) {
                List<String> list = csvRow.getRawList();
                Problem problem = new Problem();
                problem.setTitle(list.get(1));
                problem.setContent(list.get(1));
                problem.setSummary(list.get(1));
                problem.setAnswer(list.get(2));
                problem.setProblemTagUid(list.get(5));
                problem.setUserUid("e69785e2c4a70ca92f16cf46aefb63ce");
                problem.insert();
                ProblemTagRelation problemTagRelation = new ProblemTagRelation();
                problemTagRelation.setProblemUid(problem.getUid());
                problemTagRelation.setTagUid(list.get(5));
                problemTagRelation.insert();
            }
            count++;
        }
        return ResultUtil.successWithMessage("刷数成功");
    }

    @ApiOperation(value = "刷数问题数", notes = "刷数问题数", response = String.class)
    @GetMapping("/flushProblemCount")
    public String flushProblemCount() {
        return problemService.flushTagProblemCount();
    }

    /**
     * 获取用户做题记录
     * @return
     */
    @ApiOperation(value = "获取用户做题记录", notes = "获取用户做题记录", response = String.class)
    @GetMapping("/getUserProblemRecords")
    public String getUserProblemRecords() {
        return ResultUtil.successWithData(problemUserRecordService.getUserProblemRecords());
    }



    @ApiOperation(value = "获取问题列表", notes = "获取问题列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody ProblemVO problemVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问题列表");
        // 前端获取的必须是已发布的
        problemVO.setIsPublish(EPublish.PUBLISH);
        problemVO.setAuditStatus(EAuditStatus.AGREE);
        problemVO.setIsShowAnswer(EOpenStatus.CLOSE);
        return ResultUtil.result(SysConf.SUCCESS, problemService.getPageList(problemVO));
    }

    @ApiOperation(value = "获取问题列表", notes = "获取问题列表", response = String.class)
    @PostMapping("/queryPage")
    public String queryPage(@Validated({GetList.class}) @RequestBody ProblemVO problemVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问题列表");
        // 前端获取的必须是已发布的
        problemVO.setIsPublish(EPublish.PUBLISH);
        problemVO.setAuditStatus(EAuditStatus.AGREE);
//        problemVO.setIsShowAnswer(EOpenStatus.CLOSE);
        return ResultUtil.result(SysConf.SUCCESS, problemService.queryPage(problemVO));
    }

    @ApiOperation(value = "获取问题详情", notes = "获取问题详情")
    @PostMapping("/getProblem")
    public String getProblem(@RequestBody ProblemVO problemVO) {
        return problemService.getProblem(problemVO);
    }

    @ApiOperation(value = "问题快问快答翻页", notes = "问答翻页", response = String.class)
    @PostMapping("/info")
    public String info(@Validated({GetOne.class}) @RequestBody ProblemVO problemVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, problemService.getPageList(problemVO));
    }

    @UserAuth(EAuthCode.ADD_PROBLEM)
    @CheckRegexVerify(behavior = ERegexType.ADD_PROBLEM)
    @PublishLimitVerify(behavior = EPublishLimitType.PROBLEM_COUNT)
    @SubmitVerify
    @AvoidRepeatableCommit
    @BussinessLog(value = "增加问题", behavior = EBehavior.ADD_PROBLEM)
    @ApiOperation(value = "增加问题", notes = "增加问题", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody ProblemVO problemVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("增加问题");
        return problemService.addProblem(problemVO);
    }

//    @LoginVerify
//    @AvoidRepeatableCommit
//    @BussinessLog(value = "编辑问题", behavior = EBehavior.EDIT_PROBLEM)
//    @ApiOperation(value = "编辑问题", notes = "编辑问题", response = String.class)
//    @PostMapping("/edit")
//    public String edit(@Validated({Update.class}) @RequestBody ProblemVO problemVO, BindingResult result) {
//        ThrowableUtils.checkParamArgument(result);
//        log.info("编辑问题");
//        return problemService.editProblem(problemVO);
//    }
//
//    @LoginVerify
//    @AvoidRepeatableCommit
//    @BussinessLog(value = "删除问题", behavior = EBehavior.DELETE_PROBLEM)
//    @ApiOperation(value = "删除问题", notes = "删除博客", response = String.class)
//    @PostMapping("/delete")
//    public String delete(HttpServletRequest request, @Validated({Delete.class}) @RequestBody ProblemVO problemVO, BindingResult result) {
//        // 参数校验
//        ThrowableUtils.checkParamArgument(result);
//        List<ProblemVO> problemVOList = new ArrayList<>();
//        problemVOList.add(problemVO);
//        return problemService.deleteBatchProblem(problemVOList);
//    }

    @ApiOperation(value = "获取标签列表", notes = "获取标签列表", response = String.class)
    @PostMapping("/getTagList")
    public String getTagList(@Validated({GetList.class}) @RequestBody ProblemTagVO problemTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return ResultUtil.result(SysConf.SUCCESS, problemTagService.getPageList(problemTagVO));
    }

    @ApiOperation(value = "获取所有的标签列表", notes = "获取所有的标签列表", response = String.class)
    @PostMapping("/getAllTagList")
    public String getAllTagList() {
        return ResultUtil.result(SysConf.SUCCESS, problemTagService.getAllList());
    }

    @BussinessLog(value = "点击问题掌握程度", behavior = EBehavior.PROBLEM_DEGREE)
    @AvoidRepeatableCommit
    @ApiOperation(value = "点击问题掌握程度", notes = "点击问题掌握程度", response = String.class)
    @PostMapping("/problemDegree")
    public String problemDegree(@Validated({Insert.class}) @RequestBody ProblemUserRecordVO problemUserRecordVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("点击问题掌握程度");
        return problemUserRecordService.addProblemUserRecord(problemUserRecordVO);
    }

    @ApiOperation(value = "随机一题", notes = "随机一题", response = String.class)
    @PostMapping("/randomProblem")
    public String randomProblem() {
        log.info("随机一题");
        return ResultUtil.successWithData(problemService.randomProblem(null));
    }

    /**
     * 修改答案
     *
     * @param generalEditVO
     * @param result
     * @return
     */
    @AvoidRepeatableCommit
    @BussinessLog(value = "修改答案", behavior = EBehavior.EDIT_ANSWER)
    @ApiOperation(value = "修改答案", notes = "修改答案", response = String.class)
    @PostMapping("/editAnswer")
    public String editAnswer(@Validated({Update.class}) @RequestBody GeneralEditVO generalEditVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("修改答案");
        return problemService.editAnswer(generalEditVO);
    }


    /**
     * 生成试卷(题目分类)
     *
     * @param examVO
     * @param result
     * @return
     */
    @BussinessLog(value = "生成试卷(题目分类)", behavior = EBehavior.BUILDER_EXAM)
    @ApiOperation(value = "生成试卷(题目分类)", notes = "生成试卷(题目分类)", response = String.class)
    @PostMapping("/builderExam")
    public String builderExam(@RequestBody ExamVO examVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, problemService.builderExam(examVO));
    }

}

