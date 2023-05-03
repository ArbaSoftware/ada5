import { Component } from '@angular/core';
import { Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { Property } from '../model/property';

@Component({
  selector: 'app-propertydetails',
  templateUrl: './propertydetails.component.html',
  styleUrls: ['./propertydetails.component.css']
})
export class PropertydetailsComponent {
  private property: Property;
  propertyName : string = '';
  propertyType: string = '';
  required: boolean = false;
  multiple:string = 'No';

  constructor(@Inject(MAT_DIALOG_DATA) private data: any, private dialog:MatDialog) {
    this.property = data.property;
    this.propertyName = this.property.getName();
    this.propertyType = this.property.getType();
    this.required = this.property.isRequired();
    this.multiple = (this.property.isMultiple() ? "Yes" : "No");
  }

  cancel(): void {
    this.dialog.closeAll();
  }
}
