package cn.ls.hotnews;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.model.entity.Article;
import cn.ls.hotnews.model.entity.Prompt;
import cn.ls.hotnews.service.PromptService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cn.ls.hotnews.PromptTemplate.PREDEFINED_INFORMATION;
import static cn.ls.hotnews.PromptTemplate.PREDEFINED_INFORMATION2;
import static cn.ls.hotnews.service.impl.PromptServiceImpl.toDBFormat;

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

    String a = "春节是刻在中国人DNA里的仪式感\n\t" +
            "申遗成功！春节是刻在中国人DNA里的仪式感\n" +
            "中国春节列入世界非遗。春节，是中国人最有仪式感的节日。贴春联、守岁、吃年夜饭、拜年……种种习俗里，是中国人朴素美好的生活理想。辞旧迎新、祈福纳祥、团圆美满，文化中国行看春节仪式感，你最期待哪个？网友：已经开始期待春节了！";
    @Resource
    SparkClient sparkClient;

    @Resource
    PromptService promptService;

    /**
     * 智普ai
     */
    @Test
    void a() {
        Prompt default2 = promptService.queryByDefault();
        JSONArray messages = new JSONArray();
        messages.add(new JSONObject()
                .set("role", "system")
                .set("content", default2.getPromptTemplate()));
        messages.add(new JSONObject()
                .set("role", "user")
                .set("content", a.replace("，", ",")));

        // 构建请求体
        JSONObject requestBody = new JSONObject()
                .set("model", "glm-4-plus")
                .set("messages", messages);

        // 发送请求
        String result = HttpUtil.createPost("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .header("Authorization", "Bearer " + "4839ee1b15fbcb5e436413041b51684f.rdaciz4ItpXo3b5I")
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute()
                .body();
        List<Object> choicesList = (List<Object>) JSONUtil.parseObj(JSONUtil.parseObj(result)).get("choices");
        Map<String, Object> map = (Map<String, Object>) choicesList.get(0);
        JSONObject entries = JSONUtil.parseObj(map.get("message"));
        System.out.println(entries.get("content"));
    }

    /**
     * 阶跃星辰
     */
    @Test
    void b() {
        Prompt default2 = promptService.queryByDefault();
        JSONObject systemMessage = JSONUtil.createObj()
                .set("role", "system")
                .set("content", default2.getPromptTemplate());

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
        Prompt default2 = promptService.queryByDefault();
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(default2.getPromptTemplate()));
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


    @Test
    void d() {
        //String str = "'【【【【【'\n华为Mate70系列开售：320万预约背后，谁在狂欢？\\n'【【【【【'\\n华为Mate70系列的开售，无疑是近期科技圈最热门的话题。预约人数破百万，被誉为“四最”的华为Mate70系列，究竟有何亮点？从11月18日12:08预约开始，不到10分钟就达到了40万预约人数，截至11月26日7时，预约人数已突破320万。如此强劲的预售势头，反映了用户对华为Mate70系列的高度期待。\\n\\n### 1. “纯血鸿蒙”的丝滑体验\\n华为Mate70系列的最大亮点，莫过于其内置的“先锋版”HarmonyOS NEXT。正是这一“纯血鸿蒙”系统，让日常使用手机变得更丝滑、更及时、更节能。点亮屏幕的那一刻，每一次滑动、轻触、拖动，都流畅至极。轻轻扫过屏幕顶部，通知和控制中心便灵动显现；滑动待办事项卡片，手指轻触的瞬间，卡片边缘微微膨胀，仿佛实体卡片般精准响应。\\n\\n这种“动效引力体系”，将现实世界的光影和引力效果巧妙融入数字界面，仅需150至350毫秒，就能带来流畅、跟手且有序的体验。软硬件深度融合的技术，不仅提高了交互响应速度和操作流畅度，还节省了系统资源。华为表示，手机流畅度提升30%，运行内存节省1.2GB，续航时间延长近一小时。\\n\\n### 2. AI应用的革命\\n“纯血鸿蒙”不仅让手机更流畅，还让AI应用变得更实用、更便捷、更安全。小艺AI不仅是随叫随到的助手，还能调动手机整体协作，深度挖掘设备的交互潜力。一声“小艺小艺”就能唤醒，快速进行识别、翻译、导览等功能。指关节截图、圈选屏幕内容后，唤醒小艺即可进行抠图、问答、购物等操作。\\n\\n在信息安全方面，“纯血鸿蒙”为隐私设置了明确的禁区。9类重要权限被禁止开放，确保应用只能访问用户允许的数据。越来越多的应用软件在HarmonyOS NEXT应用市场上架，并不断更新迭代，甚至依托HarmonyOS NEXT进行更多开发，使用体验日益完善。\\n\\n### 3. 性能与设计的完美结合\\n华为Mate70系列不仅在系统上表现出色，硬件配置同样强悍。搭载最新麒麟芯片，性能强劲；超高清摄像头，拍照效果出众；精致的外观设计，手感极佳。无论是日常使用还是高强度工作，都能轻松应对。\\n\\n### 用户热议：你准备好抢购了吗？\\n如此强大的配置和系统，难怪预约人数爆棚。网友们纷纷表示：“这次一定要抢到！”“华为Mate70系列，值得期待！”你准备好在12月4日10点08分开售时抢购了吗？\\n\\n### 华为Mate70系列的未来展望\\n随着华为Mate70系列的正式开售，市场反应将会如何？是否会打破销售记录？用户实际体验又将如何？这一切都充满了悬念。可以预见的是，华为Mate70系列将成为智能手机市场的一颗璀璨明珠。\\n\\n### 互动时间：你最期待哪一功能？\\n在评论区留言，分享你最期待的华为Mate70系列功能，看看大家的选择是否和你一样！\\n\\n'【【【【【'\\n备选标题:\\n1. 华为Mate70系列开售：320万预约，谁在抢？\\n2. “纯血鸿蒙”加持，华为Mate70系列亮点解析\\n3. 华为Mate70系列：预约破百万，背后有何玄机？";
        //
        //String[] strings = str.split("'【【【【【'");
        //for (String string : strings) {
        //    System.out.println(string);
        //}
        System.out.println(toDBFormat(PREDEFINED_INFORMATION));
        System.out.println();
        System.out.println(toDBFormat(PREDEFINED_INFORMATION2));
    }

    @Test
    void e(){
        String str= "【【【【【'\n" +
                "'装备制造业新纪元：智能、绿色、融合引领未来'\n" +
                "'【【【【【'\n" +
                "随着2024装备制造业发展大会在重庆的圆满落幕，中国装备制造业正迎来一个崭新的发展阶段。本次大会以“智能、绿色、融合”为核心议题，不仅展示了众多前沿技术和创新成果，更标志着我国装备制造业正在经历一场深刻的变革。从智能化生产线到绿色能源解决方案，再到产业链上下游的高度融合，这些变化共同预示着一个更加高效、环保且协同发展的产业新时代的到来。\n" +
                "\n" +
                "### 一、智能化：重塑生产模式\n" +
                "\n" +
                "智能化已成为推动装备制造业转型升级的关键力量之一。通过引入物联网、大数据、云计算等先进技术，企业能够实现生产过程的自动化与信息化管理，显著提高生产效率和产品质量。例如，在重庆某新能源汽车工厂内，借助于智能化技术的支持，该厂仅用三个月时间便达到了满负荷运作状态，并且月产量突破了两万台大关；同时，其制造成本也降低了近20%。此外，数字孪生技术的应用使得产品研发周期缩短了30%，进一步加速了新产品上市的速度。\n" +
                "\n" +
                "### 二、绿色化：构建可持续发展之路\n" +
                "\n" +
                "面对全球气候变化带来的挑战，“双碳”目标成为中国经济发展的重要指导方针之一。在此背景下，“绿色化”成为了装备制造业转型过程中不可或缺的一部分。无论是氢燃料燃气轮机的研发还是采用新材料减少能耗的做法，都体现了行业对于环境保护责任的认识加深以及对未来清洁能源利用前景的信心增强。据统计，经过一系列节能减排措施后，部分汽车制造企业的年度温室气体排放量相比前一年下降超过30%，固体废弃物综合利用率则达到了90%以上。\n" +
                "\n" +
                "### 三、融合化：促进全产业链协同发展\n" +
                "\n" +
                "除了技术创新外，加强产业链内部及跨行业间的交流合作也是提升整体竞争力的有效途径。近年来，许多企业开始注重与上下游合作伙伴建立紧密联系，并通过共享资源、信息和技术来优化供应链管理体系。比如一家农机设备制造商就通过定期发布需求清单的方式促进了上下游企业的沟通协作，成功实现了多项关键零部件国产化替代项目。与此同时，政府相关部门也在积极出台政策鼓励产学研用结合，旨在打造开放包容的创新生态系统。\n" +
                "\n" +
                "综上所述，“智能+绿色+融合”的战略组合拳正在为中国装备制造业注入新的活力。随着更多支持性政策措施落地实施，相信未来几年内我们将见证这一领域涌现出更多具有国际影响力的优秀企业和品牌。然而，值得注意的是，尽管取得了一定成绩，但在推进过程中仍面临不少困难与挑战，如如何平衡经济效益与社会效益之间的关系、怎样更好地吸引并留住人才等问题仍需各方共同努力解决。那么，您认为接下来我国装备制造业还有哪些领域值得重点关注或投资呢？欢迎留言讨论！\n" +
                "\n" +
                "'【【【【【'\n" +
                "备选标题方案：\n" +
                "1. '装备制造业革新：智能化、绿色化与融合化齐头并进'\n" +
                "2. '2024装备制造业大会亮点：智能、绿色、融合成主旋律'\n" +
                "3. '中国装备制造业新篇章：三大关键词解读未来发展'";
        String[] strings = str.replace("'","").split("【【【【【");
        Article article = new Article();
        article.setTitle(strings[1]);
        article.setConText(strings[2]);

        String string = strings[3].trim();
        if(string!=null){
            String[] split = string.split("\n");
            article.setAlternateTitleList(Arrays.asList(split[1],split[2],split[3]));
        }
    }

}
