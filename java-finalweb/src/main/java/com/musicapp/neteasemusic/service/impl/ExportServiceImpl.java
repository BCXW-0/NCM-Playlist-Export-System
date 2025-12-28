package com.musicapp.neteasemusic.service.impl;

import com.musicapp.neteasemusic.model.Playlist;
import com.musicapp.neteasemusic.model.Song;
import com.musicapp.neteasemusic.service.ExportService;
import com.musicapp.neteasemusic.service.PlaylistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);

    @Autowired
    private PlaylistService playlistService;

    @Override
    public void exportPlaylistToTxt(Playlist playlist, OutputStream outputStream) throws IOException {
        if (playlist == null) {
            throw new IllegalArgumentException("Playlist cannot be null");
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            // 写入歌单信息
            writer.write("===== 歌单信息 =====\n");
            writer.write("歌单名称: " + playlist.getName() + "\n");
            if (playlist.getDescription() != null && !playlist.getDescription().isEmpty()) {
                writer.write("歌单描述: " + playlist.getDescription() + "\n");
            }
            writer.write("网易云歌单ID: " + playlist.getNeteasePlaylistId() + "\n");
            writer.write("导出时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("\n===== 歌曲列表 =====\n\n");

            // 写入歌曲列表
            List<Song> songs = playlist.getSongs();
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                writer.write(String.format("%3d. %s - %s\n", i + 1, song.getArtist(), song.getName()));
                writer.write("   专辑: " + song.getAlbum() + "\n");
                writer.write("   时长: " + formatDuration(song.getDuration()) + "\n");
                writer.write("   网易云歌曲ID: " + song.getNeteaseSongId() + "\n\n");
            }

            // 写入统计信息
            writer.write("===== 统计信息 =====\n");
            writer.write("总歌曲数: " + songs.size() + "首\n");
            long totalDuration = songs.stream().mapToLong(Song::getDuration).sum();
            writer.write("总时长: " + formatDuration(totalDuration) + "\n");
            writer.write("\n===== 导出完成 =====\n");

            writer.flush();
        }
    }

    @Override
    public void exportPlaylistToTxtById(Long playlistId, Long userId, OutputStream outputStream) throws IOException {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        if (playlist == null) {
            throw new IllegalArgumentException("Playlist not found");
        }

        // 验证权限
        if (!playlist.getUser().getId().equals(userId)) {
            throw new SecurityException("You don't have permission to export this playlist");
        }

        exportPlaylistToTxt(playlist, outputStream);
    }

    /**
     * 格式化时长（秒）为 HH:mm:ss 格式
     */
    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%d:%02d", minutes, secs);
        }
    }
}