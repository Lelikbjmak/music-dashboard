package com.innowise.trackmicroservice.mapper;

import com.innowise.spotifycommon.dto.SpotifyAlbumDto;
import com.innowise.trackmicroservice.domain.Album;
import com.innowise.trackmicroservice.dto.AlbumDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ArtistListMapper.class})
public interface AlbumMapper {

    Album mapToEntity(SpotifyAlbumDto albumDto);

    AlbumDto mapToDto(Album album);

    void updateEntityFromDto(AlbumDto albumToEditDto, @MappingTarget Album albumToEdit);
}
