import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrackSearchComponent } from './track-search.component';

describe('TrackSearchComponent', () => {
  let component: TrackSearchComponent;
  let fixture: ComponentFixture<TrackSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TrackSearchComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrackSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
