package com.musicapp.neteasemusic.model;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "songs")
@Data
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "netease_song_id", nullable = false, unique = true, length = 50)
    private String neteaseSongId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "artists", nullable = false, length = 500)
    private String artists;

    @Column(name = "album", length = 200)
    private String album;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "mp3_url", length = 500)
    private String mp3Url;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(name = "lyrics", columnDefinition = "TEXT")
    private String lyrics;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}