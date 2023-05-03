import { Component } from '@angular/core';
import { RestService } from '../rest.service';
import { Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { AdaClass } from '../model/ada-class';
import { Property } from '../model/property';
import { PropertydetailsComponent } from '../propertydetails/propertydetails.component';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-classdetails',
  templateUrl: './classdetails.component.html',
  styleUrls: ['./classdetails.component.css']
})
export class ClassdetailsComponent {
  public className: string = '<nog niet ingevuld>';
  public classId: string = '';
  public dataSource: Property[] = [];

  constructor(private service: RestService, @Inject(MAT_DIALOG_DATA) private data: any, private dialog: MatDialog) {
    this.service.getClass(data.storeid, data.classid, this.applyClassData.bind(this));
  }

  applyClassData(data: AdaClass): void {
    this.className = data.getName();
    this.classId = data.getId();
    this.dataSource = data.getProperties();
  }

  openProperty(property: Property) : void {
    let dialogRef = this.dialog.open(PropertydetailsComponent, {
      height: '400px',
      width: '600px',
      data: {property: property}
    });    
  }

  cancel() : void {
    this.dialog.closeAll();
  }
}
