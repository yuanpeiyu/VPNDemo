package com.example.yuanpeiyu.vpntest;


import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yuanpeiyu on 2015/12/29.
 */
class TestClass {
    public static void main(String[] args) {
        /*int[] dic = {1,0,10,9,8,7,6,5,4,3,2};
        String num = "41018219891027050";
        String[] nums = num.split("");
        int[] a = new int[nums.length-1];
        for (int i = 0; i < nums.length - 1; i++) {
            if (!nums[i+1].equals("")) {
                a[i] = Integer.parseInt(nums[i+1]);
            }
        }

        int ret = (a[0] * 7 + a[1] * 9 + a[2] * 10 + a[3] * 5 + a[4] * 8 + a[5] * 4 + a[6] * 2
                + a[7] * 1 + a[8] * 6 + a[9] * 3 + a[10] * 7 + a[11] * 9 + a[12] * 10 + a[13] * 5
                + a[14] * 8 + a[15] * 4 + a[16] * 2);
        ret = ret % 11;
        num = num + (dic[ret] == 10 ? "x" : dic[ret]);
        System.out.println("num is " + num);*/
//        new Student();
        /*Map<String, Integer> test = new HashMap<>();
        test.put("a", null);
        String abc = "aaaaaaaaaaa";
//        Integer a = (abc.contains("a") ? test.get("a") : 0);
//        test.put("b", (abc.contains("a") ? test.get("a") : 0));
        test.put("b", test.get("a"));
        System.out.println("num is " + (abc.contains("a") ? null : true));*/
//        test();
//        System.load("E:\\libprotectClass_x86.so");
        /*System.out.println("num is " + (*//*1779070 & *//*(~1023)));
        System.out.println("num is " + (float)5 * 6 / 2);
        String str = "abc";
        Object obj = str;
        System.out.println(obj.getClass() == String.class);*/
        /*toUnicode("百度应用");
        toUnicode("360");
        toUnicode("QQ");
        toUnicode("微信");
        toUnicode("hao123");
        System.out.println((int)"百度应用".charAt(0));*/
//        System.out.println(getMD5("abcd"));
//        parseJson();
//        test();
        test1();
    }

    private static void test1() {
//        System.out.println(Integer.toHexString(206));
        /*System.out.println(Integer.toString(0xEA));
        System.out.println(Integer.toString(0xA8));
        System.out.println(Integer.toString(0xC0));
        System.out.println(Integer.toString(0x80));*/
        /*int seq = (hexToDecimal(0x50) << 24) + (hexToDecimal(0x5f) << 16) + (hexToDecimal(0xa9) << 8) + (hexToDecimal(0x06));
        System.out.println(seq);*/
//        System.out.println((134<<8) + 1);

//        System.out.println((0x70 & 240) >> 4);
        System.out.println(Byte.parseByte("010", 2));
    }

    private static int hexToDecimal(int i) {
        return new Integer(Integer.toString(i));
    }

    public static void parseJson() {
        String str = "{\"content\":{\"column\":[[0,1463554411000,\"203506077@chatroom\",\"wxid_63f5sqlpxv3e22:\\n王老师在出差，请雁鸣给看下。 \"],[0,1463554436000,\"203506077@chatroom\",    \"wxid_63f5sqlpxv3e22:\\n另外，建议成都的项目尽快迁移到211服务器上。 \"]],\"schema\":\"isSend INTEGER, createTime INTEGER, talker TEXT, content TEXT\"}}";
        try {
            JSONObject object = new JSONObject(str);
            JSONObject object2 = object.getJSONObject("content");
            JSONArray array = object2.getJSONArray("column");
            for (int i = 0; i < array.length(); i++) {
                JSONArray obj = array.getJSONArray(i);
                for (int j = 0; j < obj.length(); j++) {
                    System.out.println("array[" + i + "] obj[" + j + "] is " + obj.getString(j));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getMD5(String seed) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(seed.getBytes());
            byte[] tmp = digest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            if (tmp != null) {
                for (byte b : tmp) {
                    stringBuilder.append(String.format("%02x", b & 0xFF));
                }
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return seed;
    }


    public static String toUnicode(String str){
        //存放返回值
        String restr="";
        System.out.print("字符串\""+str+"\"的unicode码:");
        for(char a : str.toCharArray()){
            //十六进制显示
            String ch=Integer.toHexString((int)a);
            //用0补齐四位
            for(int i=ch.length();i<4;i++){
                ch="0"+ch;
            }
            //全部转换成大写    不转换也无所谓
            ch=ch.toUpperCase();
            restr+=" " + ch;
        }
        System.out.println(restr);
        return restr;
    }

    private static void test() {
        try {
            String str = "{\"userInfo\":{\"username\":\"yuanpeiyu\",\"phone\":\"\",\"account\":\"yuanpeiyu\",\"email\":\"yuanpeiyu\",\"comId\":97000782,\"userKey\":\"js961xhfewf07acpa16vd4q8r9mv39rdcxhruhsadrglm99tnrwderxak3h30zqvy0q48xtmg8rid5h84g12w49v15tmuy95r0xhyh0xtggzsqgnpkegywo88st8ulh9\"},\"deviceInfo\":{\"deviceId\":\"8a808895521095420152112c8da300a1\"},\"serviceInfo\":{\"serverToken\":\"ea8cd3a879c1171021c984a7bc19676149ac368208f39298e80de5a2dbeb42a25b504b19494048f016fec3ebd903b66a\"},\"policyRuleInfo\":{\"policyId\":\"8a80889551ecc0b00151eccf5f8c000f\",\"ruleId\":\"8a80889551ecc0b00151eccf63900040\"},\"licenseKey\":\"10513DF4EBF8AB9827E21EF29279033AFF4500C6CBAF6417F3FD34E323F14061F6C5C00BDADC7B6C7CC5C5C834FD64E1FBFF780A542538BEF4D3B916F059BDF3\",\"knoxContainerLicenseKey\":\"KLM03-CNJ9K-R7DX6-AEQ1I-RCWAS-DTKSM\",\"pushAssignServerUrl\":\"https://army.demo.everknow.cn:4433/push/v1/assignServer\",\"sysconfig\":{\"check_policy_interval\":5}}";
            JSONObject object = new JSONObject();
            object.put("aa", "sss");
            JSONObject b = new JSONObject();
            b.put("c", object.toString());
            System.out.println("jsopn is " + b.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



class Test1 {
    public Test1() {
        System.out.println("call test1 contruct");
    }
    public void test(){

    }
}

class Student{
    private Test1 test1 = new Test1(){
        @Override
        public void test() {
            super.test();
            System.out.println("call test1 test");
        }
    };

    public Student() {
        System.out.println("call student contruct");
    }
}