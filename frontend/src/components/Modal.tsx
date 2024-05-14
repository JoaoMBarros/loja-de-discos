import { AlbumModel } from '@/models/AlbumModel';
import { albumApi } from '@/services/apiService';
import { useEffect, useState } from 'react';
import { toast } from 'react-hot-toast';
import '../../src/styles/modal-style.css';
import { UserModel } from '@/models/UserModel';
import { ModalText } from './ModalText';

interface props {
    album: AlbumModel;
    isVisible: boolean;
    onClose: () => void;
}

export function Modal( {album, isVisible, onClose} : props){
    const [isModalVisible, setModalVisible] = useState(false);
    const formattedReleaseDate = album.releaseDate.split('-').reverse().join('/');

    useEffect (() => {
        setModalVisible(isVisible);
    })

    const handleClose = () => {
        onClose();
    };

    const buyAlbum = (album: AlbumModel) => {
        const userLocalData = localStorage.getItem('@Auth.Data');
    
        if (userLocalData) {
            const userData: UserModel = JSON.parse(userLocalData);
        
            const data = {
                name: album.name,
                idSpotify: album.id,
                artistName: album.artists[0].name,
                imageUrl: album.images[0].url,
                value: album.value,
                users: {
                    id: userData.id,
                    name: userData.name,
                    email: userData.email,
                    password: userData.password,
                }
            };

            albumApi.post(`/albums/sale`, data, { headers: { Authorization: `Basic ${localStorage.getItem('@Auth.Token')}` } })
                .then(() => {
                    toast.success('Album comprado com sucesso!');
                    setTimeout(() => {
                        onClose();
                    }, 1000);
                }).catch(() => {
                    toast.error('Erro ao comprar album');
                });
            };
        };

        const handleOverlayClick = (event: React.MouseEvent<HTMLDivElement>) => {
            // Check if the click was outside
            if (!(event.target as HTMLElement).closest('.modal-content')) {
                handleClose();
            }
        };

    return (
        <>
            {/* Modal for buying an album */}
            {isVisible && (
                <div className={`fixed ${isModalVisible ? 'modal-open' : 'modal-close'} sm:w-screen z-[2] h-full`} id='buy-album-modal' onClick={(e) => handleOverlayClick(e)}>
                    <div className='flex items-center justify-center h-full backdrop-blur-sm shadow-lg w-screen'>
                        <div className='modal-content sm:flex sm:flex-row flex-col sm:max-w-[650px] sm:max-h-[310px] sm:h-full sm:w-full max-w-[350px] max-h-[700px] bg-white sm:justify-between rounded-3xl'>
                            <div className="hidden relative sm:block">
                                <button className='absolute flex sm:left-[620px] top-3 rounded-full hover:scale-150' onClick={handleClose}> 
                                    <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z" fill="black"/>
                                    </svg>
                                </button>
                            </div>
                            <img src={album.images[0].url} className='h-auto rounded-s-3xl w-full'/>
                            <div className='flex flex-col items-center justify-between w-full h-full'>
                                <h1 className='text-black text-2xl font-bold mt-3'>{album.artists[0].name}</h1>
                                    <div className='flex flex-col items-center text-center'>
                                        <ModalText text='Álbum' textValue={album.name}/>
                                        <ModalText text='Lançamento' textValue={formattedReleaseDate}/>
                                        <ModalText text="Preço" textValue={`R$${album.value}`} />
                                    </div>
                                <button onClick={() => buyAlbum(album)} className='bg-yellow-400 text-white rounded-lg p-2 sm:w-64 mb-3 mt-3 hover:scale-110 w-auto'>Comprar</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}