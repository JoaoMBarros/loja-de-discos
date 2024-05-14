export class Album {
    artistName: string;
    imageUrl: string;
    id: string;
    idSpotify: string;
    urlSpotify : string;
    name: string;
    value: number;
    users: User;
}

type User = {
    id : string;
    email : string;
    password : string;
}