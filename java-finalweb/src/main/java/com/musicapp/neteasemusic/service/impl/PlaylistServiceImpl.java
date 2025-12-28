package com.musicapp.neteasemusic.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.musicapp.neteasemusic.model.Playlist;
import com.musicapp.neteasemusic.model.Song;
import com.musicapp.neteasemusic.model.User;
import com.musicapp.neteasemusic.repository.PlaylistRepository;
import com.musicapp.neteasemusic.repository.SongRepository;
import com.musicapp.neteasemusic.service.PlaylistService;
import com.musicapp.neteasemusic.util.NeteaseMusicApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private NeteaseMusicApiClient neteaseMusicApiClient;

    @Override
    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id).orElse(null);
    }

    @Override
    public List<Playlist> getUserPlaylists(Long userId) {
        return playlistRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Playlist saveNeteasePlaylist(Long neteasePlaylistId, User user) throws IOException {
        // 检查是否已经保存过该歌单
        Optional<Playlist> existingPlaylist = playlistRepository.findByNeteasePlaylistIdAndUserId(neteasePlaylistId, user.getId());
        if (existingPlaylist.isPresent()) {
            return existingPlaylist.get();
        }

        // 从网易云音乐API获取歌单详情
        JsonNode playlistDetail = neteaseMusicApiClient.getPlaylistDetail(neteasePlaylistId);
        
        if (playlistDetail.has("code") && playlistDetail.get("code").asInt() == 200) {
            JsonNode playlistNode = playlistDetail.get("playlist");
            
            // 创建歌单对象
            Playlist playlist = new Playlist();
            playlist.setName(playlistNode.get("name").asText());
            playlist.setNeteasePlaylistId(neteasePlaylistId);
            playlist.setDescription(playlistNode.has("description") ? playlistNode.get("description").asText() : "");
            playlist.setUser(user);
            
            // 保存歌单
            Playlist savedPlaylist = playlistRepository.save(playlist);
            
            // 处理歌曲列表
            List<Song> songs = new ArrayList<>();
            if (playlistNode.has("tracks")) {
                JsonNode tracksNode = playlistNode.get("tracks");
                for (JsonNode trackNode : tracksNode) {
                    Song song = new Song();
                    song.setNeteaseSongId(trackNode.get("id").asLong());
                    song.setName(trackNode.get("name").asText());
                    song.setArtist(getArtistName(trackNode.get("ar")));
                    song.setAlbum(trackNode.get("al").get("name").asText());
                    song.setDuration(trackNode.get("dt").asLong() / 1000); // 转换为秒
                    
                    // 检查歌曲是否已存在
                    Optional<Song> existingSong = songRepository.findByNeteaseSongId(song.getNeteaseSongId());
                    Song savedSong;
                    if (existingSong.isPresent()) {
                        savedSong = existingSong.get();
                    } else {
                        savedSong = songRepository.save(song);
                    }
                    
                    songs.add(savedSong);
                }
            }
            
            // 设置歌单歌曲关联
            savedPlaylist.setSongs(songs);
            
            return playlistRepository.save(savedPlaylist);
        } else {
            throw new IOException("Failed to get playlist detail from Netease API");
        }
    }

    @Override
    public List<Song> getPlaylistSongs(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        return playlist != null ? playlist.getSongs() : new ArrayList<>();
    }

    @Override
    public JsonNode searchNeteasePlaylists(String keyword, int limit, int offset) throws IOException {
        return neteaseMusicApiClient.searchPlaylist(keyword, limit, offset);
    }

    @Override
    public void deletePlaylist(Long playlistId, Long userId) {
        playlistRepository.deleteByIdAndUserId(playlistId, userId);
    }

    /**
     * 获取艺术家名称，多个艺术家以逗号分隔
     */
    private String getArtistName(JsonNode artistsNode) {
        StringBuilder artistBuilder = new StringBuilder();
        for (int i = 0; i < artistsNode.size(); i++) {
            if (i > 0) {
                artistBuilder.append(", ");
            }
            artistBuilder.append(artistsNode.get(i).get("name").asText());
        }
        return artistBuilder.toString();
    }
}