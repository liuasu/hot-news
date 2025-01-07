package cn.ls.hotnews;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * 主类（项目启动入口）
 */
// todo 如需开启 Redis，须移除 exclude 中的内容 exclude = {RedisAutoConfiguration.class}
@SpringBootApplication
@MapperScan("cn.ls.hotnews.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication {

    public static void main(String[] args) throws UnknownHostException {
        long startTime = System.currentTimeMillis();
        ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class, args);
        long endTime = System.currentTimeMillis();

        String port = context.getEnvironment().getProperty("server.port");
        String contextPath = context.getEnvironment().getProperty("server.servlet.context-path");
        String localHostAddress = InetAddress.getLocalHost().getHostAddress();

        String localUrl = "http://localhost:" + port ;
        String networkUrl = "http://" + localHostAddress + ":" + port;

        System.out.println("DONE successfully in " + (endTime - startTime) + "ms");
        System.out.println("Time: " + new Date());
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║ App listening at:                                  ║");
        System.out.println("║    > Local:   " + localUrl + "                      ");
        System.out.println("║    > Network: " + networkUrl + "                    ");
        System.out.println("║                                                    ║");
        System.out.println("║ Now you can open browser with the above addresses↑ ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

}
