import { Component, Input } from '@angular/core';
import { ArtistInterface } from 'src/app/models/ArtistInterface';

@Component({
  selector: 'app-artist',
  templateUrl: './artist.component.html',
  styleUrls: ['./artist.component.css']
})
export class ArtistComponent {

  @Input()
  artist: ArtistInterface;
  
}
