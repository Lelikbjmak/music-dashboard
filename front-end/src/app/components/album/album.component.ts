import { Component, Input } from '@angular/core';
import { AlbumInterface } from 'src/app/models/AlbumInterface';

@Component({
  selector: 'app-album',
  templateUrl: './album.component.html',
  styleUrls: ['./album.component.css']
})
export class AlbumComponent {

  @Input()
  album: AlbumInterface;

}
