import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, delay, map, retry } from 'rxjs';
import { environment } from 'src/environments/environment';
import { TrackInterface } from '../models/TrackInterface';
import { AlbumInterface } from '../models/AlbumInterface';
import { ArtistInterface } from '../models/ArtistInterface';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private readonly trackServiceRoute = environment.trackServiceRoute;
  private readonly albumServiceRoute = environment.albumServiceRoute;
  private readonly artistServiceRoute = environment.artistServiceRoute;

  constructor(private httpClient: HttpClient) {

  }

  deleteTrack(track: TrackInterface): Observable<string> {
    return this.httpClient.delete(`${this.trackServiceRoute}/${track.id}`)
      .pipe(
        delay(1000),
        retry(2),
        map(() => {
          return `Successfully deleted track ${track.title} - ${track.artistList.map(artist => artist.name).join(', ')}`;
        })
      );
  }

  deleteAlbum(album: AlbumInterface): Observable<string> {
    return this.httpClient.delete(`${this.albumServiceRoute}/${album.id}`)
      .pipe(
        delay(1000),
        retry(2),
        map(() => {
          return `Successfully deleted album ${album.name}`;
        })
      );
  }

  deleteArtist(artist: ArtistInterface): Observable<string> {
    return this.httpClient.delete(`${this.artistServiceRoute}/${artist.id}`)
      .pipe(
        delay(1000),
        retry(2),
        map(() => {
          return `Successfully deleted artist ${artist.name}`;
        })
      );
  }

  editArtist(editedArtist: ArtistInterface) {
    return this.httpClient.put<ArtistInterface>(`${this.artistServiceRoute}`, editedArtist)
      .pipe(
        delay(1000),
        retry(2)
      );
  }

  editAlbum(editedAlbum: AlbumInterface) {
    return this.httpClient.put<AlbumInterface>(`${this.albumServiceRoute}`, editedAlbum)
      .pipe(
        delay(1000),
        retry(2)
      );
  }

  editTrack(editedTrack: TrackInterface) {
    return this.httpClient.put<TrackInterface>(`${this.trackServiceRoute}`, editedTrack)
      .pipe(
        delay(1000),
        retry(2)
      );
  }
}
