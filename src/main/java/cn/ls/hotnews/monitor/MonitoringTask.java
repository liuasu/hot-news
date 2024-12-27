package cn.ls.hotnews.monitor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.ssl.SSLSocketFactoryBuilder;
import cn.hutool.http.ssl.TrustAnyHostnameVerifier;
import cn.ls.hotnews.ai.AIService;
import cn.ls.hotnews.model.dto.thirdpartyaccount.AccountTrusteeship;
import cn.ls.hotnews.model.entity.AiConfig;
import cn.ls.hotnews.model.entity.Article;
import cn.ls.hotnews.model.entity.HotApi;
import cn.ls.hotnews.model.entity.Prompt;
import cn.ls.hotnews.model.vo.ArticleVO;
import cn.ls.hotnews.service.HotNewsService;
import cn.ls.hotnews.strategy.ChromeDriverStrategy;
import cn.ls.hotnews.strategy.HotNewsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static cn.ls.hotnews.constant.CommonConstant.articleKey;

/**
 * 监控任务类
 * 负责监控热点新闻并使用AI生成文章进行发布
 */
@Slf4j
@Component
public class MonitoringTask {

    // 监控间隔时间设置
    private static final long MONITOR_INTERVAL = 5;  // 每5分钟一个监控周期
    private static final long REST_INTERVAL = 1;     // 每次处理后休息1分钟

    // 线程安全的集合和状态控制
    private final Map<String, MonitoringState> accountStates;  // 存储每个账号的发布状态
    private final Queue<ArticleVO> articleVOQueue;   // 存储待处理的文章队列
    private final AtomicBoolean isRunning;          // 控制整个监控任务的运行状态
    private final AtomicBoolean isResting;          // 控制是否处于休息状态
    private final Set<String> processedTypes;        // 记录已处理的新闻类型

    // 监控任务所需的配置和服务
    private List<AccountTrusteeship> accounts;       // 需要监控的账号列表
    private Map<String, List<HotApi>> urlMap;       // 热点API映射
    private Prompt prompt;                           // AI提示词
    private AiConfig aiConfig;                      // AI配置
    private AIService aiService;                    // AI服务
    private LocalDateTime nowLocalDateTime;
    @Resource
    private HotNewsStrategy hotNewsStrategy;

    @Resource
    private ChromeDriverStrategy chromeDriverStrategy;

    // 默认构造函数
    public MonitoringTask() {
        this.accountStates = new ConcurrentHashMap<>();
        this.articleVOQueue = new ConcurrentLinkedQueue<>();
        this.isRunning = new AtomicBoolean(true);
        this.isResting = new AtomicBoolean(false);
        this.processedTypes = new HashSet<>();
    }

    /**
     * 初始化监控任务
     * 在使用前必须调用此方法设置必要参数
     */
    public void init(Prompt prompt, AiConfig aiConfig, List<AccountTrusteeship> accounts, Map<String, List<HotApi>> urlMap, AIService aiService) {
        this.accounts = accounts;
        this.prompt = prompt;
        this.aiConfig = aiConfig;
        this.urlMap = urlMap;
        this.aiService = aiService;
        initializeAccountStates();
        this.processedTypes.clear(); // 清空已处理类型集合
    }

    /**
     * 初始化所有账号的状态
     */
    private void initializeAccountStates() {
        accounts.forEach(account -> accountStates.put(account.getAccount(), new MonitoringState()));
    }

    /**
     * 启动监控任务
     * 使用CompletableFuture异步执行监控循环
     */
    public void start() {
        CompletableFuture.runAsync(this::monitoringLoop);
    }

    /**
     * 主监控循环
     * 负责控制监控和休息的时间周期
     */
    private void monitoringLoop() {
        while (isRunning.get()) {
            try {
                if (!isResting.get()) {
                    log.info("开始监控热点新闻...");
                    nowLocalDateTime = LocalDateTime.now();

                    // 获取所有未处理的类型
                    List<String> remainingTypes = accounts.stream()
                            .map(AccountTrusteeship::getHotType)
                            .distinct()
                            .filter(type -> !processedTypes.contains(type))
                            .collect(Collectors.toList());

                    if (remainingTypes.isEmpty()) {
                        // 所有类型都已处理，重置处理状态并进入休息
                        processedTypes.clear();
                        isResting.set(true);
                        log.info("所有类型已处理完成，进入休息时间，持续{}分钟...", REST_INTERVAL);
                        sleepMinutes(REST_INTERVAL);
                        isResting.set(false);
                        continue;
                    }

                    // 处理一个未处理的类型
                    String currentType = remainingTypes.get(0);
                    fetchAndProcessNewsByType(currentType);

                    // 不在这里进入休息状态，而是继续处理下一个类型
                    // 短暂休息1秒，避免请求过于频繁
                    sleepSeconds(1);
                }
                if(isResting.get()){
                    log.info("休息,持续时间{}分钟...",REST_INTERVAL);
                    sleepMinutes(1L);
                    isResting.set(false);
                }
            } catch (Exception e) {
                log.error("监控过程发生错误", e);
                sleepSeconds(10);
            }
        }
    }

    /**
     * 获取并处理指定类型的热点新闻
     */
    private void fetchAndProcessNewsByType(String hotType) {
        try {
            List<HotApi> hotApis = urlMap.get(hotType);
            if (hotApis == null) return;

            // 获取该类型的所有账号
            List<AccountTrusteeship> typeAccounts = accounts.stream()
                    .filter(account -> account.getHotType().equals(hotType))
                    .collect(Collectors.toList());

            if (typeAccounts.isEmpty()) return;

            // 遍历该类型的API
            for (HotApi itemHotApi : hotApis) {
                log.info("{} - {} 监控中...", hotType, itemHotApi.getApiName());
                String apiURL = itemHotApi.getApiURL();
                String platform = itemHotApi.getPlatform().split("_")[0];

                try {
                    String body = doSecureGet(apiURL);
                    if (body != null) {
                        HotNewsService hotNewsService = hotNewsStrategy.getHotNewsByPlatform(platform);
                        Map<String, Object> extractResponseInfo = hotNewsService.extractResponseInfo(body, nowLocalDateTime);

                        if (CollectionUtil.isNotEmpty(extractResponseInfo)) {
                            ArticleVO articleVO = (ArticleVO) extractResponseInfo.get(articleKey);
                            if (articleVO != null) {
                                articleVO.setLabType(hotType);
                                articleVOQueue.add(articleVO);

                                // 找到可用账号并发布
                                Optional<AccountTrusteeship> availableAccount = findAvailableAccount(hotType);
                                if (availableAccount.isPresent()) {
                                    // 标记该类型已处理
                                    processedTypes.add(hotType);
                                    processHotNews();
                                    return; // 成功处理了文章
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("获取URL内容失败: {}", apiURL, e);
                }
            }
            // 如果遍历完所有API都没有找到可处理的内容
            log.info("{} 类型暂无可处理的内容", hotType);
            processedTypes.add(hotType); // 标记该类型已处理，避免重复检查
        } catch (Exception e) {
            log.error("处理热点新闻失败: {}", hotType, e);
        }
    }

    /**
     * 执行安全的GET请求
     *
     * @param url 请求URL
     * @return 响应内容
     */
    private String doSecureGet(String url) {
        try {
            HttpRequest request = HttpRequest.get(url).setSSLSocketFactory(SSLSocketFactoryBuilder.create().setTrustManagers()  // 信任所有证书
                            .build()).setHostnameVerifier(new TrustAnyHostnameVerifier())  // 信任所有主机名
                    .timeout(10000);  // 设置超时时间为10秒

            try (HttpResponse response = request.execute()) {
                if (response.isOk()) {
                    return response.body().replaceAll(" ","");
                } else {
                    log.warn("请求失败, 状态码: {}, URL: {}", response.getStatus(), url);
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("请求异常: {}", url, e);
            return null;
        }
    }


    /**
     * 处理队列中的热点新闻
     * 为每条新闻找到合适的发布账号
     */
    private void processHotNews() {
        while (!articleVOQueue.isEmpty() && !isResting.get()) {
            ArticleVO articleVO = articleVOQueue.poll();
            if (articleVO == null) continue;

            // 查找可用的发布账号
            Optional<AccountTrusteeship> availableAccount = findAvailableAccount(articleVO.getLabType());
            if (availableAccount.isPresent()) {
                publishNews(articleVO, availableAccount.get());
                // 发布后入休息状态
                isResting.set(true);
            }
        }
    }

    /**
     * 查找可用的发布账号
     * 根据新闻类型和账号状态筛选
     */
    private Optional<AccountTrusteeship> findAvailableAccount(String newsType) {
        return accounts.stream().filter(account -> account.getHotType().equals(newsType)).filter(account -> isAccountAvailable(account.getAccount())).findFirst();
    }

    /**
     * 检查账号是否可用
     * 根据上次发布时间判断是否可以再次发布
     */
    private boolean isAccountAvailable(String account) {
        MonitoringState state = accountStates.get(account);
        return state != null && state.canPublish();
    }

    /**
     * 发布新闻
     */
    private void publishNews(ArticleVO articleVO, AccountTrusteeship account) {
        try {
            // 构造AI请求参数
            Map<String, Object> aiRequestParams = buildAiRequestParams(articleVO, account);

            // 调用AIService处理AI生成文章
            Article article = aiService.generateArticle(aiRequestParams);
            chromeDriverStrategy.getChromeDriverKey(account.getPlatForm()).chromePublishArticle(account.getAccount(), article, imgMap(articleVO));
        } catch (Exception e) {
            log.error("发布文章失败", e);
        } finally {
            // 更新账号状态，即使发布失败也要更新，避免频繁重试
            updateAccountState(account.getAccount());
        }
    }

    /**
     * 构建AI请求参数
     */
    private Map<String, Object> buildAiRequestParams(ArticleVO articleVO, AccountTrusteeship account) {
        Map<String, Object> params = new HashMap<>();
        List<String> articleList = new ArrayList<>(4);
        articleList.add(String.format("%s \n%s", articleVO.getTitle(), articleVO.getConText().replace("，", ",")));
        params.put("articleList", articleList);
        params.put("prompt", prompt);
        params.put("aiConfig", aiConfig);
        return params;
    }

    private Map<String, List<String>> imgMap(ArticleVO articleVO) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> imgList = articleVO.getImgList();
        if (CollectionUtil.isNotEmpty(imgList)) {
            map.put("editing_img", imgList);
        }
        return map;
    }

    /**
     * 更新账号状态
     * 记录最新发布时间
     */
    private void updateAccountState(String account) {
        MonitoringState state = accountStates.get(account);
        if (state != null) {
            state.updateLastPublishTime();
        }
    }

    /**
     * 停止监控任务
     */
    public void stop() {
        isRunning.set(false);
        log.info("正在停止监控任务...");
    }

    /**
     * 休眠秒数
     *
     * @param minutes 纪要
     */
    private void sleepSeconds(int minutes) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(minutes));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            log.error("休眠被中断: ", e);
        }
    }

    /**
     * 睡眠分钟
     *
     * @param minutes 纪要
     */
    private void sleepMinutes(Long minutes) {
        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(minutes));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            log.error("休眠被中断: ", e);
        }
    }
} 