package com.innowise.trackmicroservice.controller;

import com.innowise.trackmicroservice.dto.ArtistDto;
import com.innowise.trackmicroservice.service.ArtistService;
import com.innowise.trackmicroservice.validation.group.EditGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/artists")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<ArtistDto> findAll() {
        return artistService.findAll();
    }

    @GetMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ArtistDto getTrackById(@PathVariable(name = "id") String id) {
        return artistService.findById(id);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteTrackById(@PathVariable(name = "id") String id) {
        artistService.delete(id);
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ArtistDto editTrackById(@RequestBody @Validated(value = EditGroup.class) ArtistDto artistToEditDto) {
        return artistService.edit(artistToEditDto);
    }

}
