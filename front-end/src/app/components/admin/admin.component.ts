import { Component, ComponentFactoryResolver, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { AlbumInterface } from 'src/app/models/AlbumInterface';
import { ArtistInterface } from 'src/app/models/ArtistInterface';
import { TrackInterface } from 'src/app/models/TrackInterface';
import { AdminService } from 'src/app/services/admin.service';
import { MusicService } from 'src/app/services/music.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  @ViewChild('popupContainer', { read: ViewContainerRef }) popupContainer: ViewContainerRef;

  isLoading: boolean;

  message: string;
  selectedRadio: any;

  trackList: TrackInterface[];
  albumList: AlbumInterface[];
  artistList: ArtistInterface[];

  selectedTrack: any;
  selectedAlbum: any;
  selectedArtist: any;

  constructor(private adminService: AdminService, private musicService: MusicService) {

  }

  ngOnInit(): void {
    this.musicService.findAllTracks()
      .subscribe(
        (trackList) => {
          this.trackList = trackList;
        }
      );
    this.musicService.findAllAlbums()
      .subscribe(
        (albumList) => {
          this.albumList = albumList;
        }
      );
    this.musicService.findAllArtists()
      .subscribe(
        (artistList) => {
          this.artistList = artistList;
        }
      );
  }

  onRadioChange() {
    this.selectedAlbum = null;
    this.selectedArtist = null;
    this.selectedTrack = null;
  }

  delete() {
    if (this.selectedTrack) {
      this.isLoading = true;
      this.adminService.deleteTrack(this.selectedTrack)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe(
          (message: string) => {
            this.message = message;
            this.trackList = this.trackList.filter(track => track !== this.selectedTrack);
            this.selectedTrack = null;
          }
        );
    } else if (this.selectedAlbum) {
      this.isLoading = true;
      this.adminService.deleteAlbum(this.selectedAlbum)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe(
          (message: string) => {
            this.message = message;
            this.albumList = this.albumList.filter(album => album !== this.selectedAlbum);
            this.selectedAlbum = null;
          }
        );
    } else if (this.selectedArtist) {
      this.isLoading = true;
      this.adminService.deleteArtist(this.selectedArtist)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe(
          (message: string) => {
            this.message = message;
            this.artistList = this.artistList.filter(artist => artist !== this.selectedTrack);
            this.selectedArtist = null;
          }
        );
    } else {
      this.message = 'Choose item to delete';
    }
  }

  editArtist(editedArtist: ArtistInterface) {
    if (editedArtist && editedArtist.id) {
      this.isLoading = true;
      this.adminService.editArtist(editedArtist)
        .pipe(finalize(() => {
          this.isLoading = false;
          this.isEditStatus = false;
        }))
        .subscribe(
          (editedArtist) => {
            this.message = 'Artist successfully edited.';
            this.artistList.map(artist => {
              if (artist.id == editedArtist.id) {
                artist = editedArtist;
              }
            });
            this.selectedArtist = editedArtist;
          }
        );
    }
  }

  editAlbum(editedAlbum: AlbumInterface) {
    if (editedAlbum && editedAlbum.id) {
      this.isLoading = true;
      this.adminService.editAlbum(editedAlbum)
        .pipe(finalize(() => {
          this.isLoading = false;
          this.isEditStatus = false;
        }))
        .subscribe(
          (editedAlbum) => {
            this.message = 'Album successfully edited.';
            this.albumList.map(album => {
              if (album.id == editedAlbum.id) {
                album = editedAlbum;
              }
            });
            this.selectedArtist = editedAlbum;
          }
        );
    }
  }

  editTrack(editedTrack: TrackInterface) {
    if (editedTrack && editedTrack.id) {
      this.isLoading = true;
      this.adminService.editTrack(editedTrack)
        .pipe(finalize(() => {
          this.isLoading = false;
          this.isEditStatus = false;
        }))
        .subscribe(
          (editedTrack) => {
            this.message = 'Album successfully edited.';
            this.trackList.map(track => {
              if (track.id == editedTrack.id) {
                track = editedTrack;
              }
            });
            this.selectedArtist = editedTrack;
          }
        );
    }
  }

  onEditStatusChange(updatedEditStatus: boolean) {
    this.isEditStatus = updatedEditStatus;
  }

  isEditStatus: boolean = false;
}
