import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadTrackComponent } from './upload-track.component';

describe('UploadTrackComponent', () => {
  let component: UploadTrackComponent;
  let fixture: ComponentFixture<UploadTrackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadTrackComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadTrackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
