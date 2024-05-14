import AuthFormInput from '../../components/AuthFormInput';
import AuthFormButton from '../../components/AuthFormButton';
import { useState, FormEvent } from 'react';
import { useAuth } from '@/hooks/UseAuth';
import { useNavigate } from 'react-router-dom';
import { AuthFormCard } from '@/components/AuthFormCard';
import toast from "react-hot-toast";

export function Login(){
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const { login, isAuthenticated, isLoading } = useAuth();
    const _navegate = useNavigate();

    async function handleLogin(e: FormEvent) {
        e.preventDefault();
        try {
            await login(email, password);

            if (!isLoading){
                _navegate('/home');
            }

        } catch (error) {
            toast.error("Erro ao efetuar login!");
        }
    }

    return (
        <>
        {!isLoading && !isAuthenticated && (
            <AuthFormCard handleAuth={handleLogin} linkTo="/signup" decision='Inscrever-se' title='Acesse sua conta' footer="Ainda não tem conta?">
                <AuthFormInput htmlFor='email' type='email' required onChange={e => setEmail(e.target.value)}>Email</AuthFormInput>
                <AuthFormInput htmlFor='password' type='password' required onChange={e => setPassword(e.target.value)}>Senha</AuthFormInput>
                <AuthFormButton>Entrar</AuthFormButton>
            </AuthFormCard>
        )}
        </>
    );
}