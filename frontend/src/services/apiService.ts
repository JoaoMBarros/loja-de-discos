import axios from "axios";

export const albumApi = axios.create({
    baseURL: "http://localhost:8082/api",
});

export const userApi = axios.create({
    baseURL: "http://localhost:8080/api",
});