import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { TrackInterface } from 'src/app/models/TrackInterface';
import { ErrorService } from 'src/app/services/error.service';
import { MusicService } from 'src/app/services/music.service';

@Component({
  selector: 'app-track-search',
  templateUrl: './track-search.component.html',
  styleUrls: ['./track-search.component.css']
})
export class TrackSearchComponent implements OnInit {

  searchTrackForm: FormGroup;

  isLoading: boolean;

  track: TrackInterface;

  isPresent: boolean = false;

  constructor(private formBuilder: FormBuilder, private musicService: MusicService,
    private errorService: ErrorService) {

  }

  ngOnInit(): void {
    this.searchTrackForm = this.formBuilder.group({
      id: new FormControl('', [Validators.required])
    });
  }

  submit() {
    if (this.searchTrackForm.valid) {
      this.isLoading = true;
      const id = this.id?.value;

      this.musicService.findTrackById(id)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe(
          trackDto => {
            if (trackDto == null) {
              this.errorService.handleError('Track is not found');
            } else {
              this.track = trackDto;
              this.isPresent = true;
              this.searchTrackForm.reset();
            }
          }
        );
    }
  }

  get id() {
    return this.searchTrackForm.get('id');
  }
}
