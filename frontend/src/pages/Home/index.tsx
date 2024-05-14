import searchIcon from '../../assets/search-icon.svg'
import { AlbumModel } from '@/models/AlbumModel';
import { useState, useRef } from 'react';
import '../../styles/home-style.css'
import { Modal } from '@/components/Modal';
import { AlbumsCarousel } from '@/components/AlbumsCarousel';
import { SearchedAlbums } from '@/components/SearchedAlbums';
import toast from 'react-hot-toast';
import { Header } from '@/components/Header';
import { useNavigate } from 'react-router-dom';
import { useAlbumApi } from '@/hooks/useAlbumApi';

export function Home(){
    const _navegate = useNavigate();
    const searchInputRef = useRef<HTMLInputElement>(null);
    const [search, setSearch] = useState('');
    const [inputValue, setInputValue] = useState('');

    // UI state management that controls the visibility of the modal, carousel and search grid
    const [uiState, setUiState] = useState({
        isModalVisible: false,
        selectedAlbum: {} as AlbumModel,
        isCarouselVisible: true,
        showSearch: false,
    });

    //Show modal with album details
    const showModal = (album : AlbumModel) => {
        setUiState(prevState => ({
           ...prevState,
            selectedAlbum: album,
            isModalVisible: true,
        }));
    };

    // Hide modal
    const hideModal = () => {
        setUiState(prevState => ({
           ...prevState,
            isModalVisible: false,
        }));
    };

    // // Toggle carousel visibility
    // const toggleCarouselVisibility = () => {
    //     setUiState(prevState => ({
    //        ...prevState,
    //         isCarouselVisible:!prevState.isCarouselVisible,
    //     }));
    // };

    // Toggle search grid visibility
    const toggleSearchVisibility = () => {
        setUiState(prevState => ({
           ...prevState,
            isCarouselVisible: false,
            showSearch:true,
        }));
    };

    // Search albums by search string
    const searchAlbums = () => {
        const searchString = searchInputRef.current?.value;
        if (searchString) {
            setSearch(searchString);
        } else {
            toast.error('Digite algo para pesquisar');
        }
    };

    // Enter key event to search albums
    const enterKeyInput = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            searchAlbums();
        }
    }

    // Fetch albums from API Album hook based on search string
    // If no search string is provided, fetch albums based on default search string 'Rock' and it does not toggle search visibility
    const albums = search? useAlbumApi(search, toggleSearchVisibility) : useAlbumApi('Red hot chilli peppers', () => {});

    return (
        <>
            <main className="content-wrapper bg-home-fundo bg-cover bg-no-repeat h-screen overflow-x-hidden" role="main">
                <div className='content-body flex flex-col h-full'>
                    <Header showUserAvatar navigate={_navegate}>
                        <a onClick={() => _navegate('/my-collection')} className="text-white font-semibold hover:text-zinc-300 hover:scale-110 cursor-pointer">Meus discos</a>
                        <a className="text-white font-bold hover:text-zinc-300">Carteira</a>
                    </Header>
                    <section className="backdrop-brightness-50 bg-gradient-to-b from-transparent to-[#19181f] z-[0]">
                        <div className='flex flex-col h-[50vh] items-start justify-center'>
                            <div className="flex flex-col m-auto gap-6 ml-7 max-w-[504px]">
                                <h1 className="text-4xl font-bold text-white">A história da música não pode ser esquecida!</h1>
                                <p className="text-2xl text-white">Sucessos que marcaram o tempo!!!</p>
                            </div>
                        </div>
                    </section>
                    <section className='flex items-center justify-center bg-[#19181f]'>
                        <div className="relative">
                            <label htmlFor="search" className="sr-only">Search for albums</label>
                            <input onKeyDown={(e) => enterKeyInput(e)} id="search" ref={searchInputRef} className="bg-[#19181f] w-[300px] ps-3 h-8 text-sm text-white border border-gray-300 rounded-lg" onChange={(e) => {setInputValue(e.target.value)}}/>
                            <div className="absolute inset-y-0 end-0 flex items-center pr-2">
                                <button onClick={searchAlbums} disabled={inputValue === ''}>
                                    <img src={searchIcon} className='h-4 w-4 text-black' alt='Search icon'/>
                                </button>
                            </div>
                        </div>
                    </section>
                    
                    {uiState.showSearch && <SearchedAlbums albums={albums} isVisible={uiState.showSearch} showModal={showModal}/>}
                    {uiState.isCarouselVisible && <AlbumsCarousel albums={albums} isVisible={uiState.isCarouselVisible} showModal={showModal}/>}
                    {uiState.isModalVisible && <Modal album={uiState.selectedAlbum} isVisible={uiState.isModalVisible} onClose={hideModal}/>}  
                </div>
            </main>
        </>
    );
}