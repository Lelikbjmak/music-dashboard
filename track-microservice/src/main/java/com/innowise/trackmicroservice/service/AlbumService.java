package com.innowise.trackmicroservice.service;

import com.innowise.trackmicroservice.dto.AlbumDto;

import java.util.List;

public interface AlbumService {

    AlbumDto registerNewAlbumIfNotExists(String spotifyAlbumId);

    AlbumDto findById(String id);

    void delete(String id);

    AlbumDto edit(AlbumDto albumToEditDto);

    List<AlbumDto> findAll();
}
