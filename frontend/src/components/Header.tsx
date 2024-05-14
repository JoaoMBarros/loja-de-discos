import React, { useState } from 'react';
import logo from '../assets/logo.svg';
import avatar from '../assets/user-avatar.jpg';
import { useAuth } from '@/hooks/UseAuth';
import { AvatarDropdownMenu } from './UserAvatarDropDownMenu';

interface props {
    children: React.ReactNode;
    showUserAvatar?: boolean;
    navigate: (path: string) => void;
}

export function Header({children, showUserAvatar, navigate}: props) {
    const [isDropDownMenuVisible, setDropDownMenuVisible] = useState(false);
    const { logout } = useAuth();

    function toggleDropDownMenu() {
        setDropDownMenuVisible(!isDropDownMenuVisible);
    }

    function handleLogout() {
        logout();
        navigate('/login');
    }

    // I'm handling the carroussel and search as just states in the Home component
    // So, when the user is in the home page and searches for an album, the carroussel will be hidden and the search grid will be shown
    // Then the user can go back to the carroussel by clicking on the BootPlay logo and refreshing the page
    const handleHomeButtonClick = () => {
        if (window.location.pathname === '/home') {
            window.location.reload();
        } else {
            navigate('/home');
        }
    };

    return (
        <>
            <header className="bg-white/30 sticky top-0 z-10 backdrop-blur-md backdrop-brightness-50">
                <nav className="navbar flex sm:flex-row h-[50px] items-center p-2 sm:p-4 justify-between">
                    <button className="home-button cursor-pointer" onClick={handleHomeButtonClick}>
                        <div className="flex items-center space-x-3">
                            <img src={logo} alt="BootPlay Logo" className="h-full"/>
                            <span className="hidden text-md text-white sm:block">BootPlay</span>
                        </div>
                    </button>
                    <div className="flex flex-row sm:flex-row md:space-x-8 items-center p-4 gap-6">
                        {children}
                        {showUserAvatar && (
                            <div className="relative">
                                <img src={avatar} onClick={toggleDropDownMenu} className='hidden h-12 rounded-full cursor-pointer sm:block mr-4' alt="User Avatar"/>
                                <button onClick={toggleDropDownMenu} className='sm:hidden'>
                                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" className="h-6 w-6 text-white mt-1 mr-4">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                                    </svg>
                                </button>
                                {isDropDownMenuVisible && (
                                    <AvatarDropdownMenu isVisible={isDropDownMenuVisible} onLogout={handleLogout} />
                                )}
                            </div>
                        )}
                    </div>
                </nav>
            </header>
        </>
    );
}
