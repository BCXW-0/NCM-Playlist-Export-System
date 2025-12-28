package com.musicapp.neteasemusic.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicapp.neteasemusic.model.Playlist;
import com.musicapp.neteasemusic.model.Song;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NeteaseMusicApiService {

    @Value("${netease.api.url}")
    private String neteaseApiUrl;

    @Value("${netease.api.timeout}")
    private int timeout;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public NeteaseMusicApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取用户的歌单列表
     */
    public List<Playlist> getUserPlaylists(String userId, String cookie) throws IOException {
        String url = neteaseApiUrl + "/user/playlist?uid=" + userId;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .addHeader("Referer", "https://music.163.com/")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }

            String responseBody = response.body().string();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode playlistArray = rootNode.path("playlist");

            List<Playlist> playlists = new ArrayList<>();
            if (playlistArray.isArray()) {
                for (JsonNode node : playlistArray) {
                    Playlist playlist = new Playlist();
                    playlist.setNeteasePlaylistId(node.path("id").asText());
                    playlist.setName(node.path("name").asText());
                    playlist.setDescription(node.path("description").asText());
                    playlist.setCoverUrl(node.path("coverImgUrl").asText());
                    playlist.setTrackCount(node.path("trackCount").asInt());
                    playlists.add(playlist);
                }
            }
            return playlists;
        }
    }

    /**
     * 获取歌单详情和歌曲列表
     */
    public List<Song> getPlaylistTracks(String playlistId, String cookie) throws IOException {
        String url = neteaseApiUrl + "/playlist/track/all?id=" + playlistId + "&limit=1000&offset=0";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", cookie)
                .addHeader("Referer", "https://music.163.com/")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }

            String responseBody = response.body().string();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode songsArray = rootNode.path("songs");

            List<Song> songs = new ArrayList<>();
            if (songsArray.isArray()) {
                for (JsonNode node : songsArray) {
                    Song song = new Song();
                    song.setNeteaseSongId(node.path("id").asText());
                    song.setName(node.path("name").asText());

                    // 处理艺术家信息
                    StringBuilder artists = new StringBuilder();
                    JsonNode artistsArray = node.path("ar");
                    if (artistsArray.isArray()) {
                        for (int i = 0; i < artistsArray.size(); i++) {
                            if (i > 0) {
                                artists.append("/");
                            }
                            artists.append(artistsArray.get(i).path("name").asText());
                        }
                    }
                    song.setArtists(artists.toString());

                    // 处理专辑信息
                    JsonNode albumNode = node.path("al");
                    song.setAlbum(albumNode.path("name").asText());
                    song.setCoverUrl(albumNode.path("picUrl").asText());

                    // 处理时长
                    song.setDuration(node.path("dt").asInt());

                    songs.add(song);
                }
            }
            return songs;
        }
    }

    /**
     * 保存网易云用户的cookie信息
     */
    public void saveUserCookie(String userId, String cookie) {
        // 这里可以实现将cookie保存到数据库的逻辑
        // 目前仅作为接口预留
    }
}
