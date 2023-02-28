import { Header } from "../components/display/Header";

export const MainLayout = ({ children }) => {
    return (
        <div className="flex flex-col space-y-2 h-screen">
            <Header />
            {children}
        </div>
    );
};
