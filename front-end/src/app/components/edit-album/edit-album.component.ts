import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AlbumInterface } from 'src/app/models/AlbumInterface';

@Component({
  selector: 'app-edit-album',
  templateUrl: './edit-album.component.html',
  styleUrls: ['./edit-album.component.css']
})
export class EditAlbumComponent {

  @Input()
  selectedAlbum: AlbumInterface;

  @Output()
  artistChange: EventEmitter<AlbumInterface> = new EventEmitter<AlbumInterface>();

  @Input()
  isEditStatus: boolean;

  @Output()
  editStatusChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  albumEditForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {

  }

  ngOnInit(): void {
    this.albumEditForm = this.formBuilder.group({
      id: [this.selectedAlbum?.id || '', Validators.required],
      name: [this.selectedAlbum?.name || '', Validators.required],
      albumType: [this.selectedAlbum?.albumType || '', Validators.required],
      popularity: [this.selectedAlbum?.popularity || 0, Validators.required],
      releaseDate: [this.selectedAlbum?.releaseDate || null, Validators.required],
      totalTracks: [this.selectedAlbum?.totalTracks || 0, Validators.required],
      label: [this.selectedAlbum?.label || '', Validators.required],
      spotifyIconUri: [this.selectedAlbum?.spotifyIconUri || '', Validators.required],
      spotifyUri: [this.selectedAlbum?.spotifyUri || '', Validators.required],
    });
  }

  updateAlbum() {
    this.selectedAlbum = this.albumEditForm.value;
    this.artistChange.emit(this.selectedAlbum);
  }

  updateEditStatus() {
    this.isEditStatus = false;
    this.editStatusChange.emit(this.isEditStatus);
  }

}
