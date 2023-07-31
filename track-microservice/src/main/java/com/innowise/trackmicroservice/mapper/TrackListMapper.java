package com.innowise.trackmicroservice.mapper;

import com.innowise.trackmicroservice.domain.Track;
import com.innowise.trackmicroservice.dto.TrackDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = TrackMapper.class)
public interface TrackListMapper {

    List<TrackDto> mapToDtoList(List<Track> trackList);

    List<Track> mapToEntityList(List<TrackDto> trackDtoList);

}
