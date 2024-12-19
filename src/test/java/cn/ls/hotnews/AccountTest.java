package cn.ls.hotnews;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.ls.hotnews.utils.ChromeDriverUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

/**
 * title: AccountTest
 * author: liaoshuo
 * date: 2024/12/18 19:22
 * description:
 */
@SpringBootTest
public class AccountTest {

    public static void main(String[] args) {
        String str = "BAIDUID=B7690176AB22E76D2E60E6CFF6E383FA:FG=1; BAIDUID_BFESS=B7690176AB22E76D2E60E6CFF6E383FA:FG=1; theme=bjh; BDUSS=4tWjBYT0pBdUVqcUtNTGdUSWktWXFnTXVscG55QmZBeEZMdFd1VURFbmVZb3BuSVFBQUFBJCQAAAAAAQAAAAEAAAAEZhWSyqfW2M3izKu~1XRpbQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN7VYmfe1WJnM2; BDUSS_BFESS=4tWjBYT0pBdUVqcUtNTGdUSWktWXFnTXVscG55QmZBeEZMdFd1VURFbmVZb3BuSVFBQUFBJCQAAAAAAQAAAAEAAAAEZhWSyqfW2M3izKu~1XRpbQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN7VYmfe1WJnM2; __bid_n=193da13824092969d70e67; Hm_lvt_f7b8c775c6c8b6a716a75df506fb72df=1734573004,1734573064,1734573823,1734574088; devStoken=c61d44edb8a6eb069a2658575f24c4c2c02577e6424fda9def562742c93c328c; bjhStoken=2f42a6fbed2cf6e19acb8a114b7566c3c02577e6424fda9def562742c93c328c; ab_sr=1.0.1_M2Y4NWY2Y2NjN2VlODI3ZmNjZWExMjI5ODdmNzExZTBjOWMzMTcwMGFmMTdlNWQ2MjU4YjhhNDUxMWMyMmM5ODA1NDgzZDhmM2UyZDkyYjdmN2RlOTc4OWJlMTM2NDQwZTdiMDQzMWU0MTU1MjMxODM4ZmQ4NjgwODQ5YjJiOWU1OWFlNDA3YTU5YmU1ODlmZTA3MjE1ZDgwZGM4ZTMxMzk1Yzk0NGNkNTUzYzliNDBlNjIwOWUzMzljMzYyNmNmZTQzNDVkMDljOWI5ODUzNTFjODZhMGY5N2YwYzM5MWM5NDA2NDQ3MjM1MjdmMjQ1NTY0YjFiNWJlYjI2NzczZQ==; RT=\"z=1&dm=baidu.com&si=4440b894-4c67-4579-901b-6a2c158c06b7&ss=m4unzbhv&sl=0&tt=0&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf\"";
        int lastIndexOf = str.lastIndexOf("; ") + 2;
        System.out.println(str.substring(lastIndexOf));
        System.out.println(str.charAt(lastIndexOf) == 'R');
        System.out.println(str.charAt(lastIndexOf + 1) == 'T');
        System.out.println(str.charAt(str.length() - 1) == '"');
    }

    // 请求token 的返回值
    //{"errno":0,"errmsg":"success","data":{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjoiUDRUYldHenRPd01BLVJoeDVWWDdxa1NGT2wtQkFLVlg1Rl91NFUwWXl5VVFvOHJCaTBUN0lCcnpPNnVmZHM5alRYcUdQUEI4N3VBM25lZnZVS0xqRGZxNHRpT3pmcVUyb0h6WkFZRzZhYzhsZzJTN00tZkI0SnlwWDUtSTZyVDFRVFJGTEUzQWlwSF9rRW56aXlDak9Pa2pBT1paNUxGNXA2N3N2RlBib1dJIiwiZXhwIjoxNzM0NjE1MzE1LCJpc3MiOiJtYmQtaW0tc2VydmVyLWZvci1qc3NkayIsInZlcnNpb24iOjEsImFwcGlkIjoxMTA5NTk3M30.nvEfZaVtHgElYnBvmg-wlRPnJeEeIoK-MR-X7ecnbxQ"}}

    @Test
    void a() {
//        String body = HttpUtil.createGet("https://baijiahao.baidu.com/builder/app/appinfo")
//        //String body = HttpUtil.createGet("https://baijiahao.baidu.com/pcui/im/getimtoken")
//                .cookie(
//"BAIDUID=7D63C3A90B8C8D2CF4F0EC34AB3A9D58:FG=1; BAIDUID_BFESS=7D63C3A90B8C8D2CF4F0EC34AB3A9D58:FG=1; BDUSS=05WWU5-dlh-dElJTVcyVHo3R1FXbkd4UzIzUk1pcTZIOEtOeUZUdXlLeHBrSXRuRVFBQUFBJCQAAAAAAQAAAAEAAAAEZhWSyqfW2M3izKu~1XRpbQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGkDZGdpA2RnV; BDUSS_BFESS=05WWU5-dlh-dElJTVcyVHo3R1FXbkd4UzIzUk1pcTZIOEtOeUZUdXlLeHBrSXRuRVFBQUFBJCQAAAAAAQAAAAEAAAAEZhWSyqfW2M3izKu~1XRpbQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGkDZGdpA2RnV; __bid_n=193dead65cb4032e230202; ab_jid=2e8b93186411cc719da87463eefdc0c8d348; ab_jid_BFESS=2e8b93186411cc719da87463eefdc0c8d348; ab_bid=cc719da87463eefdc0c8d34876d3db60ac59; ab_sr=1.0.1_ZTExOGU2NDI5MmM0YjllZjljZTA1ZDFlOWU1ZjFlYzk5ODUzYWFhZDI4NDBjNThjYWUwMWM1YmUxM2UxMjc5OWZhZDBjMTJlNGE2MWE5ZTU3OTQyYzc1ZTUwY2EyODk3MzY1OGVmNzQyZTQ2MTM4MTgyZWNhYmExZTQ4YzE1YTY5ZGNjYzc5YTYzMzU2NmFjZjAxYWM3YjVmZTIzNzRlNTFjZGQ0MDQxNDk5NDBjYTlkMDg4OTgwNjI3MDIyNzBmZjNmNmZkYzE0N2E5NzlkN2Y3YjJiZTUxZWNmYWYyMGEyOGZmZGE0ZGRlODdhODFmNmNkNjcyMzRlMGFmYzNiNQ==; RT=\"z=1&dm=baidu.com&si=86d8c6fc-0ef5-4eae-ad25-0a5fb839ab9b&ss=m4v8vvfa&sl=5&tt=bkh&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf\""                )
//                .header("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjoiUDRUYldHenRPd01BLVJoeDVWWDdxa1NGT2wtQkFLVlg1Rl91NFUwWXl5VVFvOHJCaTBUN0lCcnpPNnVmZHM5alRYcUdQUEI4N3VBM25lZnZVS0xqRGZxNHRpT3pmcVUyb0h6WkFZRzZhYzhsZzJTN00tZkI0SnlwWDUtSTZyVDFRVFJGTEUzQWlwSF9rRW56aXlDak9Pa2pBT1paNUxGNXA2N3N2RlBib1dJIiwiZXhwIjoxNzM0NjE1MzE1LCJpc3MiOiJtYmQtaW0tc2VydmVyLWZvci1qc3NkayIsInZlcnNpb24iOjEsImFwcGlkIjoxMTA5NTk3M30.nvEfZaVtHgElYnBvmg-wlRPnJeEeIoK-MR-X7ecnbxQ")
//                .execute().body();
//        System.out.println(body);
//        //System.out.println(JSONUtil.parseObj(body).get("errno").toString());

        //String body = "{\"data\":{\"user\":{\"ability\":{\"article_tag_num\":5,\"auth_know_quora\":1,\"bound_baidu_shop\":0,\"can_auto_cut_pic\":null,\"can_be_supported\":1,\"can_be_supported_news\":1,\"can_be_supported_video\":true,\"can_create_serveral_group\":0,\"can_dynamic_long\":1,\"can_dynamic_mount_lightapp\":1,\"can_dynamic_mount_pricelightapp\":0,\"can_enter_homepage\":1,\"can_fans_layer\":0,\"can_gallery_subtitle\":0,\"can_insert_multi_video\":0,\"can_know_quora\":0,\"can_lemma\":true,\"can_live_goods\":0,\"can_live_subtitle\":0,\"can_manage_mapp\":0,\"can_mount_shop\":0,\"can_mount_wejianzhan\":0,\"can_news_subtitle\":0,\"can_news_to_video\":1,\"can_open_shop\":0,\"can_pay_for_show\":1,\"can_pay_for_show_effect_status\":0,\"can_pay_for_show_live\":0,\"can_pay_for_show_video_status\":1,\"can_pay_read\":0,\"can_pay_separately\":0,\"can_pub_serial\":0,\"can_publish_original_article\":0,\"can_quiz_profit\":0,\"can_reprint\":1,\"can_set_developer\":0,\"can_set_dynamic\":1,\"can_set_live\":1,\"can_set_pay_live\":0,\"can_set_set\":1,\"can_set_voiceset\":0,\"can_set_xpan\":1,\"can_show_ad_mutual_pick\":1,\"can_show_quality\":0,\"can_show_yingxiao\":0,\"can_split_mthvideo\":0,\"can_use_brand\":0,\"can_use_casher\":1,\"can_use_charge_group\":0,\"can_use_common_card\":0,\"can_use_dynamic_goods\":1,\"can_use_group\":0,\"can_use_legal\":0,\"can_use_live_shop\":0,\"can_use_news_ad\":0,\"can_use_video_ad\":0,\"can_use_video_goods\":0,\"can_use_video_procedure\":0,\"can_use_wenda\":0,\"can_use_write_knowledge\":true,\"can_video_edit\":0,\"can_video_mount_yxt\":0,\"can_video_subtitle\":0,\"can_videoset_nolimit\":0,\"can_yingxiao_mount\":0,\"dynamic_mount_column\":1,\"get_ad_profit\":true,\"got_support_news\":1,\"grey\":1,\"has_bind_telephone\":0,\"have_recommend_ability\":1,\"is_audio_pay_user\":0,\"is_live_pay_user\":0,\"is_news_pay_user\":0,\"is_training_camp_user\":0,\"is_video_pay_user\":0,\"news_insert_inner_link\":0,\"news_insert_outer_link\":0,\"news_mount_column\":0,\"pub_limitless\":false,\"pub_limitless_news\":false,\"pub_limitless_video\":false,\"publish_num\":5,\"search_card\":false,\"support_news_threshold\":false,\"total_fans\":0,\"video_mount_column\":0,\"video_size_unlimit\":0},\"account_excep_reason\":\"\",\"alive_check\":1,\"app_id\":1815708198132082,\"app_level\":0,\"app_level_desc\":\"新手\",\"audit_at\":\"0000-00-00 00:00:00\",\"authUser\":1,\"avatar\":\"//pic.rmb.bdstatic.com/bjh/user/9e81562de41e12000808e0a52cf5fb7a.jpeg\",\"avatar_unify\":\"//pic.rmb.bdstatic.com/bjh/user/9e81562de41e12000808e0a52cf5fb7a.jpeg\",\"banned_status\":{},\"bluev_business\":0,\"can_bind_uc\":false,\"cancel_info\":{},\"child_profit_set\":{\"can_modify\":1,\"last_modify_time\":\"\",\"self_profit\":0,\"valid_date\":\"\"},\"cms_pc_show_status\":\"已认证\",\"content_setting\":0,\"domain\":\"其它\",\"ext_info\":\"\",\"external_pids\":{},\"finance_info\":{\"accountType\":null,\"account_status\":0},\"finance_redpoint\":0,\"gallery_original_status\":0,\"goverMedia\":0,\"gray_author_growth\":0,\"has_bind_telephone\":0,\"has_consultation\":0,\"hide_flag\":\"\",\"id\":6745843204,\"identity_edit\":0,\"identity_original\":1,\"im_status\":\"pass\",\"import\":{\"canImport\":0},\"is_autocover\":1,\"is_block_mcn\":0,\"is_conflict\":0,\"is_medical_auth\":0,\"is_need_navigation_guide\":0,\"is_new\":1,\"is_novel_auth\":0,\"is_official\":1,\"is_proxy_user\":0,\"is_qunfa\":0,\"is_save_material\":0,\"is_split_article\":0,\"is_toAll\":0,\"is_use_im\":0,\"is_yxtcpa\":0,\"level\":0,\"location\":\"北京市-北京市\",\"masterId\":\"\",\"media_type\":\"individual\",\"name\":\"失重外太空\",\"org_info\":{},\"org_name\":\"\",\"original_auth_original_whitelist\":0,\"original_auth_video_status\":0,\"original_exclusive\":0,\"original_material_audit\":0,\"original_material_audit_deadline\":\"\",\"original_status\":0,\"pc_button_content\":\"\",\"pc_jump_pop_url\":null,\"pc_jump_url\":\"\",\"pc_qr_code\":\"\",\"pc_show_content\":\"\",\"people_sync\":0,\"perms\":[\"news_insert_inner_link\",\"can_auto_cut_pic\",\"can_video_subtitle\",\"can_be_supported_video\",\"can_live_goods\",\"article_tag_num\",\"pub_limitless\",\"can_use_write_knowledge\",\"can_use_video_goods\",\"can_use_casher\",\"news_insert_outer_link\",\"grey\",\"can_lemma\",\"can_publish_original_article\",\"bound_baidu_shop\",\"can_show_ad_mutual_pick\",\"publish_num\",\"get_ad_profit\",\"video_mount_column\",\"dynamic_mount_column\",\"can_be_supported_news\",\"can_open_shop\",\"search_card\",\"can_news_subtitle\",\"pub_limitless_news\",\"can_be_supported\",\"can_live_subtitle\",\"can_mount_shop\",\"news_mount_column\",\"got_support_news\",\"support_news_threshold\",\"can_pay_read\",\"can_gallery_subtitle\",\"can_reprint\",\"can_use_charge_group\",\"can_use_live_shop\",\"pub_limitless_video\",\"can_use_dynamic_goods\"],\"pic_water_log\":1,\"plantform_auth\":1,\"plat_auth_st\":-1,\"pop_diag_window\":0,\"pop_mp_window\":0,\"profession_check\":0,\"protocol_status\":0,\"qiyehao_child_type\":0,\"qualification\":null,\"register_basicinfo_status\":\"pass\",\"register_realinfo_status\":\"pass\",\"shoubai_c_appid\":1815708198132082,\"show_gains\":0,\"show_msg_tab\":1,\"show_spring\":0,\"show_vane\":0,\"source_type\":\"ugc\",\"status\":\"newbie\",\"status_cn\":\"新手期\",\"strategy_task_type\":0,\"sub_type\":\"individual\",\"telephone\":[],\"tpid\":\"\",\"type\":\"individual\",\"uc_bind_type\":0,\"uname\":\"失重外太空tim\",\"user_status\":\"pass\",\"user_type\":\"media\",\"userid\":6745843204,\"username\":\"失重外太空tim\",\"userstate\":0,\"v_type\":0,\"video_original_status\":0,\"water_logo\":\"\",\"wishes\":\"还没有任何签名哦\",\"ws_token\":\"8e2aa2a29bf6d2e5f80e22e90186eb22\",\"xpan_bind\":0,\"yingxiaotong_cpa\":0}},\"errmsg\":\"success\",\"errno\":0}\n";
        //Object objData = JSONUtil.parseObj(body).get("data");
        //Object objUser = JSONUtil.parseObj(objData).get("user");
        //System.out.println(objUser);
        //Map<String,Object> userMap = (Map<String, Object>)objUser;
        //System.out.println(userMap.get("name"));
        //System.out.println(userMap.get("shoubai_c_appid"));
        String body = "{ \"auth_type\":-1, \"avatar_url\":\"https://sf6-cdn-tos.toutiaostatic.com/img/user-avatar/70f44a5ab18cdf0bc91be1468fefff6b~300x300.image\", \"code\":0, \"extra\":{ \"claim_origin_permission\":\"1\", \"impr_boost_pkg_permission\":\"0\", \"medium_video_cnt\":\"0\", \"show_authentication_entrance\":\"1\" }, \"is_creator\":false, \"media_id\":1815611467541508, \"message\":\"success\", \"name\":\"限量版逗比\", \"show_data_assistant\":false, \"show_impression\":false, \"tie_fans\":{ \"banner_image_url\":\"https://p9.toutiaoimg.com/obj/toutiao-activity-bucket/activity/pgc_media/task4/f8722a4963abfc81ebc014b84bae74af\", \"banner_schema\":\"sslocal://webview?hide_search=1&status_bar_color=white&bounce_disable=1&hide_more=1&hide_back_close=1&hide_bar=1&hide_status_bar=1&should_append_common_param=1&url=https%3A%2F%2Fi.snssdk.com%2Ffeoffline%2Ftt_data_assistant%2Fincrease-fans.html\", \"tie_fan_authority\":false }, \"total_fans_count\":0, \"user_id\":570028048259673, \"user_id_str\":\"570028048259673\", \"welcome_msg\":\"在头条创作的第 16 天\" }";
        JsonObject asJsonObject = JsonParser.parseString(body).getAsJsonObject();
        System.out.println(asJsonObject.get("name"));
        System.out.println(asJsonObject.get("user_id_str"));
    }

    @Test
    void b() throws InterruptedException {
        String proFileName = String.format("baijia%s", 1);
        ChromeDriver driver = ChromeDriverUtils.initChromeDriver(proFileName);
        // 打开目标网页
        driver.get("https://baijiahao.baidu.com/builder/theme/bjh/login");

        Thread.sleep(5000);
        //LogEntries performanceLog = driver.manage().logs().get(LogType.PERFORMANCE);
        //// 用于存储唯一 Cookie
        //Set<String> cookies = new HashSet<>();
        //// 处理性能日志
        //for (LogEntry entry : performanceLog) {
        //    String message = entry.getMessage();
        //    // 使用 Hutool 的 JSON 工具解析 JSON 字符串
        //    JSONObject jsonMessage = JSONUtil.parseObj(message).getJSONObject("message");
        //    String packetMethod = jsonMessage.getStr("method");
        //    //System.out.println(jsonMessage);
        //    if (packetMethod.startsWith("Network")) {
        //
        //        String params = jsonMessage.getJSONObject("params").toString();
        //        //System.out.println(params);
        //        if (params.contains("headers")) {
        //            JSONObject entries = JSONUtil.parseObj(params);
        //            Object headers = entries.get("headers");
        //            if (headers != null) {
        //                JSONObject headersJson = JSONUtil.parseObj(headers);
        //                if (headersJson.containsKey("Cookie")) {
        //                    String cookie = headersJson.getStr("Cookie");
        //                    if (cookie != null && !cookie.isEmpty()) {
        //                        int lastIndexOf = cookie.lastIndexOf("; ") + 2;
        //                        boolean R = cookie.charAt(lastIndexOf) == 'R';
        //                        boolean T = cookie.charAt(lastIndexOf + 1) == 'T';
        //                        boolean x = cookie.charAt(cookie.length() - 1) == '"';
        //                        if (R && T && x) {
        //                            cookies.add(cookie);
        //                        }
        //                    }
        //                }
        //            }
        //        }
        //    }
        //}
        //
        //System.out.println("Unique Cookies:");
        //for (String cookie : cookies) {
        //
        //    System.out.println(cookie);
        //}
        //driver.navigate().refresh();
        System.out.println(driver.getCurrentUrl());
        //Thread.sleep(10000);
        //driver.quit();
    }


    /**
     * todo 接下来获取token就可以了
     */
    @Test
    void c() {

        String proFileName = String.format("baijia%s", 1);
        //ChromeDriver driver = ChromeDriverUtils.initChromeDriver(proFileName);
        ChromeDriver driver = ChromeDriverUtils.initHeadlessChromeDriver(proFileName);
        driver.get("https://baijiahao.baidu.com/builder/rc/home");


        LogEntries performanceLog = driver.manage().logs().get(LogType.PERFORMANCE);
        // 用于存储唯一 Cookie
        Set<String> cookies = new HashSet<>();
        // 处理性能日志
        for (LogEntry entry : performanceLog) {
            String message = entry.getMessage();
            // 使用 Hutool 的 JSON 工具解析 JSON 字符串
            JSONObject jsonMessage = JSONUtil.parseObj(message).getJSONObject("message");
            String packetMethod = jsonMessage.getStr("method");
            if (packetMethod.startsWith("Network")) {

                String params = jsonMessage.getJSONObject("params").toString();
                //System.out.println(params);
                if (params.contains("headers")) {
                    JSONObject entries = JSONUtil.parseObj(params);
                    Object headers = entries.get("headers");
                    if (headers != null) {
                        JSONObject headersJson = JSONUtil.parseObj(headers);
                        if (headersJson.containsKey("Cookie")) {
                            String cookie = headersJson.getStr("Cookie");
                            if (cookie != null && !cookie.isEmpty()) {
                                int lastIndexOf = cookie.lastIndexOf("; ") + 2;
                                boolean R = cookie.charAt(lastIndexOf) == 'R';
                                boolean T = cookie.charAt(lastIndexOf + 1) == 'T';
                                boolean x = cookie.charAt(cookie.length() - 1) == '"';
                                if (R && T && x) {
                                    cookies.add(cookie);
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Unique Cookies:");
        for (String cookie : cookies) {
            String body = HttpUtil.createGet("https://baijiahao.baidu.com/pcui/im/getimtoken").cookie(cookie).execute().body();
            JSONObject bodyJson = JSONUtil.parseObj(body);
            String errno = bodyJson.get("errno").toString();
            if(errno.equals("0")){
                System.out.println(JSONUtil.parseObj(bodyJson.get("data")).get("token"));
                break;
            }
        }
        driver.quit();
    }
}
