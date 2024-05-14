import React from 'react';

interface Props {
    children: React.ReactNode;
    type: string;
    required?: boolean;
    htmlFor?: string;
    onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export default function Input({ children, type, required, onChange, htmlFor } : Props) {
    return (
        <>
            <label htmlFor={htmlFor} className="text-sm font-normal">
                {children}
            </label>
            <input type={type} onChange={onChange} required = {required} className="bg-zinc-50 ring-1 p-2 mb-3 ring-zinc-900/20 rounded-md"/>
        </>
    );
}