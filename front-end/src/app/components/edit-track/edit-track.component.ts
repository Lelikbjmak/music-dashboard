import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TrackInterface } from 'src/app/models/TrackInterface';

@Component({
  selector: 'app-edit-track',
  templateUrl: './edit-track.component.html',
  styleUrls: ['./edit-track.component.css']
})
export class EditTrackComponent implements OnInit {

  @Input()
  selectedTrack: TrackInterface;

  @Output()
  trackChange: EventEmitter<TrackInterface> = new EventEmitter<TrackInterface>();

  @Input()
  isEditStatus: boolean;

  @Output()
  editStatusChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  trackEditForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {

  }
 
  ngOnInit(): void {
    this.trackEditForm = this.formBuilder.group({
      id: [this.selectedTrack?.id || '', Validators.required],
      title: [this.selectedTrack?.title || '', Validators.required],
      discNumber: [this.selectedTrack?.discNumber || 0, Validators.required],
      trackNumber: [this.selectedTrack?.trackNumber || 0, Validators.required],
      durationMs: [this.selectedTrack?.durationMs || 0, Validators.required],
      popularity: [this.selectedTrack?.popularity || 0, Validators.required],
      spotifyIconUri: [this.selectedTrack?.spotifyIconUri || '', Validators.required],
      spotifyUri: [this.selectedTrack?.spotifyUri || '', Validators.required],
      album: [this.selectedTrack?.album || null, Validators.required],
      artistList: [this.selectedTrack?.artistList || null, Validators.required],
    });
  }

  updateTrack() {
    this.selectedTrack = this.trackEditForm.value;
    this.trackChange.emit(this.selectedTrack);
  }

  updateEditStatus() {
    this.isEditStatus = false;
    this.editStatusChange.emit(this.isEditStatus);
  }

}
