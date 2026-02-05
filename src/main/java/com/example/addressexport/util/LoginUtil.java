package com.example.addressexport.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class LoginUtil {
    private static final String path = "/opt/address-export/rp-demo/log/tokens.txt";
    private static final Set<String> TOKENS = new ConcurrentHashSet<>();

    public static Set<String> getTokens() {
        return TOKENS;
    }

    public static void writeToken(String token) {
        TOKENS.add(token);
    }

    public static void cleanToken() {
        TOKENS.clear();
    }

    public static void cleanExpireToken() {
        TOKENS.removeIf(LoginUtil::checkInvalidToken);
    }

    public static boolean checkInvalidToken(String token) {
        if (StrUtil.isBlank(token)) {
            return true;
        }
        try {
            String res = null;
            for (int i = 0; i < 5; i++) {
                if ((res = getBaseInfo(token)) != null) break;
            }
            if (res == null) return true;
            if (new JSONObject(res).getInt("code") != 200) {
                log.info("token 无效, res={}", res);
                return true;
            }
        } catch (Exception e) {
            log.info("check token exception", e);
            return false;
        }
        return false;
    }

    private static String getBaseInfo(String token) {
        try {
            String urlString = "https://api.superexchang.com/user/user/base";
            String res = HttpRequest.get(urlString)
                    .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36")
                    .header("token", token.split(":")[0])
                    .execute().body();
            if (StrUtil.isBlank(res)) {
                return null;
            }
            return res;
        } catch (Exception e) {
            log.info("req token exception, token={}", token, e);
            return null;
        }
    }

    public static List<String> openRedP(String code) {
        Set<String> tokens = getTokens();
        return tokens.stream().parallel().map(token -> {
            String r = HttpRequest
                    .post("https://api.superexchang.com/wallet/v3/wallet/red/packet/receive")
                    .header("Content-Type", "application/json")
                    .header("Accept-Language", "zh-CN")
                    .header("token", token.split(":")[0])
                    .body("{\"code\": \"" + code + "\"}").execute().body();
            try {
                Thread.sleep((ThreadLocalRandom.current().nextInt(200) + 1));
            } catch (InterruptedException ignored) {
            }
            JSONObject jsonObject = JSONUtil.parseObj(r);
            if (jsonObject.getInt("code") != 200) {
                log.info("open rp fail, token={}", token);
                return jsonObject.getStr("msg") + " ---> " + token;
            } else {
                return jsonObject.getStr("data") + " ---> " + token;
            }
        }).collect(Collectors.toList());
    }


    public static Set<String> loadTokens() {
        String absolutePath = FileUtil.getAbsolutePath(path);
        File file = new File(absolutePath);
        if (!file.exists()) {
            return Collections.emptySet();
        }
        return new ConcurrentHashSet<>(FileUtil.readLines(file, "UTF-8"));
    }

    public static void save() {
        if (CollUtil.isEmpty(TOKENS)) {
            return;
        }
        FileUtil.del(path);
        FileUtil.writeLines(TOKENS, path, "UTF-8", true);
    }

    public static void load() {
        Set<String> tokens = loadTokens();
        tokens.removeIf(e -> checkInvalidToken(e)
                || TOKENS.stream().anyMatch(t -> t.split(":")[0].equals(e.split(":")[0])));
        if (CollUtil.isNotEmpty(tokens)) {
            TOKENS.addAll(tokens);
        }

    }
}
