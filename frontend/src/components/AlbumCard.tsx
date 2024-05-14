interface props {
    albumName: string;
    albumValue: number;
    cursor?: string;
}

export function AlbumCard( {albumName, albumValue, cursor} : props ){
    return (
        <>
            <div className={`flex flex-col h-full justify-center items-center backdrop-brightness-50 p-6 ${cursor}`}>
                <div className='flex items-center justify-center h-full overflow-hidden'>
                    <h1 className="text-2xl font-semibold text-center text-white">{albumName}</h1>
                </div>
                <div className='items-end w-full'>
                    <p className='text-white text-right font-semibold justify-end'>R${albumValue}</p>
                </div>
            </div>
        </>
    );
}