import { Property } from "./property";
export class AdaClass {
    private id: string = '';
    private name: string = '';
    private documentclass: boolean = false;
    private folderclass: boolean = false;
    private properties : Property[] = [];

    public setId(id: string) : void {
        this.id = id;
    }

    public getId() : string {
        return this.id;
    }

    public setName(name: string) : void {
        this.name = name;
    }

    public getName() : string {
        return this.name;
    }

    public getProperties() : Property[] {
        return this.properties;
    }

    public static fromJson(json:any) : AdaClass {
        var result = new AdaClass();
        result.setId(json.id);
        result.setName(json.name);
        if (json.properties) {
            for (var index = 0; index < json.properties.length; index++) {
                result.properties.push(Property.fromJson(json.properties[index]));
            }
        }
        return result;
    }
}
