import { addMoney, removeMoney } from "./store";
import { useDispatch } from "react-redux";


function Button() {
    const dispatch = useDispatch();
    return (
      <>
      <div>
        <button onClick={()=> dispatch(addMoney(500)) }> Add Money</button>
      </div>
      <div>
        <button onClick={()=> dispatch(removeMoney(500)) }> Remove Money</button>
      </div>
      </>
    )
}
export default Button;