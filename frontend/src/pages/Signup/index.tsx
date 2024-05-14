import { useState, FormEvent } from "react";

import AuthFormInput from "../../components/AuthFormInput";
import { userApi } from "../../services/apiService";
import toast from "react-hot-toast";
import AuthFormButton from "../../components/AuthFormButton";
import { AuthFormCard } from "@/components/AuthFormCard";
import { useNavigate } from "react-router-dom";


export function Signup () {

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const _navegate = useNavigate();

    async function handleSignup(event: FormEvent) {
        event.preventDefault();
        try {
            const data = { name, email, password };
            await userApi.post("users/create", data);
            toast.success("Conta criada com sucesso!");
            _navegate('/login');
        } catch (error) {
            toast.error("Erro ao criar conta!");
        }
    }

    return (
        <AuthFormCard handleAuth={handleSignup} linkTo="/login" decision='Entrar' title='Criar conta' footer="Já tem uma conta?">
            <AuthFormInput htmlFor='text' type='text' required onChange={e => setName(e.target.value)}>Nome completo</AuthFormInput>
            <AuthFormInput htmlFor='email' type='email' required onChange={e => setEmail(e.target.value)}>Email</AuthFormInput>
            <AuthFormInput htmlFor='password' type='password' required onChange={e => setPassword(e.target.value)}>Senha</AuthFormInput>
            <AuthFormButton>Inscrever-se</AuthFormButton>
        </AuthFormCard>
    );
}