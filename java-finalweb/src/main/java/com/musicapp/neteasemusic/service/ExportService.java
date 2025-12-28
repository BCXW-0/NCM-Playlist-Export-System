package com.musicapp.neteasemusic.service;

import com.musicapp.neteasemusic.model.Playlist;
import com.musicapp.neteasemusic.model.Song;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ExportService {

    /**
     * 将歌单导出为txt格式
     */
    public byte[] exportPlaylistToTxt(Playlist playlist, List<Song> songs) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

            // 写入歌单信息
            writer.write("===== 歌单信息 =====\n");
            writer.write("歌单名称: " + playlist.getName() + "\n");
            if (playlist.getDescription() != null && !playlist.getDescription().isEmpty()) {
                writer.write("歌单描述: " + playlist.getDescription() + "\n");
            }
            writer.write("歌曲数量: " + songs.size() + "\n");
            writer.write("导出时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
            writer.write("==================\n\n");

            // 写入歌曲列表
            writer.write("===== 歌曲列表 =====\n");
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                writer.write(String.format("%d. %s - %s\n", i + 1, song.getName(), song.getArtists()));
                if (song.getAlbum() != null && !song.getAlbum().isEmpty()) {
                    writer.write("   专辑: " + song.getAlbum() + "\n");
                }
                if (song.getDuration() != null) {
                    writer.write("   时长: " + formatDuration(song.getDuration()) + "\n");
                }
                writer.write("\n");
            }
            writer.write("==================\n");
            writer.flush();

            return baos.toByteArray();
        }
    }

    /**
     * 格式化时长（毫秒转换为分:秒）
     */
    private String formatDuration(int milliseconds) {
        int totalSeconds = milliseconds / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * 获取导出文件名
     */
    public String getExportFileName(String playlistName) {
        // 移除文件名中可能的非法字符
        String safeName = playlistName.replaceAll("[\\/:*?\"<>|]", "_");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return safeName + "_歌单导出_" + timestamp + ".txt";
    }
}