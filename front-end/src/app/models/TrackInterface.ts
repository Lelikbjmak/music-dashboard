import { AlbumInterface } from "./AlbumInterface";
import { ArtistInterface } from "./ArtistInterface";

export interface TrackInterface {
    id: string,
    title: string,
    discNumber: number,
    durationMs: number,
    spotifyUri: string,
    spotifyIconUri: string,
    trackNumber: number,
    popularity: number,
    album: AlbumInterface,
    artistList: ArtistInterface[]
}