import React from 'react';

interface Props {
    children: React.ReactNode;
}

export default function Button({ children } : Props) {
    return (
        <button type='submit' className= "bg-zinc-900 text-white rounded-xl p-3 mb-2 hover:bg-zinc-900/90 transition">
            { children }
        </button>
    );
}