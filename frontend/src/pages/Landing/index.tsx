import { Header } from '@/components/Header';
import { useNavigate } from 'react-router-dom'

export function Landing(){
    const _navegate = useNavigate();

    return (
        <main className="bg-landing-fundo bg-cover bg-no-repeat h-screen">
            <section className='flex flex-col h-screen'>
                
                <Header navigate={_navegate}>
                    <button onClick={() => _navegate('/login')} className="text-white font-semibold bg-black w-48 rounded-2xl h-11">Entrar</button>
                    <button onClick={() => _navegate('/signup')} className="text-black font-bold bg-[#9EE2FF] w-48 rounded-2xl h-11">Inscrever-se</button>
                </Header>

                <div className="flex backdrop-blur-sm backdrop-brightness-50 h-screen">
                    <div className="flex flex-col justify-center gap-6 ml-24 max-w-[700px]">
                        <h1 className="text-6xl font-bold text-white">A história da música não pode ser esquecida!</h1>
                        <p className="text-2xl text-white">Crie já sua conta e curta os sucessos que marcaram os tempos no Vinil.</p>
                        <button className="text-black font-bold bg-[#9EE2FF] w-48 rounded-2xl h-11">Inscrever-se</button>
                    </div>
                </div>
            </section>
        </main>
    );
}