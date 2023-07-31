package com.innowise.trackmicroservice.mapper;

import com.innowise.trackmicroservice.domain.Artist;
import com.innowise.trackmicroservice.dto.ArtistDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = ArtistMapper.class)
public interface ArtistListMapper {

    List<ArtistDto> mapToDtoList(List<Artist> artists);

    List<Artist> mapToEntityList(List<ArtistDto> artistDtoList);

}
