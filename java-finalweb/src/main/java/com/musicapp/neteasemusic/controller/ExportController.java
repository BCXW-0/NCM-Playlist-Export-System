package com.musicapp.neteasemusic.controller;

import com.musicapp.neteasemusic.model.Playlist;
import com.musicapp.neteasemusic.model.Song;
import com.musicapp.neteasemusic.model.User;
import com.musicapp.neteasemusic.repository.UserRepository;
import com.musicapp.neteasemusic.security.services.UserDetailsImpl;
import com.musicapp.neteasemusic.service.ExportService;
import com.musicapp.neteasemusic.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 导出单个歌单为txt文件
     */
    @GetMapping("/playlist/{id}/txt")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> exportPlaylistToTxt(@PathVariable Long id) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            // 获取用户的网易云Cookie
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            String cookie = user.getNeteaseCookie();

            if (cookie == null || cookie.isEmpty()) {
                return ResponseEntity.badRequest().body("Netease cookie not found. Please sync your playlists first.");
            }

            // 获取歌单信息
            Playlist playlist = playlistService.getPlaylistById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));

            // 验证歌单所属权
            if (!playlist.getUser().getId().equals(userId)) {
                return ResponseEntity.badRequest().body("You don't have permission to export this playlist");
            }

            // 获取歌单的歌曲列表
            List<Song> songs = playlistService.getPlaylistSongs(id, cookie);

            // 导出为txt文件
            byte[] txtContent = exportService.exportPlaylistToTxt(playlist, songs);

            // 设置响应头，触发文件下载
            String fileName = exportService.getExportFileName(playlist.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/plain; charset=utf-8"))
                    .contentLength(txtContent.length)
                    .body(new ByteArrayResource(txtContent));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error exporting playlist: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * 导出用户所有歌单为txt文件
     */
    @GetMapping("/all/txt")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> exportAllPlaylistsToTxt() {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            // 获取用户的网易云Cookie
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            String cookie = user.getNeteaseCookie();

            if (cookie == null || cookie.isEmpty()) {
                return ResponseEntity.badRequest().body("Netease cookie not found. Please sync your playlists first.");
            }

            // 获取用户所有歌单
            List<Playlist> playlists = playlistService.getUserPlaylists(userId);

            if (playlists.isEmpty()) {
                return ResponseEntity.badRequest().body("No playlists found for export");
            }

            // 创建一个包含所有歌单的大型txt文件
            StringBuilder allContent = new StringBuilder();
            allContent.append("===== 网易云音乐歌单导出 =====\n");
            allContent.append("用户: " + userDetails.getUsername() + "\n");
            allContent.append("导出时间: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            allContent.append("歌单总数: " + playlists.size() + "\n");
            allContent.append("=========================\n\n");

            for (Playlist playlist : playlists) {
                // 获取每个歌单的歌曲列表
                List<Song> songs = playlistService.getPlaylistSongs(playlist.getId(), cookie);

                // 添加歌单信息
                allContent.append("\n===== 歌单: " + playlist.getName() + " =====\n");
                if (playlist.getDescription() != null && !playlist.getDescription().isEmpty()) {
                    allContent.append("描述: " + playlist.getDescription() + "\n");
                }
                allContent.append("歌曲数量: " + songs.size() + "\n");
                allContent.append("=========================\n\n");

                // 添加歌曲列表
                for (int i = 0; i < songs.size(); i++) {
                    Song song = songs.get(i);
                    allContent.append(String.format("%d. %s - %s\n", i + 1, song.getName(), song.getArtists()));
                    if (song.getAlbum() != null && !song.getAlbum().isEmpty()) {
                        allContent.append("   专辑: " + song.getAlbum() + "\n");
                    }
                    if (song.getDuration() != null) {
                        int totalSeconds = song.getDuration() / 1000;
                        int minutes = totalSeconds / 60;
                        int seconds = totalSeconds % 60;
                        allContent.append(String.format("   时长: %d:%02d\n", minutes, seconds));
                    }
                    allContent.append("\n");
                }
                allContent.append("\n");
            }

            // 转换为字节数组
            byte[] txtContent = allContent.toString().getBytes("UTF-8");

            // 设置响应头，触发文件下载
            String fileName = "网易云音乐_歌单导出_" + 
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                    ".txt";
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/plain; charset=utf-8"))
                    .contentLength(txtContent.length)
                    .body(new ByteArrayResource(txtContent));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error exporting playlists: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}