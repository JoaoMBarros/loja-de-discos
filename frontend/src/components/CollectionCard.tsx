interface props {
    children: React.ReactNode;
    icon: string;
}

export function CollectionCard( {children, icon} : props ){
    return (
        <>
            <div className="flex h-[87px] w-[237px] bg-white justify-center items-center rounded-lg shadow-md shadow-[#4e4672]">
                <div className="flex bg-black justify-center items-center rounded-xl h-9 w-9 mr-4">
                    <img src={icon}/>
                </div>
                <div className="flex flex-col">
                    { children }
                </div>
            </div>
        </>
    );
}