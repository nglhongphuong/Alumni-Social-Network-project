import "./Gemini.css"
import Sidebar from "./Sidebar/Sidebar";
import Main from "./Main/Main";
const Gemini = () => {

    return (
        <div className="gemini-background">
            <div className="gemini-container">
                <>
                <Sidebar />
                <Main />
                </>

            </div>


        </div>
    )

}
export default Gemini;