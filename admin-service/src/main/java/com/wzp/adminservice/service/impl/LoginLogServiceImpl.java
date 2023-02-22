package com.wzp.adminservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzp.adminservice.dao.LoginLog;
import com.wzp.adminservice.es.LoginLogEs;
import com.wzp.adminservice.excel.LoginLogData;
import com.wzp.adminservice.mapper.LoginLogMapper;
import com.wzp.adminservice.repository.LoginLogRepository;
import com.wzp.adminservice.service.LoginLogService;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @description 针对表【login_log】的数据库操作Service实现
 * @createDate 2023-02-16 14:54:52
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {


    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private LoginLogRepository loginLogRepository;


    /**
     * 每次查询数量
     */
    private static final Integer EXCEL_ROWS = 20000;
    /**
     * 每个sheet存放数量
     */
    private static final Integer EXCEL_SHEET_ROW = 1000000;


    @Override
    public JSONObject findAll(LoginLogVO loginLogVO) {
        Pageable pageable = PageRequest.of(loginLogVO.getPageNumber(), loginLogVO.getPageSize());
        QueryBuilder queryBuilder = null;
        if (ObjectUtils.isNotEmpty(loginLogVO.getUsername())) {
            queryBuilder = QueryBuilders.matchQuery("username", loginLogVO.getUsername());
        }
        NativeSearchQuery build = new NativeSearchQueryBuilder().withQuery(queryBuilder).withPageable(pageable).build();
        SearchHits<LoginLogEs> search = elasticsearchOperations.search(build, LoginLogEs.class);
        List<SearchHit<LoginLogEs>> searchHits = search.getSearchHits();
        List<LoginLogEs> loginLogs = new ArrayList<>();
        searchHits.forEach(hit -> {
            loginLogs.add(hit.getContent());
        });
        Map<String, Object> map = new HashMap<>(3);
        map.put("pageSize", loginLogVO.getPageSize());
        map.put("pageNumber", loginLogVO.getPageNumber());
        map.put("total", search.getTotalHits());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page", map);
        jsonObject.put("list", loginLogs);
        return jsonObject;
    }


    @Override
    public void loginLogExcel(LoginLogVO loginLogVO, HttpServletResponse response) throws UnsupportedEncodingException {
        StopWatch sw = new StopWatch("任务的耗时");
        sw.start();
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("登录日志.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream()).build();
            //查询满足条件的总条数,根据数据总数据量和每次拿的数据量计算出需要拿几次数据（设定一百万数据存放一个sheet）
            Long totalNum = loginLogRepository.countAllByUsernameLike(loginLogVO.getUsername());
            long number = (totalNum % EXCEL_ROWS) > 0 ? (totalNum / EXCEL_ROWS) + 1 : (totalNum / EXCEL_ROWS);
            int count = 0;
            List<LoginLogData> data = new ArrayList<>();
            for (int i = 0; i <= number - 1; i++) {
                System.out.println(i);
                //查询数据
                List<LoginLogEs> list = loginLogRepository.findAllByUsernameLike(loginLogVO.getUsername(), PageRequest.of(i, EXCEL_ROWS));
                list.forEach(loginLog -> {
                    data.add(new LoginLogData(loginLog.getId(), loginLog.getUsername(), loginLog.getLoginTime(), loginLog.getCreateTime(), loginLog.getUpdateTime()));
                });
                //count控制插入哪一个sheet
                count += list.size();
                Integer sheet = (count % EXCEL_SHEET_ROW) > 0 ? (count / EXCEL_SHEET_ROW) + 1 : (count / EXCEL_SHEET_ROW);
                WriteSheet writeSheet = EasyExcel.writerSheet(sheet, "数据" + sheet).head(LoginLogData.class).build();
                excelWriter.write(data, writeSheet);
                data.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
        sw.stop();
        System.out.println(sw.getTotalTimeMillis());
    }


    @Override
    public void loginLogBatchExcel(LoginLogVO loginLogVO, HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("登录日志.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(response.getOutputStream()).build();
            //查询满足条件的总条数,根据数据总数据量和每次拿的数据量计算出需要拿几次数据（设定一百万数据存放一个sheet）
            long number = 100L;
            int count = 0;
            List<LoginLogData> data = new ArrayList<>();
            for (int i = 0; i <= number - 1; i++) {
                System.out.println(i);
                //查询数据
                List<LoginLogEs> list = loginLogRepository.findAllByUsernameLike(loginLogVO.getUsername(), PageRequest.of(i, 20000));
                list.forEach(loginLog -> {
                    data.add(new LoginLogData(loginLog.getId(), loginLog.getUsername(), loginLog.getLoginTime(), loginLog.getCreateTime(), loginLog.getUpdateTime()));
                });
                //count控制插入哪一个sheet
                count += list.size();
                Integer sheet = (count % EXCEL_SHEET_ROW) > 0 ? (count / EXCEL_SHEET_ROW) + 1 : (count / EXCEL_SHEET_ROW);
                WriteSheet writeSheet = EasyExcel.writerSheet(sheet, "数据" + sheet).head(LoginLogData.class).build();
                excelWriter.write(data, writeSheet);
                data.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }


}




