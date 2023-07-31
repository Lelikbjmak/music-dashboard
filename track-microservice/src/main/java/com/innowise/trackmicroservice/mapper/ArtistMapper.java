package com.innowise.trackmicroservice.mapper;

import com.innowise.spotifycommon.dto.SpotifyArtistDto;
import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.dto.ArtistDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ArtistMapper {

    Artist mapToEntity(SpotifyArtistDto artistDto);

    ArtistDto mapToDto(Artist artist);

    Artist mapToEntity(ArtistDto artistDto);

    void updateEntityFromDto(ArtistDto artistToEditDto, @MappingTarget Artist artistToEdit);

}
