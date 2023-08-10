import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SigninComponent } from './components/signin/signin.component';
import { SignupComponent } from './components/signup/signup.component';
import { UploadTrackComponent } from './components/upload-track/upload-track.component';
import { TrackSearchComponent } from './components/track-search/track-search.component';
import { authenticationGuard } from './guards/authantication.guard';
import { AdminComponent } from './components/admin/admin.component';
import { authorizationGuard } from './guards/authorization.guard';

const routes: Routes = [
  {
    path: 'sign-in',
    component: SigninComponent
  },
  {
    path:
      'sign-up',
    component: SignupComponent
  },
  {
    path: 'upload-track',
    component: UploadTrackComponent,
    canActivate: [authenticationGuard]
  },
  {
    path: 'search-track',
    component: TrackSearchComponent,
    canActivate: [authenticationGuard]
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [authenticationGuard, authorizationGuard],
    data: {
      roles: ['ROLE_ADMIN']
    }
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
