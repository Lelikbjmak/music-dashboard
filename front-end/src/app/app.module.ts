import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { SignupComponent } from './components/signup/signup.component';
import { SigninComponent } from './components/signin/signin.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProgressLoaderComponent } from './components/progress-loader/progress-loader.component';
import { ErrorComponent } from './components/error/error.component';
import { UploadTrackComponent } from './components/upload-track/upload-track.component';
import { TrackSearchComponent } from './components/track-search/track-search.component';
import { TrackComponent } from './components/track/track.component';
import { AlbumComponent } from './components/album/album.component';
import { ArtistComponent } from './components/artist/artist.component';
import { TimePipe } from './pipes/time.pipe';
import { FormatDatePipe } from './pipes/format-date.pipe';
import { JwtInterceptor } from './handlers/interceptors/jwt.interceptor';
import { RouterModule } from '@angular/router';
import { ErrorInterceptor } from './handlers/events/error.interceptor';
import { AdminComponent } from './components/admin/admin.component';
import { MatSelectModule } from '@angular/material/select';
import { EditAlbumComponent } from './components/edit-album/edit-album.component';
import { EditTrackComponent } from './components/edit-track/edit-track.component';
import { EditArtistComponent } from './components/edit-artist/edit-artist.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    SignupComponent,
    SigninComponent,
    ProgressLoaderComponent,
    ErrorComponent,
    UploadTrackComponent,
    TrackSearchComponent,
    TrackComponent,
    AlbumComponent,
    ArtistComponent,
    TimePipe,
    FormatDatePipe,
    AdminComponent,
    EditTrackComponent,
    EditAlbumComponent,
    EditArtistComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatProgressSpinnerModule,
    MatSelectModule
  ],
  exports: [
    RouterModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
