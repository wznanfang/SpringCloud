package com.wzp.adminservice.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ContentRowHeight(20)
@HeadRowHeight(20)
@ColumnWidth(25)
public class LoginLogExcel {

    @ExcelProperty("序号")
    private Integer id;

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("登录时间")
    private String loginTime;

    @ExcelProperty("创建时间")
    private String createTime;

    @ExcelProperty("修改时间")
    private String updateTime;


}
