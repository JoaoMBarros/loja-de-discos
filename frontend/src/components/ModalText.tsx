interface Props {
    text: string;
    textValue: string;
}

export function ModalText({ text, textValue }: Props) {
    return (
        <>
            <div className='flex flex-col items-center text-center'>
                <p className='font-thin mr-1'>{text}:</p>
                <p className='font-bold'>{textValue}</p>
            </div>
        </>
    );
}
