package com.innowise.trackmicroservice.controller;

import com.innowise.trackmicroservice.dto.AlbumDto;
import com.innowise.trackmicroservice.service.AlbumService;
import com.innowise.trackmicroservice.validation.group.EditGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public AlbumDto getTrackById(@PathVariable(name = "id") String id) {
        return albumService.findById(id);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteTrackById(@PathVariable(name = "id") String id) {
        albumService.delete(id);
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)
    public AlbumDto editTrackById(@RequestBody @Validated(value = EditGroup.class) AlbumDto albumToEditDto) {
        return albumService.edit(albumToEditDto);
    }

}
