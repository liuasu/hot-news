package cn.ls.hotnews.utils;

import cn.ls.hotnews.model.dto.hotapi.HotApiAddReq;
import cn.ls.hotnews.model.entity.HotApi;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * title: ExcelUtils
 * author: liaoshuo
 * date: 2024/11/18 22:39
 * description:
 */
@Slf4j
public class ExcelUtils {

    public static List<HotApi> exceltoHotAPIList(MultipartFile multipartFile) {
        List<HotApi> list = new ArrayList<>();
        try {
            List<Map<Integer,String> > list1 = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(1)
                    .doReadSync();
            for (Map<Integer, String> map : list1) {
                HotApi hotApi = new HotApi();
                hotApi.setPlatform(map.get(0));
                hotApi.setApiName(map.get(1));
                hotApi.setApiURL(map.get(2));
                hotApi.setApiDescribe(map.get(3));
                list.add(hotApi);
            }
        } catch (IOException e) {
            log.error("表格处理错误", e);
            throw new RuntimeException(e);
        }

        return list;
    }
}
