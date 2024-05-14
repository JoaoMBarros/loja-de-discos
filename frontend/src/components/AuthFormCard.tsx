import logo from '../assets/logo.svg';
import { Link } from 'react-router-dom';
import '../../src/styles/modal-style.css';
import { useEffect, useState } from 'react';

interface props {
    children: React.ReactNode;
    handleAuth: (event: React.FormEvent) => void;
    linkTo: string;
    title : string;
    footer: string;
    decision: string;
}

export function AuthFormCard( {children, handleAuth, linkTo, title, footer, decision} : props ) {
    const [isLoaded, setIsLoaded] = useState(false);

    // Just so the modal doesn't open instantly when the page loads for the effect to be visible
    useEffect(() => {
        setIsLoaded(true);
    }, []);
    
    return (
        <>
        <main className="bg-landing-fundo bg-cover bg-no-repeat h-screen">
            <div className={'flex h-screen items-center justify-center backdrop-brightness-50 backdrop-blur-sm'}>
                <div className={`flex ${isLoaded ? 'modal-open' : 'modal-close'} max-w-[544px] bg-white rounded-md p-10`}>
                    <div className="flex flex-col items-center w-full gap-2">
                        <img src={logo} className='h-12'/>
                        <h1 className='text-xl font-semibold'> {title} </h1>
                        {/* Form */}
                        <form onSubmit={handleAuth} className='flex flex-col w-72'>
                            { children }
                        </form>
                        {/* Form */}
                        <span className='text-xs font-light'> {footer} <Link to={linkTo} className='font-semibold underline'>{decision}</Link> </span>
                    </div>
                </div>
            </div>
        </main>
        </>
    );
}