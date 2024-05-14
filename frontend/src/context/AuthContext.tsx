import { UserModel } from "@/models/UserModel";
import { albumApi, userApi } from "@/services/apiService";
import { createContext, useCallback, useEffect, useState } from "react";

interface AuthContextModel extends UserModel {
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<string | void>;
  logout: () => void;
}

export const AuthContext = createContext({} as AuthContextModel);

interface Props {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<Props> = ({children}) => {
  const [userData, setUserData] = useState<UserModel>();
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
      const data: UserModel = JSON.parse(localStorage.getItem('@Auth.Data') || "{}");
      if(data.id) {
        setIsAuthenticated(true);
        setUserData(data);
        setIsLoading(false);
      } else {
        setIsLoading(false);
      }
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('@Auth.Token');
    if (token) {
      userApi.defaults.headers.common.Authorization = `Basic ${token}`;
      albumApi.defaults.headers.common.Authorization = `Basic ${token}`;
    }
  }, []);

  const Login = useCallback(async (email: string, password: string) => {
    setIsLoading(true); // Start loading
    const respAuth = await userApi.post('/users/auth', {email, password});

    // Check if the response of authenticating is an error
    if(respAuth instanceof Error) {
      setIsLoading(false);
      return respAuth.message;
    }

    localStorage.setItem('@Auth.Token', respAuth.data.token);
    userApi.defaults.headers.common.Authorization = `Basic ${respAuth.data.token}`;
    const respUserInfo = await userApi.get(`/users/${respAuth.data.id}`);

    // Check if the response of getting the user info is an error
    if(respUserInfo instanceof Error) {
      setIsLoading(false);
      return respUserInfo.message;
    }

    localStorage.setItem('@Auth.Data', JSON.stringify(respUserInfo.data));
    setUserData(respUserInfo.data);
    setIsAuthenticated(true);
    setIsLoading(false);
  }, []);

  const Logout = useCallback(() => {
    localStorage.removeItem('@Auth.Data');
    localStorage.removeItem('@Auth.Token');
    setUserData(undefined);
    setIsAuthenticated(false);
    
    // return <Navigate to="/login"/>;
  }, []);


  // Need to provide default values for the user data because TypeScript
  // AuthContextProvider expects a UserModel object, but the initial state is undefined, so we need to provide default values
  const userDataWithDefaults = {
      ...userData,
      id: userData?.id?? 0,
      name: userData?.name?? '',
      email: userData?.email?? '',
      password: userData?.password?? '',
   };

  return (
    <AuthContext.Provider value={{ isLoading: isLoading, isAuthenticated: isAuthenticated, ...userDataWithDefaults, login: Login, logout: Logout}}>
      {children}
    </AuthContext.Provider>
  );
}