//package com.example.yuanpeiyu.vpntest.uusafe;
//
//import android.os.Environment;
//import android.util.Log;
//
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//import java.io.File;
//
///**
// * Created by yuanpeiyu on 2016/4/22.
// */
//public class KeyWordManager {
//    private static final String PACKAGENAME = "replace_package_name";
//    private static final String DEFAULT_PERM_JSON_2 = "{\"content\":{\"desc\":\"app-permission\",\"ver\":1,\"app\":{\"pkg\":\"" + PACKAGENAME + "\"},\"permission\":{\"launch\":{\"ctl\":1},\"autostart\":{\"ctl\":1},\"bg_run\":{\"ctl\":1,\"time_bg\":5,\"time_lock\":1},\"notification\":{\"ctl\":1},\"sms\":{\"send\":1,\"query\":1,\"update\":1},\"call\":{\"ctl\":1},\"call_logs\":{\"query\":1,\"update\":1},\"contacts\":{\"query\":1,\"update\":1},\"input\":{\"ctl\":2},\"sensor\":{\"ctl\":1},\"phone\":{\"number\":1},\"location\":{\"ctl\":1},\"net\":{\"m_switch\":1,\"mobile\":1,\"w_switch\":1,\"wifi\":1},\"audio\":{\"ctl\":1},\"camera\":{\"ctl\":1},\"bluetooth\":{\"switch\":1,\"use\":1},\"window\":{\"shots\":1},\"range_zone\":{\"ctl\":1,\"act\":{\"on\":1,\"out\":2,\"dft\":1},\"lst\":[]},\"range_time\":{\"ctl\":1,\"act\":{\"on\":1,\"out\":2,\"dft\":1},\"lst\":[]},\"clipboard\":{\"ctl\":2},\"mail\":{\"ctl\":1},\"print\":{\"ctl\":1},\"file\":{\"ctl\":1,\"mode\":\"aes256\"},\"media_res\":{\"ctl\":1},\"config\":{\"dbim\":2},\"web\":{\"urlbw\":1,\"urlrcd\":1},\"vpn\":{\"ctl\":1,\"type\":\"sangfor\",\"ip\":\"\",\"port\":\"\"}}}}";
//    private static final String DEFAULT_PERM_JSON = "{\n" +
//            "  \"content\": {\n" +
//            "    \"desc\": \"app-permission\",\n" +
//            "    \"ver\": 1,\n" +
//            "    \"app\": {\n" +
//            "      \"pkg\": \""+ PACKAGENAME +"\"\n" +
//            "    },\n" +
//            "    \"permission\": {\n" +
//            "      \"launch\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"autostart\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"bg_run\": {\n" +
//            "        \"ctl\": 1,\n" +
//            "        \"time_bg\": 5,\n" +
//            "        \"time_lock\": 1\n" +
//            "      },\n" +
//            "      \"notification\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"sms\": {\n" +
//            "        \"send\": 1,\n" +
//            "        \"query\": 1,\n" +
//            "        \"update\": 1\n" +
//            "      },\n" +
//            "      \"call\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"call_logs\": {\n" +
//            "        \"query\": 1,\n" +
//            "        \"update\": 1\n" +
//            "      },\n" +
//            "      \"contacts\": {\n" +
//            "        \"query\": 1,\n" +
//            "        \"update\": 1\n" +
//            "      },\n" +
//            "      \"input\": {\n" +
//            "        \"ctl\": 2\n" +
//            "      },\n" +
//            "      \"sensor\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"phone\": {\n" +
//            "        \"number\": 1\n" +
//            "      },\n" +
//            "      \"location\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"net\": {\n" +
//            "        \"m_switch\": 1,\n" +
//            "        \"mobile\": 1,\n" +
//            "        \"w_switch\": 1,\n" +
//            "        \"wifi\": 1\n" +
//            "      },\n" +
//            "      \"audio\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"camera\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"bluetooth\": {\n" +
//            "        \"switch\": 1,\n" +
//            "        \"use\": 1\n" +
//            "      },\n" +
//            "      \"window\": {\n" +
//            "        \"shots\": 1\n" +
//            "      },\n" +
//            "      \"range_zone\": {\n" +
//            "        \"ctl\": 1,\n" +
//            "        \"act\": {\n" +
//            "          \"on\": 1,\n" +
//            "          \"out\": 2,\n" +
//            "          \"dft\": 1\n" +
//            "        },\n" +
//            "        \"lst\": []\n" +
//            "      },\n" +
//            "      \"range_time\": {\n" +
//            "        \"ctl\": 1,\n" +
//            "        \"act\": {\n" +
//            "          \"on\": 1,\n" +
//            "          \"out\": 2,\n" +
//            "          \"dft\": 1\n" +
//            "        },\n" +
//            "        \"lst\": []\n" +
//            "      },\n" +
//            "      \"clipboard\": {\n" +
//            "        \"ctl\": 2\n" +
//            "      },\n" +
//            "      \"mail\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"print\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"file\": {\n" +
//            "        \"ctl\": 1,\n" +
//            "        \"mode\": \"aes256\"\n" +
//            "      },\n" +
//            "      \"media_res\": {\n" +
//            "        \"ctl\": 1\n" +
//            "      },\n" +
//            "      \"config\": {\n" +
//            "        \"dbim\": 2\n" +
//            "      },\n" +
//            "      \"web\": {\n" +
//            "        \"urlbw\": 1,\n" +
//            "        \"urlrcd\": 1\n" +
//            "      },\n" +
//            "      \"vpn\": {\n" +
//            "        \"ctl\": 1,\n" +
//            "        \"type\": \"sangfor\",\n" +
//            "        \"ip\": \"\",\n" +
//            "        \"port\": \"\"\n" +
//            "      }\n" +
//            "    }\n" +
//            "  }\n" +
//            "}";
//    private static final HashSet<String> DEFAULT_KEYWORD = new HashSet<>();
//    private static KeyWordManager sInstance = new KeyWordManager();
//
//    private KeyWordManager() {
//        DEFAULT_KEYWORD.add("毛泽东");
//        DEFAULT_KEYWORD.add("邓小平");
//        DEFAULT_KEYWORD.add("江泽民");
//        DEFAULT_KEYWORD.add("胡锦涛");
//        DEFAULT_KEYWORD.add("习近平");
//        DEFAULT_KEYWORD.add("法轮功");
//        DEFAULT_KEYWORD.add("法轮大法好");
//        DEFAULT_KEYWORD.add("番号");
//        DEFAULT_KEYWORD.add("六四");
//    }
//
//    public static KeyWordManager getInstance() {
//        return sInstance;
//    }
//
//    public boolean addDefaultPerm(String pkgName) {
//        Log.d("ypy", "AppPerm is " + DEFAULT_PERM_JSON_2.replace(PACKAGENAME, pkgName));
//        return UUSandboxSdk.Permissions.setAppPerm(DEFAULT_PERM_JSON_2.replace(PACKAGENAME, pkgName))
//                && UUSandboxSdk.Keyword.setKeywords(getKeywordsJsonPath(1, DEFAULT_KEYWORD));
//    }
//
//    public static String generateKeywordsJson(int black, Set<String> set) {
//        String json = null;
//
//        try {
//            if (null == set || set.size() <= 0)
//                return null;
//
//            JSONObject object = new JSONObject();
//
//            //generate info
//            JSONObject infoObject = new JSONObject();
//            JSONArray infoJsonarray = new JSONArray();
//            JSONArray infoRow = new JSONArray();
//            infoRow.put((int)(System.currentTimeMillis() / 1000));
//            infoRow.put(black);
//            infoRow.put(set.size());
//            infoRow.put("input");
//            infoJsonarray.put(infoRow);
//
//            infoObject.put("column", infoJsonarray);
//            infoObject.put("schema", "ver integer, bw integer, count integer, desc text");
//
//            //generate content
//            JSONObject contentObject = new JSONObject();
//            JSONArray contentJsonarray = new JSONArray();
//            Iterator it = set.iterator();
//            while(it.hasNext()){//遍历
//                contentJsonarray.put(it.next());
//            }
//            contentObject.put("column", contentJsonarray);
//            contentObject.put("schema", "key text");
//
//            //add info content
//            object.put("content", contentObject);
//            object.put("info", infoObject);
//
//            json = object.toString();
//            Log.d("ypy", "KeywordsJson is " + json);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return json;
//    }
//
//    public static String getKeywordsJsonPath(int black, Set<String> set) {
//        String json = generateKeywordsJson(black, set);
//        if (null != json) {
//            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            String path = sdPath + File.separator + "keywords.json";
//            FileUtils.writeToFile(new File(path), json);
//            return path;
//        }
//
//        return null;
//    }
//
//    public static int COUNT_KEYWORDS = 1;
//    public static String generateKeywordsJson2(int black, Set<String> set) {
//        String json = null;
//
//        try {
//            if (null == set || set.size() <= 0)
//                return null;
//
//            JSONObject object = new JSONObject();
//
//            //generate info
//            JSONObject infoObject = new JSONObject();
//            JSONArray infoJsonarray = new JSONArray();
//            infoJsonarray.put(++COUNT_KEYWORDS);
//            infoJsonarray.put(black);
//            infoJsonarray.put(set.size());
//            infoJsonarray.put("input");
//
//            infoObject.put("column", infoJsonarray);
//            infoObject.put("schema", "ver integer, bw integer, count integer, desc text");
//
//            //generate content
//            JSONObject contentObject = new JSONObject();
//            JSONArray contentJsonarray = new JSONArray();
//            Iterator it = set.iterator();
//            while(it.hasNext()){//遍历
//                contentJsonarray.put(it.next());
//            }
//            contentObject.put("column", contentJsonarray);
//            contentObject.put("schema", "key text");
//
//            //add info content
//            object.put("content", contentObject);
//            object.put("info", infoObject);
//
//            json = object.toString();
//            Log.d("ypy", "KeywordsJson is " + json);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return json;
//    }
//}
