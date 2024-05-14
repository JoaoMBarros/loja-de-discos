import React from 'react';
import ReactDOM from 'react-dom/client'
import './global.css'
import { AuthProvider } from './context/AuthContext';
import { Toaster } from 'react-hot-toast';
import { Home } from './pages/Home';
import { Login } from './pages/Login';
import { Signup } from './pages/Signup';
import { Landing } from './pages/Landing';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { PrivateRoutes } from './utils/PrivateRoutes';
import { UserCollection } from './pages/UserCollection';
import { Error404 } from './pages/Error404';

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.Fragment>
        <Toaster position='top-right' toastOptions={{ duration: 5000 }} />
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path='/' element={<Landing />} />
                    <Route path='/login' element={<Login />} />
                    <Route path='/signup' element={<Signup />} />

                    <Route element={<PrivateRoutes />}>
                        <Route path='/home' element={<Home />} />
                        <Route path='/my-collection' element={<UserCollection />} />
                    </Route>


                    <Route path="*" element={<Error404 />} />
                    </Routes>
                </BrowserRouter>
        </AuthProvider>
    </React.Fragment>
)
