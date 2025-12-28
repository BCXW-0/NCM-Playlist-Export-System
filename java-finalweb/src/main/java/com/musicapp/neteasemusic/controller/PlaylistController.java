package com.musicapp.neteasemusic.controller;

import com.musicapp.neteasemusic.model.Playlist;
import com.musicapp.neteasemusic.model.Song;
import com.musicapp.neteasemusic.model.User;
import com.musicapp.neteasemusic.repository.UserRepository;
import com.musicapp.neteasemusic.security.services.UserDetailsImpl;
import com.musicapp.neteasemusic.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/playlist")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取当前用户的歌单列表
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserPlaylists() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        List<Playlist> playlists = playlistService.getUserPlaylists(userId);
        return ResponseEntity.ok(playlists);
    }

    /**
     * 同步用户的网易云音乐歌单
     */
    @PostMapping("/sync")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> syncUserPlaylists(@RequestParam String neteaseUserId, @RequestParam String cookie) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            List<Playlist> playlists = playlistService.syncUserPlaylists(userId, neteaseUserId, cookie);
            return ResponseEntity.ok(playlists);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error syncing playlists: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * 获取歌单详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getPlaylistById(@PathVariable Long id) {
        return playlistService.getPlaylistById(id)
                .map(playlist -> ResponseEntity.ok(playlist))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取歌单的歌曲列表
     */
    @GetMapping("/{id}/songs")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getPlaylistSongs(@PathVariable Long id) {
        try {
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

            List<Song> songs = playlistService.getPlaylistSongs(id, cookie);
            return ResponseEntity.ok(songs);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error getting playlist songs: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * 删除歌单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deletePlaylist(@PathVariable Long id) {
        try {
            playlistService.deletePlaylist(id);
            return ResponseEntity.ok("Playlist deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}