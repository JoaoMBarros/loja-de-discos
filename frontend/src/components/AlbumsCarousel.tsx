import { AlbumModel } from "@/models/AlbumModel";
import { AlbumCard } from "./AlbumCard";
import '../../src/styles/carousel-style.css';

interface props {
    albums: AlbumModel[];
    isVisible: boolean;
    showModal: (album: AlbumModel) => void;
}

export function AlbumsCarousel({ albums, isVisible, showModal} : props){

    return(
        <>
        {isVisible}
        <div className=" bg-[#19181f] h-full">
            <div className="flex flex-col justify-start">
                <h1 className="text-3xl sm:text-start text-center font-bold sm:ml-4 mb-2 mt-2 text-white">Trends</h1>
            </div>
            <div className="flex flex-col items-center justify-start h-fit relative">
                {albums.length === 0 && ( <div className="text-center text-white"> <p>Nenhum álbum encontrado.</p> </div> )}
                <div className='carousel-home left-0 flex items-center w-full'>
                    {/* Card */}
                    { albums?.map((album, i) => (
                        <button key={i} onClick={() => {showModal(album)}}>
                            <div className='p-4 '>
                                <div style={{'--bg-fundo': `url(${album.images[0].url})`} as React.CSSProperties} className="bg-[image:var(--bg-fundo)] bg-cover bg-no-repeat w-60 h-[245px] rounded-md hover:scale-110 transition">
                                    <AlbumCard albumName={album.name} albumValue={album.value} cursor="cursor-pointer"/>
                                </div>
                            </div>
                        </button>
                    ))}
                </div>
            </div>
            </div>
        </>
    );
}