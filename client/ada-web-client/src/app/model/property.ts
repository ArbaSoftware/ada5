export class Property {
    private id : string = '';
    private name: string = '';
    private type: string = '';
    private required: boolean = false;
    private multiple: boolean = false;

    public static fromJson(json:any) : Property {
        var result : Property = new Property();
        result.id = json.id;
        result.name = json.name;
        result.type = json.type;
        result.required = json.required;
        result.multiple = json.multiple;
        return result;
    }

    public getId() : string { 
        return this.id;
    }

    public getName() : string { 
        return this.name;
    }

    public getType() : string {
        return this.type;
    }

    public isRequired() : boolean { 
        return this.required;
    }

    public isMultiple() : boolean { 
        return this.multiple;
    }
}
