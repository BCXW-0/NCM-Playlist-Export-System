package com.musicapp.neteasemusic.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NeteaseMusicApiClient {

    private static final Logger logger = LoggerFactory.getLogger(NeteaseMusicApiClient.class);
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public NeteaseMusicApiClient(@Value("${netease.music.api.url}") String baseUrl) {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
    }

    /**
     * 获取歌单详情
     */
    public JsonNode getPlaylistDetail(long playlistId) throws IOException {
        String url = baseUrl + "/playlist/detail?id=" + playlistId;
        Request request = new Request.Builder().url(url).build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            
            String responseBody = response.body().string();
            return objectMapper.readTree(responseBody);
        }
    }

    /**
     * 获取用户歌单列表
     */
    public JsonNode getUserPlaylists(long userId) throws IOException {
        String url = baseUrl + "/user/playlist?uid=" + userId;
        Request request = new Request.Builder().url(url).build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            
            String responseBody = response.body().string();
            return objectMapper.readTree(responseBody);
        }
    }

    /**
     * 搜索歌单
     */
    public JsonNode searchPlaylist(String keyword, int limit, int offset) throws IOException {
        String url = baseUrl + "/search?keywords=" + keyword + "&type=1000&limit=" + limit + "&offset=" + offset;
        Request request = new Request.Builder().url(url).build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }
            
            String responseBody = response.body().string();
            return objectMapper.readTree(responseBody);
        }
    }
}