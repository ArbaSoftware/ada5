import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {FlatTreeControl} from '@angular/cdk/tree';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import { RestService } from '../rest.service';
import { AdaClass } from '../model/ada-class';
import { ClassdetailsComponent } from '../classdetails/classdetails.component';
import { MatDialog } from '@angular/material/dialog';

interface StoreNode {
  name: string;
  id: string;
  children: StoreNode[];
}

/** Flat node with expandable and level information */
interface ExampleFlatNode {
  expandable: boolean;
  name: string;
  level: number;
  id: string;
}

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.css']
})
export class StoreComponent {
  treeData: StoreNode[] = [
    {
      name: 'Classes',
      id: 'root_classes',
      children: [
        {
          name: 'Folder classes',
          id: 'root_folder_classes',
          children: []
        },
        {
          name: 'Document classes',
          id: 'root_document_classes',
          children: []
        }
      ],
    },
    {
      name: 'Security',
      id: 'root_security',
      children: []
    },
  ];
  storeId: string = '';
  private _transformer = (node: StoreNode, level: number) => {
    return {
      expandable: !!node.children && node.children.length > 0,
      name: node.name,
      level: level,
      id: node.id
    };
  };

  treeControl = new FlatTreeControl<ExampleFlatNode>(
    node => node.level,
    node => node.expandable,
  );

  treeFlattener = new MatTreeFlattener(
    this._transformer,
    node => node.level,
    node => node.expandable,
    node => node.children,
  );

  dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

  hasChild = (_: number, node: ExampleFlatNode) => node.expandable;

  constructor(private route: ActivatedRoute, private service: RestService, private dialog: MatDialog) {
    this.storeId = route.snapshot.params['id'];
    this.service.getClasses(this.storeId, this.addClasses.bind(this));
    this.dataSource.data = this.treeData;
  }

  addClasses(classes: AdaClass[]) {
    var treeData = this.treeData;
    classes.forEach(function(item) {
      var newNode: StoreNode ={name: item.getName(), id: item.getId(), children: []};
      treeData[0].children[0].children.push(newNode);
    });
    this.dataSource.data = this.treeData;
  }

  onStoreClick(id: string) : void {
    let dialogRef = this.dialog.open(ClassdetailsComponent, {
      height: '400px',
      width: '600px',
      data: {storeid: this.storeId, classid: id}
    });    
  }
}