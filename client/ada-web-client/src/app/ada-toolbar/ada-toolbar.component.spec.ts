import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdaToolbarComponent } from './ada-toolbar.component';

describe('AdaToolbarComponent', () => {
  let component: AdaToolbarComponent;
  let fixture: ComponentFixture<AdaToolbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdaToolbarComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdaToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
