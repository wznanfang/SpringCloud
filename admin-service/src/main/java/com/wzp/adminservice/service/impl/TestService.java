package com.wzp.adminservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.wzp.adminservice.config.CustomConfig;
import com.wzp.adminservice.es.LoginLogEs;
import com.wzp.adminservice.excel.LoginLogData;
import com.wzp.adminservice.repository.LoginLogRepository;
import com.wzp.adminservice.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
public class TestService {

    @Autowired
    private LoginLogRepository loginLogRepository;

    /**
     * 每批次处理的数据量
     */
    private static final int LIMIT = 10000;

    private String EXCEL_SAVE_PATH = CustomConfig.excelSavePath;

    public static Queue<Map<String, Object>> queue;

    static {
        queue = new ConcurrentLinkedQueue<Map<String, Object>>();
    }


    /**
     * 初始化队列
     */
    public void initQueue() {
        int listCount = 2000000;
        int count = listCount / LIMIT + (listCount % LIMIT > 0 ? 1 : 0);
        for (int i = 1; i <= count; i++) {
            Map<String, Object> map = new HashMap<>(3);
            map.put("page", i);
            map.put("limit", LIMIT);
            map.put("path", EXCEL_SAVE_PATH);
            queue.offer(map);
        }
    }


    /**
     * 多线程批量导出 excel
     *
     * @param response 用于浏览器下载
     * @throws InterruptedException
     */
    public void threadExcel(HttpServletResponse response) throws InterruptedException {
        initQueue();
        try {
            List<LoginLogData> data = new ArrayList<>();
            CountDownLatch cdl = new CountDownLatch(queue.size());
            while (queue.size() > 0) {
                executeAsyncTask(queue.poll(), cdl, data);
            }
            cdl.await();
            //压缩文件
            File zipFile = new File(EXCEL_SAVE_PATH.substring(0, EXCEL_SAVE_PATH.length() - 1) + ".zip");
            FileOutputStream fos1 = new FileOutputStream(zipFile);
            //压缩文件目录
            ZipUtils.toZip(EXCEL_SAVE_PATH, fos1, true);
            //发送zip包
            ZipUtils.sendZip(response, zipFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Async("taskExecutor")
    public void executeAsyncTask(Map<String, Object> map, CountDownLatch cdl, List<LoginLogData> data) {
        int page = (int) map.get("page");
        int size = (int) map.get("limit");
        try {
            List<LoginLogEs> list = loginLogRepository.findAllByUsernameLike("nf", PageRequest.of(page, size));
            list.forEach(loginLog -> {
                data.add(new LoginLogData(loginLog.getId(), loginLog.getUsername(), loginLog.getLoginTime(), loginLog.getCreateTime(), loginLog.getUpdateTime()));
            });
            String filepath = map.get("path").toString() + map.get("page") + ".xlsx";
            EasyExcel.write(filepath, LoginLogData.class).sheet("模板").doWrite(data);
            data.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //执行完毕线程数减一
        cdl.countDown();
        System.out.println("剩余任务数=======> " + cdl.getCount());
    }

}
