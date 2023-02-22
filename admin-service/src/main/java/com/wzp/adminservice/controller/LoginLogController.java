package com.wzp.adminservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.wzp.adminservice.config.Result;
import com.wzp.adminservice.service.impl.TestService;
import com.wzp.adminservice.service.LoginLogService;
import com.wzp.adminservice.vo.LoginLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/loginLog")
public class LoginLogController {

    @Autowired
    private LoginLogService loginLogService;


    /**
     * 分页查询登录日志
     *
     * @param loginLogVO 条件
     * @return
     */
    @GetMapping("/findAll")
    public Result findAll(LoginLogVO loginLogVO) {
        JSONObject jsonObject = loginLogService.findAll(loginLogVO);
        return Result.ok(jsonObject);
    }


    /**
     * 登录日志excel导出
     *
     * @param loginLogVO 条件
     * @param response
     * @throws IOException
     */
    @GetMapping("/excelExport")
    public void excelExport(LoginLogVO loginLogVO, HttpServletResponse response) throws IOException {
        loginLogService.loginLogExcel(loginLogVO, response);
    }

    @Autowired
    private TestService testService;

    @GetMapping("batchExcelExport")
    public void easyExcelDownload(LoginLogVO loginLogVO, HttpServletResponse response) throws UnsupportedEncodingException {
        StopWatch stopWatch = new StopWatch("任务的耗时");
        stopWatch.start();
//        testService.threadExcel(response);
        loginLogService.loginLogBatchExcel(loginLogVO, response);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


}
