package cn.ls.hotnews;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.exception.SparkException;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static cn.ls.hotnews.PromptTemplate.PREDEFINED_INFORMATION;

/**
 * title: AITest
 * author: liaoshuo
 * date: 2024/12/3 11:45
 * description:
 */
@Slf4j
@SpringBootTest
public class AITest {

    // 请自定义自己的业务id
    private static final String requestIdTemplate = "hotnews-%d";
    private static final ObjectMapper mapper = new ObjectMapper();

    String a="华为Mate70系列开售\n\t" +
            "预约人数破百万，被誉“四最”的华为Mate\n" +
            "70系列，亮点有几何？ 在刚刚过去的“华为Mate品牌盛典”上，华为Mate 70系列被冠以“最精致强悍、最可靠、最出彩和最智慧”的形容。并且从11月18日12:08预约开始，不到10分钟就达到了40万预约人数，截至11月26日7时，预约人数已突破320万，而正式开售时间也已公布：12月4日10点08分。 如此强劲的预售势头，反映了用户对华为Mate 70系列的高度期待，同时，也证明了作为“史上最强Mate”，华为Mate 70系列凭借其原生鸿蒙系统和卓越性能，必然会成为智能手机市场中的亮点。 而用户们对华为Mate 70系列最关注的亮点，莫过于其内置的“先锋版”HarmonyOS NEXT。也正是因为，华为Mate 70系列是这样的“纯血鸿蒙”，才会有“精致强悍、可靠、出彩、智慧”的表现。 首先，“纯血鸿蒙”的华为Mate 70系列，让日常使用手机变得更丝滑、更及时、更节能。 秉承着“更顺手才能更顺心”的理念，当你点亮华为Mate 70系列手机的屏幕后，就会发现，它的每一次滑动、轻触、拖动，都流畅至极。 比如，只要轻轻扫过屏幕顶部，通知和控制中心便灵动的涌现在整个屏幕上；当你在桌面上滑动待办事项卡片时，手指轻触屏幕的瞬间，卡片边缘便会微微膨胀，就像是一张张实体卡片，而无论你用什么速度滑动卡片，它都能精准响应。 这种将现实世界的光影和引力效果，巧妙融入数字界面的技术，就是华为Mate 70系列的“动效引力体系”。也就是说，仅需150至350毫秒，就能带给用户流畅、跟手且有序的体验。 这种将软硬件的深度融合的技术，在提高交互响应速度、提高操作流畅度的同时，还能有效节省了系统资源。华为表示，手机流畅度提升30%，同时节省了1.2GB的运行内存，续航时间也得到了近一个小时的延长。 其次，“纯血鸿蒙”的华为Mate 70系列，让AI在手机的应用上变得更实用、更便捷、更安全。 “用AI来驱动硬件”的“纯血鸿蒙”，为手机的日常使用打开了新的路径。比如，当你在着急付款的时候，不用再在手机里慌张的翻找支付软件，只需将手机对准二维码，根本不用打开相机，而这就是由小艺AI驱动的“智感扫码”。 其实在华为Mate 70系列里，小艺AI不仅是随叫随到的助手，还能调动手机整体协作，深度挖掘设备的交互潜力。只要一声“小艺小艺”就能唤醒，并且快速进行识别、翻译、导览等功能。 此外，小艺AI不仅能识别指尖指令，更能识别指关节的交互任务。在用指关节截图、圈选屏幕内容之后，唤醒小艺就可以进行抠图、问答、购物等操作。 值得注意的是，在信息安全方面，“纯血鸿蒙”的华为Mate 70系列，为隐私设置了明确的禁区。比如，9类重要权限被禁止开放，确保应用只能访问用户允许的数据。 现在，越来越多的应用软件，都在HarmonyOS NEXT应用市场上架，而那些已经上架的应用软件，也在不断的更新迭代，甚至有些应用还会依托HarmonyOS NEXT进行更多开发，这说明“纯血鸿蒙”华为Mate 70系列的使用体验在日益完善。";

    @Resource
    SparkClient sparkClient;

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }

    /**
     * 智普ai
     */
    @Test
    void a() {
    }

    /**
     * 阶跃星辰
     */
    @Test
    void b() {
        JSONObject systemMessage = JSONUtil.createObj()
                .set("role", "system")
                .set("content", PREDEFINED_INFORMATION);

        JSONObject userMsg = JSONUtil.createObj()
                .set("role", "user")
                .set("content", a.replace("，", ","));

        // 构建请求体
        JSONObject requestBody = JSONUtil.createObj()
                .set("model", "step-1-8k")
                .set("messages", JSONUtil.createArray().set(systemMessage).set(userMsg));

        // 发送请求
        String result = HttpRequest.post("https://api.stepfun.com/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 1iGs2zzIuuxXusq7PfauhBOtuGVo3aCvc4zBgUZ8I9JpgWVFJ0csTVhrHcknT0qGN")
                .body(requestBody.toString())
                .execute()
                .body();
        //System.out.println(result);
        List<Object> list = (List<Object>) JSONUtil.parseObj(JSONUtil.parseObj(result)).get("choices");
        JSONObject entries = JSONUtil.parseObj(JSONUtil.parseObj(list.get(0)));
        String content = (String) JSONUtil.parseObj(entries.get("message")).get("content");
        //String str = "【【【【【\n\"纯血鸿蒙\"Mate70有多强?10分钟预约破40万,内幕揭秘!\n【【【【【\n\n华为Mate70系列自亮相以来，便稳坐风口浪尖。其预售势头之猛，令人瞠目结舌：从11月18日12:08预约启动，短短10分钟便突破40万大关，截至11月26日7时，预约人数已飙升至320万!这股热潮不仅彰显了用户对华为Mate70系列的殷殷期待，更昭示着其作为\"史上最强Mate\"的王者归来。\n\n那么，华为Mate70系列到底有何魔力，能掀起如此狂澜?答案就在其被冠以的\"四最\"头衔中：最精致强悍、最可靠、最出彩和最智慧。而这\"四最\"的底气，便源自其内置的\"先锋版\"HarmonyOS NEXT，也即\"纯血鸿蒙\"系统。\n\n**丝滑触感，极致体验**\n\n点亮华为Mate70系列的屏幕，你会发现每一次滑动、轻触、拖动都如行云流水般顺畅。这得益于其独特的\"动效引力体系\"，它将现实世界的光影和引力效果融入数字界面，让你的指尖与屏幕的每一次互动都变得妙趣横生。例如，当你在桌面滑动待办事项卡片时，卡片边缘会微微膨胀，仿佛一张张实体卡片在你指尖跃动。而这丝滑的触感背后，是华为 Mate70系列对软硬件深度融合的极致追求，它不仅提升了30%的手机流畅度，更节省了1.2GB的运行内存，延长了近一个小时的续航时间。\n\n**AI赋能，智慧生活**\n\n如果说\"动效引力体系\"让华为Mate70系列的操作体验焕然一新，那么其内置的小艺AI则让手机的智能化应用更上一层楼。想象一下，当你在拥挤的超市排队结账时，只需将手机对准二维码，无需打开相机，支付流程便能瞬间完成。这就是小艺AI驱动的\"智感扫码\"，它让支付变得更便捷、更高效。\n\n而小艺AI的本领远不止于此。它能调动手机整体协作，深度挖掘设备的交互潜力。一声\"小艺小艺\"，它便能快速响应，为你提供识别、翻译、导览等智能服务。更令人惊喜的是，小艺AI还能识别指关节的交互任务。只需用指关节敲击屏幕，你就能轻松实现截图、圈选等操作，而后续的抠图、问答、购物等功能也能一气呵成。\n\n**安全为先，隐私至上**\n\n在信息安全日益重要的今天，华为Mate70系列的\"纯血鸿蒙\"系统为用户隐私设置了明确的禁区。它禁止开放9类重要权限，确保应用只能访问用户允许的数据。这一举措无疑为用户的隐私安全筑起了一道坚实的防线。\n\n**未来可期，生态共荣**\n\n随着越来越多的应用软件在HarmonyOS NEXT应用市场上架，华为Mate70系列的使用体验也在不断完善。这些应用不仅在功能上不断迭代更新，更依托HarmonyOS NEXT进行更多开发，为用户带来更丰富、更个性化的使用体验。而这，也正是华为Mate70系列备受用户青睐的原因之一。\n\n华为Mate70系列的惊艳表现，让我们有理由相信，\"纯血鸿蒙\"的未来将更加美好。它将以卓越的性能、极致的体验和可靠的安全，引领智能手机市场的新潮流。而你，准备好迎接这场科技盛宴了吗?12月4日10点08分，让我们共同见证华为Mate70系列的王者归来!\n\n**互动话题**：你对华为Mate70系列的\"纯血鸿蒙\"系统有何期待?你认为它将如何改变你的手机使用习惯?\n\n**结尾留悬念**：华为Mate70系列的\"纯血鸿蒙\"系统还有哪些隐藏技能等待我们发掘?让我们一起期待12月4日的正式开售，共同解锁更多惊喜!\n\n【【【【【\n备选标题:\n1. Mate70\"纯血鸿蒙\"大揭秘:10分钟预约破40万的背后\n2. 华为Mate70:不止于丝滑,AI重新定义手机体验\n3. 320万人的共同选择:华为Mate70\"纯血鸿蒙\"的魔力解析";
        String[] strings = content.split("'【【【【【'");
        for (int i = 0; i < strings.length; i++) {
            System.out.println("下标" + i + "\n" + strings[i].trim());
        }
        //System.out.println(strings[1]);
        //System.out.println(strings[2]);
        //System.out.println(strings[3]);
    }

    /**
     * 讯飞ai调试
     */
    @Test
    void c() {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(PREDEFINED_INFORMATION));
        String replace = a.replace("，", ",");
        messages.add(SparkMessage.userContent(replace));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用最新3.0版本
                .apiVersion(SparkApiVersion.V4_0)
                .build();

        try {
            // 同步调用
            SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
            SparkTextUsage textUsage = chatResponse.getTextUsage();

            System.out.println("\n回答：" + chatResponse.getContent());
            System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
                    + "，回答tokens：" + textUsage.getCompletionTokens()
                    + "，总消耗tokens：" + textUsage.getTotalTokens());
        } catch (SparkException e) {
            System.out.println("发生异常了：" + e.getMessage());
        }

    }


}
