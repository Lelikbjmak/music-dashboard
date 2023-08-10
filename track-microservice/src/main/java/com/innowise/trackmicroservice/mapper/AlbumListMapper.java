package com.innowise.trackmicroservice.mapper;

import com.innowise.trackmicroservice.domain.Album;
import com.innowise.trackmicroservice.dto.AlbumDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AlbumMapper.class)
public interface AlbumListMapper {

    List<AlbumDto> mapToDtoList(List<Album> albumList);

}
