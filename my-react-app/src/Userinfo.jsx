import { useSelector } from "react-redux";



function Userinfo() {
let data = useSelector(state => state.user)
return (
    <div>
        <h1>{data.username},{data.balance}</h1>
        
    </div>
)
}
export default Userinfo;