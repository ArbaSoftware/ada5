export class Store {
    private id: string;
    private name: string;

    constructor(id: string, name: string) {
        this.id = id;
        this.name = name;
    }

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

    public static fromJson(source: any) : Store {
        return new Store(source.id, source.name);
    }
}
