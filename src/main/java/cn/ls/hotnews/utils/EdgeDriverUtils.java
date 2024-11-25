package cn.ls.hotnews.utils;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * title: EdgeDriverUtils
 * author: liaoshuo
 * date: 2024/11/19 10:51
 * description:
 */
@Service
public class EdgeDriverUtils {
    @Value("${Edge.Property}")
    public  String Property;

    private EdgeOptions init(){
        System.setProperty("webdriver.edge.driver", Property);
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments(String.format("user-data-dir=E:\\Cache\\%s", "edge"));
        return options;
    }

    public  EdgeDriver initEdgeDriver() {
        return new EdgeDriver(init());
    }

    /**
     * 自动操作(不打开浏览器)
     *
     * @return {@link EdgeDriver }
     */
    public  EdgeDriver initEdgeDriverNotHeadless() {
        return new EdgeDriver( init().setHeadless(true));
    }
}
