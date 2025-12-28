package com.musicapp.neteasemusic.repository;

import com.musicapp.neteasemusic.model.Playlist;
import com.musicapp.neteasemusic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findByUser(User user);

    Optional<Playlist> findByNeteasePlaylistId(String neteasePlaylistId);

    List<Playlist> findByUserId(Long userId);

    boolean existsByNeteasePlaylistId(String neteasePlaylistId);
}
