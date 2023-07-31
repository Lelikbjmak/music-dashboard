package com.innowise.trackmicroservice.mapper;

import com.innowise.spotifycommon.dto.SpotifyTrackDto;
import com.innowise.trackmicroservice.domain.Track;
import com.innowise.trackmicroservice.dto.TrackDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ArtistListMapper.class, AlbumMapper.class})
public interface TrackMapper {

    Track mapToEntity(TrackDto trackDto);

    TrackDto mapToDto(Track track);

    @Mapping(target = "album", ignore = true)
    @Mapping(target = "artistList", ignore = true)
    Track mapToEntity(SpotifyTrackDto trackDto);

    void updateEntityFromDto(TrackDto editedTrackDto, @MappingTarget Track trackToEdit);
}
