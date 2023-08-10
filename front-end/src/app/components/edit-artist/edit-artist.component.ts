import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ArtistInterface } from 'src/app/models/ArtistInterface';

@Component({
  selector: 'app-edit-artist',
  templateUrl: './edit-artist.component.html',
  styleUrls: ['./edit-artist.component.css']
})
export class EditArtistComponent implements OnInit {

  @Input()
  selectedArtist: ArtistInterface;

  @Output()
  artistChange: EventEmitter<ArtistInterface> = new EventEmitter<ArtistInterface>();

  @Input()
  isEditStatus: boolean;

  @Output()
  editStatusChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  artistEditForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {

  }

  ngOnInit(): void {
    this.artistEditForm = this.formBuilder.group({
      id: [this.selectedArtist?.id || '', Validators.required],
      name: [this.selectedArtist?.name || '', Validators.required],
      genres: [this.selectedArtist?.genres || '', Validators.required],
      popularity: [this.selectedArtist?.popularity || 0, Validators.required],
      spotifyIconUri: [this.selectedArtist?.spotifyIconUri || '', Validators.required],
      spotifyUri: [this.selectedArtist?.spotifyUri || '', Validators.required],
    });
  }

  updateArtist() {
    this.selectedArtist = this.artistEditForm.value;
    this.artistChange.emit(this.selectedArtist);
  }

  updateEditStatus() {
    this.isEditStatus = false;
    this.editStatusChange.emit(this.isEditStatus);
  }

}
