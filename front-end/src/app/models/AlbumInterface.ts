export interface AlbumInterface {
    id: string,
    name: string,
    albumType: string,
    popularity: number,
    releaseDate: Date,
    totalTracks: number,
    spotifyIconUri: string,
    spotifyUri: string,
    label: string
}