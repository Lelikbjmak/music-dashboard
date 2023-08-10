import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditTrackComponent } from './edit-artist.component';

describe('EditTrackComponent', () => {
  let component: EditTrackComponent;
  let fixture: ComponentFixture<EditTrackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditTrackComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditTrackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
