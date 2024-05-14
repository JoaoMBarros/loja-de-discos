import { useState, useEffect } from 'react';
import { albumApi } from '@/services/apiService';

export const useAlbumApi = (searchText = '', toggleSearchVisibility:() => void) => {
    const [albums, setAlbums] = useState([]);

    useEffect(() => {
        const fetchAlbums = async () => {
            const response = await albumApi.get(`/albums/all?search=${searchText}`, { headers: { Authorization: `Basic ${localStorage.getItem('@Auth.Token')}` } });
            setAlbums(response.data);
            toggleSearchVisibility();
        };
        fetchAlbums();
    }, [searchText]);

    return albums;
};