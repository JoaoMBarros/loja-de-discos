import { Header } from "@/components/Header";
import { useNavigate } from "react-router-dom";
import { CollectionCard } from "@/components/CollectionCard";
import totalAlbumsIcon from "@/assets/total-albums.svg";
import totalSpendingIcon from "@/assets/total-spending.svg";
import { albumApi } from "@/services/apiService";
import { Album } from "@/models/Album";
import { useEffect, useState, useMemo } from "react";
import { AlbumCard } from "@/components/AlbumCard";

const MY_COLLECTION_TEXT = "Minha coleção";
const TOTAL_ALBUMS_TEXT = "Total de albums";
const TOTAL_SPENDING_TEXT = "Valor investido";
const NO_ALBUMS_TEXT = "Nenhum album encontrado";

export function UserCollection(){
    const _navegate = useNavigate();
    const [albums, setAlbums] = useState<Album[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        albumApi.get('/albums/my-collection', { headers: { Authorization: `Basic ${localStorage.getItem('@Auth.Token')}` } })
            .then((resp) => {
                setAlbums(resp.data);
                setLoading(false);
            })
    }, []);

    const totalAlbums = useMemo(() => albums.length, [albums]);
    const totalSpending = useMemo(() => albums.reduce((acc, album) => acc + album.value, 0).toFixed(2), [albums]);

    return (
       <>
        <section className="flex flex-col h-screen bg-[#19181f] overflow-x-hidden">
            <Header showUserAvatar navigate={_navegate}>
                <a className="text-black font-extrabold cursor-pointer">Meus discos</a>
                <a className="text-white font-semibold hover:text-zinc-300 hover:scale-110 cursor-pointer">Carteira</a>
            </Header>
            <div className="flex flex-col h-fit bg-[#19181f]">
                <div className="flex flex-col h-[55vh] w-full items-start justify-center">
                    <h1 className="text-3xl sm:text-start text-center font-bold text-white sm:ml-11 w-full">{MY_COLLECTION_TEXT}</h1>
                    <div className="flex p-4 gap-4 sm:mx-7 sm:justify-normal justify-center w-full">
                        <CollectionCard icon={totalAlbumsIcon}>
                            <h1 className="text-black text-lg font-semibold">{TOTAL_ALBUMS_TEXT}</h1>
                            <p className="text-black text-lg text-start font-semibold">{totalAlbums}</p>
                        </CollectionCard>
                        <CollectionCard icon={totalSpendingIcon}>
                            <h1 className="text-black text-lg font-semibold">{TOTAL_SPENDING_TEXT}</h1>
                            <p className="text-black text-lg text-start font-semibold">R${totalSpending}</p>
                        </CollectionCard>
                    </div>
                </div>
 
                { {loading} && (
                    <div className="flex flex-wrap gap-4 justify-center">
                        { albums.length === 0 && <div className="flex w-full justify-center"><h1 className="text-white text-2xl">{NO_ALBUMS_TEXT}</h1></div>}
                        { albums?.map((album, i) => (
                            <div key={i} style={{'--bg-fundo': `url(${album.imageUrl})`} as React.CSSProperties} className="bg-[image:var(--bg-fundo)] bg-cover bg-no-repeat w-60 h-[245px] rounded-md hover:scale-110">
                                <AlbumCard albumName={album.name} albumValue={album.value}/>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </section>
       </>
    );
}