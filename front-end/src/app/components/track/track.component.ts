import { Component, Input } from '@angular/core';
import { TrackInterface } from 'src/app/models/TrackInterface';

@Component({
  selector: 'app-track',
  templateUrl: './track.component.html',
  styleUrls: ['./track.component.css']
})
export class TrackComponent {

  @Input()
  track: TrackInterface;

  artistDetails = false;
  albumDetails = false;

  getCreatorNameList() {
    return this.track.artistList.map(artist => artist.name).join(', ');
  }
}
