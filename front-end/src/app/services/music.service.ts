import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ErrorService } from './error.service';
import { environment } from '../../environments/environment';
import { TrackInterface } from '../models/TrackInterface';
import { JwtService } from './jwt.service';
import { Observable, catchError, delay, retry, throwError } from 'rxjs';
import { ArtistInterface } from '../models/ArtistInterface';
import { AlbumInterface } from '../models/AlbumInterface';

@Injectable({
  providedIn: 'root'
})
export class MusicService {

  private readonly trackServiceRoute = environment.trackServiceRoute;
  private readonly albumServiceRoute = environment.albumServiceRoute;
  private readonly artistServiceRoute = environment.artistServiceRoute;


  constructor(private httpClient: HttpClient) {

  }

  findTrackById(id: string) {
    return this.httpClient.get<TrackInterface>(`${this.trackServiceRoute}/${id}`)
      .pipe(
        delay(2000),
        retry(2)
      );
  }

  findAllTracks(): Observable<TrackInterface[]> {
    return this.httpClient.get<TrackInterface[]>(`${this.trackServiceRoute}`)
      .pipe(
        delay(1000),
        retry(2)
      );
  }

  findAllAlbums(): Observable<AlbumInterface[]> {
    return this.httpClient.get<AlbumInterface[]>(`${this.albumServiceRoute}`)
      .pipe(
        delay(1000),
        retry(2)
      );
  }

  findAllArtists(): Observable<ArtistInterface[]> {
    return this.httpClient.get<ArtistInterface[]>(`${this.artistServiceRoute}`)
      .pipe(
        delay(1000),
        retry(2)
      );
  }
}
