import { AlbumModel } from "@/models/AlbumModel";
import { AlbumCard } from "./AlbumCard";

interface props {
    albums: AlbumModel[];
    isVisible: boolean;
    showModal: (album: AlbumModel) => void;
}
export function SearchedAlbums( {albums, isVisible, showModal } : props){
    const isEmpty = albums.length === 0;
    return (
        <>
       {isVisible}
        <section className="flex flex-col bg-[#19181f] h-full">
            <div className="flex sm:justify-start sm:ml-4 justify-center mt-4">
                <h1 className="text-3xl text-start font-bold text-white">Resultados da busca</h1>
            </div>
            
            <div className="flex flex-col h-fit bg-[#19181f]">
                <div className="flex flex-wrap gap-4 mt-10 justify-center items-center w-full">
                {isEmpty? (
                    // If albums array is empty, show an error message
                    <div className="text-center text-white">
                        <p>Nenhum álbum encontrado.</p>
                    </div>
                ) : (
                 albums?.map((album, i) => (
                    <button key={i} onClick={() => {showModal(album)}}>
                        <div style={{'--bg-fundo': `url(${album.images[0].url})`} as React.CSSProperties} className="bg-[image:var(--bg-fundo)] bg-cover bg-no-repeat w-60 h-[245px] rounded-md hover:scale-110">
                            <AlbumCard albumName={album.name} albumValue={album.value}/>
                        </div>
                    </button>
                )))}
                </div>
            </div>
        </section>
       </>
    );
}