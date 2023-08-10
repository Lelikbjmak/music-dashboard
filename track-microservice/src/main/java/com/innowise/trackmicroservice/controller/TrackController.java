package com.innowise.trackmicroservice.controller;

import com.innowise.trackmicroservice.dto.TrackDto;
import com.innowise.trackmicroservice.service.TrackService;
import com.innowise.trackmicroservice.validation.group.EditGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/tracks")
public class TrackController {

    private final TrackService trackService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<TrackDto> getAllTracks() {
        return trackService.findAll();
    }

    @GetMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public TrackDto getTrackById(@PathVariable(name = "id") String id) {
        return trackService.findById(id);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteTrackById(@PathVariable(name = "id") String id) {
        trackService.delete(id);
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)
    public TrackDto editTrackById(@RequestBody @Validated(value = EditGroup.class) TrackDto trackToEditDto) {
        return trackService.edit(trackToEditDto);
    }

}
