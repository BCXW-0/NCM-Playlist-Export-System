package com.musicapp.neteasemusic.repository;

import com.musicapp.neteasemusic.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    Optional<Song> findByNeteaseSongId(String neteaseSongId);

    boolean existsByNeteaseSongId(String neteaseSongId);
}
