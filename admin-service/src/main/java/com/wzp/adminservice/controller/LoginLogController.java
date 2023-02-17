package com.wzp.adminservice.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.wzp.adminservice.config.Result;
import com.wzp.adminservice.es.LoginLog;
import com.wzp.adminservice.excel.LoginLogData;
import com.wzp.adminservice.repository.LoginLogRepository;
import com.wzp.adminservice.vo.LoginLogVO;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/loginLog")
public class LoginLogController {


    @Autowired
    private LoginLogRepository loginLogRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;


    private static final Integer EXCEL_NUMBER = 10;
    private static final Integer EXCEL_SHEET_ROW = 20;


    /**
     * 分页查询登录日志
     *
     * @param loginLogVO 条件
     * @return
     */
    @GetMapping("/findAll")
    public Result findAll(LoginLogVO loginLogVO) {
        Pageable pageable = PageRequest.of(loginLogVO.getPageNumber(), loginLogVO.getPageSize());
        QueryBuilder queryBuilder = null;
        if (ObjectUtils.isNotEmpty(loginLogVO.getUsername())) {
            queryBuilder = QueryBuilders.matchQuery("username", loginLogVO.getUsername());
        }
        NativeSearchQuery build = new NativeSearchQueryBuilder().withQuery(queryBuilder).withPageable(pageable).build();
        SearchHits<LoginLog> search = elasticsearchOperations.search(build, LoginLog.class);
        List<SearchHit<LoginLog>> searchHits = search.getSearchHits();
        List<LoginLog> loginLogs = new ArrayList<>();
        searchHits.forEach(hit -> {
            loginLogs.add(hit.getContent());
        });
        Map<String, Object> map = new HashMap<>();
        map.put("pageSize", loginLogVO.getPageSize());
        map.put("pageNumber", loginLogVO.getPageNumber());
        map.put("total", search.getTotalHits());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page", map);
        jsonObject.put("list", loginLogs);
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
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("登录日志.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build()) {
            //查询满足条件的总条数
            Long totalNum = loginLogRepository.countAllByUsernameLike(loginLogVO.getUsername());
            //根据数据总数据量和每次拿的数据量计算出需要拿几次数据（设定一百万数据存放一个sheet）
            Long number = (totalNum % EXCEL_NUMBER) > 0 ? (totalNum / EXCEL_NUMBER) + 1 : (totalNum / EXCEL_NUMBER);
            int count = 0;
            List<LoginLogData> data = new ArrayList<>();
            for (int i = 0; i <= number - 1; i++) {
                //查询数据
                List<LoginLog> list = loginLogRepository.findAllByUsernameLike(loginLogVO.getUsername(), PageRequest.of(i, EXCEL_NUMBER));
                list.forEach(loginLog -> {
                    data.add(new LoginLogData(loginLog.getId(), loginLog.getUsername(), loginLog.getLoginTime(), loginLog.getCreateTime(), loginLog.getUpdateTime()));
                });
                //count 将控制插入哪一个sheet
                count += list.size();
                Integer sheet = (count % EXCEL_SHEET_ROW) > 0 ? (count / EXCEL_SHEET_ROW) + 1 : (count / EXCEL_SHEET_ROW);
                WriteSheet writeSheet = EasyExcel.writerSheet(sheet, "数据" + sheet).head(LoginLogData.class).build();
                excelWriter.write(data, writeSheet);
                data.clear();
            }
        }
    }


}
