package com.musicapp.neteasemusic.service;

import com.musicapp.neteasemusic.model.Playlist;
import com.musicapp.neteasemusic.model.Song;
import com.musicapp.neteasemusic.model.User;
import com.musicapp.neteasemusic.repository.PlaylistRepository;
import com.musicapp.neteasemusic.repository.SongRepository;
import com.musicapp.neteasemusic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NeteaseMusicApiService neteaseMusicApiService;

    /**
     * 获取用户的所有歌单
     */
    public List<Playlist> getUserPlaylists(Long userId) {
        return playlistRepository.findByUserId(userId);
    }

    /**
     * 根据ID获取歌单详情
     */
    public Optional<Playlist> getPlaylistById(Long id) {
        return playlistRepository.findById(id);
    }

    /**
     * 同步用户的网易云音乐歌单到本地数据库
     */
    @Transactional
    public List<Playlist> syncUserPlaylists(Long userId, String neteaseUserId, String cookie) throws IOException {
        // 获取用户信息
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        // 更新用户的网易云ID和Cookie
        user.setNeteaseUserId(neteaseUserId);
        user.setNeteaseCookie(cookie);
        userRepository.save(user);

        // 从网易云API获取歌单列表
        List<Playlist> neteasePlaylists = neteaseMusicApiService.getUserPlaylists(neteaseUserId, cookie);

        // 保存歌单到数据库
        for (Playlist neteasePlaylist : neteasePlaylists) {
            // 检查歌单是否已存在
            Optional<Playlist> existingPlaylist = playlistRepository.findByNeteasePlaylistId(neteasePlaylist.getNeteasePlaylistId());
            if (existingPlaylist.isPresent()) {
                // 更新现有歌单
                Playlist playlist = existingPlaylist.get();
                playlist.setName(neteasePlaylist.getName());
                playlist.setDescription(neteasePlaylist.getDescription());
                playlist.setCoverUrl(neteasePlaylist.getCoverUrl());
                playlist.setTrackCount(neteasePlaylist.getTrackCount());
                playlist.setUser(user);
                playlistRepository.save(playlist);
            } else {
                // 创建新歌单
                neteasePlaylist.setUser(user);
                playlistRepository.save(neteasePlaylist);
            }
        }

        // 返回用户的所有歌单
        return playlistRepository.findByUser(user);
    }

    /**
     * 获取歌单的歌曲列表
     */
    @Transactional
    public List<Song> getPlaylistSongs(Long playlistId, String cookie) throws IOException {
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
        if (!playlistOptional.isPresent()) {
            throw new IllegalArgumentException("Playlist not found");
        }
        Playlist playlist = playlistOptional.get();

        // 从网易云API获取歌单歌曲
        List<Song> neteaseSongs = neteaseMusicApiService.getPlaylistTracks(playlist.getNeteasePlaylistId(), cookie);

        // 保存歌曲到数据库
        for (Song neteaseSong : neteaseSongs) {
            // 检查歌曲是否已存在
            if (!songRepository.existsByNeteaseSongId(neteaseSong.getNeteaseSongId())) {
                songRepository.save(neteaseSong);
            }
        }

        // 更新歌单的歌曲列表
        List<Song> songsToSave = new java.util.ArrayList<>();
        for (Song neteaseSong : neteaseSongs) {
            Song song = songRepository.findByNeteaseSongId(neteaseSong.getNeteaseSongId()).orElse(neteaseSong);
            songsToSave.add(song);
        }
        playlist.setSongs(songsToSave);
        playlistRepository.save(playlist);

        return songsToSave;
    }

    /**
     * 删除歌单
     */
    public void deletePlaylist(Long playlistId) {
        playlistRepository.deleteById(playlistId);
    }
}