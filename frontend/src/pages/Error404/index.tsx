import { Header } from '@/components/Header';
import errorImage from '../../assets/error.png';
import { useNavigate } from 'react-router-dom';

export function Error404(){
    const _navegate = useNavigate();
    const userAuthenticated = localStorage.getItem('@Auth.Token');

    return (
        <>
            <main className="content-wrapper bg-home-fundo bg-cover bg-no-repeat h-screen overflow-hidden" role="main">
                <div className='flex flex-col'> 
                        { userAuthenticated? (
                            <Header showUserAvatar navigate={_navegate}>
                                <a onClick={() => _navegate('/my-collection')} className="text-white font-semibold hover:text-zinc-300 hover:scale-110 cursor-pointer">Meus discos</a>
                                <a className="text-white font-bold hover:text-zinc-300">Carteira</a>
                            </Header>
                        ) : (
                            <Header navigate={_navegate}>
                                <button onClick={() => _navegate('/login')} className="text-white font-semibold bg-black w-48 rounded-2xl h-11">Entrar</button>
                                <button onClick={() => _navegate('/signup')} className="text-black font-bold bg-[#9EE2FF] w-48 rounded-2xl h-11">Inscrever-se</button>
                            </Header>
                        )}
                </div>
                <section className="h-full backdrop-brightness-50 bg-gradient-to-b from-transparent to-[#19181f] z-[0]">
                    <div className="flex flex-col justify-center items-center h-full">
                        <img src={errorImage} className='h-40' />
                        <p className="text-white text-3xl">Página não encontrada</p>
                    </div>
                </section>
            </main>
        </>
    );
}