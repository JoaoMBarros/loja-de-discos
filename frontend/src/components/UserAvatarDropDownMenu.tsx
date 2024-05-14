interface props {
    isVisible: boolean;
    onLogout: () => void;
}

export function AvatarDropdownMenu({ isVisible, onLogout } : props) {

    return (
        {isVisible} && (
            <div className={`absolute right-0 mt-2 w-56 origin-top-right rounded-md bg-white shadow-lg ring-black`} role="menu">
                <div className="py-1">
                    <a className="block px-4 py-2 text-sm hover:text-black hover:font-extrabold hover:cursor-pointer" role="menuitem">Perfil</a>
                    <a onClick={onLogout} className="block px-4 py-2 text-sm hover:text-black hover:font-extrabold hover:cursor-pointer" role="menuitem">Logout</a>
                </div>
            </div>
        )
    );
};
